package com.examly.springapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String optionText;
    private boolean isCorrect;
    
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}