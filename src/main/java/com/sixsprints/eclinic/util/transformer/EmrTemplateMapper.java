package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.EmrTemplate;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.EmrTemplateDto;
import com.sixsprints.eclinic.dto.user.UserDto;
import com.sixsprints.eclinic.service.user.UserService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.initdata.SpecialityData;

@Mapper(componentModel = "spring")
public abstract class EmrTemplateMapper extends GenericTransformer<EmrTemplate, EmrTemplateDto> {

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  @Override
  public abstract EmrTemplateDto toDto(EmrTemplate chatPackageBilling);

  @Override
  public abstract EmrTemplate toDomain(EmrTemplateDto dto);

  @AfterMapping
  protected void afterToDto(EmrTemplate domain, @MappingTarget EmrTemplateDto.EmrTemplateDtoBuilder builder) {
    builder.createdByUser(findUser(domain.getCreatedBy()));
    builder.lastModifiedByUser(findUser(domain.getLastModifiedBy()));
    builder.speciality(SpecialityData.search(domain.getSpecialitySlug()));
  }

  private UserDto findUser(String number) {
    User user = userService.findByMobileNumber(number);
    if (user == null) {
      user = Constants.SYSTEM_USER;
    }
    return userMapper.toDto(user);
  }

}
