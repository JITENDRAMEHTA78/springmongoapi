package com.sixsprints.eclinic.dto;

import java.util.List;

import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {

  private EntityPermission entityPermission;

  @Singular
  private List<AccessPermission> accessPermissions;

}
