package com.smhrd.jumeokbap.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class Article {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;
}
