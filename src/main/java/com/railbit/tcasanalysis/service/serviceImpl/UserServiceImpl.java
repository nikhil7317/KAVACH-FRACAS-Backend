package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.DTO.UserDTO;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.repository.RoleRepo;
import com.railbit.tcasanalysis.repository.UserRepo;
import com.railbit.tcasanalysis.service.DesignationService;
import com.railbit.tcasanalysis.service.EmailService;
import com.railbit.tcasanalysis.service.OtpService;
import com.railbit.tcasanalysis.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final ModelMapper mapper;
    private final RoleRepo roleRepo;
    private final EmailService emailService;
    private final OtpService otpService;
    private final DesignationService designationService;

    @Autowired
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public UserDTO getUserById(Long id) {
        Optional<User> data=userRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("User not found");
//            data.ifPresent(emailService::sendUsernameAndPassword);
        return mapper.map(data.get(),UserDTO.class);
    }

    @Override
    public User getUserByUserId(Long id) {
        Optional<User> data=userRepo.findById(id);
        return data.orElse(null);
    }

    @Override
    public List<User> getAllUser() {
        return userRepo.findAll();
    }
    @Override
    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }
    @Override
    public void postUserList(List<User> userList){
            userRepo.saveAll(userList);
    }
    @Override
    public void updateUser(UserDTO userDTO){
        Optional<User> user = userRepo.findById(userDTO.getId());
        if(user.isPresent()) {
            System.out.println("isPresent");
            User existingUser = user.get();
            String existingPassword = existingUser.getPassword();
            mapper.map(userDTO,existingUser);
            userRepo.save(user.get());
        }
        else throw new UsernameNotFoundException("user not found to update");
    }
    @Override
    public void postUser(UserDTO user) throws Exception {
        User data=userRepo.findByEmail(user.getEmail());
        if(data!=null)
            throw new UsernameNotFoundException("Email/UserId already exist");
        data=userRepo.findByContact(user.getContact());
        if(data!=null)
            throw new UsernameNotFoundException("Phone No already exist");

        if (!StringUtils.isEmpty(user.getFullDesignation())) {
            Designation designation = designationService.getDesignationByName(user.getFullDesignation());
            if (designation == null){
                designation = new Designation(null,user.getFullDesignation(), user.getFullDesignation());
            }
            user.setDesignation(designationService.postDesignation(designation));
        }

        User newUser = new User();
        mapper.map(user,newUser);
        log.info("User Before Add : {}", newUser);
        newUser = userRepo.save(newUser);
//        log.info("User : {}", newUser);
        emailService.sendUsernameAndPassword(newUser);

    }

    @Override
    public void registerUser(UserDTO user) throws Exception {

        if (StringUtils.isEmpty(user.getContact())) {
            throw new Exception("Please provide phone");
        } else if (user.getRole() == null) {
            throw new Exception("Please provide ROLE");
        } else if (StringUtils.isEmpty(user.getPassword())) {
            throw new Exception("Please provide password");
        }

        User data=userRepo.findByEmail(user.getEmail());
        if(data!=null)
            throw new UsernameNotFoundException("Email already exist");
        data=userRepo.findByContact(user.getContact());
        if(data!=null)
            throw new UsernameNotFoundException("Phone No already exist");

        TemporaryUser temporaryUser = new TemporaryUser();
        mapper.map(user,temporaryUser);
        otpService.storeTemporaryUser(temporaryUser);
    }

    @Override
    public void verifyUser(UserDTO user) throws Exception {

        if (StringUtils.isEmpty(user.getEmail())) {
            throw new Exception("Phone no not found");
        } else if (StringUtils.isEmpty(user.getOtp())) {
            throw new Exception("Please provide 6 digits OTP");
        }
        Optional<TemporaryUser> tempUserOpt = otpService.verifyOTP(user.getEmail(), user.getOtp());
        if (StringUtils.isEmpty(user.getContact())) {
            tempUserOpt = otpService.verifyOTP(user.getEmail(), user.getOtp());
        }
        if (tempUserOpt.isPresent()) {
            UserDTO newUser = mapper.map(tempUserOpt.get(),UserDTO.class);
            log.info("User : {}",newUser);
            postUser(newUser);
            otpService.removeTemporaryUser(user.getEmail());
        } else {
            throw new Exception("Verification Failed.");
        }
    }

    @Override
    public List<User> getUserByRole(Integer roleId) {
        return userRepo.findByRoleId(roleId);
    }

    @Override
    public List<User> getUserByFirmId(Integer firmId) {
        return userRepo.findByFirmId(firmId);
    }

    @Override
    public List<User> getUserByRoleName(String roleName) {
        return userRepo.findByRoleName(roleName);
    }

    @Override
    public List<User> getUserByFirmName(String firmName) {
        return userRepo.findByFirmName(firmName);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //System.out.println(email);
        User user=userRepo.findByEmail(email);
        if(user==null){
            user=userRepo.findByContact(email);
        }
        if(user==null){
            throw new UsernameNotFoundException("User with "+ email +" not found");
        }
        return user;
    }
    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByContact(email);
        if (user==null) {
            throw new UsernameNotFoundException("User with "+ email +" not found");
        }

        return mapper.map(user,UserDTO.class);
    }
    @Override
    public UserDTO getUserByPhone(String phone) throws Exception {
        User user = userRepo.findByContact(phone);
        if (user==null) {
            throw new UsernameNotFoundException("User with "+ phone +" not found");
        }

        return mapper.map(user,UserDTO.class);
    }

    @Override
    public List<User> getUserByRoleIdAndDivisionId(Integer roleId, Integer divisionId) {
        return userRepo.findByRoleIdAndDivisionId(roleId,divisionId);
    }

    @Override
    public List<User> getUserByRoleIdAndShedIdAndDivisionId(Integer roleId, Integer shedId, Integer divisionId) {
        return userRepo.findByRoleIdAndShedIdAndDivisionId(roleId,shedId,divisionId);
    }

    @Override
    public List<User> getUserByRoleIdAndFirmIdAndDivisionId(Integer roleId, Integer firmId, Integer divisionId) {
        return userRepo.findByRoleIdAndFirmIdAndDivisionId(roleId,firmId,divisionId);
    }

    @Override
    public List<User> getAllUsersRelatedToInspection(TcasBreakingInspection inspection) {
        // Initializing with admins
        List<User> userListToNotify = new ArrayList<>(getUserByRoleName("ROLE_ADMIN"));

        // Adding division users (FaultyStation and related objects are checked for nulls)
        if (inspection != null && inspection.getFaultyStation() != null) {
            if (inspection.getFaultyStation().getDivision() != null) {
                int divisionId = inspection.getFaultyStation().getDivision().getId();

                // Adding division users by role 2
                userListToNotify.addAll(getUserByRoleName("ROLE_DIVISION"));

                // Adding officers by role 5
                userListToNotify.addAll(getUserByRoleName("ROLE_OFFICER"));

                userListToNotify.addAll(getUserByRoleName("ROLE_HQ"));
                userListToNotify.addAll(getUserByRoleName("ROLE_CoE/IRISET"));
            }
        }

        // Adding loco shed users (Loco and related objects are checked for nulls)
        if (inspection != null && inspection.getLoco() != null) {
            if (inspection.getLoco().getShed() != null && inspection.getLoco().getFirm() != null &&
                    inspection.getFaultyStation() != null &&
                    inspection.getFaultyStation().getDivision() != null) {
                int shedId = inspection.getLoco().getShed().getId();
//                int firmId = inspection.getLoco().getFirm().getId();
                int divisionId = inspection.getFaultyStation().getDivision().getId();

                // Adding loco shed users by role 3
                userListToNotify.addAll(getUserByRoleIdAndShedIdAndDivisionId(3, shedId, divisionId));

            }
        }

        // If you need to add the user who submitted the inspection, ensure inspection.getUser() is not null
        // if (inspection.getUser() != null) {
        //     userListToNotify.add(inspection.getUser());
        // }

        return userListToNotify;
    }


    @Override
    public List<User> getAllOEMUsersRelatedToInspection(TcasBreakingInspection inspection) {
        List<User> userListToNotify = new ArrayList<>();

        if (inspection != null && inspection.getFaultyStation() != null && inspection.getLoco() != null) {
            // Check if the FaultyStation has a Firm and Section with Division
            if (inspection.getFaultyStation().getFirm() != null &&
                    inspection.getFaultyStation().getDivision() != null) {

                int faultyFirmId = inspection.getFaultyStation().getFirm().getId();
                int divisionId = inspection.getFaultyStation().getDivision().getId();

                // Adding Section Firm's OEMs
                userListToNotify.addAll(getUserByRoleIdAndFirmIdAndDivisionId(6, faultyFirmId, divisionId));
            }

            // Check if Loco has a Firm and if the FaultyStation has a Section with Division
            if (inspection.getLoco().getFirm() != null &&
                    inspection.getFaultyStation() != null &&
                    inspection.getFaultyStation().getDivision() != null) {

                int locoFirmId = inspection.getLoco().getFirm().getId();
                int divisionId = inspection.getFaultyStation().getDivision().getId();

                // Adding Loco Firm's OEMs
                userListToNotify.addAll(getUserByRoleIdAndFirmIdAndDivisionId(6, locoFirmId, divisionId));
            }
        }

        return userListToNotify;
    }

    @Override
    public List<User> getAllUsersForAddIncident(TcasBreakingInspection inspection) {
        // Initialize the user list
        List<User> userList;

        // Extract parameters from inspection
        Long divisionId = Long.valueOf(inspection.getDivision().getId());
        Long zoneId = Long.valueOf(inspection.getDivision().getZone().getId());
        Long ltcasFirmId = Long.valueOf(inspection.getLoco().getFirm().getId());
        Long stcasFirmId = Long.valueOf(inspection.getFaultyStation().getFirm().getId());

        // Create CriteriaBuilder and CriteriaQuery
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);

        // Select the user
        query.select(userRoot);

        // Create predicates for role-based filtering
        Predicate roleDivision = cb.and(
                cb.equal(userRoot.get("role").get("name"), "ROLE_DIVISION"),
                cb.equal(userRoot.get("division").get("id"), divisionId)
        );

        Predicate roleZone = cb.and(
                cb.equal(userRoot.get("role").get("name"), "ROLE_ZONE"),
                cb.equal(userRoot.get("zone").get("id"), zoneId)
        );

        Predicate roleOem = cb.and(
                cb.equal(userRoot.get("role").get("name"), "ROLE_OEM"),
                cb.equal(userRoot.get("division").get("id"), divisionId),
                cb.or(
                        cb.equal(userRoot.get("firm").get("id"), ltcasFirmId),
                        cb.equal(userRoot.get("firm").get("id"), stcasFirmId)
                )
        );

        // Combine all conditions
        Predicate combinedPredicate = cb.or(roleDivision, roleZone, roleOem);

        query.where(combinedPredicate);

        // Execute query
        userList = entityManager.createQuery(query).getResultList();

        return userList;
    }

    @Override
    public List<String> getEmailsByZoneDivisionAndRole(Integer zoneId, Integer divisionId, String roleName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<User> userRoot = query.from(User.class);

        Join<User, Division> divisionJoin = userRoot.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
        Join<User, Role> roleJoin = userRoot.join("role");

        List<Predicate> predicates = new ArrayList<>();
        if (zoneId != null && zoneId > 0) {
            predicates.add(cb.equal(zoneJoin.get("id"), zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            predicates.add(cb.equal(divisionJoin.get("id"), divisionId));
        }
        if (roleName != null && !roleName.isEmpty()) {
            predicates.add(cb.equal(roleJoin.get("name"), roleName));
        }
        predicates.add(cb.isNotNull(userRoot.get("email")));
        predicates.add(cb.notEqual(userRoot.get("email"), ""));

        query.select(userRoot.get("email")).where(predicates.toArray(new Predicate[0]));

        TypedQuery<String> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

}
