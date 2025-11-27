package com.railbit.tcasanalysis.controller.userController;


import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.DTO.UserDTO;
import com.railbit.tcasanalysis.config.ActiveUserSessionListener;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.service.UserService;
import com.railbit.tcasanalysis.util.Constants;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseDTO<?> getUserById(@PathVariable @Valid Long id){
        return ResponseDTO.<UserDTO>builder()
                .data(userService.getUserById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("getUserByMail/{mail}")
    public ResponseDTO<?> getUserByMail(@PathVariable @Valid String mail){
        return ResponseDTO.<UserDTO>builder()
                .data(userService.getUserByEmail(mail))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/")
    public ResponseDTO<List<User>> getAllUser(){
        return ResponseDTO.<List<User>>builder()
                .data(userService.getAllUser())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?> addUser(@Valid @RequestBody UserDTO user) throws Exception {
//        System.out.println(user.toString());
        Long adminId = HelpingHand.getUserIdByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        user.setAdminId(adminId);
        userService.postUser(user);

        return ResponseDTO.<Object>builder()
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/verifyUser/")
    public ResponseDTO<?> verifyUser(@Valid @RequestBody UserDTO user) throws Exception {

        userService.verifyUser(user);

        return ResponseDTO.<Object>builder()
                .data("User Verified Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/sendRegistrationOtp/")
    public ResponseDTO<?> sendRegistrationOtp(@Valid @RequestBody UserDTO user) throws Exception {
//        System.out.println(user.toString());
        userService.registerUser(user);

        return ResponseDTO.<Object>builder()
                .data("OTP sent successfully to " + user.getEmail())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
//      import from Excel sheet;
//    @PostMapping("/import/")
//    public ResponseDTO<?> importUsers(@Valid MultipartFile excelSheet) throws Exception {
//
//        userService.importByExcelSheet(excelSheet);
//
//        return ResponseDTO.<Object>builder()
//                .data("Users successfully added ")
//                .message(Constants.SUCCESS_MSG)
//                .status(HttpStatus.OK.value())
//                .build();
//    }
    @PutMapping("/")
    public ResponseDTO<?> updateUser(@Valid @RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteUser(@PathVariable @Valid Long id){
        userService.deleteUserById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/hasRole/{roleId}")
    public ResponseDTO<List<User>> getUserByRole(@PathVariable @Valid Integer roleId){
        return ResponseDTO
                .<List<User>>builder()
                .data(userService.getUserByRole(roleId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getUserByFirmId/{firmId}")
    public ResponseDTO<List<User>> getUserByFirmId(@PathVariable @Valid Integer firmId){
        return ResponseDTO
                .<List<User>>builder()
                .data(userService.getUserByFirmId(firmId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getUserByFirmName/{firmName}")
    public ResponseDTO<List<User>> getUserByFirmName(@PathVariable @Valid String firmName){
        return ResponseDTO
                .<List<User>>builder()
                .data(userService.getUserByFirmName(firmName))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/getUserByRoleName/{roleName}")
    public ResponseDTO<List<User>> getUserByRoleName(@PathVariable @Valid String roleName){
        return ResponseDTO
                .<List<User>>builder()
                .data(userService.getUserByRoleName(roleName))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/activeUsers")
    public int getActiveUsers() {
        return ActiveUserSessionListener.getActiveSessions();
    }

}
