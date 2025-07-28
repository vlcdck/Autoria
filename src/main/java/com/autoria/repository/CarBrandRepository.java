package com.autoria.repository;

import com.autoria.models.car.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, UUID> {
    Optional<CarBrand> findByNameIgnoreCase(String name);

}
