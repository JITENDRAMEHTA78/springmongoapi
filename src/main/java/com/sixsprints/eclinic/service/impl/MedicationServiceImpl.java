package com.sixsprints.eclinic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.Medication;
import com.sixsprints.eclinic.dto.MedicationDto;
import com.sixsprints.eclinic.repository.MedicationRepository;
import com.sixsprints.eclinic.service.MedicationService;
import com.sixsprints.eclinic.util.csv.MedicationFieldData;

@Service
public class MedicationServiceImpl extends AbstractCrudService<Medication> implements MedicationService {

  @Autowired
  private MedicationRepository medicationRepository;

  @Override
  protected GenericRepository<Medication> repository() {
    return medicationRepository;
  }

  @Override
  protected MetaData<Medication> metaData() {
    return MetaData.<Medication>builder()
      .classType(Medication.class).dtoClassType(MedicationDto.class)
      .defaultSort(Sort.by(Direction.DESC, "dateCreated"))
      .fields(MedicationFieldData.field()).build();
  }

  @Override
  protected Medication findDuplicate(Medication entity) {
    return medicationRepository.findByNameAndDose(entity.getName(), entity.getDose());
  }

}
