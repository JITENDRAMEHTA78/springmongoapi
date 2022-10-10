package com.sixsprints.eclinic.util;

import com.github.slugify.Slugify;

public class SlugUtil {

  private static final Slugify slug = new Slugify();

  public static String slugify(String text) {
    return slug.slugify(text);
  }
}
