package com.sixsprints.eclinic.service;

import org.springframework.data.domain.Page;

import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.ChatPackage;
import com.sixsprints.eclinic.domain.ChatSession;

public interface ChatSessionService extends GenericCrudService<ChatSession> {

  ChatSession findByDoctorSlugAndPatientSlug(String doctorSlug, String patientSlug);

  Page<ChatSession> findMySessions(String slug, int page, int size);

  ChatSession topUpOnPurchase(ChatPackage chatPackage, String patientSlug);

  ChatSession markResolve(String sessionSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  ChatSession markUnResolve(String sessionSlug) throws EntityNotFoundException, BaseException;

}