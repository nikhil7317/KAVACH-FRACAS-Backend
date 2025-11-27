package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.repository.AssignedIncidentRepo;
import com.railbit.tcasanalysis.repository.TcasBreakingInspectionRepo;
import com.railbit.tcasanalysis.repository.TcasBreakingInspectionStatusRepo;
import com.railbit.tcasanalysis.service.*;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TcasBreakingInspectionStatusServiceImpl implements TcasBreakingInspectionStatusService {
    private final TcasBreakingInspectionStatusRepo tcasBreakingInspectionStatusRepo;
    private final FileService fileService;
    private final String INSPECTION_PATH="inspectionImages";
    @Value("${resource.directory}")
    private String RESOURCE_DIRECTORY;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final TcasBreakingInspectionRepo tcasBreakingInspectionRepo;
    private final UserService userService;
    private final FCMNotificationService fcmNotificationService;
    private final FCMTokenService fcmTokenService;
    private final AssignedIncidentRepo assignedIncidentRepo;
    private final Logger logger = LoggerFactory.getLogger(TcasBreakingInspectionStatusServiceImpl.class);

    @Override
    public TcasBreakingInspectionStatus getTcasBreakingInspectionStatusById(Long id) {
        Optional<TcasBreakingInspectionStatus> data=tcasBreakingInspectionStatusRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("TcasBreakingInspectionStatus not found");
        return setImagesPathsToTrack(data.get());
    }

    @Override
    public List<TcasBreakingInspectionStatus> getAllTcasBreakingInspectionStatusByInspection(Long id) {
        return setImagesPathsToTrackList(tcasBreakingInspectionStatusRepo.findByTcasBreakingInspectionIdOrderByIdAsc(id));
    }

    @Override
    public List<TcasBreakingInspectionStatus> getAllTcasBreakingInspectionStatus() {
        return setImagesPathsToTrackList(tcasBreakingInspectionStatusRepo.findAll());
    }

    @Override
    public TcasBreakingInspectionStatus postTcasBreakingInspectionStatus(TcasBreakingInspectionStatus tcasBreakingInspectionStatus) {

        TcasBreakingInspectionStatus newTcasBreakingInspectionStatus = tcasBreakingInspectionStatusRepo.save(tcasBreakingInspectionStatus);
        TcasBreakingInspection newTcasBreakingInspection = tcasBreakingInspectionRepo.findById(newTcasBreakingInspectionStatus.getTcasBreakingInspection().getId()).orElse(null);
//        logger.info("Inspection Status : {}",newTcasBreakingInspection);

        try {
//            logger.info("Division : {}",newTcasBreakingInspection.getTcasStationFrom().getDivision().getName());
//            System.out.println("Division : " + newTcasBreakingInspection.getTcasStationFrom().getDivision().getName());
            List<User> userListToNotify = userService.getAllUsersRelatedToInspection(newTcasBreakingInspection);
            assert newTcasBreakingInspection != null;
            String title = newTcasBreakingInspectionStatus.getStatus() + " for Tag " + newTcasBreakingInspection.getIncidentTag();
            String msg = newTcasBreakingInspectionStatus.getStatus() + " for the incident of loco " + newTcasBreakingInspection.getLoco().getLocoNo();

            if (!newTcasBreakingInspectionStatus.getStatus().equalsIgnoreCase("Issue Closed")){
                msg += " by " + newTcasBreakingInspection.getUser().getName() + " ( " +newTcasBreakingInspection.getUser().getDesignation().getName() + " )";
            }

            notificationService.sendNotificationToUsersAfterInspectionStatusAdded(userListToNotify, newTcasBreakingInspection, title, msg);

            for (User toUser : userListToNotify) {
                String mailBody = "Dear "+ toUser.getName() +",\n\n" +
                        msg +
                        "\n\n" +
                        "Best Regards,\n\n" +
                        "Kavach Administration" + "\n";
                String mailSubject = newTcasBreakingInspectionStatus.getStatus();
                List<String> toList = Collections.singletonList(toUser.getEmail());
                emailService.mailService(mailSubject,mailBody,toList);
            }
        } catch (Exception e) {
            logger.error("Exception ", e);
        }

        return newTcasBreakingInspectionStatus;
    }

    @Override
    public TcasBreakingInspectionStatus addIncidentStatus(TcasBreakingInspectionStatus tcasBreakingInspectionStatus) {
        return tcasBreakingInspectionStatusRepo.save(tcasBreakingInspectionStatus);
    }

    @Override
    public TcasBreakingInspectionStatus addTcasBreakingInspectionStatus(TcasBreakingInspectionStatus inspectionTrack,
                                                                        List<MultipartFile> fileList) throws Exception {
        inspectionTrack = tcasBreakingInspectionStatusRepo.save(inspectionTrack);
        if (fileList != null && !fileList.isEmpty()) {
            fileList.forEach(file -> System.out.println(file.getOriginalFilename()));
            fileService.uploadFile(fileList,inspectionTrack.getTcasBreakingInspection().getId(), inspectionTrack.getId(), INSPECTION_PATH);

            inspectionTrack.setImages(fileList.stream()
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.joining(",")));
        }

        TcasBreakingInspectionStatus newInspectionTrack = tcasBreakingInspectionStatusRepo.save(inspectionTrack);

//        TcasBreakingInspection newTcasBreakingInspection = inspectionTrack.getTcasBreakingInspection();
        TcasBreakingInspection newTcasBreakingInspection = tcasBreakingInspectionRepo.findById(inspectionTrack.getTcasBreakingInspection().getId()).orElse(null);

        assert newTcasBreakingInspection != null;
        User user = userService.getUserByUserId(newTcasBreakingInspection.getUser().getId());
        List<User> userListToNotify = userService.getUserByRoleName("ROLE_ADMIN");
        userListToNotify.addAll(userService.getUserByRoleName("ROLE_DIVISION"));
        userListToNotify.addAll(userService.getUserByRoleName("ROLE_RDSO"));
        userListToNotify.addAll(userService.getUserByRoleName("ROLE_OFFICER"));
        userListToNotify.addAll(userService.getUserByRoleName("ROLE_HQ"));
        userListToNotify.addAll(userService.getUserByRoleName("ROLE_CoE/IRISET"));
        logger.info("Users : {}", userListToNotify);

        String title = "New Incident Added at " + (newTcasBreakingInspection.getFaultyStation() == null ? "" : newTcasBreakingInspection.getFaultyStation().getCode());
        String msg = "New incident added at " + (newTcasBreakingInspection.getFaultyStation() == null ? "" : newTcasBreakingInspection.getFaultyStation().getCode()) +" for loco " + newTcasBreakingInspection.getLoco().getLocoNo() + " by " + user.getName() + " ( " + user.getDesignation().getName() + " )";

        notificationService.sendNotificationToUsersAfterInspectionStatusAdded(userListToNotify,newTcasBreakingInspection,title,msg);

        for (User toUser : userListToNotify) {

            String mailBody = "Dear "+ toUser.getName() +",\n\n" +
                    "This mail is to inform you of an incident that has been reported in the kavach system. Below are the details of the incident:\n\n" +
                    "Incident Details:\n\n" +
                    "Incident Tag: " + newTcasBreakingInspection.getIncidentTag() + "\n" +
                    "Loco ID/Number: " + (newTcasBreakingInspection.getLoco() == null ? "" : newTcasBreakingInspection.getLoco().getLocoNo()) + "\n" +
                    "Division: " + (newTcasBreakingInspection.getFaultyStation() == null ? "" : newTcasBreakingInspection.getFaultyStation().getDivision().getName()) + "\n" +
                    "Station: " + (newTcasBreakingInspection.getFaultyStation() == null ? "" : newTcasBreakingInspection.getFaultyStation().getName()) + "\n" +
                    "Reported By: " + user.getName() + "\n" +
                    "Designation: " + (user.getDesignation() == null ? "" : user.getDesignation().getName()) + "\n" +
                    "Issue Category: " +
                    (newTcasBreakingInspection.getIssueCategory() != null ? newTcasBreakingInspection.getIssueCategory().getName() : "") + "\n" +
                    "Possible Issue: " +
                    (newTcasBreakingInspection.getPossibleIssue() != null ? newTcasBreakingInspection.getPossibleIssue().getName() : "") + "\n" +
                    "Root Cause: " +
                    (newTcasBreakingInspection.getPossibleRootCause() != null ? newTcasBreakingInspection.getPossibleRootCause().getName() : "") + "\n\n" +

                    "Remarks:\n\n" +
                    "Date & Time of Incident: " + HelpingHand.dateFormatter.format(newTcasBreakingInspection.getCreatedDateTime()) + " " + HelpingHand.timeFormatter.format(newTcasBreakingInspection.getCreatedDateTime()) + "\n" +
                    "We kindly request your immediate attention to this matter to ensure that the necessary preventive and corrective actions are taken promptly.\n\n" +
                    "Thank you for your prompt action on this matter.\n\n" +
                    "Best Regards,\n\n" +
                    "Kavach Administration" + "\n";
            String mailSubject = "New Incident Reported";
            List<String> toList = Collections.singletonList(toUser.getEmail());
            emailService.mailService(mailSubject,mailBody,toList);

        }
        String contacts = userListToNotify.stream()
                .map(User::getContact)                  // Extract contact
                .filter(contact -> contact != null && !contact.isEmpty()) // Filter out null and empty
                .collect(Collectors.joining(","));      // Join with commas

        System.out.println(contacts);

        try {
            otpService.sendAddIncidentSms("User" , newTcasBreakingInspection.getLoco().getLocoNo(),  newTcasBreakingInspection.getIncidentTag(),newTcasBreakingInspection.getFaultyStation().getCode(), "")
                    .doOnNext(response -> {
                        System.out.println("Mobile: " + contacts);
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
            logger.error("Exception : ", e);
        }

        return newInspectionTrack;
    }
    @Override
    public void updateTcasBreakingInspectionStatus(TcasBreakingInspectionStatus tcasBreakingInspectionStatus) {
        tcasBreakingInspectionStatusRepo.save(tcasBreakingInspectionStatus);
    }

    @Override
    public void deleteTcasBreakingInspectionStatusById(Long id) {
        tcasBreakingInspectionStatusRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }

    @Override
    public List<String> findRemarkTypesByRoleNameAndInspectionId(String roleName, Long inspectionId) {
        return tcasBreakingInspectionStatusRepo.findRemarkTypesByRoleNameAndInspectionId(roleName,inspectionId);
    }

    private List<String> setImages(String images,Integer inspectionId, Integer inspectionTrackId){
        List<String> imageList = new ArrayList<>();
        if(images!=null)
            imageList = Arrays.stream(images.split(","))
                    .toList().stream().map(image->"RESOURCE_DIRECTORY"+"INSPECTION_PATH"
                            +"/"+inspectionId
                            +"/"+inspectionTrackId
                            +"/"+image).toList();
        return imageList;
    }

    private TcasBreakingInspectionStatus setImagesPathsToTrack(TcasBreakingInspectionStatus inspectionTrack) {
        List<String> imageList = new ArrayList<>();
        if (inspectionTrack.getImages() != null) {
            imageList = Arrays.stream(inspectionTrack.getImages().split(","))
                    .toList().stream().map(image -> RESOURCE_DIRECTORY + INSPECTION_PATH
                            + "/" + inspectionTrack.getTcasBreakingInspection().getId()
                            + "/" + inspectionTrack.getId()
                            + "/" + image).toList();
        }

        inspectionTrack.setImageList(imageList);

        return inspectionTrack;
    }

    private List<TcasBreakingInspectionStatus> setImagesPathsToTrackList(List<TcasBreakingInspectionStatus> trackList) {
        List<TcasBreakingInspectionStatus> newTrackList = new ArrayList<>();
        for (TcasBreakingInspectionStatus inspectionTrack : trackList) {
            inspectionTrack.setTcasBreakingInspection(findAndSetLastAssignedUserStatusInIncidentListInIncident(inspectionTrack.getTcasBreakingInspection()));
            newTrackList.add(setImagesPathsToTrack(inspectionTrack));
        }
        return newTrackList;
    }

    @Override
    public TcasBreakingInspection findAndSetLastAssignedUserStatusInIncidentListInIncident(TcasBreakingInspection inspection) {
        TcasBreakingInspection result;
        {
            {
//                logger.info("Inspection {}",inspection);
                AssignedIncident lastAssignedIncident = assignedIncidentRepo.findFirstByTcasBreakingInspectionOrderByIdDesc(inspection);

                if (lastAssignedIncident != null) {
                    inspection.setAssignStatus(lastAssignedIncident.getStatus());
                    inspection.setAssignedRole(lastAssignedIncident.getAssignedToUser().getRole().getName());
                    inspection.setAssignedToUserId(lastAssignedIncident.getAssignedToUser().getId());

//                    logger.info("AssignedUserID {}",lastAssignedIncident.getAssignedToUser().getId());
                }
            }

            {
                List<String> remarkTypes = findRemarkTypesByRoleNameAndInspectionId("kavach_vendor",inspection.getId());
                if (remarkTypes.isEmpty()) {
                    inspection.setRemarkType("");
                } else {
                    inspection.setRemarkType(remarkTypes.get(0));
                }
            }

            result = inspection;

        }

        return result;
    }


}
