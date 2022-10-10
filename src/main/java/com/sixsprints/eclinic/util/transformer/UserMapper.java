package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.user.UserDto;

@Mapper(componentModel = "spring")
public abstract class UserMapper extends AbstractUserTransformer<User, UserDto> {

  @Override
  @Mapping(target = "password", ignore = true)
  public abstract UserDto toDto(User user);

  @Override
  public abstract User toDomain(UserDto dto);

}
