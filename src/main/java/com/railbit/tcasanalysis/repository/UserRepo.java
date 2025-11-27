package com.railbit.tcasanalysis.repository;


import com.railbit.tcasanalysis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
//public interface UserRepo extends JpaRepository<User,Integer> {

    //@Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findOptionalByEmail(String email);

    User findByEmail(String email);
    User findByContact(String contact);
    List<User> findByRoleId(Integer roleId);
    List<User> findByFirmId(Integer firmId);
    List<User> findByRoleIdAndDivisionId(Integer roleId,Integer divisionId);
    List<User> findByRoleIdAndShedIdAndDivisionId(Integer roleId,Integer shedId,Integer divisionId);
    List<User> findByRoleIdAndFirmIdAndDivisionId(Integer roleId,Integer firmId,Integer divisionId);

    @Query("SELECT u FROM user u JOIN u.role r WHERE r.name LIKE %:roleName%")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM user u JOIN u.firm f WHERE f.name LIKE %:firmName%")
    List<User> findByFirmName(@Param("firmName") String firmName);

}
