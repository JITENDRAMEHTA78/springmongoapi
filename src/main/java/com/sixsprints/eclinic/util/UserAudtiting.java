package com.sixsprints.eclinic.util;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class UserAudtiting implements AuditorAware<String> {

  private static final String BLANK = "SYSTEM";

  @Override
  public Optional<String> getCurrentAuditor() {
    String user = ThreadContext.getCurrentUser();
    String id = StringUtils.isNotBlank(user) ? user : BLANK;
    return Optional.of(id);
  }

}
