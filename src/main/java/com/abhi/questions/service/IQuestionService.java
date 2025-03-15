package com.abhi.questions.service;

import com.abhi.questions.model.Question;

import java.util.List;

public interface IQuestionService {
    Question saveQuestion(Question question);
    Question updateQuestion(Question question);
    List<Question> getAllQuestions();
    Question getQuestionById(int id);
    void deleteQuestionById(int id);
}
