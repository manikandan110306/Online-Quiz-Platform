package com.examly.springapp.repository;

import com.examly.springapp.model.QuizAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAssignmentRepository extends JpaRepository<QuizAssignment, Long> {
    List<QuizAssignment> findByStudentId(Long studentId);
    List<QuizAssignment> findByQuizId(Long quizId);
    boolean existsByQuizIdAndStudentId(Long quizId, Long studentId);
}
