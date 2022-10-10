package com.sixsprints.eclinic.util.transformer;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.dto.PaymentOptionDto;
import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.util.SlugUtil;
import com.sixsprints.eclinic.util.initdata.SpecialityData;

@Mapper(componentModel = "spring")
public abstract class DoctorMapper extends AbstractUserTransformer<Doctor, DoctorDto> {
  @Override
  @Mapping(target = "password", ignore = true)
  public abstract DoctorDto toDto(Doctor user);

  @Override
  public abstract Doctor toDomain(DoctorDto dto);

  @AfterMapping
  protected void afterToDto(Doctor doctor, @MappingTarget DoctorDto.DoctorDtoBuilder<?, ?> dto) {
    String text = SpecialityData.LIST.stream().filter(sp -> SlugUtil.slugify(sp).equals(doctor.getSpeciality()))
      .findFirst()
      .orElse(doctor.getSpeciality());
    dto.speciality(text);

    if (!CollectionUtils.isEmpty(doctor.getPaymentOptions())) {
      for (PaymentOptionDto p : doctor.getPaymentOptions()) {
        p.setLogo(p.getPaymentMode().getLogo());
      }
    }
  }

}
