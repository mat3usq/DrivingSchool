package com.driving.school.controller;

import com.driving.school.model.*;
import com.driving.school.service.QuestionService;
import com.driving.school.service.StudentExamAnswerService;
import com.driving.school.service.StudentExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ExamController {
    private final QuestionService questionService;
    private final StudentExamService studentExamService;
    private final StudentExamAnswerService studentExamAnswerService;

    @Autowired
    public ExamController(QuestionService questionService, StudentExamService studentExamService, StudentExamAnswerService studentExamAnswerService) {
        this.questionService = questionService;
        this.studentExamService = studentExamService;
        this.studentExamAnswerService = studentExamAnswerService;
    }

    @GetMapping("/exam")
    public ModelAndView examInfo(HttpSession session) {
        if (session.getAttribute("exam") == null)
            return new ModelAndView("instructionExam");
        return examSolve(session, false);
    }

    @PostMapping("/exam/generate")
    public ModelAndView generateExam(HttpSession session) {
        if (session.getAttribute("exam") == null) {
            SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
            if (user.getCurrentCategory().isEmpty())
                return new ModelAndView("redirect:/dashboard");
            String category = user.getCurrentCategory();
            List<Question> questionSet = studentExamService.generateQuestionSet(category);

            StudentExam studentExam = new StudentExam();
            studentExam.setSchoolUser(user);
            studentExam.setCategory(category);
            studentExam.setPoints(Long.valueOf("0"));
            studentExam.setStartTime(LocalDateTime.now());
            studentExam = studentExamService.createStudentExam(studentExam);

            session.setAttribute("questionSet", questionSet);
            session.setAttribute("exam", studentExam);

            return examSolve(session, true);
        }
        return examSolve(session, false);
    }

    @GetMapping("/exam/solve")
    public ModelAndView examSolve(HttpSession session, @RequestParam(defaultValue = "true") boolean getNext) {
        if (session.getAttribute("questionSet") == null)
            return examInfo(session);

        // 1. TODO: dodac jak przekroczy czas 25 minut to przekierowac na result
        //

        // 3. todo: naprawic buga, gdy sie robi jakies pytanka w egz i jest sie na tym pytaniu to
        // 3. todo: gdy sie da ctrl+r czy odswiezy sie strone to przeskakuje na nowe pytanie i
        // 3. todo: zapisuje w bazie jakies poprzednie pytanie i ja idk czy cos z tym robimy???
        //
        // 4. todo: dodac guzik zakoncz egzamin czy cus

        ModelAndView modelAndView = new ModelAndView("solveExam");
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");

        if (!questionSet.isEmpty() && getNext) {
            Question question = questionSet.getFirst();
            questionSet.remove(question);
            modelAndView.addObject("question", question);
            session.setAttribute("latestQuestion", question);
        } else modelAndView.addObject("question", session.getAttribute("latestQuestion"));

        modelAndView.addObject("category", ((StudentExam) session.getAttribute("exam")).getCategory());

        int maxQuestions = 32;
        int remainingQuestions = maxQuestions - questionSet.size();
        int noSpecCounter;
        int specCounter = 0;

        if (remainingQuestions <= 20)
            noSpecCounter = remainingQuestions;
        else {
            noSpecCounter = 20;
            specCounter = remainingQuestions - 20;
        }

        int timeToEnd = 25 * 60 - 150 - noSpecCounter * 35 - specCounter * 50;

        modelAndView.addObject("timeToEnd", String.format("%dm %ds", timeToEnd / 60, timeToEnd % 60));
        modelAndView.addObject("noSpecCounter", noSpecCounter);
        modelAndView.addObject("specCounter", specCounter);

        return modelAndView;
    }


    @PostMapping(value = {"/exam/action"})
    public ModelAndView getActionFromExam(@RequestParam("questionId") Long
                                                  questionId, @RequestParam("action") String action, HttpSession session) {
        List<Question> questionSet = (List<Question>) session.getAttribute("questionSet");
        StudentExam studentExam = (StudentExam) session.getAttribute("exam");

        studentExam = studentExamService.getStudentExamById(studentExam.getId());
        Question question = questionService.findById(questionId).orElse(null);
        if (question == null)
            return examSolve(session, false);

        StudentExamAnswer studentExamAnswer = new StudentExamAnswer();
        studentExamAnswer.setStudentExam(studentExam);
        studentExamAnswer.setQuestion(question);
        studentExamAnswer.setAnswer(action);
        studentExamAnswer.setCorrectness(false);

        if (action.equals(question.getCorrectAnswer())) {
            studentExam.setPoints(studentExam.getPoints() + question.getPoints());
            studentExamAnswer.setCorrectness(true);
            studentExamService.updateStudentExam(studentExam.getId(), studentExam);
        }

        studentExamAnswerService.save(studentExamAnswer);

        ModelAndView modelAndView;
        if (questionSet.isEmpty())
            modelAndView = summary(session);
        else
            modelAndView = examSolve(session, true);

        return modelAndView;
    }

    public ModelAndView summary(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("examResult");
        StudentExam studentExam = (StudentExam) session.getAttribute("exam");
        studentExam = studentExamService.setSummaryOfExam(studentExamService.getStudentExamById(studentExam.getId()));
        modelAndView.addObject("exam", studentExam);
        session.setAttribute("questionSet", null);
        session.setAttribute("exam", null);
        session.setAttribute("latestQuestion", null);
        return modelAndView;
    }

    @PostMapping("/exam/result")
    public ModelAndView showResultExam(HttpSession session, @RequestParam("examId") Long examId) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        StudentExam exam = studentExamService.getStudentExamById(examId);

        if (Objects.equals(exam.getSchoolUser().getId(), user.getId()) && exam.getPassed() != null) {
            ModelAndView modelAndView = new ModelAndView("examResult");
            modelAndView.addObject("exam", exam);
            return modelAndView;
        } else return examInfo(session);
    }

    @PostMapping("/exam/showAnswer")
    public ModelAndView showAnswerResult(HttpSession session, @RequestParam("answerId") long answerId) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<StudentExamAnswer> answer = studentExamAnswerService.findById(answerId);

        if (answer.isPresent()) {
            StudentExamAnswer studentAnswer = answer.get();
            if (Objects.equals(studentAnswer.getStudentExam().getSchoolUser().getId(), user.getId()) && studentAnswer.getStudentExam().getPassed() != null)
                return new ModelAndView("answerResultExam").addObject("answer", studentAnswer);
        }

        return examInfo(session);
    }

    @GetMapping("/exam/end")
    public ModelAndView endExam(@RequestParam("questionId") Long questionId, @RequestParam("action") String action, HttpSession session){
            return examSolve(session, false);
    }
}
