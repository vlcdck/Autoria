package com.autoria.repository;

import com.autoria.models.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);

    @Query("""
       SELECT u FROM AppUser u
       LEFT JOIN FETCH u.roles r
       LEFT JOIN FETCH r.permissions
       WHERE u.email = :email
       """)
    Optional<AppUser> findByEmailWithRolesAndPermissions(@Param("email") String email);


    boolean existsByEmail(String email);
}
