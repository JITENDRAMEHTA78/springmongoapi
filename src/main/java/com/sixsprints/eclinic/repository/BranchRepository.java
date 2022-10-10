package com.sixsprints.eclinic.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.Branch;

@Repository
public interface BranchRepository extends GenericRepository<Branch> {

}