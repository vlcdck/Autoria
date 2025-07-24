package com.autoria.repository;

import com.autoria.models.user.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    boolean existsByCode(String code);
}
