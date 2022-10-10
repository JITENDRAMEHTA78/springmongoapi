package com.sixsprints.eclinic.service;

import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.Role;

public interface RoleService extends GenericCrudService<Role> {

  Role findByName(String name);

}