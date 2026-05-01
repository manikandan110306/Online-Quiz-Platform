package com.examly.springapp.service;

import com.examly.springapp.dto.QuizDTO;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Quiz;
import com.examly.springapp.model.QuizAssignment;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.QuizAssignmentRepository;
import com.examly.springapp.repository.QuizRepository;
import com.examly.springapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public QuizDTO createQuiz(QuizDTO quizDTO) {
        Quiz quiz = Quiz.builder()
                .title(quizDTO.getTitle())
                .description(quizDTO.getDescription())
                .timeLimit(quizDTO.getTimeLimit())
                .createdAt(LocalDateTime.now())
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToDTO(savedQuiz);
    }

    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<QuizDTO> getQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        return convertToDTO(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        quizRepository.deleteById(id);
    }

    public void assignQuizToStudents(Long quizId, List<Long> studentIds) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        for (Long studentId : studentIds) {
            if (!assignmentRepository.existsByQuizIdAndStudentId(quizId, studentId)) {
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
                QuizAssignment assignment = new QuizAssignment();
                assignment.setQuiz(quiz);
                assignment.setStudent(student);
                assignmentRepository.save(assignment);
            }
        }
    }

    public List<QuizDTO> getAssignedQuizzesForStudent(Long studentId) {
        return assignmentRepository.findByStudentId(studentId).stream()
                .map(a -> convertToDTO(a.getQuiz()))
                .collect(Collectors.toList());
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .createdAt(quiz.getCreatedAt())
                .build();
    }

}