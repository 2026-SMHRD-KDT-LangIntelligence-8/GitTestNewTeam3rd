package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.service.TodayRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@Controller
@RequiredArgsConstructor
public class TodayRecordController {

    private final TodayRecordService service;


}
