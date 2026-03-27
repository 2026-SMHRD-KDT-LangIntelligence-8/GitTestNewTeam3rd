package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final BudgetService budgetRepository;
    private final SpendingLogRepository spendingLogRepository;



}
