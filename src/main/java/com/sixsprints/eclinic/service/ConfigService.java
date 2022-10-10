package com.sixsprints.eclinic.service;

import java.util.Locale;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.eclinic.domain.Config;

public interface ConfigService {
  Config get();

  Config get(Locale locale);
  
  public void initAppointmentTypeAndActions() throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException;
}
