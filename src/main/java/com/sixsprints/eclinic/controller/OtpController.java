package com.sixsprints.eclinic.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.auth.domain.Otp;
import com.sixsprints.auth.service.OtpService;
import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.dto.filter.SortModel;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.OtpDto;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.OtpMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/otp")
public class OtpController extends AbstractCrudController<Otp, OtpDto, User> {

  public OtpController(OtpService service, OtpMapper mapper) {
    super(service, mapper);
  }

  @Override
  public ResponseEntity<RestResponse<PageDto<OtpDto>>> filter(User user,
    @RequestBody FilterRequestDto filterRequestDto) {
    if (filterRequestDto == null) {
      filterRequestDto = FilterRequestDto.builder().build();
    }
    if (CollectionUtils.isEmpty(filterRequestDto.getSortModel())) {
      filterRequestDto.setSortModel(new ArrayList<>());
    }
    List<SortModel> sortModel = filterRequestDto.getSortModel();
    sortModel.add(SortModel.builder().colId("dateModified").sort(Direction.DESC).build());
    filterRequestDto.setSortModel(sortModel);
    return super.filter(user, filterRequestDto);
  }

}