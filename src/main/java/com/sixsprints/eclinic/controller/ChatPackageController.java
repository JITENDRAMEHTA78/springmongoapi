package com.sixsprints.eclinic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.ChatPackage;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.ChatPackageDto;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.service.ChatPackageService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.ChatPackageMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/chat-package")
public class ChatPackageController extends AbstractCrudController<ChatPackage, ChatPackageDto, User> {

  private ChatPackageService chatPackageService;
  private ChatPackageMapper chatPackageMapper;

  public ChatPackageController(ChatPackageService chatPackageService, ChatPackageMapper chatPackageMapper) {
    super(chatPackageService, chatPackageMapper);
    this.chatPackageService = chatPackageService;
    this.chatPackageMapper = chatPackageMapper;
  }

  @Override
  public ResponseEntity<RestResponse<ChatPackageDto>> add(
    @Authenticated(entity = EntityPermission.CHAT_PACKAGE, access = AccessPermission.CREATE) User user,
    @RequestBody @Valid ChatPackageDto dto) throws BaseException {
    dto.setDoctorSlug(user.getSlug());
    return super.add(user, dto);
  }

  @Override
  public ResponseEntity<RestResponse<PageDto<ChatPackageDto>>> filter(
    @Authenticated(entity = EntityPermission.CHAT_PACKAGE, access = AccessPermission.READ) User user,
    @RequestBody FilterRequestDto filterRequestDto) {
    filterRequestDto = checkUserAndUpdateFilter(filterRequestDto, user);
    return super.filter(user, filterRequestDto);
  }

  @Override
  public ResponseEntity<?> patch(
    @Authenticated(entity = EntityPermission.CHAT_PACKAGE, access = AccessPermission.UPDATE) User user,
    @RequestBody ChatPackageDto chatPackageDto,
    @RequestParam String propChanged) throws BaseException {
    return super.patch(user, chatPackageDto, propChanged);
  }

  @GetMapping("/package-list/{doctorSlug}")
  public ResponseEntity<RestResponse<List<ChatPackageDto>>> packages(@PathVariable String doctorSlug) {
    List<ChatPackageDto> chatPackage = chatPackageMapper.toDto(chatPackageService.findByDoctorSlug(doctorSlug));
    return RestUtil.successResponse(chatPackage, "success");
  }

  private FilterRequestDto checkUserAndUpdateFilter(FilterRequestDto filters, User user) {
    if (filters == null) {
      filters = FilterRequestDto.builder().build();
    }
    if (UserRole.DOCTOR.toString().equals(user.getRoleName())) {
      Map<String, ColumnFilter> filterModel = filters.getFilterModel();
      if (filterModel == null) {
        filterModel = new HashMap<>();
      }
      filterModel.put("doctorSlug", ExactMatchColumnFilter.builder().filter(user.getSlug()).build());
      filters.setFilterModel(filterModel);
    }
    return filters;
  }
}
