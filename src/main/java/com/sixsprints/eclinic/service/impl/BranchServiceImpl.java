package com.sixsprints.eclinic.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.Branch;
import com.sixsprints.eclinic.repository.BranchRepository;
import com.sixsprints.eclinic.service.BranchService;

@Service
public class BranchServiceImpl extends AbstractCrudService<Branch> implements BranchService {
  @Resource
  private BranchRepository branchRepository;

  @Override
  protected GenericRepository<Branch> repository() {
    return branchRepository;
  }

  @Override
  protected MetaData<Branch> metaData() {
    return MetaData.<Branch>builder()
      .classType(Branch.class)
      .build();
  }

  @Override
  protected Branch findDuplicate(Branch entity) {
    // TODO Auto-generated method stub
    return null;
  }
}