package com.sixsprints.eclinic.repository.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.enums.UserRole;

@Repository
public interface PatientRepository extends UserRepository<Patient> {

  List<Patient> findByRoleName(UserRole patient);

}