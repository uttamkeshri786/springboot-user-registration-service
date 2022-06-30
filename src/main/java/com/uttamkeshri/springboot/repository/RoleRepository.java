package com.uttamkeshri.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uttamkeshri.springboot.model.Role;
import com.uttamkeshri.springboot.model.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleName roleName);
}
