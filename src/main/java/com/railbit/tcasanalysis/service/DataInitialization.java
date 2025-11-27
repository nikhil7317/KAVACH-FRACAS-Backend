package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.loco.LocoType;
import com.railbit.tcasanalysis.entity.loco.Shed;
import com.railbit.tcasanalysis.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DataInitialization {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final ProjectTypeRepo projectTypeRepo;
    private final RemarkTypeRepo remarkTypeRepo;
    private final IssueCategoryRepo issueCategoryRepo;
    private final PossibleIssueRepo possibleIssueRepo;
    private final PossibleRootCauseRepo possibleRootCauseRepo;
    private final RootCauseSubCategoryRepo rootCauseSubCategoryRepo;
    private final DivisionRepo divisionRepo;
    private final FirmRepo firmRepo;
    private final DesignationRepo designationRepo;
    private final ShedRepo shedRepo;
    private final LocoTypeRepo locoTypeRepo;
    private final TcasRepo tcasRepo;
    public void createDefaultUser() {
        User existingUser = userRepo.findById(1L).orElse(null);
        if (existingUser == null) {
            Role adminRole = roleRepo.findById(1).orElse(null);
            if (adminRole == null) {
                // Create the ROLE_ADMIN role if it doesn't exist
                createRoleIfNotExists("ROLE_SUPER_ADMIN", 1);
                Optional<Role> optionalRole =  roleRepo.findById(1);
                if (optionalRole.isPresent()){
                    adminRole = optionalRole.get();
                }

            }

            User defaultUser = new User();
            defaultUser.setId(1L);
            defaultUser.setAdminId(1L);
            defaultUser.setName("railway");
            defaultUser.setContact("1234567890");
            defaultUser.setEmail("railway@india.in");
            defaultUser.setPassword("railway");
            defaultUser.setStatus(true);
            defaultUser.setOpenPermission(true);
            defaultUser.setClosePermission(true);
            defaultUser.setReadPermission(true);
            defaultUser.setWritePermission(true);
            defaultUser.setRole(adminRole);
            defaultUser.setDesignation(new Designation(1,"",""));
            userRepo.save(defaultUser);
        }
    }

    public void createRoleIfNotExists(String roleName, int id) {
        Role existingRole = roleRepo.findById(id).orElse(null);
        if (existingRole == null) {
            Role newRole = new Role();
            newRole.setId(id);
            newRole.setName(roleName);
            roleRepo.save(newRole);
        }
    }


    public void createDefaultRoles() {
        createRoleIfNotExists("ROLE_SUPER_ADMIN", 1);
        createRoleIfNotExists("ROLE_SUPER_USER", 2);
        createRoleIfNotExists("ROLE_ZONE", 3);
        createRoleIfNotExists("ROLE_DIVISION", 4);
        createRoleIfNotExists("ROLE_OEM", 5);
    }

    public void createProjectTypeIfNotExists(String name, int id) {
        ProjectType existing = projectTypeRepo.findById(id).orElse(null);
        if (existing == null) {
            ProjectType projectType = new ProjectType(id,name);
            projectTypeRepo.save(projectType);
        }
    }

    public void createRemarkTypeIfNotExists(String name, int id) {
        RemarkType existing = remarkTypeRepo.findById(id).orElse(null);
        if (existing == null) {
            RemarkType remarkType = new RemarkType(id,name);
            remarkTypeRepo.save(remarkType);
        }
    }

    public void createDefaultProjectType() {
        createProjectTypeIfNotExists("Kavach", 1);
    }

    public void createDefaultRemarkType() {
        createRemarkTypeIfNotExists("Remark",1);
        createRemarkTypeIfNotExists("Preventive Action",2);
        createRemarkTypeIfNotExists("Corrective Action",3);
    }

    public void createIssueCategoryIfNotExists(String name, int id) {
        IssueCategory existing = issueCategoryRepo.findById(id).orElse(null);
        if (existing == null) {
            IssueCategory issueCategory = new IssueCategory(id,name,new ProjectType(1,""));
            issueCategoryRepo.save(issueCategory);
        }
    }
    public void createDefaultIssueCategories() {
        createIssueCategoryIfNotExists("Mode Change", 1);
        createIssueCategoryIfNotExists("Undesirable Braking", 2);
    }
    public void createDivisionIfNotExists(String name, int id,String code) {
        Division existing = divisionRepo.findById(id).orElse(null);
        if (existing == null) {
            Division division = new Division();
            division.setId(id);
            division.setName(name);
            division.setCode(code);
            division.setIncidentCount(0L);
            divisionRepo.save(division);
        }
    }
    public void createDefaultDivisions() {
        createDivisionIfNotExists("Secunderabad",1,"SC");
        createDivisionIfNotExists("Nanded",2,"NED");
        createDivisionIfNotExists("Hyderabad",3,"HYB");
        createDivisionIfNotExists("Guntakal",4,"GTL");
    }
    public void createFirmIfNotExists(int id,String name) {
        Firm existing = firmRepo.findById(id).orElse(null);
        if (existing == null) {
            Firm firm = new Firm(id,name);
            firmRepo.save(firm);
        }
    }
    public void createDefaultFirms() {
        createFirmIfNotExists(1,"HBL");
        createFirmIfNotExists(2,"Kernex");
        createFirmIfNotExists(3,"Medha");
    }
    public void createPossibleIssuesIfNotExists(String name, int id) {
        PossibleIssue existing = possibleIssueRepo.findById(id).orElse(null);
        if (existing == null) {
            PossibleIssue possibleIssue = new PossibleIssue(id,name,null,new ProjectType(1,""));
            possibleIssueRepo.save(possibleIssue);
        }
    }
    public void createDefaultPossibleIssues() {
//        createPossibleIssuesIfNotExists("EB", 1);
//        createPossibleIssuesIfNotExists("FSB", 2);
//        createPossibleIssuesIfNotExists("Override Mode", 3);
//        createPossibleIssuesIfNotExists("Trip Mode", 4);
//        createPossibleIssuesIfNotExists("LS Mode", 5);
    }
    public void createRootCausesIfNotExists(String name, int id) {
        PossibleRootCause existing = possibleRootCauseRepo.findById(id).orElse(null);
        if (existing == null) {
            PossibleRootCause possibleRootCause = new PossibleRootCause(id,name,new ProjectType(1,""));
            possibleRootCauseRepo.save(possibleRootCause);
        }
    }
    public void createDefaultRootCauses() {
        createRootCausesIfNotExists("Station Kavach", 1);
        createRootCausesIfNotExists("Onboard Kavach", 2);
        createRootCausesIfNotExists("LP Issue", 3);
        createRootCausesIfNotExists("Alteration Work", 4);
        createRootCausesIfNotExists("Engineering Work", 5);
        createRootCausesIfNotExists("Signal/Site Issue", 6);
        createRootCausesIfNotExists("OEM Fault", 7);
        createRootCausesIfNotExists("Misc", 8);
    }
    public void createRootCauseSubCategoryIfNotExists(String name, int id,PossibleRootCause possibleRootCause) {
        RootCauseSubCategory existing = rootCauseSubCategoryRepo.findById(id).orElse(null);
        if (existing == null) {
            RootCauseSubCategory rootCauseSubCategory = new RootCauseSubCategory(id,name,possibleRootCause);
            rootCauseSubCategoryRepo.save(rootCauseSubCategory);
        }
    }
    public void createDefaultRootCauseSubCategories() {
//        createRootCauseSubCategoryIfNotExists("RFID TAG Removed",1);
    }
    public void createDesignationsIfNotExists(int id,String name,String title) {
        Designation existing = designationRepo.findById(id).orElse(null);
        if (existing == null) {
            Designation designation = new Designation(id,name,title);
            designationRepo.save(designation);
        }
    }
    public void createDefaultDesignations() {
        createDesignationsIfNotExists(1,"Admin","Admin");
        createDesignationsIfNotExists(2,"OEM","Kavach Vendor");
    }
    public void createShedIfNotExists(String name, int id) {
        Shed existing = shedRepo.findById(id).orElse(null);
        if (existing == null) {
            Shed shed = new Shed(id,name);
            shedRepo.save(shed);
        }
    }
    public void createDefaultSheds() {
        createShedIfNotExists("MLY/DLS", 1);
        createShedIfNotExists("BZA/ELS", 2);
        createShedIfNotExists("DLS/GTL", 3);
        createShedIfNotExists("DLS/MLY", 4);
        createShedIfNotExists("ECS/MLY", 5);
        createShedIfNotExists("ELS/LGD", 6);
        createShedIfNotExists("ELS/BZA", 7);
        createShedIfNotExists("GTL/ELS", 8);
        createShedIfNotExists("LGD/ELS", 9);
    }
    public void createLocoTypeIfNotExists(String name, int id) {
        LocoType existing = locoTypeRepo.findById(id).orElse(null);
        if (existing == null) {
            LocoType locoType = new LocoType(id,name);
            locoTypeRepo.save(locoType);
        }
    }
    public void createDefaultLocoTypes() {
        createLocoTypeIfNotExists("WDG-3A", 1);
        createLocoTypeIfNotExists("WAP-4", 2);
        createLocoTypeIfNotExists("WDM-3D", 3);
        createLocoTypeIfNotExists("Conv. EMU", 4);
        createLocoTypeIfNotExists("WAP-7", 5);
        createLocoTypeIfNotExists("WAG-7", 6);
        createLocoTypeIfNotExists("3-Phase EMU", 7);
        createLocoTypeIfNotExists("WAG-9", 8);
    }

    public void createTcasOptionsIfNotExists(String name, int id) {
        Tcas existing = tcasRepo.findById(id).orElse(null);
        if (existing == null) {
            Tcas tcas = new Tcas(id,name,new ProjectType(1,""));
            tcasRepo.save(tcas);
        }
    }
    public void createDefaultTcasOptions() {
        createTcasOptionsIfNotExists("Kavach",1);
        createTcasOptionsIfNotExists("Non-Kavach",2);
    }
}
