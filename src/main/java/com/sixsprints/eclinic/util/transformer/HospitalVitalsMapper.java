package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.Mapper;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.HospitalVitals;
import com.sixsprints.eclinic.dto.HospitalVitalsDto;
@Mapper(componentModel = "spring")
public abstract class HospitalVitalsMapper extends GenericTransformer<HospitalVitals, HospitalVitalsDto> {

  @Override
  public abstract HospitalVitals toDomain(HospitalVitalsDto dto);

  @Override
  public abstract HospitalVitalsDto toDto(HospitalVitals domain);

}
