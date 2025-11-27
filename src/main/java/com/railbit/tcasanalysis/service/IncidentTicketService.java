package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.repository.IncidentTicketRepo;
import com.railbit.tcasanalysis.repository.TcasBreakingInspectionRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IncidentTicketService {

    private static final Logger log = LoggerFactory.getLogger(IncidentTicketService.class);
    @Autowired
    IncidentTicketRepo incidentTicketRepo;

    private final IncidentTicketTrackService incidentTicketTrackService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    TcasBreakingInspectionRepo tcasBreakingInspectionRepo;

    public List<IncidentTicket> getTicketsByStatus(int divisionId,boolean status) {
        return incidentTicketRepo.findByDivisionIdOrderByIdDesc(divisionId);
    }

    public IncidentTicket getIncidentTicketByNo(String ticketNo) {
        return incidentTicketRepo.findByTicketNo(ticketNo);
    }
    
    public IncidentTicket getIncidentTicketById(Long id) {
        Optional<IncidentTicket> data=incidentTicketRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("IncidentTicket not found");
        else {
            IncidentTicket incidentTicket = data.get();
            incidentTicket.setAssignedFirms(incidentTicketRepo.findAssignedFirmsByIncidentTicketId(incidentTicket.getId()));
            return incidentTicket;
        }
    }

    public List<IncidentTicket> getAllIncidentTicket() {
        List<IncidentTicket> incidentTicketList = incidentTicketRepo.findAllByOrderByIdDesc();

        for (IncidentTicket ticket : incidentTicketList) {
            ticket.setIncidentCount(ticket.getInspections().size()); // Replace newValue with the desired value
        }

        return incidentTicketList;
    }
    
    public IncidentTicket postIncidentTicket(IncidentTicket incidentTicket) {
        return incidentTicketRepo.save(incidentTicket);
    }

    public void addIncidentTicket(IncidentTicket incidentTicket) {

        IncidentTicket addedTicket = incidentTicketRepo.save(incidentTicket);
        if (addedTicket.getId() != null) {
            {
                IncidentTicketTrack incidentTicketTrack = new IncidentTicketTrack();
                incidentTicketTrack.setIncidentTicket(addedTicket);
                incidentTicketTrack.setStatus("Incident Ticket Created");
                incidentTicketTrack.setUser(addedTicket.getUser());

                incidentTicketTrackService.postIncidentTicketTrack(incidentTicketTrack);
            }

            if (addedTicket.getAssignedFirms() != null && !addedTicket.getAssignedFirms().isEmpty()) {
                IncidentTicketTrack incidentTicketTrack = new IncidentTicketTrack();
                Set<Firm> firmSet = addedTicket.getAssignedFirms();
                String firmNames = firmSet.stream()
                        .map(Firm::getName) // Assuming `getName()` gets the firm's name
                        .collect(Collectors.joining(", "));
                incidentTicketTrack.setIncidentTicket(addedTicket);
                incidentTicketTrack.setStatus("Ticket Assigned To "+firmNames);
                incidentTicketTrack.setUser(addedTicket.getUser());

                incidentTicketTrackService.postIncidentTicketTrack(incidentTicketTrack);

                for (Firm firm : firmSet) {
                    Role role = roleService.getRoleByName("ROLE_OEM");
                    List<User> userList = userService.getUserByRoleIdAndFirmIdAndDivisionId(role.getId(),firm.getId(),incidentTicket.getDivision().getId());

                    for (User user : userList) {
                        String userName = user.getName() != null ? user.getName() : "";
                        String userEmail = user.getEmail() != null ? user.getEmail() : "";
                        String userContact = user.getContact() != null ? user.getContact() : "";
                        String ticketNo = incidentTicket.getTicketNo() != null ? incidentTicket.getTicketNo() : "";
                        String divisionCode = incidentTicket.getDivision() != null && incidentTicket.getDivision().getCode() != null
                                ? incidentTicket.getDivision().getCode()
                                : "";

                        String mailBody = "Dear " + userName + ",\n\n" +
                                "New Ticket has been Assigned. \n" +
                                "Ticket No : " + ticketNo + "\n" +
                                "Division : " + divisionCode +
                                "\n\n" +
                                "Best Regards,\n\n" +
                                "Kavach Administration" + "\n";
                        String mailSubject = "KAVACH Incident Ticket Assigned";
                        List<String> toList = Collections.singletonList(userEmail);

                        // Sending Email
//                        emailService.mailService(mailSubject, mailBody, toList);

                        // Sending SMS
                        try {
//                            otpService.sendAssignIncidentSms(
//                                            userName,firm.getName(), "", ticketNo, divisionCode, userContact)
//                                    .doOnNext(response -> {
//                                        System.out.println("Response Code: " + response.getResponseCode());
//                                        System.out.println("Response Message: " + response.getResponseMessage());
//                                        System.out.println("Transaction ID: " + response.getTxId());
//                                        System.out.println("SMS Encoding: " + response.getSmsEncoding());
//                                        System.out.println("SMS Length: " + response.getSmsLength());
//                                        System.out.println("Balance Used: " + response.getBalanceUsed());
//                                        System.out.println("Total Mobile Number Submitted: " + response.getTotalMobileNumberSubmitted());
//                                    })
//                                    .block();
                        } catch (Exception e) {
                            log.error("Exception : ", e);
                        }
                    }

                }

            }
            if (addedTicket.getStatus() != null && !addedTicket.getStatus()) {
                IncidentTicketTrack incidentTicketTrack = new IncidentTicketTrack();
                incidentTicketTrack.setIncidentTicket(addedTicket);
                incidentTicketTrack.setStatus("Ticket Closed");
                incidentTicketTrack.setUser(addedTicket.getUser());

                incidentTicketTrackService.postIncidentTicketTrack(incidentTicketTrack);
            }

        }



        incidentTicketRepo.save(incidentTicket);
    }

    public void updateIncidentTicket(IncidentTicket incidentTicket) {

        // Fetch the existing ticket from the database using the id
//        Optional<IncidentTicket> existingTicketOpt = incidentTicketRepo.findById(incidentTicket.getId());
        IncidentTicket existingTicket = incidentTicketRepo.findById(incidentTicket.getId()).orElseThrow(() -> new RuntimeException("Ticket not Found"));
        if (existingTicket != null) {
//            IncidentTicket existingTicket = existingTicketOpt.get();

            if (existingTicket.getAssignedFirms() != null){
                if (existingTicket.getAssignedFirms().isEmpty()) {
                    if (incidentTicket.getAssignedFirms() != null && !incidentTicket.getAssignedFirms().isEmpty()) {
                        IncidentTicketTrack incidentTicketTrack = new IncidentTicketTrack();
                        Set<Firm> firmSet = incidentTicket.getAssignedFirms();
                        String firmNames = firmSet.stream()
                                .map(Firm::getName) // Assuming `getName()` gets the firm's name
                                .collect(Collectors.joining(", "));
                        incidentTicketTrack.setIncidentTicket(incidentTicket);
                        incidentTicketTrack.setStatus("Ticket Assigned To "+firmNames);
                        incidentTicketTrack.setUser(incidentTicket.getUser());

                        incidentTicketTrackService.postIncidentTicketTrack(incidentTicketTrack);

                        for (Firm firm : firmSet) {

                            Role role = roleService.getRoleByName("ROLE_OEM");
                            List<User> userList = userService.getUserByRoleIdAndFirmIdAndDivisionId(role.getId(),firm.getId(),incidentTicket.getDivision().getId());

                            for (User user : userList) {
                                String userName = user.getName() != null ? user.getName() : "";
                                String userEmail = user.getEmail() != null ? user.getEmail() : "";
                                String userContact = user.getContact() != null ? user.getContact() : "";
                                String ticketNo = incidentTicket.getTicketNo() != null ? incidentTicket.getTicketNo() : "";
                                String divisionCode = incidentTicket.getDivision() != null && incidentTicket.getDivision().getCode() != null
                                        ? incidentTicket.getDivision().getCode()
                                        : "";

                                String mailBody = "Dear " + userName + ",\n\n" +
                                        "New Ticket has been Assigned. \n" +
                                        "Ticket No : " + ticketNo + "\n" +
                                        "Division : " + divisionCode +
                                        "\n\n" +
                                        "Best Regards,\n\n" +
                                        "Kavach Administration" + "\n";
                                String mailSubject = "KAVACH Incident Ticket Assigned";
                                List<String> toList = Collections.singletonList(userEmail);

                                // Sending Email
//                                emailService.mailService(mailSubject, mailBody, toList);

                                // Sending SMS
                                try {
//                                    otpService.sendAssignIncidentSms(
//                                                    userName,firm.getName(), "", ticketNo, divisionCode, userContact)
//                                            .doOnNext(response -> {
//                                                System.out.println("Response Code: " + response.getResponseCode());
//                                                System.out.println("Response Message: " + response.getResponseMessage());
//                                                System.out.println("Transaction ID: " + response.getTxId());
//                                                System.out.println("SMS Encoding: " + response.getSmsEncoding());
//                                                System.out.println("SMS Length: " + response.getSmsLength());
//                                                System.out.println("Balance Used: " + response.getBalanceUsed());
//                                                System.out.println("Total Mobile Number Submitted: " + response.getTotalMobileNumberSubmitted());
//                                            })
//                                            .block();
                                } catch (Exception e) {
                                    log.error("Exception : ", e);
                                }
                            }

                        }
                    }
                }
            }

            if (existingTicket.getStatus() != null) {
                if (existingTicket.getStatus()) {
                    if (incidentTicket.getStatus() != null && !incidentTicket.getStatus()) {
                        IncidentTicketTrack incidentTicketTrack = new IncidentTicketTrack();
                        incidentTicketTrack.setIncidentTicket(incidentTicket);
                        incidentTicketTrack.setStatus("Ticket Closed");
                        incidentTicketTrack.setUser(incidentTicket.getUser());

                        incidentTicketTrackService.postIncidentTicketTrack(incidentTicketTrack);
                    }
                }
            }

            // Clear previous assigned firms (optional based on your use case)
            if (existingTicket.getAssignedFirms() != null) {
                existingTicket.getAssignedFirms().clear();  // Clear old firms
            }

            // Add new assigned firms
            existingTicket.setAssignedFirms(incidentTicket.getAssignedFirms());

            // Create a ModelMapper instance (You can inject this via Spring if you prefer)
            ModelMapper modelMapper = new ModelMapper();

            // This will map only the non-null fields of incidentTicket to existingTicket
            modelMapper.map(incidentTicket, existingTicket);

            // Save the updated ticket back to the database
            incidentTicketRepo.save(existingTicket);
        } else {
            // Handle case where the ticket doesn't exist (optional)
//            throw new TicketNotFoundException("Ticket not found with id " + incidentTicket.getId());
            log.error("Ticket not found with id {}", incidentTicket.getId());
        }
    }
    
    public void deleteIncidentTicketById(Long id) {
        incidentTicketRepo.deleteById(id);
    }
    
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }

    public Set<Firm> getAssignedFirmsByTicketId(Long incidentTicketId) {
        return incidentTicketRepo.findAssignedFirmsByIncidentTicketId(incidentTicketId);
    }

    public List<TcasBreakingInspection> getInspectionsByIncidentTicketId(Long incidentTicketId) {
        return tcasBreakingInspectionRepo.findByIncidentTicket_Id(incidentTicketId);
    }

    public List<TcasBreakingInspection> getInspectionsByIncidentTicket(IncidentTicket incidentTicket) {
        return tcasBreakingInspectionRepo.findByIncidentTicket(incidentTicket);
    }

}
