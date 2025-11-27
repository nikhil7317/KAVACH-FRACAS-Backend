package com.railbit.tcasanalysis.controller.rolecontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.PossibleRootCause;
import com.railbit.tcasanalysis.entity.Role;
import com.railbit.tcasanalysis.service.RoleService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/role")
public class RoleController {
    private final RoleService roleService;
    @GetMapping("/")
    public ResponseDTO<List<Role>> getAllRoles(){
        return ResponseDTO.<List<Role>>builder()
                .data(roleService.getAllRole())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getRoleById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Role>builder()
                .data(roleService.getRoleById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addRole(@Valid @RequestBody Role role) {
        return ResponseDTO.<Object>builder()
                .data(roleService.postRole(role))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateRole(@Valid @RequestBody Role role) {
        roleService.updateRole(role);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteRole(@PathVariable @Valid Integer id){
        roleService.deleteRoleById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
