package com.smhrd.jumeokbap.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.dto.CodefTransactionRequest;
import com.smhrd.jumeokbap.service.CodefTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/codef/bank")
@RequiredArgsConstructor
public class CodefTransactionController {

    private final CodefTransactionService codefTransactionService;

    @PostMapping("/transaction")
    public JsonNode transaction(@RequestBody CodefTransactionRequest dto) {
        return codefTransactionService.getTransactionList(dto);
    }
}