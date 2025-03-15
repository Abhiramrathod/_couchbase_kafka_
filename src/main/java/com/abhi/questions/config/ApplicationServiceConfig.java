package com.abhi.questions.config;

import com.abhi.questions.service.IQuestionService;
import com.abhi.questions.service.QuestionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean(name = "questionService")
    public IQuestionService getQuestionservice() {
        return new QuestionService();
    }
}
