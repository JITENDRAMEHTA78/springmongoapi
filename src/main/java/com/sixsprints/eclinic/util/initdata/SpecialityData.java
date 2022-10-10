package com.sixsprints.eclinic.util.initdata;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sixsprints.eclinic.dto.SpecialityDto;
import com.sixsprints.eclinic.util.SlugUtil;

public class SpecialityData {

  public static final List<String> LIST = Arrays.asList("Orthopaedics & Sports Medicine", "Urology & Andrology",
    "General Surgery", "Pathology", "Gynaecology & Uro-Gynaecology", "Anesthesiology & Pain Management", "Diabetology",
    "Medicine", "Wellness", "Neurology", "Cardiology", "Physiotherapy");

  public static final List<SpecialityDto> SPECIALITIES = LIST.stream()
    .map(item -> SpecialityDto.builder().label(item).value(SlugUtil.slugify(item)).doctorCount(1)
      .image("https://storage.googleapis.com/proclinic_pub/specialities/" + SlugUtil.slugify(item) + ".svg")
      .build())
    .collect(Collectors.toList());

  public static SpecialityDto search(String slug) {
    return SPECIALITIES.stream().filter(spec -> spec.getValue().equals(slug)).findFirst().orElse(null);
  }

}
