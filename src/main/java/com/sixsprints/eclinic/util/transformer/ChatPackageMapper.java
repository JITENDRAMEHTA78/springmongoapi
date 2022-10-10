package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.Mapper;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.ChatPackage;
import com.sixsprints.eclinic.dto.ChatPackageDto;

@Mapper(componentModel = "spring")
public abstract class ChatPackageMapper extends GenericTransformer<ChatPackage, ChatPackageDto> {

  @Override
  public abstract ChatPackageDto toDto(ChatPackage chatPackage);

  @Override
  public abstract ChatPackage toDomain(ChatPackageDto dto);

}
