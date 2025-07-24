package com.autoria.repository;

import com.autoria.models.dealership.Dealership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DealershipRepository extends JpaRepository<Dealership, UUID> {
}
