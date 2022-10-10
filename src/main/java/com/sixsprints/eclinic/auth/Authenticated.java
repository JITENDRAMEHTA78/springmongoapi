package com.sixsprints.eclinic.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authenticated {

  boolean required() default true;

  EntityPermission entity() default EntityPermission.ANY;

  AccessPermission access() default AccessPermission.ANY;

}
