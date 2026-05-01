package com.examly.springapp.controller;

import com.examly.springapp.dto.QuizDTO;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import com.examly.springapp.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(quizDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<QuizDTO>> createMultipleQuizzes(@Valid @RequestBody List<QuizDTO> quizDTOs) {
        List<QuizDTO> createdQuizzes = quizDTOs.stream()
                .map(quizService::createQuiz)
                .collect(java.util.stream.Collectors.toList());
        return new ResponseEntity<>(createdQuizzes, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        List<QuizDTO> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping
    public ResponseEntity<Page<QuizDTO>> getQuizzes(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<QuizDTO> page = quizService.getQuizzes(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        QuizDTO quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<String> assignQuiz(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> studentIds = body.get("studentIds");
        quizService.assignQuizToStudents(id, studentIds);
        return ResponseEntity.ok("Quiz assigned successfully");
    }

    @GetMapping("/assigned/{studentId}")
    public ResponseEntity<List<QuizDTO>> getAssignedQuizzes(@PathVariable Long studentId) {
        return ResponseEntity.ok(quizService.getAssignedQuizzesForStudent(studentId));
    }

    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(userRepository.findByRole("STUDENT"));
    }
}