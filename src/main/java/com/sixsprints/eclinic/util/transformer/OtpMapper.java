package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.Mapper;

import com.sixsprints.auth.domain.Otp;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.dto.OtpDto;

@Mapper(componentModel = "spring")
public abstract class OtpMapper extends GenericTransformer<Otp, OtpDto> {

  @Override
  public abstract Otp toDomain(OtpDto dto);

  @Override
  public abstract OtpDto toDto(Otp domain);

}
