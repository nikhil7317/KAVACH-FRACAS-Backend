package com.railbit.tcasanalysis.controller.mailcontroller;

import com.railbit.tcasanalysis.DTO.ResponseDTO;
import com.railbit.tcasanalysis.entity.Asset;
import com.railbit.tcasanalysis.entity.mail.MailEntity;
import com.railbit.tcasanalysis.service.EmailService;
import com.railbit.tcasanalysis.service.ZohoEmailService;
import com.railbit.tcasanalysis.util.Constants;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tcasapi/mail")
public class MailController {

    private final ZohoEmailService emailService;

    @GetMapping("/")
    public ResponseDTO<?> getAllAssets(@Valid @RequestBody MailEntity mailEntity){

//        emailService.sendEmail(mailEntity.getTo(), mailEntity.getSubject(), mailEntity.getMsg());

        List<String> recipients = List.of("rohitkumar.railbit@gmail.com",
                "rohit.kumar@railbit.in",
                "iamrohit8540@gmail.com",
                "rohitsinghmaxwell@gmail.com");
        emailService.sendBulkEmails(recipients,mailEntity.getSubject(),mailEntity.getMsg());

        return ResponseDTO.<String>builder()
                .data("Mail Sent Successfully")
                .message(Constants.SUCCESS_MSG)
                .status(HttpStatus.OK.value())
                .build();
    }

}
