package com.hotel.webapp.service.owner;

import com.hotel.webapp.dto.request.AddressDTO;
import com.hotel.webapp.entity.Address;
import com.hotel.webapp.entity.Districts;
import com.hotel.webapp.entity.Streets;
import com.hotel.webapp.entity.Wards;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.AddressRepository;
import com.hotel.webapp.repository.LocalRepository;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
  AddressRepository repository;
  LocalRepository localRepository;
  AuthService authService;

  public Address save(AddressDTO addressDTO) {
    validateDTOCommon(addressDTO);

    var address = Address.builder()
                         .streetNumber(addressDTO.getStreetNumber())
                         .streetId(addressDTO.getStreetId())
                         .wardCode(addressDTO.getWardCode())
                         .districtCode(addressDTO.getDistrictCode())
                         .provinceCode(addressDTO.getProvinceCode())
                         .note(addressDTO.getNote())
                         .createdAt(LocalDateTime.now())
                         .createdBy(authService.getAuthLogin())
                         .build();
    return repository.save(address);
  }

  public void update(Integer id, AddressDTO addressDTO) {
    validateDTOCommon(addressDTO);
    var addressCrr = repository.findByIdAndDeletedAtIsNull(id)
                               .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, id + "Address"));

    var address = Address.builder()
                         .id(addressCrr.getId())
                         .streetNumber(addressDTO.getStreetNumber())
                         .streetId(addressDTO.getStreetId())
                         .wardCode(addressDTO.getWardCode())
                         .districtCode(addressDTO.getDistrictCode())
                         .provinceCode(addressDTO.getProvinceCode())
                         .note(addressDTO.getNote())
                         .createdAt(LocalDateTime.now())
                         .createdBy(authService.getAuthLogin())
                         .build();
    repository.save(address);
  }

  protected void validateDTOCommon(AddressDTO addressDTO) {
    if (!localRepository.existsProvincesByCode(addressDTO.getProvinceCode()))
      throw new AppException(ErrorCode.NOT_FOUND, "Province");

    Districts districts = localRepository.findDistrictByCode(addressDTO.getDistrictCode())
                                         .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "District"));

    if (!Objects.equals(districts.getProvinceCode(), addressDTO.getProvinceCode()))
      throw new AppException(ErrorCode.COMMON_400, "District not include in province");

    Wards wards = localRepository.findWardByWardCode(addressDTO.getWardCode())
                                 .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Ward"));

    if (!Objects.equals(wards.getDistrictCode(), addressDTO.getDistrictCode()))
      throw new AppException(ErrorCode.COMMON_400, "Ward not include in district");

    Streets streets = localRepository.findStreetsById(addressDTO.getStreetId())
                                     .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Street"));

    if (!Objects.equals(streets.getWardCode(), addressDTO.getWardCode()))
      throw new AppException(ErrorCode.COMMON_400, "Street not include in district");
  }


}