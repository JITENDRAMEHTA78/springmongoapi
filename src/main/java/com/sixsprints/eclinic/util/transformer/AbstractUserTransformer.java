package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.Role;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.user.UserDto;
import com.sixsprints.eclinic.service.RoleService;

public abstract class AbstractUserTransformer<U extends User, D extends UserDto> extends GenericTransformer<U, D> {

  @Autowired
  private RoleService roleService;

  @AfterMapping
  protected void addPermissions(U domain, @MappingTarget D.UserDtoBuilder<?, ?> builder) {
    Role role = roleService.findByName(domain.getRoleName());
    if (role != null) {
      builder.permissions(role.getPermissions());
    }
  }

}
