package com.sixsprints.eclinic.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.TenantDto;
import com.sixsprints.eclinic.util.initdata.TenantData;

public class ThreadContext {

  private static final String USER_KEY = "user";

  private static final String TENANT_KEY = "tenant";

  private static final String TENANT_ACTUAL_KEY = "actualTenant";

  private static InheritableThreadLocal<Map<String, String>> threadData = new InheritableThreadLocal<>();

  private static InheritableThreadLocal<User> userData = new InheritableThreadLocal<>();

  public static void setCurrentUser(String user) {
    set(USER_KEY, user);
  }

  public static String getCurrentUser() {
    return get(USER_KEY);
  }

  public static void setCurrentUserData(User user) {
    userData.set(user);
  }

  public static User getCurrentUserData() {
    return userData.get();
  }

  public static String getActualTenant() {
    return get(TENANT_ACTUAL_KEY);
  }

  public static void setCurrentTenant(String tenant) {
    set(TENANT_KEY, tenant);
    set(TENANT_ACTUAL_KEY, tenant);
  }

  public static TenantDto getCurrentTenant() {

    String tenant = get(TENANT_KEY);
    if (StringUtils.isEmpty(tenant)) {
      tenant = TenantData.DEFAULT_TENANT;
    }
    final String search = tenant;
    return TenantData.TENANTS.stream().filter(t -> search.equals(t.getId()) || search.contains(t.getDomainName()))
      .findFirst()
      .orElse(TenantData.TENANTS.get(0));
  }

  private static void set(String key, String data) {
    if (threadData.get() == null) {
      threadData.set(Maps.newConcurrentMap());
    }
    threadData.get().put(key, data);
  }

  private static String get(String key) {
    String data = null;
    Map<String, String> map = threadData.get();
    if (map != null) {
      data = map.get(key);
    }
    return data;
  }
}