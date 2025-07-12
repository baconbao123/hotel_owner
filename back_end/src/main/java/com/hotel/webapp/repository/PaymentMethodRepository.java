package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.PaymentMethod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends BaseRepository<PaymentMethod, Integer> {
  //  find-all
  @Query("select t from PaymentMethod t where t.deletedAt is null")
  List<PaymentMethod> findAllPayment();
}

