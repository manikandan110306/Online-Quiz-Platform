package com.examly.springapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String studentName;
    private int score;
    private int totalQuestions;
    private LocalDateTime completedAt;
    
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}