package com.sixsprints.eclinic.service;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.Report;
import com.sixsprints.eclinic.domain.user.User;

public interface ReportService extends GenericCrudService<Report> {

  Report appointmentReport(Report report, String appointmentSlug) throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

    void handleChatAction(Appointment appointment, User doctor) throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException;
}
