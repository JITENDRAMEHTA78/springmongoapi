package com.sixsprints.eclinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.ChatSession;

@Repository
public interface ChatSessionRepository extends GenericRepository<ChatSession> {

  Page<ChatSession> findByDoctorSlugOrPatientSlug(String userSlug, String userSlug2, Pageable page);

  ChatSession findByDoctorSlugAndPatientSlug(String doctorSlug, String patientSlug);

}
