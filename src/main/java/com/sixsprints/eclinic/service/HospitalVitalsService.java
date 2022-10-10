package com.sixsprints.eclinic.service;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.HospitalVitals;
import com.sixsprints.eclinic.enums.MasterType;

public interface HospitalVitalsService extends GenericCrudService<HospitalVitals> {

  HospitalVitals addVitals(HospitalVitals domain)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException;

  HospitalVitals findByTenantIdAndType(String tenantId, MasterType type);

}
