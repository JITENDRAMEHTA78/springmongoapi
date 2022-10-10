package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.Medication;

@Repository
public interface MedicationRepository extends GenericRepository<Medication> {

  Medication findByNameAndDose(String name, String dose);

}
