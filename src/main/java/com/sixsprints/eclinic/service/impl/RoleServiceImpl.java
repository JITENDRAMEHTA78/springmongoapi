package com.sixsprints.eclinic.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.Role;
import com.sixsprints.eclinic.dto.RoleDto;
import com.sixsprints.eclinic.repository.RoleRepository;
import com.sixsprints.eclinic.service.RoleService;

@Service
public class RoleServiceImpl extends AbstractCrudService<Role> implements RoleService {

  @Resource
  private RoleRepository roleRepository;

  @Override
  protected GenericRepository<Role> repository() {
    return roleRepository;
  }

  @Override
  protected MetaData<Role> metaData() {
    return MetaData.<Role>builder()
      .classType(Role.class).dtoClassType(RoleDto.class)
      .build();
  }

  @Override
  protected Role findDuplicate(Role role) {
    return findByName(role.getName());
  }

  @Override
  protected boolean isInvalid(Role role) {
    return role == null || StringUtils.isBlank(role.getName());
  }

  @Override
  public Role findByName(String name) {
    return roleRepository.findByName(name);
  }

}
