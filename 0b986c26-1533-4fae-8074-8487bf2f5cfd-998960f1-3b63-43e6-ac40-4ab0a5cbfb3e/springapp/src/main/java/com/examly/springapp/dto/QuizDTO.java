package com.examly.springapp.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private int timeLimit;
    private LocalDateTime createdAt;

    private List<QuestionDTO> questions;
    private List<QuizAttemptDTO> attempts;
}
