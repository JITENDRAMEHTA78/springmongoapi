package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.Role;

@Repository
public interface RoleRepository extends GenericRepository<Role> {

  Role findByName(String name);

}