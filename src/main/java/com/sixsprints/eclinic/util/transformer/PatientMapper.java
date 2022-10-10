package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.dto.user.PatientDto;

@Mapper(componentModel = "spring")
public abstract class PatientMapper extends AbstractUserTransformer<Patient, PatientDto> {

  @Override
  @Mapping(target = "password", ignore = true)
  public abstract PatientDto toDto(Patient user);

  @Override
  public abstract Patient toDomain(PatientDto dto);
}
