package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.Config;

@Repository
public interface ConfigRepository extends GenericRepository<Config> {

}
