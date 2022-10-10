package com.sixsprints.eclinic.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.ChatPackage;

@Repository
public interface ChatPackageRepository extends GenericRepository<ChatPackage> {

  List<ChatPackage> findByDoctorSlug(String doctorSlug);

}
