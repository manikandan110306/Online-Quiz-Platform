package com.examly.springapp.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private Long questionId;
    private Long selectedOptionId;
    private boolean correct;
}
