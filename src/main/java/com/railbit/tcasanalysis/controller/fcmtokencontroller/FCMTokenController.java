package com.railbit.tcasanalysis.controller.fcmtokencontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.FCMToken;
import com.railbit.tcasanalysis.service.FCMTokenService;
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
@RequestMapping("/tcasapi/fCMToken")
public class FCMTokenController {
    private final FCMTokenService fCMTokenService;
    @GetMapping("/")
    public ResponseDTO<List<FCMToken>> getAllFCMTokens(){
        return ResponseDTO.<List<FCMToken>>builder()
                .data(fCMTokenService.getAllFCMToken())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDTO<?> getFCMTokenById(@PathVariable @Valid Long id){
        return ResponseDTO.<FCMToken>builder()
                .data(fCMTokenService.getFCMTokenById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addFCMToken(@Valid @RequestBody FCMToken fCMToken) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(fCMTokenService.postFCMToken(fCMToken))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateFCMToken(@Valid @RequestBody FCMToken fCMToken) {
        fCMTokenService.updateFCMToken(fCMToken);
        return ResponseDTO.<Object>builder()
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteFCMToken(@PathVariable @Valid Long id){
        fCMTokenService.deleteFCMTokenById(id);
        return ResponseDTO.<Object>builder()
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/removeFCMToken")
    public ResponseDTO<?> removeToken(@RequestBody FCMToken fcmToken) {
        boolean success = fCMTokenService.removeToken(fcmToken.getUser().getId(), fcmToken.getToken());
        String msg = "";
        if (success) {
            msg = "Token removed successfully";

        } else {
            msg = "Failed to remove token";
        }
        return ResponseDTO.<Object>builder()
                .data(msg)
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
