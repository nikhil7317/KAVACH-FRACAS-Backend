package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.DTO.UserDTO;
import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.TemporaryUser;
import com.railbit.tcasanalysis.entity.User;

import java.util.List;

public interface UserService {

    UserDTO getUserById(Long id);
    User getUserByUserId(Long id);
    List<User> getAllUser();
    void deleteUserById(Long id);
    void postUserList(List<User> userList);
    void updateUser(UserDTO user);
    void postUser(UserDTO user) throws Exception;
    void registerUser(UserDTO user) throws Exception;
    void verifyUser(UserDTO user) throws Exception;
    List<User> getUserByRole(Integer roleId);
    List<User> getUserByFirmId(Integer firmId);
    List<User> getUserByRoleName(String roleName);
    List<User> getUserByFirmName(String firmName);
    public UserDTO getUserByEmail(String email);
    public UserDTO getUserByPhone(String phone) throws Exception;
    List<User> getUserByRoleIdAndDivisionId(Integer roleId,Integer divisionId);
    List<User> getUserByRoleIdAndShedIdAndDivisionId(Integer roleId,Integer shedId,Integer divisionId);
    List<User> getUserByRoleIdAndFirmIdAndDivisionId(Integer roleId,Integer firmId,Integer divisionId);
    List<User> getAllUsersRelatedToInspection(TcasBreakingInspection inspection);
    List<User> getAllOEMUsersRelatedToInspection(TcasBreakingInspection inspection);
    List<User> getAllUsersForAddIncident(TcasBreakingInspection inspection);
    List<String> getEmailsByZoneDivisionAndRole(Integer zoneId, Integer divisionId, String roleName);

}
