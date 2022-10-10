package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.HospitalVitals;
import com.sixsprints.eclinic.enums.MasterType;

@Repository
public interface HospitalVitalsRepository extends GenericRepository<HospitalVitals> {

  HospitalVitals findByTenantIdAndType(String tenantId, MasterType type);

}
