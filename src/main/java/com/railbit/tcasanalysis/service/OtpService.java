package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.Designation;
import com.railbit.tcasanalysis.entity.SmsResponse;
import com.railbit.tcasanalysis.entity.TemporaryUser;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface OtpService {
    public String generateOTP();
    public void storeTemporaryUser(TemporaryUser tempUser);
    public Optional<TemporaryUser> verifyOTP(String email, String otp);
    public void removeTemporaryUser(String email);
    public Mono<SmsResponse> sendAddIncidentSms(String user, String loco, String tag, String station, String mobileNumber);
    public Mono<SmsResponse> sendAssignIncidentSms(String user,String oem, String loco, String tag,String station, String mobileNumber);
    public Mono<SmsResponse> sendCloseIncidentSms(String user,String oem,String loco, String tag,String station, String mobileNumber);
}
