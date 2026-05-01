package com.kalana.customer_management_system.service;

import com.kalana.customer_management_system.dto.CustomerRequest;
import com.kalana.customer_management_system.dto.CustomerResponse;
import com.kalana.customer_management_system.entity.*;
import com.kalana.customer_management_system.exception.ResourceNotFoundException;
import com.kalana.customer_management_system.repositry.CityRepository;
import com.kalana.customer_management_system.repositry.CountryRepository;
import com.kalana.customer_management_system.repositry.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ExcelImportService excelImportService;

    // ==================== CREATE ====================
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByNicNumber(request.getNicNumber())) {
            throw new RuntimeException("NIC number already exists: " + request.getNicNumber());
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNicNumber(request.getNicNumber());

        // Set Mobile Numbers
        if (request.getMobileNumbers() != null) {
            List<MobileNumber> mobiles = request.getMobileNumbers().stream()
                    .map(num -> {
                        MobileNumber mobile = new MobileNumber();
                        mobile.setNumber(num);
                        mobile.setCustomer(customer);
                        return mobile;
                    }).collect(Collectors.toList());
            customer.setMobileNumbers(mobiles);
        }

        // Set Addresses
        if (request.getAddresses() != null) {
            List<Address> addresses = request.getAddresses().stream()
                    .map(addr -> {
                        Address address = new Address();
                        address.setAddressLine1(addr.getAddressLine1());
                        address.setAddressLine2(addr.getAddressLine2());
                        address.setCustomer(customer);

                        if (addr.getCityId() != null) {
                            City city = cityRepository.findById(addr.getCityId())
                                    .orElseThrow(() -> new ResourceNotFoundException("City not found"));
                            address.setCity(city);
                        }

                        if (addr.getCountryId() != null) {
                            Country country = countryRepository.findById(addr.getCountryId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Country not found"));
                            address.setCountry(country);
                        }

                        return address;
                    }).collect(Collectors.toList());
            customer.setAddresses(addresses);
        }

        // Set Family Members
        if (request.getFamilyMemberIds() != null) {
            List<Customer> familyMembers = request.getFamilyMemberIds().stream()
                    .map(id -> customerRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id)))
                    .collect(Collectors.toList());
            customer.setFamilyMembers(familyMembers);
        }

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    // ==================== GET ALL ====================
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== GET BY ID ====================
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    // ==================== UPDATE ====================
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Check NIC uniqueness (allow same NIC for same customer)
        if (!customer.getNicNumber().equals(request.getNicNumber()) &&
                customerRepository.existsByNicNumber(request.getNicNumber())) {
            throw new RuntimeException("NIC number already exists: " + request.getNicNumber());
        }

        customer.setName(request.getName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNicNumber(request.getNicNumber());

        // Update Mobile Numbers
        customer.getMobileNumbers().clear();
        if (request.getMobileNumbers() != null) {
            request.getMobileNumbers().forEach(num -> {
                MobileNumber mobile = new MobileNumber();
                mobile.setNumber(num);
                mobile.setCustomer(customer);
                customer.getMobileNumbers().add(mobile);
            });
        }

        // Update Addresses
        customer.getAddresses().clear();
        if (request.getAddresses() != null) {
            request.getAddresses().forEach(addr -> {
                Address address = new Address();
                address.setAddressLine1(addr.getAddressLine1());
                address.setAddressLine2(addr.getAddressLine2());
                address.setCustomer(customer);

                if (addr.getCityId() != null) {
                    cityRepository.findById(addr.getCityId())
                            .ifPresent(address::setCity);
                }

                if (addr.getCountryId() != null) {
                    countryRepository.findById(addr.getCountryId())
                            .ifPresent(address::setCountry);
                }

                customer.getAddresses().add(address);
            });
        }

        // Update Family Members
        customer.getFamilyMembers().clear();
        if (request.getFamilyMemberIds() != null) {
            request.getFamilyMemberIds().forEach(fid -> {
                customerRepository.findById(fid)
                        .ifPresent(customer.getFamilyMembers()::add);
            });
        }

        Customer updated = customerRepository.save(customer);
        return mapToResponse(updated);
    }

    // ==================== DELETE ====================
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    // ==================== BULK IMPORT ====================
    public int bulkImport(MultipartFile file) throws IOException {
        return excelImportService.importCustomers(file, customerRepository);
    }

    // ==================== MAP TO RESPONSE ====================
    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setNicNumber(customer.getNicNumber());

        // Map Mobile Numbers
        response.setMobileNumbers(
                customer.getMobileNumbers().stream()
                        .map(MobileNumber::getNumber)
                        .collect(Collectors.toList())
        );

        // Map Addresses
        response.setAddresses(
                customer.getAddresses().stream()
                        .map(addr -> {
                            CustomerResponse.AddressResponse ar = new CustomerResponse.AddressResponse();
                            ar.setId(addr.getId());
                            ar.setAddressLine1(addr.getAddressLine1());
                            ar.setAddressLine2(addr.getAddressLine2());
                            if (addr.getCity() != null) ar.setCityName(addr.getCity().getName());
                            if (addr.getCountry() != null) ar.setCountryName(addr.getCountry().getName());
                            return ar;
                        }).collect(Collectors.toList())
        );

        // Map Family Members
        response.setFamilyMembers(
                customer.getFamilyMembers().stream()
                        .map(fm -> {
                            CustomerResponse.FamilyMemberResponse fmr = new CustomerResponse.FamilyMemberResponse();
                            fmr.setId(fm.getId());
                            fmr.setName(fm.getName());
                            fmr.setNicNumber(fm.getNicNumber());
                            return fmr;
                        }).collect(Collectors.toList())
        );

        return response;
    }
}