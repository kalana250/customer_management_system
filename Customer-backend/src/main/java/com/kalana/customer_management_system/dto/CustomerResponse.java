package com.kalana.customer_management_system.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerResponse {

    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String nicNumber;
    private List<String> mobileNumbers;
    private List<AddressResponse> addresses;
    private List<FamilyMemberResponse> familyMembers;

    @Data
    public static class AddressResponse {
        private Long id;
        private String addressLine1;
        private String addressLine2;
        private String cityName;
        private String countryName;
    }

    @Data
    public static class FamilyMemberResponse {
        private Long id;
        private String name;
        private String nicNumber;
    }
}