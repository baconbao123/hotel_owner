package com.hotel.webapp.service.owner;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.request.PaymentDTO;
import com.hotel.webapp.entity.Payment;
import com.hotel.webapp.entity.PaymentMethod;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.PaymentMethodRepository;
import com.hotel.webapp.repository.PaymentRepository;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService extends BaseServiceImpl<Payment, Integer, PaymentDTO, PaymentRepository> {
  PaymentMethodRepository paymentMethodRepository;

  public PaymentService(PaymentRepository repository, BaseMapper<Payment, PaymentDTO> mapper,
        AuthService authService,
        PaymentMethodRepository paymentMethodRepository) {
    super(repository, mapper, authService);
    this.paymentMethodRepository = paymentMethodRepository;
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    throw new AppException(ErrorCode.NOT_FOUND, "Payment");
  }

  public List<PaymentMethod> findAllPayment() {
    return paymentMethodRepository.findAllPayment();
  }


}
