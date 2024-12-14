package com.driving.school.service;


import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.model.UserLikedQuestion;
import com.driving.school.repository.QuestionRepository;
import com.driving.school.repository.StudentAnswersTestRepository;
import com.driving.school.repository.UserLikedQuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final StudentAnswersTestService studentAnswersTestService;
    private final UserLikedQuestionRepository userLikedQuestionRepository;
    private final SchoolUserService schoolUserService;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, StudentAnswersTestService studentAnswersTestService, UserLikedQuestionRepository userLikedQuestionRepository, SchoolUserService schoolUserService) {
        this.questionRepository = questionRepository;
        this.studentAnswersTestService = studentAnswersTestService;
        this.userLikedQuestionRepository = userLikedQuestionRepository;
        this.schoolUserService = schoolUserService;
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    public Question getNextQuestion(Long testId, SchoolUser user, String selectedTypeQuestions) {
        Question nextQuestion = new Question();

        switch (selectedTypeQuestions) {
            case "correctAnswers":
                user.setSelectedTypeQuestions("correctAnswers");
                List<StudentAnswersTest> studentCorrectAnswers = studentAnswersTestService.getCorrectStudentAnswersTestByUserIdandTestId(user.getId(), testId);
                if (!studentCorrectAnswers.isEmpty())
                    nextQuestion = studentCorrectAnswers.getFirst().getQuestion();
                else
                    nextQuestion.setQuestionNumber(0);
                break;
            case "incorrectAnswers":
                user.setSelectedTypeQuestions("incorrectAnswers");
                List<StudentAnswersTest> studentIncorrectAnswers = studentAnswersTestService.getInCorrectStudentAnswersTestByUserIdandTestId(user.getId(), testId);
                if (!studentIncorrectAnswers.isEmpty())
                    nextQuestion = studentIncorrectAnswers.getFirst().getQuestion();
                else
                    nextQuestion.setQuestionNumber(0);
                break;
            case "skippedQuestions":
                user.setSelectedTypeQuestions("skippedQuestions");
                List<StudentAnswersTest> studentSkippedQuestions = studentAnswersTestService.getSkippedStudentAnswersTestByUserIdandTestId(user.getId(), testId);
                if (!studentSkippedQuestions.isEmpty())
                    nextQuestion = studentSkippedQuestions.getFirst().getQuestion();
                else
                    nextQuestion.setQuestionNumber(0);
                break;
            case "likedQuestions":
                user.setSelectedTypeQuestions("likedQuestions");
                List<UserLikedQuestion> userLikedQuestions = userLikedQuestionRepository.findAllBySchoolUserAndTestId(user, testId);
                if (!userLikedQuestions.isEmpty())
                    nextQuestion = questionRepository.findById(userLikedQuestions.getFirst().getQuestionId()).get();
                else
                    nextQuestion.setQuestionNumber(0);
                break;
            default:
                user.setSelectedTypeQuestions("remainingQuestions");
                List<Question> allQuestions = questionRepository.findAllByTestId(testId);
                List<Question> usersQuestions = studentAnswersTestService.findQuestionsByUserIdAndTestId(user.getId(), testId);
                List<Question> remainingQuestions = new ArrayList<>(allQuestions);
                remainingQuestions.removeAll(usersQuestions);
                if (remainingQuestions.isEmpty())
                    if (allQuestions.isEmpty())
                        nextQuestion.setQuestionNumber(0);
                    else nextQuestion.setQuestionNumber(allQuestions.size() + 1);
                else {
                    nextQuestion = remainingQuestions.getFirst();
                    nextQuestion.setQuestionNumber(allQuestions.size() - remainingQuestions.size() + 1);
                }

                break;
        }

        SchoolUser userdb = schoolUserService.findUserById(user.getId());
        if (userdb != null) {
            userdb.setSelectedTypeQuestions(user.getSelectedTypeQuestions());
            schoolUserService.saveUser(userdb);
        }

        return nextQuestion;
    }


    public List<Question> getAllNoSpecialisticQuestionByCategoryAndExactPoints(String category, Long exactPoints) {
        return findAll().stream()
                .filter(q -> q.getDrivingCategory().contains(category)
                        && Boolean.FALSE.equals(q.getQuestionType())
                        && q.getPoints().equals(exactPoints))
                .toList();
    }

    public List<Question> getAllSpecialisticQuestionByCategoryAndExactPoints(String category, Long exactPoints) {
        return findAll().stream()
                .filter(q -> q.getDrivingCategory().contains(category)
                        && Boolean.TRUE.equals(q.getQuestionType())
                        && q.getPoints().equals(exactPoints))
                .toList();
    }

    public List<Question> getAllSpecialisticQuestionByCategory(String category) {
        return findAll().stream()
                .filter(q -> q.getDrivingCategory().contains(category) && Boolean.TRUE.equals(q.getQuestionType()))
                .collect(Collectors.toList());
    }

    public List<Question> getRandomQuestionsByCategoryForExam(String category, int exactPoints, int numberOfQuestions, boolean isSpecial) {
        List<Question> filteredQuestions;
        if (isSpecial)
            filteredQuestions = getAllSpecialisticQuestionByCategoryAndExactPoints(category, (long) exactPoints);
        else
            filteredQuestions = getAllNoSpecialisticQuestionByCategoryAndExactPoints(category, (long) exactPoints);

        int size = filteredQuestions.size();

        if (numberOfQuestions > size)
            return filteredQuestions;

        Random rand = new Random();
        Set<Integer> indices = new HashSet<>();

        while (indices.size() < numberOfQuestions) {
            int randomIndex = rand.nextInt(size);
            indices.add(randomIndex);
        }

        return indices.stream()
                .map(filteredQuestions::get)
                .collect(Collectors.toList());
    }

    public Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId).orElse(null);
    }

    public boolean questionIsLiked(SchoolUser user, Question question, Long testId) {
        return false;
    }
}


