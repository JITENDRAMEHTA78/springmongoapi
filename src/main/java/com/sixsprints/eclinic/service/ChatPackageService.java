package com.sixsprints.eclinic.service;

import java.util.List;

import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.ChatPackage;

public interface ChatPackageService extends GenericCrudService<ChatPackage> {

  List<ChatPackage> findByDoctorSlug(String doctorSlug);
}
