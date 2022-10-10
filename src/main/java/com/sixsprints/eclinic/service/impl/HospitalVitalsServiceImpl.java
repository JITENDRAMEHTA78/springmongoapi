package com.sixsprints.eclinic.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.HospitalVitals;
import com.sixsprints.eclinic.dto.HospitalVitalsDto;
import com.sixsprints.eclinic.enums.MasterType;
import com.sixsprints.eclinic.repository.HospitalVitalsRepository;
import com.sixsprints.eclinic.service.HospitalVitalsService;
import com.sixsprints.eclinic.util.ThreadContext;

@Service
public class HospitalVitalsServiceImpl extends AbstractCrudService<HospitalVitals> implements HospitalVitalsService {
  @Resource
  private HospitalVitalsRepository hospitalVitalsRepository;

  @Override
  protected GenericRepository<HospitalVitals> repository() {
    return hospitalVitalsRepository;
  }

  @Override
  protected MetaData<HospitalVitals> metaData() {
    return MetaData.<HospitalVitals>builder()
      .classType(HospitalVitals.class).dtoClassType(HospitalVitalsDto.class)
      .build();
  }

  @Override
  protected HospitalVitals findDuplicate(HospitalVitals entity) {
    if (entity == null || StringUtils.isBlank(entity.getSlug())) {
      return null;
    }
    return hospitalVitalsRepository.findBySlug(entity.getSlug());
  }

  @Override
  public HospitalVitals addVitals(HospitalVitals domain)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    HospitalVitals vitals = hospitalVitalsRepository.findByTenantIdAndType(ThreadContext.getCurrentTenant().getId(),
      domain.getType());
    domain.setTenantId(ThreadContext.getCurrentTenant().getId());
    if (vitals == null) {
      return hospitalVitalsRepository.save(domain);
    }
    return super.update(vitals.getId(), domain);

  }

  @Override
  public HospitalVitals findByTenantIdAndType(String tenantId, MasterType type) {
    return hospitalVitalsRepository.findByTenantIdAndType(tenantId, type);
  }

}
