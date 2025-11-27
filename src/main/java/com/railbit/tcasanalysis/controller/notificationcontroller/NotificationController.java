package com.railbit.tcasanalysis.controller.notificationcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Notification;
import com.railbit.tcasanalysis.service.NotificationService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/notification")
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping("/")
    public ResponseDTO<List<Notification>> getAllNotifications(){
        return ResponseDTO.<List<Notification>>builder()
                .data(notificationService.getAllNotification())
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/getNotificationByUserId/{userId}")
    public ResponseDTO<List<Notification>> getNotificationByUserId(@PathVariable @Valid Integer userId){
        return ResponseDTO.<List<Notification>>builder()
                .data(notificationService.getByUserId(userId))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseDTO<?> getNotificationById(@PathVariable @Valid Integer id){
        return ResponseDTO.<Notification>builder()
                .data(notificationService.getNotificationById(id))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PostMapping("/")
    public ResponseDTO<?>addNotification(@Valid @RequestBody Notification notification) {
        System.out.println("Running");
        return ResponseDTO.<Object>builder()
                .data(notificationService.postNotification(notification))
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @PutMapping("/")
    public ResponseDTO<?> updateNotification(@Valid @RequestBody Notification notification) {
        notificationService.updateNotification(notification);
        return ResponseDTO.<Object>builder()
                .data("Updated Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/{id}")
    public ResponseDTO<?> deleteNotification(@PathVariable @Valid Integer id){
        notificationService.deleteNotificationById(id);
        return ResponseDTO.<Object>builder()
                .data("Deleted Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }
}
