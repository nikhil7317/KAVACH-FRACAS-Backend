package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Role;
import com.railbit.tcasanalysis.repository.RoleRepo;
import com.railbit.tcasanalysis.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;
    @Override
    public Role getRoleById(Integer id) {
        Optional<Role> data=roleRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Role not found");
        return data.get();
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepo.findByName(name);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepo.findAll();
    }

    @Override
    public Role postRole(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public void updateRole(Role role) {
        roleRepo.save(role);
    }

    @Override
    public void deleteRoleById(Integer id) {
        roleRepo.deleteById(id);
    }
}
