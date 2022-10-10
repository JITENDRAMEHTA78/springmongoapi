package com.sixsprints.eclinic.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TenantFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    String header = req.getHeader("Origin");
    if (StringUtils.isBlank(header)) {
      header = req.getRequestURL().toString();
    }
    header = sanitize(header);
    ThreadContext.setCurrentTenant(header);
    chain.doFilter(request, response);
  }

  private static String sanitize(String str) {
    str = str.replace("http://", "").replace("https://", "");

    if (str.contains(".")) {
      str = str.split("\\.")[0];
    }

    if (str.contains("/")) {
      str = str.split("/")[0];
    }

    if (str.contains(":")) {
      str = str.split(":")[0];
    }

    return str;
  }

}
