package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.dto.CodefTransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodefTransactionService {

    private final CodefApiService codefApiService;

    public JsonNode getTransactionList(CodefTransactionRequest dto) {

        String requestBody = """
        {
          "organization": "%s",
          "connectedId": "%s",
          "account": "%s",
          "startDate": "%s",
          "endDate": "%s",
          "orderBy": "%s",
          "inquiryType": "%s"
        }
        """.formatted(
                dto.getOrganization(),
                dto.getConnectedId(),
                dto.getAccount(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getOrderBy(),
                dto.getInquiryType()
        );

        JsonNode response = codefApiService.callPostApi(
                "https://development.codef.io/v1/kr/bank/p/account/transaction-list",
                requestBody
        );

        System.out.println("거래내역 응답 = " + response.toPrettyString());

        return response;
    }
}