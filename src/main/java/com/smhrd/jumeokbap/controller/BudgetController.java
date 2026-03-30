package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.BudgetRequest;
import com.smhrd.jumeokbap.service.BudgetService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    // 예산 설정 페이지 이동
    @GetMapping("/create-page")
    public ModelAndView showCreatePage() {
        // templates 폴더의 budget_create.html로 이동
        return new ModelAndView("budget_create");
    }

    // 예산 설정 데이터 저장
    @PostMapping("/budget-save")
    public String saveBudget(@RequestBody BudgetRequest budgetRequest, HttpSession session) {
        // 1. 세션에서 로그인한 유저의 ID를 가져옵니다.
        String loginId = (String) session.getAttribute("loginUserId");

        // 2. 비로그인 상태라면 예외 처리 (안전장치)
        if (loginId == null) {
            return "로그인이 필요합니다.";
        }

        // 3. DTO에 유저 ID를 세팅하여 서비스로 넘깁니다.
        budgetRequest.setUserId(loginId);

        // 4. 서비스 호출하여 DB 저장 (이제 유저 정보가 포함됨!)
        budgetService.saveBudget(budgetRequest);

        return "예산 설정이 완료되었습니다!";
    }

}