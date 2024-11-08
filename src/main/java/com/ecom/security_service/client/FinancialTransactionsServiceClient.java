package com.ecom.security_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "financial-transactions-service", url = "http://localhost:8082")
public interface FinancialTransactionsServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/payments/intent/{uuid}")
    PaymentDetails getPaymentDetails(@PathVariable("uuid") String uuid);

    record PaymentDetails(String intentId, Long userId) {
    }
}

