package com.sixsprints.eclinic.util;

import java.util.List;

import com.sixsprints.eclinic.domain.Role;
import com.sixsprints.eclinic.dto.PermissionDto;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;

public class PermissionUtil {

  public static Boolean allAny(EntityPermission entityPermission, AccessPermission accessPermission) {
    return AccessPermission.ANY.equals(accessPermission) && EntityPermission.ANY.equals(entityPermission);
  }

  public static Boolean hasAccess(Role role, EntityPermission entityPermission, AccessPermission accessPermission) {

    if (allAny(entityPermission, accessPermission)) {
      return true;
    }

    List<PermissionDto> permissions = allPermissions(role);
    for (PermissionDto dto : permissions) {
      if (hasAccess(dto, entityPermission, accessPermission)) {
        return true;
      }
    }

    return false;
  }

  private static boolean hasAccess(PermissionDto dto, EntityPermission entityPermission,
    AccessPermission accessPermission) {
    return (EntityPermission.ANY.equals(entityPermission) || EntityPermission.ANY.equals(dto.getEntityPermission())
      || entityPermission.equals(dto.getEntityPermission()))
      && (AccessPermission.ANY.equals(accessPermission) || dto.getAccessPermissions().contains(AccessPermission.ANY)
        || dto.getAccessPermissions().contains(accessPermission));
  }

  private static List<PermissionDto> allPermissions(Role roles) {
    return roles.getPermissions();
  }

}