package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.Mapper;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.Medication;
import com.sixsprints.eclinic.dto.MedicationDto;

@Mapper(componentModel = "spring")
public abstract class MedicationMapper extends GenericTransformer<Medication, MedicationDto> {

  @Override
  public abstract MedicationDto toDto(Medication medication);

  @Override
  public abstract Medication toDomain(MedicationDto dto);
}
