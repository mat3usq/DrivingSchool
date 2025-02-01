package com.driving.school;

import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentExam;
import com.driving.school.model.StudentExamAnswer;
import com.driving.school.repository.StudentExamRepository;
import com.driving.school.service.ExamStatisticsService;
import com.driving.school.service.QuestionService;
import com.driving.school.service.StudentExamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Przykładowe testy jednostkowe dla StudentExamService.
 */
@ExtendWith(MockitoExtension.class)
class StudentExamServiceTest {

    @Mock
    private StudentExamRepository studentExamRepository;
    @Mock
    private QuestionService questionService;
    @Mock
    private ExamStatisticsService examStatisticsService;

    @InjectMocks
    private StudentExamService studentExamService;

    private StudentExam sampleExam;
    private SchoolUser sampleUser;
    private Question sampleQuestion;

    @BeforeEach
    void setUp() {
        sampleUser = new SchoolUser();
        sampleUser.setId(1L);
        sampleUser.setName("Jan");
        sampleUser.setSurname("Kowalski");

        sampleExam = new StudentExam();
        sampleExam.setId(100L);
        sampleExam.setCategory("B");
        sampleExam.setPoints(0L);
        sampleExam.setSchoolUser(sampleUser);
        sampleExam.setStartTime(LocalDateTime.now());

        sampleQuestion = new Question();
        sampleQuestion.setId(10L);
        sampleQuestion.setQuestion("Pytanie testowe");
        sampleQuestion.setDrivingCategory("B");
        sampleQuestion.setQuestionType(false);
        sampleQuestion.setPoints(3L);
    }

    @Nested
    @DisplayName("Metoda: createStudentExam")
    class CreateStudentExamTests {

        @Test
        @DisplayName("Powinna zapisać i zwrócić egzamin studenta w repozytorium")
        void shouldCreateAndReturnStudentExam() {
            when(studentExamRepository.save(any(StudentExam.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            StudentExam created = studentExamService.createStudentExam(sampleExam);

            verify(studentExamRepository, times(1)).save(sampleExam);
            assertThat(created).isEqualTo(sampleExam);
        }
    }

    @Nested
    @DisplayName("Metoda: getStudentExamById")
    class GetStudentExamByIdTests {

        @Test
        @DisplayName("Powinna zwrócić StudentExam, jeśli istnieje w repozytorium")
        void shouldReturnStudentExamIfExists() {
            when(studentExamRepository.findById(100L))
                    .thenReturn(Optional.of(sampleExam));

            StudentExam exam = studentExamService.getStudentExamById(100L);

            assertThat(exam).isEqualTo(sampleExam);
        }

        @Test
        @DisplayName("Powinna zwrócić null, jeśli egzamin nie istnieje w repozytorium")
        void shouldReturnNullIfNotExist() {
            when(studentExamRepository.findById(999L))
                    .thenReturn(Optional.empty());

            StudentExam exam = studentExamService.getStudentExamById(999L);

            assertThat(exam).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: getAllStudentExams")
    class GetAllStudentExamsTests {

        @Test
        @DisplayName("Powinna zwrócić listę wszystkich egzaminów studenckich z repozytorium")
        void shouldReturnAllStudentExams() {
            List<StudentExam> exams = List.of(sampleExam, new StudentExam());
            when(studentExamRepository.findAll()).thenReturn(exams);

            List<StudentExam> result = studentExamService.getAllStudentExams();

            assertThat(result).hasSize(2);
            verify(studentExamRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Metoda: updateStudentExam")
    class UpdateStudentExamTests {

        @Test
        @DisplayName("Powinna zaktualizować egzamin, jeśli istnieje, i zwrócić go")
        void shouldUpdateIfExists() {
            StudentExam details = new StudentExam();
            details.setCategory("A");
            details.setPoints(50L);

            when(studentExamRepository.findById(100L))
                    .thenReturn(Optional.of(sampleExam));
            when(studentExamRepository.save(any(StudentExam.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            StudentExam updated = studentExamService.updateStudentExam(100L, details);

            assertThat(updated.getCategory()).isEqualTo("A");
            assertThat(updated.getPoints()).isEqualTo(50L);
            verify(studentExamRepository, times(1)).save(sampleExam);
        }

        @Test
        @DisplayName("Powinna rzucić RuntimeException, jeśli egzamin nie istnieje")
        void shouldThrowIfNotExists() {
            when(studentExamRepository.findById(999L))
                    .thenReturn(Optional.empty());

            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                    studentExamService.updateStudentExam(999L, sampleExam)
            );
        }
    }

    @Nested
    @DisplayName("Metoda: deleteStudentExam")
    class DeleteStudentExamTests {

        @Test
        @DisplayName("Powinna usunąć egzamin, jeśli istnieje w repozytorium")
        void shouldDeleteIfExists() {
            when(studentExamRepository.existsById(100L)).thenReturn(true);
            doNothing().when(studentExamRepository).deleteById(100L);

            studentExamService.deleteStudentExam(100L);

            verify(studentExamRepository, times(1)).deleteById(100L);
        }

        @Test
        @DisplayName("Powinna rzucić RuntimeException, jeśli nie istnieje w repozytorium")
        void shouldThrowIfNotExist() {
            when(studentExamRepository.existsById(999L)).thenReturn(false);

            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                    studentExamService.deleteStudentExam(999L)
            );
        }
    }

    @Nested
    @DisplayName("Metoda: getStudentExamsBySchoolUser")
    class GetStudentExamsBySchoolUserTests {

        @Test
        @DisplayName("Powinna zwrócić listę egzaminów dla danego użytkownika")
        void shouldReturnExamsForUser() {
            List<StudentExam> exams = List.of(sampleExam);
            when(studentExamRepository.findBySchoolUser(sampleUser))
                    .thenReturn(exams);

            List<StudentExam> result = studentExamService.getStudentExamsBySchoolUser(sampleUser);

            assertThat(result).hasSize(1).contains(sampleExam);
            verify(studentExamRepository, times(1)).findBySchoolUser(sampleUser);
        }
    }

    @Nested
    @DisplayName("Metoda: generateQuestionSet")
    class GenerateQuestionSetTests {

        @Test
        @DisplayName("Powinna wywołać getRandomQuestionsByCategoryForExam 6 razy (3x noSpecialist + 3x specialist) i zwrócić zsumowane pytania")
        void shouldGenerateQuestions() {
            List<Question> noSpec1 = List.of(sampleQuestion);  // 1 pytanie
            List<Question> noSpec2 = List.of();                 // 0
            List<Question> noSpec3 = List.of();
            List<Question> spec1 = List.of();
            List<Question> spec2 = List.of();
            List<Question> spec3 = List.of(new Question());     // 1 pytanie

            when(questionService.getRandomQuestionsByCategoryForExam("B", 1, 4, false)).thenReturn(noSpec1);
            when(questionService.getRandomQuestionsByCategoryForExam("B", 2, 6, false)).thenReturn(noSpec2);
            when(questionService.getRandomQuestionsByCategoryForExam("B", 3, 10, false)).thenReturn(noSpec3);
            when(questionService.getRandomQuestionsByCategoryForExam("B", 1, 2, true)).thenReturn(spec1);
            when(questionService.getRandomQuestionsByCategoryForExam("B", 2, 4, true)).thenReturn(spec2);
            when(questionService.getRandomQuestionsByCategoryForExam("B", 3, 6, true)).thenReturn(spec3);

            List<Question> result = studentExamService.generateQuestionSet("B");

            assertThat(result).hasSize(2);
            verify(questionService, times(1)).getRandomQuestionsByCategoryForExam("B", 1, 4, false);
            verify(questionService, times(1)).getRandomQuestionsByCategoryForExam("B", 2, 6, false);
            verify(questionService, times(1)).getRandomQuestionsByCategoryForExam("B", 3, 10, false);
            verify(questionService, times(1)).getRandomQuestionsByCategoryForExam("B", 1, 2, true);
            verify(questionService, times(1)).getRandomQuestionsByCategoryForExam("B", 2, 4, true);
            verify(questionService, times(1)).getRandomQuestionsByCategoryForExam("B", 3, 6, true);
        }
    }

    @Nested
    @DisplayName("Metoda: setSummaryOfExam")
    class SetSummaryOfExamTests {

        @Test
        @DisplayName("Powinna ustawić czasy, policzyć odpowiedzi i zapisać egzamin zaktualizowany")
        void shouldSetSummaryAndSave() {
            StudentExamAnswer ans1 = new StudentExamAnswer();
            ans1.setQuestion(sampleQuestion);
            ans1.setCorrectness(true);
            ans1.setAnswer("B"); // np.

            StudentExamAnswer ans2 = new StudentExamAnswer();
            Question specQ = new Question();
            specQ.setQuestionType(true);
            ans2.setQuestion(specQ);
            ans2.setCorrectness(true);
            ans2.setAnswer("A");

            StudentExamAnswer ans3 = new StudentExamAnswer();
            ans3.setQuestion(sampleQuestion);
            ans3.setCorrectness(false);
            ans3.setAnswer(""); // skip

            sampleExam.getStudentExamAnswers().addAll(List.of(ans1, ans2, ans3));
            sampleExam.setId(100L);
            sampleExam.setPoints(68L); // => passed ?

            when(studentExamRepository.findById(100L))
                    .thenReturn(Optional.of(sampleExam));
            when(studentExamRepository.save(sampleExam)).thenAnswer(inv -> inv.getArgument(0));

            StudentExam updated = studentExamService.setSummaryOfExam(sampleExam);

            assertThat(updated.getEndTime()).isNotNull();
            assertThat(updated.getExamDuration()).isNotNull();
            assertThat(updated.getAverageTimePerQuestion()).isGreaterThanOrEqualTo(0);
            assertThat(updated.getAmountCorrectNoSpecAnswers()).isEqualTo(1);
            assertThat(updated.getAmountCorrectSpecAnswers()).isEqualTo(1);
            assertThat(updated.getAmountSkippedQuestions()).isEqualTo(1);
            assertThat(updated.getPassed()).isTrue();

            verify(studentExamRepository, times(1)).save(sampleExam);
            verify(examStatisticsService, times(1)).updateStatisticsExamForUser(sampleExam);
        }
    }

    @Nested
    @DisplayName("Metoda: existsAnswerToQuestionInExam")
    class ExistsAnswerToQuestionInExamTests {

        @Test
        @DisplayName("Powinna zwrócić true, jeśli w egzaminie istnieje odpowiedź do danego pytania")
        void shouldReturnTrueIfAnswerExists() {
            StudentExamAnswer sea = new StudentExamAnswer();
            sea.setQuestion(sampleQuestion);
            sea.setAnswer("A");
            sampleExam.getStudentExamAnswers().add(sea);

            boolean result = studentExamService.existsAnswerToQuestionInExam(sampleExam, sampleQuestion);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Powinna zwrócić false, jeśli w egzaminie nie ma odpowiedzi do danego pytania")
        void shouldReturnFalseIfNoAnswer() {

            boolean result = studentExamService.existsAnswerToQuestionInExam(sampleExam, sampleQuestion);

            assertThat(result).isFalse();
        }
    }
}
