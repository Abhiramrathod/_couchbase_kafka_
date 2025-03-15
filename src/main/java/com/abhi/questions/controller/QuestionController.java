package com.abhi.questions.controller;

import com.abhi.questions.model.Question;
import com.abhi.questions.service.IQuestionService;

import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;


@RestController
@RequestMapping("/abhi")
public class QuestionController {

    @Inject
    private IQuestionService questionService;

    @PostMapping("/add")
    public Question createQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }

    @GetMapping("/get")
    public List<Question> getQuestion() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/get/{id}")
    public Question getQuestionById(@PathVariable int id) {
        return questionService.getQuestionById(id);
    }

    @PutMapping("/update")
    public Question updateQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }

    @DeleteMapping("delete/{id}")
    public String deleteQuestion(@PathVariable int id) {
        questionService.deleteQuestionById(id);
        return "Deleted Successful";
    }


}
