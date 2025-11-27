package com.railbit.tcasanalysis.repository;

import com.railbit.tcasanalysis.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Integer> {
    Role findByName(String name);
}
