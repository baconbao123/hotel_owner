package com.hotel.webapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/payment")
public class VnPayController {
  private static final String vnpHashSecret = "OZDJTCTJZ0MVOBYF6X05MM3VJV64W377";

  @GetMapping("/create-qr")
  public String hello() {
    return "Hello from Payment API!";
  }

  @GetMapping("/pay")
  public ResponseEntity<?> createPayment(
        jakarta.servlet.http.HttpServletRequest request) throws UnsupportedEncodingException {
    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";
    String orderType = "billpayment";
    long amount = 100000 * 100L;
    String bankCode = "NCB";

    String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
    String vnp_IpAddr = request.getRemoteAddr();
    String vnp_TmnCode = "AIBIAS9A";
    String vnp_ReturnUrl = "http://localhost:8000/payment/return";
    String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String vnpPayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    Map<String, String> vnpParams = new HashMap<>();
    vnpParams.put("vnp_Version", vnp_Version);
    vnpParams.put("vnp_Command", vnp_Command);
    vnpParams.put("vnp_TmnCode", vnp_TmnCode);
    vnpParams.put("vnp_Amount", String.valueOf(amount));
    vnpParams.put("vnp_CurrCode", "VND");
    vnpParams.put("vnp_BankCode", bankCode);
    vnpParams.put("vnp_TxnRef", vnp_TxnRef);
    vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);
    vnpParams.put("vnp_OrderType", orderType);
    vnpParams.put("vnp_Locale", "vn");
    vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
    vnpParams.put("vnp_IpAddr", vnp_IpAddr);
    vnpParams.put("vnp_CreateDate", vnp_CreateDate);

    List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();

    for (int i = 0; i < fieldNames.size(); i++) {
      String fieldName = fieldNames.get(i);
      String value = vnpParams.get(fieldName);
      hashData.append(fieldName).append('=').append(URLEncoder.encode(value, "UTF-8"));
      query.append(fieldName).append('=').append(URLEncoder.encode(value, "UTF-8"));
      if (i != fieldNames.size() - 1) {
        hashData.append('&');
        query.append('&');
      }
    }
    String secureHash = hmacSHA512(vnpHashSecret, hashData.toString());
    query.append("&vnp_SecureHash=").append(secureHash);

    String paymentUrl = vnpPayUrl + "?" + query.toString();

    System.out.println("Payment URL: " + paymentUrl);

    return ResponseEntity.ok(Collections.singletonMap("paymentUrl", paymentUrl));
  }

  // Xử lý callback từ VNPAY
  @GetMapping("/return")
  public ResponseEntity<?> vnpReturn(@RequestParam Map<String, String> params) {
    String responseCode = params.get("vnp_ResponseCode");
    if ("00".equals(responseCode)) {
      return ResponseEntity.ok("Thanh toán thành công");
    } else {
      return ResponseEntity.ok("Thanh toán thất bại: Mã lỗi " + responseCode);
    }
  }

  private String hmacSHA512(String key, String data) {
    try {
      Mac hmac512 = Mac.getInstance("HmacSHA512");
      SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
      hmac512.init(secretKey);
      byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
      StringBuilder hash = new StringBuilder();
      for (byte b : bytes) {
        hash.append(String.format("%02x", b));
      }
      return hash.toString();
    } catch (Exception ex) {
      throw new RuntimeException("Lỗi tạo chữ ký", ex);
    }
  }
}
