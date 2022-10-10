package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.EmrTemplate;

@Repository
public interface EmrTemplateRepository extends GenericRepository<EmrTemplate> {

  EmrTemplate findByNameAndTenantId(String name, String tenantId);

}