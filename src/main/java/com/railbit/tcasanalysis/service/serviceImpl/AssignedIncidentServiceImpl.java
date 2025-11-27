package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.repository.AssignedIncidentRepo;
import com.railbit.tcasanalysis.repository.RemarkTypeRepo;
import com.railbit.tcasanalysis.service.*;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AssignedIncidentServiceImpl implements AssignedIncidentService {
    private static final Logger log = LoggerFactory.getLogger(AssignedIncidentServiceImpl.class);
    private final TcasBreakingInspectionStatusService tcasBreakingInspectionStatusService;
    private final TcasBreakingInspectionService tcasBreakingInspectionService;
    private final AssignedIncidentRepo assignedIncidentRepo;
    private final RemarkTypeRepo remarkTypeRepo;
    private final UserService userService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final NotificationService notificationService;
    @Override
    public AssignedIncident getAssignedIncidentById(Long id) {
        Optional<AssignedIncident> data=assignedIncidentRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("AssignedIncident not found");
        return data.get();
    }

    @Override
    public List<AssignedIncident> getAllAssignedIncident() {
        return assignedIncidentRepo.findAll();
    }

    @Override
    public AssignedIncident postAssignedIncident(AssignedIncident assignedIncident) {

        String incidentTag = assignedIncident.getIncidentTag();
        assignedIncident.setTcasBreakingInspection(tcasBreakingInspectionService.getTcasBreakingInspectionByIncidentTag(incidentTag));

        User assignedFromUser = userService.getUserByUserId(assignedIncident.getAssignedFromUser().getId());
        assignedIncident.setAssignedFromUser(assignedFromUser);
        User assignedToUser;

        AssignedIncident prevAssignedIncident = assignedIncidentRepo.findFirstByTcasBreakingInspectionAndAssignedToUserOrderByIdDesc(assignedIncident.getTcasBreakingInspection(), assignedFromUser);

        if (assignedFromUser.getRole().getName().equalsIgnoreCase("role_oem")) {
            assignedToUser = prevAssignedIncident.getAssignedFromUser();
        } else {
            assignedToUser = userService.getUserByUserId(assignedIncident.getAssignedToUser().getId());
        }

        assignedIncident.setAssignedToUser(assignedToUser);

//        log.info("From User {}",assignedIncident.getAssignedFromUser());
//        log.info("To User {}",assignedIncident.getAssignedToUser());

//        AssignedIncident lastAssignedIncident = assignedIncidentRepo.findFirstByTcasBreakingInspectionAndAssignedToUserOrderByIdDesc(assignedIncident.getTcasBreakingInspection(), assignedToUser);
        AssignedIncident savedAssignedIncident = assignedIncidentRepo.save(assignedIncident);

        if (prevAssignedIncident != null) {
            prevAssignedIncident.setStatus(true);
            assignedIncidentRepo.save(prevAssignedIncident);
        }

        RemarkType remarkType = remarkTypeRepo.findById(assignedIncident.getRemarkType()).orElse(null);

        if (savedAssignedIncident.getId() != null) {
            TcasBreakingInspectionStatus tcasBreakingInspectionStatus = new TcasBreakingInspectionStatus();
            tcasBreakingInspectionStatus.setTcasBreakingInspection(assignedIncident.getTcasBreakingInspection());
            tcasBreakingInspectionStatus.setRemarks(assignedIncident.getRemark());
            tcasBreakingInspectionStatus.setRemarkType(remarkType);
            String status = "Incident Assigned";
            if (assignedFromUser.getRole().getName().contains("KAVACH_VENDOR")){
                status = ((remarkType != null ? remarkType.getType() : "Remark") + " Added");
            }

            tcasBreakingInspectionStatus.setStatus(status);
            tcasBreakingInspectionStatus.setUser(assignedFromUser);
            tcasBreakingInspectionStatus.setAssignedIncident(savedAssignedIncident);
            tcasBreakingInspectionStatus.setProjectType(new ProjectType(1,""));

            tcasBreakingInspectionStatusService.postTcasBreakingInspectionStatus(tcasBreakingInspectionStatus);

            String title = status + " for Tag " + incidentTag;
            String msg = "An incident has been assigned for Tag " + incidentTag;

            List<User> userListToNotify = Collections.singletonList(userService.getUserByUserId(assignedToUser.getId()));
            notificationService.sendNotificationToUsersAfterInspectionStatusAdded(userListToNotify, assignedIncident.getTcasBreakingInspection(), title, msg);

            for (User toUser : userListToNotify) {
                String mailBody = "Dear "+ toUser.getName() +",\n\n" +
                        msg +
                        "\n\n" +
                        "Best Regards,\n\n" +
                        "Kavach Administration" + "\n";
                String mailSubject = status;
                List<String> toList = Collections.singletonList(toUser.getEmail());
                emailService.mailService(mailSubject,mailBody,toList);

                try {
                    otpService.sendAssignIncidentSms(toUser.getName(), assignedIncident.getTcasBreakingInspection().getFaultyStation().getFirm().getName() , assignedIncident.getTcasBreakingInspection().getLoco().getLocoNo(), incidentTag, assignedIncident.getTcasBreakingInspection().getFaultyStation().getName(), toUser.getContact())
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

        }

        return savedAssignedIncident;
    }

    @Override
    public void updateAssignedIncident(AssignedIncident assignedIncident) {
        assignedIncidentRepo.save(assignedIncident);
    }

    @Override
    public void deleteAssignedIncidentById(Long id) {
        assignedIncidentRepo.deleteById(id);
    }
}
