package com.sixsprints.eclinic.repository.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.enums.UserRole;

@Repository
public interface DoctorRepository extends UserRepository<Doctor> {

  Doctor findByWebsiteKey(String websiteKey);

  List<Doctor> findByRoleName(UserRole doctor);

}