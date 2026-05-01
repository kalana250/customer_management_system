package com.kalana.customer_management_system;

import com.kalana.customer_management_system.dto.CustomerRequest;
import com.kalana.customer_management_system.dto.CustomerResponse;
import com.kalana.customer_management_system.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerServiceTest {

	@Autowired
	private CustomerService customerService;

	private CustomerRequest buildRequest(String name, String nic) {
		CustomerRequest request = new CustomerRequest();
		request.setName(name);
		request.setNicNumber(nic);
		request.setDateOfBirth(LocalDate.of(1990, 1, 1));
		return request;
	}

	@Test
	void testCreateCustomer() {
		CustomerRequest request = buildRequest("John Doe", "NIC123456");
		CustomerResponse response = customerService.createCustomer(request);
		assertNotNull(response.getId());
		assertEquals("John Doe", response.getName());
	}

	@Test
	void testGetAllCustomers() {
		customerService.createCustomer(buildRequest("Alice", "NIC111111"));
		customerService.createCustomer(buildRequest("Bob", "NIC222222"));
		List<CustomerResponse> list = customerService.getAllCustomers();
		assertTrue(list.size() >= 2);
	}

	@Test
	void testGetCustomerById() {
		CustomerResponse created = customerService.createCustomer(buildRequest("Charlie", "NIC333333"));
		CustomerResponse found = customerService.getCustomerById(created.getId());
		assertEquals("Charlie", found.getName());
	}

	@Test
	void testUpdateCustomer() {
		CustomerResponse created = customerService.createCustomer(buildRequest("Dave", "NIC444444"));
		CustomerRequest update = buildRequest("Dave Updated", "NIC444444");
		CustomerResponse updated = customerService.updateCustomer(created.getId(), update);
		assertEquals("Dave Updated", updated.getName());
	}

	@Test
	void testDeleteCustomer() {
		CustomerResponse created = customerService.createCustomer(buildRequest("Eve", "NIC555555"));
		customerService.deleteCustomer(created.getId());
		assertThrows(Exception.class, () -> customerService.getCustomerById(created.getId()));
	}

	@Test
	void testDuplicateNic() {
		customerService.createCustomer(buildRequest("Frank", "NIC666666"));
		assertThrows(RuntimeException.class, () ->
				customerService.createCustomer(buildRequest("Frank2", "NIC666666"))
		);
	}
}