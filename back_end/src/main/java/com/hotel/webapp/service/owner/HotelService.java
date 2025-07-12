package com.hotel.webapp.service.owner;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.request.AddressDTO;
import com.hotel.webapp.dto.request.HotelDTO;
import com.hotel.webapp.dto.response.HotelsRes;
import com.hotel.webapp.entity.*;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.*;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import com.hotel.webapp.service.system.StorageFileService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelService extends BaseServiceImpl<Hotels, Integer, HotelDTO, HotelRepository> {
  HotelImagesRepository hotelImagesRepository;
  HotelPolicyRepository hotelPolicyRepository;
  StorageFileService storageFileService;
  AddressService addressService;
  DocumentsHotelRepository documentsHotelRepository;
  MapHotelFacilityRepository mapHotelFacilityRepository;
  MapHotelTypeRepository mapHotelTypeRepository;
  UserRepository userRepository;
  FacilitiesRepository facilitiesRepository;

  public HotelService(
        HotelRepository repository,
        BaseMapper<Hotels, HotelDTO> mapper,
        AuthService authService,
        HotelImagesRepository hotelImagesRepository,
        HotelPolicyRepository policyRepository,
        StorageFileService storageFileService,
        AddressService addressService,
        DocumentsHotelRepository documentsHotelRepository,
        MapHotelFacilityRepository mapHotelFacilityRepository,
        MapHotelTypeRepository mapHotelTypeRepository,
        UserRepository userRepository,
        FacilitiesRepository facilitiesRepository
  ) {
    super(repository, mapper, authService);
    this.hotelImagesRepository = hotelImagesRepository;
    this.hotelPolicyRepository = policyRepository;
    this.storageFileService = storageFileService;
    this.addressService = addressService;
    this.documentsHotelRepository = documentsHotelRepository;
    this.mapHotelFacilityRepository = mapHotelFacilityRepository;
    this.mapHotelTypeRepository = mapHotelTypeRepository;
    this.userRepository = userRepository;
    this.facilitiesRepository = facilitiesRepository;
  }

  // created
//  @Transactional
//  @Override
//  public Hotels create(HotelDTO create) {
//    var hotel = mapper.toCreate(create); // name, desc, status
//
//    hotel.setNote(create.getNoteHotel());
//
//    if (create.getOwnerId() != null) {
//      hotel.setOwnerId(create.getOwnerId());
//    } else {
//      hotel.setOwnerId(null);
//    }
//
//    if (create.getAvatar() != null) {
//      HotelDTO.AvatarReq avatar = create.getAvatar();
//      if (avatar.getAvatarUrl() != null && !avatar.getAvatarUrl().isEmpty()) {
//        String avatarStr = storageFileService.uploadHotelImg(avatar.getAvatarUrl());
//        hotel.setAvatar(avatarStr);
//      } else if (avatar.getExistingAvatarUrl() != null && !avatar.getExistingAvatarUrl().isEmpty()) {
//        hotel.setAvatar(avatar.getExistingAvatarUrl());
//      }
//    }
//
//    hotel.setAddressId(getAuthId());
//    hotel.setCreatedAt(LocalDateTime.now());
//    hotel.setCreatedBy(getAuthId());
//
//    hotel = repository.save(hotel);
//
//    // type
//    for (Integer type : create.getTypeIds()) {
//      MapHotelType mapHotelType = MapHotelType.builder()
//                                              .hotelId(hotel.getId())
//                                              .typeId(type)
//                                              .createdAt(LocalDateTime.now())
//                                              .createdBy(getAuthId())
//                                              .build();
//      mapHotelTypeRepository.save(mapHotelType);
//    }
//
//
//    // address
//    AddressDTO addressDTO = new AddressDTO(
//          create.getProvinceCode(), create.getDistrictCode(), create.getWardCode(), create.getStreetId(),
//          create.getStreetNumber(), create.getNote());
//
//    var address = addressService.save(addressDTO);
//    hotel.setAddressId(address.getId());
//
//
//    // images
//    if (create.getImages() != null && !create.getImages().isEmpty()) {
//      for (HotelDTO.ImagesReq image : create.getImages()) {
//        String imgStr = storageFileService.uploadHotelImg(image.getImageFile());
//
//        HotelImages hotelImgs = HotelImages.builder()
//                                           .name(imgStr)
//                                           .hotelId(hotel.getId())
//                                           .createdAt(LocalDateTime.now())
//                                           .createdBy(getAuthId())
//                                           .build();
//        hotelImagesRepository.save(hotelImgs);
//      }
//    }
//
//    // document
//    if (create.getDocuments() != null && !create.getDocuments().isEmpty()) {
//      for (HotelDTO.DocumentReq documentReq : create.getDocuments()) {
//        String documentPdf = storageFileService.uploadDocument(documentReq.getDocumentUrl());
//
//        DocumentsHotel documentsHotel = DocumentsHotel.builder()
//                                                      .name(documentReq.getDocumentName())
//                                                      .hotelId(hotel.getId())
//                                                      .typeId(documentReq.getTypeId())
//                                                      .documentUrl(documentPdf)
//                                                      .createdAt(LocalDateTime.now())
//                                                      .createdBy(getAuthId())
//                                                      .build();
//        documentsHotelRepository.save(documentsHotel);
//      }
//    }
//
//    // policy
//    if (create.getPolicy() != null) {
//      HotelPolicy hotelPolicy = HotelPolicy.builder()
//                                           .name(create.getPolicy().getPolicyName())
//                                           .hotelId(hotel.getId())
//                                           .description(create.getPolicy().getPolicyDescription())
//                                           .createdAt(LocalDateTime.now())
//                                           .createdBy(getAuthId())
//                                           .build();
//      hotelPolicy = hotelPolicyRepository.save(hotelPolicy);
//      hotel.setPolicyId(hotelPolicy.getId());
//    }
//
//    // facilities
//    if (create.getFacilities() != null && !create.getFacilities().isEmpty()) {
//      for (Integer facilityId : create.getFacilities()) {
//        MapHotelFacility mapHotelFacility = MapHotelFacility.builder()
//                                                            .facilityId(facilityId)
//                                                            .hotelId(hotel.getId())
//                                                            .createdAt(LocalDateTime.now())
//                                                            .createdBy(getAuthId())
//                                                            .build();
//        mapHotelFacilityRepository.save(mapHotelFacility);
//      }
//    }
//
//
//    return repository.save(hotel);
//  }

  // find hotel by id
  public HotelsRes.HotelRes findHotel(Integer id) {
    List<Object[]> hotelObjs = repository.findHotel(id);

    if (hotelObjs.isEmpty()) throw new AppException(ErrorCode.NOT_FOUND, "Hotel");

    Object[] hotelObj = hotelObjs.get(0);

    Integer hotelId = (Integer) hotelObj[0];

    // images
    List<Object[]> imagesObjs = repository.getHotelImagesByHotelId(hotelId);

    List<HotelsRes.HotelRes.ImagesRes> imagesRes = new ArrayList<>();

    for (Object[] img : imagesObjs) {
      Integer imageId = (Integer) img[0];
      String imageUrl = (String) img[1];
      imagesRes.add(new HotelsRes.HotelRes.ImagesRes(imageId, imageUrl));
    }

    // type hotel
    List<Object[]> typeObjs = repository.getTypeHotelsByHotelId(hotelId);

    List<HotelsRes.HotelRes.TypeHotelRes> typeHotelRes = new ArrayList<>();

    for (Object[] type : typeObjs) {
      Integer typeId = (Integer) type[0];
      String typeName = (String) type[1];
      typeHotelRes.add(new HotelsRes.HotelRes.TypeHotelRes(typeId, typeName));
    }

    // facilities
    List<Object[]> facilityObjs = repository.getFacilitiesByHotelId(hotelId);

    List<HotelsRes.HotelRes.FacilitiesRes> facilitiesRes = new ArrayList<>();

    for (Object[] facility : facilityObjs) {
      Integer facilityId = (Integer) facility[0];
      String facilityName = (String) facility[1];
      String facilityIcon = (String) facility[2];
      facilitiesRes.add(new HotelsRes.HotelRes.FacilitiesRes(facilityId, facilityName, facilityIcon));
    }

    // documents
    List<Object[]> documentObjs = repository.getDocumentsByHotelId(hotelId);

    List<HotelsRes.HotelRes.DocumentHotelRes> documentsRes = new ArrayList<>();

    for (Object[] document : documentObjs) {
      Integer documentId = (Integer) document[0];
      String documentName = (String) document[1];
      Integer typeId = (Integer) document[2];
      String typeName = (String) document[3];
      String documentUrl = (String) document[4];

      documentsRes.add(
            new HotelsRes.HotelRes.DocumentHotelRes(documentId, documentName, typeId, typeName, documentUrl));
    }

    // policies
    List<Object[]> policyObjs = repository.getPolicyByHotelId(hotelId);

    Object[] policyObj = policyObjs.get(0);

    var policyRes = new HotelsRes.HotelRes.PolicyRes(
          (Integer) policyObj[0], (String) policyObj[1], (String) policyObj[2]);


    return new HotelsRes.HotelRes(
          hotelId,
          (String) hotelObj[1],
          (String) hotelObj[2],
          (Boolean) hotelObj[3],
          (String) hotelObj[4],
          (String) hotelObj[5],
          (LocalDateTime) hotelObj[6],
          (LocalDateTime) hotelObj[7],
          (String) hotelObj[8],

          // address
          (String) hotelObj[9],
          (Integer) hotelObj[10],
          (String) hotelObj[11],
          (String) hotelObj[12],
          (String) hotelObj[13],
          (String) hotelObj[14],
          (String) hotelObj[15],
          (String) hotelObj[16],
          (String) hotelObj[17],
          (String) hotelObj[18],

          imagesRes,
          typeHotelRes,
          facilitiesRes,
          documentsRes,
          policyRes,
          (String) hotelObj[19],
          (String) hotelObj[20],
          (Integer) hotelObj[21]

    );
  }

  // find all
  public Page<HotelsRes> findHotels(Map<String, String> filters, Map<String, String> sort, int size, int page) {
    Map<String, Object> filterMap = removedFiltersKey(filters);
    Map<String, Object> sortMap = removedSortedKey(sort);

    Specification<Hotels> spec = buildSpecification(filterMap);
    Pageable defaultPage = buildPageable(sortMap, page, size);

    Integer ownerLogin = getAuthId();

    User user = userRepository.findById(ownerLogin).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));

    Page<Hotels> hotelsPage;
    if ("sa@gmail.com".equals(user.getEmail())) {
      hotelsPage = repository.findAll(spec, defaultPage);
    } else {
      hotelsPage = repository.findAllByOwnerId(ownerLogin, spec, defaultPage);
    }

    return hotelsPage.map(hotels -> {
      HotelsRes dto = new HotelsRes();
      dto.setId(hotels.getId());
      dto.setAvatarUrl(hotels.getAvatar());
      dto.setName(hotels.getName());
      dto.setDescription(hotels.getDescription());
      dto.setStatus(hotels.getStatus());

      if (hotels.getOwnerId() != null) {
        userRepository.findById(hotels.getOwnerId()).ifPresent(user1 -> {
          dto.setOwnerName(user1.getFullName());
        });
      }

      return dto;
    });
  }

  public Hotels updateHotel(Integer id, HotelDTO.HotelUpdateDTO update) {
    var hotelCrr = findById(id);

    AddressDTO addressDTO = new AddressDTO(
          update.getProvinceCode(),
          update.getDistrictCode(),
          update.getWardCode(),
          update.getStreetId(),
          update.getStreetNumber(),
          update.getNote()
    );

    // Handle avatar
    if ("false".equals(update.getAvatar().getKeepAvatar())) {
      if (update.getAvatar().getAvatarUrl() != null && !update.getAvatar().getAvatarUrl().isEmpty()) {
        hotelCrr.setAvatar(storageFileService.uploadHotelImg(update.getAvatar().getAvatarUrl()));
      }
    } else if (update.getAvatar().getExistingAvatarUrl() != null && !update.getAvatar().getExistingAvatarUrl()
                                                                           .isEmpty()) {
      hotelCrr.setAvatar(update.getAvatar().getExistingAvatarUrl());
    } else {
      hotelCrr.setAvatar(hotelCrr.getAvatar());
    }

    hotelCrr = Hotels.builder()
                     .id(hotelCrr.getId())
                     .ownerId(hotelCrr.getOwnerId())
                     .name(update.getName())
                     .description(update.getDescription())
                     .avatar(hotelCrr.getAvatar())
                     .addressId(hotelCrr.getAddressId())
                     .policyId(hotelCrr.getPolicyId())
                     .status(update.getStatus())
                     .approveId(hotelCrr.getApproveId())
                     .note(update.getNote())
                     .updatedAt(LocalDateTime.now())
                     .updatedBy(getAuthId())
                     .build();

    hotelCrr = repository.save(hotelCrr);

    // Document
    List<DocumentsHotel> existingDocuments = documentsHotelRepository.findByHotelId(hotelCrr.getId());
    List<Integer> incomingDocumentIds = update.getDocuments() != null
          ? update.getDocuments().stream()
                  .filter(doc -> doc.getDocumentId() != null)
                  .map(HotelDTO.HotelUpdateDTO.DocumentReqUpdate::getDocumentId)
                  .toList()
          : new ArrayList<>();

    for (DocumentsHotel existingDoc : existingDocuments) {
      if (!incomingDocumentIds.contains(existingDoc.getId())) {
        documentsHotelRepository.delete(existingDoc);
      }
    }

    if (update.getDocuments() != null) {
      for (HotelDTO.HotelUpdateDTO.DocumentReqUpdate req : update.getDocuments()) {
        DocumentsHotel documentsHotel;
        if (req.getDocumentId() != null) {
          documentsHotel = documentsHotelRepository.findById(req.getDocumentId())
                                                   .orElseThrow(
                                                         () -> new AppException(ErrorCode.NOT_FOUND, "Document"));
        } else {
          documentsHotel = new DocumentsHotel();
          documentsHotel.setHotelId(hotelCrr.getId());
          documentsHotel.setCreatedAt(LocalDateTime.now());
          documentsHotel.setCreatedBy(getAuthId());
        }

        documentsHotel.setName(req.getDocumentName());
        documentsHotel.setTypeId(req.getTypeId());

        // Handle document URL based on keepDocumentUrl
        if (Boolean.TRUE.equals(req.getKeepDocumentUrl())) {
          // Keep existing document URL if available
          if (documentsHotel.getDocumentUrl() == null && req.getDocumentUrl() != null && !req.getDocumentUrl()
                                                                                             .isEmpty()) {
            documentsHotel.setDocumentUrl(storageFileService.uploadDocument(req.getDocumentUrl()));
          }
        } else {
          // Upload new file if provided
          if (req.getDocumentUrl() != null && !req.getDocumentUrl().isEmpty()) {
            documentsHotel.setDocumentUrl(storageFileService.uploadDocument(req.getDocumentUrl()));
          } else if (req.getDocumentId() == null) {
            throw new AppException(ErrorCode.FIELD_NOT_EMPTY, "Document URL");
          }
        }

        documentsHotel.setUpdatedAt(LocalDateTime.now());
        documentsHotel.setUpdatedBy(getAuthId());
        documentsHotelRepository.save(documentsHotel);
      }
    }

    // Policy
    HotelPolicy policyCrr = hotelPolicyRepository.findById(hotelCrr.getPolicyId())
                                                 .orElseThrow(
                                                       () -> new AppException(ErrorCode.NOT_FOUND, "Hotel Policy"));
    policyCrr.setHotelId(hotelCrr.getId());
    policyCrr.setName(update.getPolicy().getPolicyName());
    policyCrr.setDescription(update.getPolicy().getPolicyDescription());
    hotelPolicyRepository.save(policyCrr);

    // Address
    if (addressDTO != null) {
      addressService.update(hotelCrr.getAddressId(), addressDTO);
    }

    // Type hotel
    List<MapHotelType> findTypeExisted = mapHotelTypeRepository.findAllByHotelIdAndDeletedAtIsNull(hotelCrr.getId());
    for (MapHotelType mapHotelType : findTypeExisted) {
      mapHotelType.setDeletedAt(LocalDateTime.now());
      mapHotelType.setUpdatedBy(getAuthId());
      mapHotelTypeRepository.save(mapHotelType);
    }

    for (Integer typeId : update.getTypeIds()) {
      MapHotelType mapHotelType = new MapHotelType();
      mapHotelType.setHotelId(hotelCrr.getId());
      mapHotelType.setTypeId(typeId);
      mapHotelType.setCreatedAt(LocalDateTime.now());
      mapHotelType.setCreatedBy(getAuthId());
      mapHotelTypeRepository.save(mapHotelType);
    }

    // Facilities
    if (update.getFacilities() != null) {
      List<MapHotelFacility> findFacilitiesExisted = mapHotelFacilityRepository.findAllByHotelIdAndDeletedAtIsNull(
            hotelCrr.getId());
      for (MapHotelFacility mapHotelFacilities : findFacilitiesExisted) {
        mapHotelFacilities.setDeletedAt(LocalDateTime.now());
        mapHotelFacilities.setUpdatedBy(getAuthId());
        mapHotelFacilityRepository.save(mapHotelFacilities);
      }

      for (Integer facilityId : update.getFacilities()) {
        MapHotelFacility mapHotelFacility = new MapHotelFacility();
        mapHotelFacility.setHotelId(hotelCrr.getId());
        mapHotelFacility.setFacilityId(facilityId);
        mapHotelFacility.setCreatedAt(LocalDateTime.now());
        mapHotelFacility.setCreatedBy(getAuthId());
        mapHotelFacilityRepository.save(mapHotelFacility);
      }
    }


    // Hotel images
    List<HotelImages> existingImages = hotelImagesRepository.findAllByHotelIdAndDeletedAtIsNull(hotelCrr.getId());
    List<Integer> incomingImageIds = update.getImages() != null
          ? update.getImages().stream()
                  .filter(img -> img.getImageId() != null)
                  .map(HotelDTO.ImagesReq::getImageId)
                  .toList()
          : new ArrayList<>();

    for (HotelImages existingImage : existingImages) {
      if (!incomingImageIds.contains(existingImage.getId())) {
        existingImage.setDeletedAt(LocalDateTime.now());
        existingImage.setUpdatedBy(getAuthId());
        hotelImagesRepository.save(existingImage);
      }
    }

    if (update.getImages() != null) {
      for (HotelDTO.ImagesReq imgReq : update.getImages()) {
        HotelImages hotelImage;
        if (imgReq.getImageId() != null) {
          hotelImage = hotelImagesRepository.findById(imgReq.getImageId())
                                            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Image"));
          if (imgReq.getExistingImageUrl() != null && !imgReq.getExistingImageUrl().isEmpty()) {
            hotelImage.setName(imgReq.getExistingImageUrl());
          }
        } else {
          if (imgReq.getImageFile() != null && !imgReq.getImageFile().isEmpty()) {
            hotelImage = new HotelImages();
            hotelImage.setHotelId(hotelCrr.getId());
            hotelImage.setName(storageFileService.uploadHotelImg(imgReq.getImageFile()));
            hotelImage.setCreatedAt(LocalDateTime.now());
            hotelImage.setCreatedBy(getAuthId());
          } else {
            continue;
          }
        }
        hotelImage.setUpdatedAt(LocalDateTime.now());
        hotelImage.setUpdatedBy(getAuthId());
        hotelImagesRepository.save(hotelImage);
      }
    }

    return repository.save(hotelCrr);
  }

  // find types
  public List<TypeHotel> findTypeHotels() {
    return repository.findAllTypeHotel();
  }

  // find documents
  public List<DocumentType> findDocumentHotels() {
    return documentsHotelRepository.findDocumentTypeAndDeletedAtIsNull();
  }

  // find facilities
  public List<Facilities> findFacilities() {
    return facilitiesRepository.findAllFacilities();
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.NOT_FOUND, "Hotel");
  }

//  private boolean checkPermissionHotelForAdmin(String token) throws ParseException, JOSEException {
//    SignedJWT signedJWT = authService.verifyToken(token.replace("Bearer ", ""));
//    Integer userId = signedJWT.getJWTClaimsSet().getIntegerClaim("userId");
//
//    User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));
//
//    if (user.getEmail().equals("sa@gmail.com")) {
//      return true;
//    }
//
//    return userRepository.hasPermissionHotel(userId);
//  }
}
