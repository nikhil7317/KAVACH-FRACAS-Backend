package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.Role;

import java.util.List;

public interface RoleService {
    Role getRoleById(Integer id);
    Role getRoleByName(String name);
    List<Role> getAllRole();
    Role postRole(Role role);
    void updateRole(Role role);
    void deleteRoleById(Integer id);
}
