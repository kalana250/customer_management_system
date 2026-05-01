package com.kalana.customer_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerRequest {

    @NotBlank(message = "Name is Required")
    private String name;

    @NotNull(message = "Date of birth is Required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "NIC number is Required")
    private String nicNumber;

    private List<String> mobileNumbers;

    private List<AddressRequest> addresses;

    private List<Long> familyMemberIds;

    @Data
    public static class AddressRequest {
        private String addressLine1;
        private String addressLine2;
        private Long cityId;
        private Long countryId;
    }
}