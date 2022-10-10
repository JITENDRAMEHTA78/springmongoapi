package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.Hospital;

@Repository
public interface HospitalRepository extends GenericRepository<Hospital> {

}