package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.dto.BudgetRequest;
import com.smhrd.jumeokbap.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @org.springframework.web.bind.annotation.GetMapping("/create-page")
    public org.springframework.web.servlet.ModelAndView showCreatePage() {
        // templates 폴더의 budget_creat.html을 찾아가라는 의미
        return new org.springframework.web.servlet.ModelAndView("budget_create");
    }

    @PostMapping("/budget-save")
    public String saveBudget(@RequestBody BudgetRequest budgetRequest){
        // DTO로 받아서 서비스 호출
        budgetService.saveBudget(budgetRequest);
        return "예산 설정이 완료되었습니다!";

    }

}
