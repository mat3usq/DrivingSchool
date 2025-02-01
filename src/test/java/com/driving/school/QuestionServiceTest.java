package com.driving.school;

import com.driving.school.model.Question;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.model.UserLikedQuestion;
import com.driving.school.repository.QuestionRepository;
import com.driving.school.repository.UserLikedQuestionRepository;
import com.driving.school.service.QuestionService;
import com.driving.school.service.SchoolUserService;
import com.driving.school.service.StudentAnswersTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private StudentAnswersTestService studentAnswersTestService;
    @Mock
    private UserLikedQuestionRepository userLikedQuestionRepository;
    @Mock
    private SchoolUserService schoolUserService;

    @InjectMocks
    private QuestionService questionService;

    private Question sampleQuestion;
    private SchoolUser sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new SchoolUser();
        sampleUser.setId(10L);
        sampleUser.setName("Jan");
        sampleUser.setSelectedTypeQuestions("remainingQuestions");

        sampleQuestion = new Question();
        sampleQuestion.setId(1L);
        sampleQuestion.setQuestion("Pytanie testowe");
        sampleQuestion.setDrivingCategory("B");
        sampleQuestion.setPoints(2L);
        sampleQuestion.setQuestionType(false); // false => no specialistic
    }

    @Nested
    @DisplayName("Metoda: save")
    class SaveMethodTests {

        @Test
        @DisplayName("Powinna zapisać pytanie w repozytorium i zwrócić je")
        void shouldSaveQuestion() {
            when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

            Question saved = questionService.save(sampleQuestion);

            verify(questionRepository, times(1)).save(sampleQuestion);
            assertThat(saved).isEqualTo(sampleQuestion);
        }
    }

    @Nested
    @DisplayName("Metoda: findAll")
    class FindAllMethodTests {

        @Test
        @DisplayName("Powinna zwrócić wszystkie pytania z repozytorium")
        void shouldReturnAllQuestions() {
            Question q2 = new Question();
            q2.setId(2L);
            List<Question> questions = List.of(sampleQuestion, q2);
            when(questionRepository.findAll()).thenReturn(questions);

            List<Question> result = questionService.findAll();

            assertThat(result).hasSize(2).containsExactly(sampleQuestion, q2);
            verify(questionRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Metoda: findById")
    class FindByIdMethodTests {

        @Test
        @DisplayName("Powinna zwrócić Optional z pytaniem, jeśli istnieje")
        void shouldReturnQuestionIfExists() {
            when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion));

            Optional<Question> opt = questionService.findById(1L);

            assertThat(opt).isPresent().contains(sampleQuestion);
            verify(questionRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Powinna zwrócić pusty Optional, jeśli pytanie nie istnieje")
        void shouldReturnEmptyIfNotExists() {
            when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

            Optional<Question> opt = questionService.findById(999L);

            assertThat(opt).isEmpty();
        }
    }

    @Nested
    @DisplayName("Metoda: getNextQuestion")
    class GetNextQuestionMethodTests {

        @Test
        @DisplayName("Powinna zwrócić kolejne pytanie z correctAnswers i ustawić user.selectedTypeQuestions = correctAnswers")
        void shouldReturnNextCorrectAnswerQuestion() {
            StudentAnswersTest sat = new StudentAnswersTest();
            sat.setQuestion(sampleQuestion);
            when(studentAnswersTestService.getCorrectStudentAnswersTestByUserIdandTestId(10L, 5L))
                    .thenReturn(List.of(sat));
            when(schoolUserService.findUserById(10L)).thenReturn(sampleUser);

            Question nextQ = questionService.getNextQuestion(5L, sampleUser, "correctAnswers");

            assertThat(nextQ).isEqualTo(sampleQuestion);
            assertThat(sampleUser.getSelectedTypeQuestions()).isEqualTo("correctAnswers");
            verify(schoolUserService, times(1)).saveUser(sampleUser);
        }

        @Test
        @DisplayName("Jeśli brak correctAnswers, questionNumber = 0 (puste pytanie)")
        void shouldReturnQuestionWithNumberZeroIfNoCorrectAnswers() {
            when(studentAnswersTestService.getCorrectStudentAnswersTestByUserIdandTestId(anyLong(), anyLong()))
                    .thenReturn(Collections.emptyList());
            when(schoolUserService.findUserById(10L)).thenReturn(sampleUser);

            Question nextQ = questionService.getNextQuestion(5L, sampleUser, "correctAnswers");

            assertThat(nextQ.getQuestionNumber()).isEqualTo(0);
            verify(schoolUserService, times(1)).saveUser(sampleUser);
        }

        @Test
        @DisplayName("Powinna zwrócić pytanie z likedQuestions, jeśli istnieją i ustawić user.selectedTypeQuestions = likedQuestions")
        void shouldReturnNextLikedQuestion() {
            UserLikedQuestion liked = new UserLikedQuestion();
            liked.setQuestionId(1L);
            when(userLikedQuestionRepository.findAllBySchoolUserAndTestId(sampleUser, 5L))
                    .thenReturn(List.of(liked));
            when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion));
            when(schoolUserService.findUserById(10L)).thenReturn(sampleUser);

            Question nextQ = questionService.getNextQuestion(5L, sampleUser, "likedQuestions");

            assertThat(nextQ).isEqualTo(sampleQuestion);
            assertThat(sampleUser.getSelectedTypeQuestions()).isEqualTo("likedQuestions");
            verify(schoolUserService).saveUser(sampleUser);
        }

        @Test
        @DisplayName("Powinna zwrócić pytanie z remainingQuestions, jeśli user jeszcze nie odpowiedział na wszystkie")
        void shouldReturnNextRemainingQuestion() {
            Question q2 = new Question();
            q2.setId(2L);
            q2.setQuestion("Pytanie #2");

            when(questionRepository.findAllByTestId(5L)).thenReturn(List.of(sampleQuestion, q2));
            when(studentAnswersTestService.findQuestionsByUserIdAndTestId(10L, 5L))
                    .thenReturn(List.of(sampleQuestion)); // user odpowiedział na sampleQuestion => zostaje q2
            when(schoolUserService.findUserById(10L)).thenReturn(sampleUser);

            Question nextQ = questionService.getNextQuestion(5L, sampleUser, "remainingQuestions");

            assertThat(nextQ).isEqualTo(q2);
            assertThat(sampleUser.getSelectedTypeQuestions()).isEqualTo("remainingQuestions");
            assertThat(nextQ.getQuestionNumber()).isEqualTo(2);
            verify(schoolUserService).saveUser(sampleUser);
        }

        @Test
        @DisplayName("Gdy user odpowiedział na wszystkie pytania (remainingQuestions puste) => questionNumber = allQuestions.size()+1")
        void shouldReturnQuestionNumberAllQuestionsSizePlusOne() {
            when(questionRepository.findAllByTestId(5L)).thenReturn(List.of(sampleQuestion));
            when(studentAnswersTestService.findQuestionsByUserIdAndTestId(anyLong(), anyLong()))
                    .thenReturn(List.of(sampleQuestion));
            when(schoolUserService.findUserById(10L)).thenReturn(sampleUser);

            Question nextQ = questionService.getNextQuestion(5L, sampleUser, "remainingQuestions");

            assertThat(nextQ.getQuestionNumber()).isEqualTo(1 + 1); // 2
        }
    }

    @Nested
    @DisplayName("Metoda: getAllNoSpecialisticQuestionByCategoryAndExactPoints")
    class GetAllNoSpecialisticByCategoryAndPointsTests {

        @Test
        @DisplayName("Powinna zwrócić pytania w danej kategorii, questionType=false, i o podanych punktach")
        void shouldReturnNoSpecialisticQuestionsWithExactPoints() {
            Question q2 = new Question();
            q2.setId(2L);
            q2.setDrivingCategory("B");
            q2.setQuestionType(false);
            q2.setPoints(2L);

            Question q3 = new Question();
            q3.setId(3L);
            q3.setDrivingCategory("B");
            q3.setQuestionType(true);  // specialistic => nie pasuje
            q3.setPoints(2L);

            when(questionRepository.findAll()).thenReturn(List.of(sampleQuestion, q2, q3));

            List<Question> result = questionService.getAllNoSpecialisticQuestionByCategoryAndExactPoints("B", 2L);

            assertThat(result).hasSize(2).containsExactlyInAnyOrder(sampleQuestion, q2);
        }
    }

    @Nested
    @DisplayName("Metoda: getAllSpecialisticQuestionByCategoryAndExactPoints")
    class GetAllSpecialisticByCategoryAndPointsTests {

        @Test
        @DisplayName("Powinna zwrócić pytania w danej kategorii, questionType=true, i o podanych punktach")
        void shouldReturnSpecialisticQuestionsWithExactPoints() {
            sampleQuestion.setQuestionType(true); // tak by sampleQuestion pasował
            Question q2 = new Question();
            q2.setId(2L);
            q2.setDrivingCategory("B");
            q2.setQuestionType(true);
            q2.setPoints(3L); // inny wynik, więc się nie załapie

            when(questionRepository.findAll()).thenReturn(List.of(sampleQuestion, q2));

            List<Question> result = questionService.getAllSpecialisticQuestionByCategoryAndExactPoints("B", 2L);

            assertThat(result).hasSize(1).containsExactly(sampleQuestion);
        }
    }

    @Nested
    @DisplayName("Metoda: getAllSpecialisticQuestionByCategory")
    class GetAllSpecialisticByCategoryTests {

        @Test
        @DisplayName("Powinna zwrócić wszystkie pytania questionType=true z danej kategorii")
        void shouldReturnSpecialisticByCategory() {
            sampleQuestion.setQuestionType(true);
            sampleQuestion.setDrivingCategory("B");

            Question q2 = new Question();
            q2.setId(2L);
            q2.setQuestionType(true);
            q2.setDrivingCategory("A");

            Question q3 = new Question();
            q3.setId(3L);
            q3.setQuestionType(false); // no special
            q3.setDrivingCategory("B");

            when(questionRepository.findAll()).thenReturn(List.of(sampleQuestion, q2, q3));

            List<Question> result = questionService.getAllSpecialisticQuestionByCategory("B");

            assertThat(result).hasSize(1).containsExactly(sampleQuestion);
        }
    }

    @Nested
    @DisplayName("Metoda: getRandomQuestionsByCategoryForExam")
    class GetRandomQuestionsByCategoryForExamTests {



        @Test
        @DisplayName("Jeśli numberOfQuestions > rozmiar puli, zwróć całą listę bez losowania")
        void shouldReturnAllIfSizeIsTooBig() {
            Question q2 = new Question();
            q2.setQuestionType(true); // isSpecial
            q2.setDrivingCategory("B");
            q2.setPoints(2L);

            when(questionRepository.findAll()).thenReturn(List.of(sampleQuestion, q2));

            sampleQuestion.setQuestionType(true);

            List<Question> result = questionService.getRandomQuestionsByCategoryForExam("B", 2, 5, true);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Metoda: getQuestion")
    class GetQuestionMethodTests {

        @Test
        @DisplayName("Powinna zwrócić pytanie, jeśli istnieje w repozytorium")
        void shouldReturnQuestionIfExists() {
            when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion));

            Question found = questionService.getQuestion(1L);

            assertThat(found).isEqualTo(sampleQuestion);
            verify(questionRepository).findById(1L);
        }

        @Test
        @DisplayName("Powinna zwrócić null, jeśli pytanie nie istnieje")
        void shouldReturnNullIfNotExists() {
            when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

            Question found = questionService.getQuestion(999L);

            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: questionIsLiked")
    class QuestionIsLikedMethodTests {
        @Test
        @DisplayName("Aktualnie zwraca false – brak logiki w implementacji")
        void shouldReturnFalseAlways() {
            boolean liked = questionService.questionIsLiked(sampleUser, sampleQuestion, 5L);

            assertThat(liked).isFalse();
        }
    }
}
