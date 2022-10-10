package com.sixsprints.eclinic.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MedicationFrequency {

  MORNING("1-0-0"), AFTERNOON("0-1-0"), NIGHT("0-0-1"),
  MORNING_AFTERNOON("1-1-0"), MORNING_NIGHT("1-0-1"),
  AFTERNOON_NIGHT("0-1-1"), MORNING_AFTERNOON_NIGHT("1-1-1");

  private String label;

  private MedicationFrequency(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  private static final Map<String, MedicationFrequency> ENUM_MAP;

  static {
    Map<String, MedicationFrequency> map = new ConcurrentHashMap<String, MedicationFrequency>();
    for (MedicationFrequency instance : MedicationFrequency.values()) {
      map.put(instance.getLabel(), instance);
    }
    ENUM_MAP = Collections.unmodifiableMap(map);
  }

  public static MedicationFrequency parse(String name) {
    return ENUM_MAP.get(name);
  }

}
