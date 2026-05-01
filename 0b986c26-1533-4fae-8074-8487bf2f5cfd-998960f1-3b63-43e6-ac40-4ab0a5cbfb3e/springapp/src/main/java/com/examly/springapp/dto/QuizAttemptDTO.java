package com.examly.springapp.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptDTO {
    private Long id;
    private Long quizId;
    private String studentName;
    private int score;
    private int totalQuestions;
    private LocalDateTime completedAt;
    private List<AnswerDTO> answers;
}
