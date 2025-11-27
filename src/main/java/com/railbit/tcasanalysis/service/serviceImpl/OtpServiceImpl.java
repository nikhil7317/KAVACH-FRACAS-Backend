package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.SmsResponse;
import com.railbit.tcasanalysis.entity.TemporaryUser;
import com.railbit.tcasanalysis.repository.DesignationRepo;
import com.railbit.tcasanalysis.repository.TempUserRepo;
import com.railbit.tcasanalysis.service.DesignationService;
import com.railbit.tcasanalysis.service.EmailService;
import com.railbit.tcasanalysis.service.OtpService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final EmailService emailService;
    private final TempUserRepo tempUserRepo;
    private WebClient.Builder webClientBuilder;

    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    @Override
    public String generateOTP() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // Generate 6-digit OTP
    }

    @Override
    public void storeTemporaryUser(TemporaryUser tempUser) {

        String otp = "";
        Optional<TemporaryUser> existingTempUser = tempUserRepo.findByContact(tempUser.getContact());
        if (existingTempUser.isEmpty()) {
            existingTempUser = tempUserRepo.findByEmail(tempUser.getEmail());
        }
        if (existingTempUser.isPresent()) {
            otp = existingTempUser.get().getOtp();
            tempUser.setId(existingTempUser.get().getId());
        }

        if (StringUtils.isEmpty(otp)){
            otp = generateOTP();
        }

        int min = 5;
        tempUser.setOtp(otp);
        tempUser.setCreatedAt(LocalDateTime.now());
        tempUser.setExpiresAt(LocalDateTime.now().plusMinutes(min)); // Set expiration time
        tempUserRepo.save(tempUser);

        emailService.sendOTP(tempUser.getEmail(),tempUser.getOtp());

        try {
            sendSms(otp, min, tempUser.getContact())
                    .doOnNext(response -> {
                        System.out.println("Response Code: " + response.getResponseCode());
                        System.out.println("Response Message: " + response.getResponseMessage());
                        System.out.println("Transaction ID: " + response.getTxId());
                        System.out.println("SMS Encoding: " + response.getSmsEncoding());
                        System.out.println("SMS Length: " + response.getSmsLength());
                        System.out.println("Balance Used: " + response.getBalanceUsed());
                        System.out.println("Total Mobile Number Submitted: " + response.getTotalMobileNumberSubmitted());
                    })
                    .block();
        } catch (Exception e){
            log.error("Exception : ", e);
        }

    }

    @Override
    public Optional<TemporaryUser> verifyOTP(String contact, String otp) {
        Optional<TemporaryUser> tempUserOpt = tempUserRepo.findByContact(contact);
        if (tempUserOpt.isEmpty()) {
            tempUserOpt = tempUserRepo.findByEmail(contact);
        }

        if (tempUserOpt.isPresent()) {
            TemporaryUser tempUser = tempUserOpt.get();
            if (tempUser.getOtp().equals(otp) && tempUser.getExpiresAt().isAfter(LocalDateTime.now())) {
                return Optional.of(tempUser);
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeTemporaryUser(String email) {
        tempUserRepo.deleteByEmail(email);
    }

    public Mono<SmsResponse> sendSms(String otp,int min, String mobileNumber) {
        String route= "Transactional";
        String senderId= "RAILBT";
        String message="Hello Your Login OTP is "+otp+". OTP is Valid For "+min+" Minutes Only. Thank You. Railbit";
        String userId= "railbit";
        String password="railbit@123";
        String teid="1307161865776428896";

        WebClient webClient = webClientBuilder.baseUrl("https://bsms.gensparc.com").build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/sendsms")
                        .queryParam("route", route)
                        .queryParam("senderid", senderId)
                        .queryParam("message", message)
                        .queryParam("mobilenumber", mobileNumber)
                        .queryParam("userid", userId)
                        .queryParam("password", password)
                        .queryParam("teid", teid)
                        .build())
                .retrieve()
                .bodyToMono(SmsResponse.class);
    }

    @Override
    public Mono<SmsResponse> sendAddIncidentSms(String user,String loco, String tag, String station, String mobileNumber) {
        String route= "Transactional";
        String senderId= "RAILBT";
        String message="Dear "+ user +", a new incident has been added for loco "+ loco +" with tag "+ tag +" and station "+ station +". Railbit";
        String userId= "railbit";
        String password="railbit@123";
        String teid="1307172940172170705";

        WebClient webClient = webClientBuilder.baseUrl("https://bsms.gensparc.com").build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/sendsms")
                        .queryParam("route", route)
                        .queryParam("senderid", senderId)
                        .queryParam("message", message)
                        .queryParam("mobilenumber", mobileNumber)
                        .queryParam("userid", userId)
                        .queryParam("password", password)
                        .queryParam("teid", teid)
                        .build())
                .retrieve()
                .bodyToMono(SmsResponse.class);
    }

    @Override
    public Mono<SmsResponse> sendAssignIncidentSms(String user,String oem, String loco, String tag,String station, String mobileNumber) {
        String route= "Transactional";
        String senderId= "RAILBT";
        String message="Dear "+ user +", an incident has been assigned to OEM "+ oem +" for loco "+ loco +" with tag "+ tag +" and station "+ station +". Railbit";
        String userId= "railbit";
        String password="railbit@123";
        String teid="1307172940132889784";

        WebClient webClient = webClientBuilder.baseUrl("https://bsms.gensparc.com").build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/sendsms")
                        .queryParam("route", route)
                        .queryParam("senderid", senderId)
                        .queryParam("message", message)
                        .queryParam("mobilenumber", mobileNumber)
                        .queryParam("userid", userId)
                        .queryParam("password", password)
                        .queryParam("teid", teid)
                        .build())
                .retrieve()
                .bodyToMono(SmsResponse.class);
    }

    @Override
    public Mono<SmsResponse> sendCloseIncidentSms(String user,String oem,String loco, String tag,String station, String mobileNumber) {
        String route= "Transactional";
        String senderId= "RAILBT";
        String message="Dear "+ user +", an incident has been assigned to OEM "+ oem +" for loco "+ loco +" with tag "+ tag +" and station "+ station +". Railbit";
        String userId= "railbit";
        String password="railbit@123";
        String teid="1307172940187634541";

        WebClient webClient = webClientBuilder.baseUrl("https://bsms.gensparc.com").build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/sendsms")
                        .queryParam("route", route)
                        .queryParam("senderid", senderId)
                        .queryParam("message", message)
                        .queryParam("mobilenumber", mobileNumber)
                        .queryParam("userid", userId)
                        .queryParam("password", password)
                        .queryParam("teid", teid)
                        .build())
                .retrieve()
                .bodyToMono(SmsResponse.class);
    }
}
