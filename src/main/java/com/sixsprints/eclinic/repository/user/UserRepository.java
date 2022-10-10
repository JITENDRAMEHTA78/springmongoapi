package com.sixsprints.eclinic.repository.user;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.user.User;

@Repository
public interface UserRepository<U extends User> extends GenericRepository<U> {

  U findByMobileNumberAndTenantId(String mobileNumber, String tenantId);

}