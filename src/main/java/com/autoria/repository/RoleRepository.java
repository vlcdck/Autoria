package com.autoria.repository;

import com.autoria.models.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    boolean existsByName(String name);
}
