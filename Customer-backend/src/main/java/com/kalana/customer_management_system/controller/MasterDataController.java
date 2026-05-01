package com.kalana.customer_management_system.controller;

import com.kalana.customer_management_system.entity.City;
import com.kalana.customer_management_system.entity.Country;
import com.kalana.customer_management_system.repositry.CityRepository;
import com.kalana.customer_management_system.repositry.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@CrossOrigin(origins = "http://localhost:3000")
public class MasterDataController {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> getCountries() {
        return ResponseEntity.ok(countryRepository.findAll());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<City>> getAllCities() {
        return ResponseEntity.ok(cityRepository.findAll());
    }

    @GetMapping("/cities/country/{countryId}")
    public ResponseEntity<List<City>> getCitiesByCountry(@PathVariable Long countryId) {
        return ResponseEntity.ok(cityRepository.findByCountryId(countryId));
    }
}