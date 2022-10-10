package com.sixsprints.eclinic.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.Hospital;
import com.sixsprints.eclinic.repository.HospitalRepository;
import com.sixsprints.eclinic.service.HospitalService;

@Service
public class HospitalServiceImpl extends AbstractCrudService<Hospital> implements HospitalService {
  @Resource
  private HospitalRepository hospitalRepository;

  @Override
  protected GenericRepository<Hospital> repository() {
    return hospitalRepository;
  }

  @Override
  protected MetaData<Hospital> metaData() {
    return MetaData.<Hospital>builder()
      .classType(Hospital.class)
      .build();
  }

  @Override
  protected Hospital findDuplicate(Hospital entity) {
    // TODO Auto-generated method stub
    return null;
  }
}