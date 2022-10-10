package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.Mapper;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.Report;
import com.sixsprints.eclinic.dto.ReportDto;

@Mapper(componentModel = "spring")
public abstract class ReportMapper extends GenericTransformer<Report, ReportDto> {

  @Override
  public abstract ReportDto toDto(Report Report);

  @Override
  public abstract Report toDomain(ReportDto dto);
}
