package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Integer> {
  //  Startup - for sa
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, int id);

  Optional<User> findByRefreshToken(String refreshToken);

  @Query("select u.id, u.fullName, u.email " +
        "from User u " +
        "join UserType ut on u.userType = ut.id " +
        "where lower(ut.name) = 'Customer' " +
        "and (:keyword is null or lower(u.fullName) like lower(concat('%', :keyword, '%')))"
  )
  List<Object[]> findCustomers(@Param("keyword") String keyword, Pageable pageable);

  @Query("select count(p) > 0 from Permissions p " +
        "join Role r on p.roleId = r.id " +
        "join MapResourcesAction mra on p.mapResourcesActionId = mra.id and mra.deletedAt is null " +
        "join MapUserRoles mur on r.id = mur.roleId and mur.deletedAt is null " +
        "join User u on mur.userId = u.id " +
        "join Resources res on mra.resourceId = res.id " +
        "where u.id = :userId and lower(res.name) = 'Hotel' and p.deletedAt is null")
  boolean hasPermissionHotel(@Param("userId") Integer userId);

  @Query("select count(p) > 0 from Permissions p " +
        "join Role r on p.roleId = r.id " +
        "join MapUserRoles mur on r.id = mur.roleId and mur.deletedAt is null " +
        "join User u on mur.userId = u.id " +
        "join Hotels h on u.id = h.ownerId " +
        "join Rooms ro on h.id = ro.hotelId " +
        "where u.id = :userId and h.id = :hotelId and lower(r.name) = 'owner' and p.deletedAt is null " +
        "and r.deletedAt is null and h.deletedAt is null")
  boolean hasOwnerHotel(@Param("userId") Integer userId, @Param("hotelId") Integer hotelId);
}
