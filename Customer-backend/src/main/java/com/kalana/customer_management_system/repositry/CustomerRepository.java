package com.kalana.customer_management_system.repositry;


import com.kalana.customer_management_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByNicNumber(String nicNumber);

    Optional<Customer> findByNicNumber(String nicNumber);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.mobileNumbers LEFT JOIN FETCH c.addresses")
    List<Customer> findAllWithDetails();
}