package com.abhi.questions.service;

import com.abhi.questions.couchbase.CouchbaseChangeListener;
import com.abhi.questions.model.Question;
import javax.inject.Inject;

import java.util.List;

public class QuestionService implements IQuestionService {


    @Inject
    private CouchbaseChangeListener couchbaseChangeListener;

    @Override
    public Question saveQuestion(Question question) {
        couchbaseChangeListener.insertDocument(String.valueOf(question.getId()), question);
        return question;
    }

    @Override
    public Question updateQuestion(Question question) {
        couchbaseChangeListener.updateDocument(String.valueOf(question.getId()), question);
        return question;
    }

    @Override
    public List<Question> getAllQuestions() {
        return couchbaseChangeListener.readAllDocuments();
    }

    @Override
    public Question getQuestionById(int id) {
        return couchbaseChangeListener.readDocument(id);
    }

    @Override
    public void deleteQuestionById(int id) {
        couchbaseChangeListener.deleteDocument(String.valueOf(id));
    }
}
