package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.repository.IncidentTicketRepo;
import com.railbit.tcasanalysis.repository.IncidentTicketTrackRepo;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IncidentTicketTrackService {

    private static final Logger log = LogManager.getLogger(IncidentTicketTrackService.class);
    private final IncidentTicketTrackRepo incidentTicketTrackRepo;
    private final IncidentTicketRepo incidentTicketRepo;
    private final RoleService roleService;
    private final UserService userService;
    private final EmailService emailService;
    private final OtpService otpService;

    public List<IncidentTicketTrack> getAllByIncidentTicketIdOrderByIdAsc(Long id) {
        return incidentTicketTrackRepo.findByIncidentTicketIdOrderByIdAsc(id);
    }

    public List<IncidentTicketTrack> getAllIncidentTicketTracks() {
        return incidentTicketTrackRepo.findAll();
    }

    public IncidentTicketTrack getIncidentTicketTrackById(Long id) {
        Optional<IncidentTicketTrack> data=incidentTicketTrackRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("IncidentTicketTrack not found");
        return data.get();
    }

    public Long postIncidentTicketTrack(IncidentTicketTrack incidentTicketTrack) {
        IncidentTicketTrack newIncidentTicketTrack = incidentTicketTrackRepo.save(incidentTicketTrack);
        return newIncidentTicketTrack.getId();
    }

    public void updateIncidentTicketTrack(IncidentTicketTrack incidentTicketTrack) {
        incidentTicketTrackRepo.save(incidentTicketTrack);
    }

    public Long addIncidentTicketRemark(IncidentTicketTrack incidentTicketTrack) {
        User sendingUser = incidentTicketTrack.getUser();
        IncidentTicketTrack newIncidentTicketTrack = incidentTicketTrackRepo.save(incidentTicketTrack);

        if (newIncidentTicketTrack.getId() > 0) {

            {
                IncidentTicketTrack savedIncidentTicketTrack = getIncidentTicketTrackById(newIncidentTicketTrack.getId());
                IncidentTicket incidentTicket = incidentTicketRepo.findById(savedIncidentTicketTrack.getIncidentTicket().getId()).orElse(null);
                log.info("Incident Ticket : {}", incidentTicket);
                Role role = roleService.getRoleByName("ROLE_DIVISION");
                assert incidentTicket != null;
                List<User> userList = userService.getUserByRoleIdAndDivisionId(role.getId(),incidentTicket.getDivision().getId());

                for (User user : userList) {
                    String userName = user.getName() != null ? user.getName() : "";
                    String userEmail = user.getEmail() != null ? user.getEmail() : "";
                    String userContact = user.getContact() != null ? user.getContact() : "";
                    String ticketNo = incidentTicket.getTicketNo() != null ? incidentTicket.getTicketNo() : "";
                    String divisionCode = incidentTicket.getDivision() != null && incidentTicket.getDivision().getCode() != null
                            ? incidentTicket.getDivision().getCode()
                            : "";

                    String mailBody = "Dear " + userName + ",\n\n" +
                            "Remarks has been added by " + sendingUser.getName() +
                            "Ticket No : " + ticketNo + "\n" +
                            "Division : " + divisionCode + "\n" +
                            "Remarks : " + newIncidentTicketTrack.getRemarks() + "\n" +
                            "\n\n" +
                            "Best Regards,\n\n" +
                            "Kavach Administration" + "\n";
                    String mailSubject = savedIncidentTicketTrack.getStatus();
                    List<String> toList = Collections.singletonList(userEmail);

                    // Sending Email
                    emailService.mailService(mailSubject, mailBody, toList);

                }

            }

        }

        return newIncidentTicketTrack.getId();
    }

    public void deleteIncidentTicketTrackById(Long id) {
        incidentTicketTrackRepo.deleteById(id);
    }

    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
    
}
