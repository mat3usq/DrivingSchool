package com.driving.school;



import com.driving.school.model.StudentExamAnswer;
import com.driving.school.repository.StudentExamAnswerRepository;
import com.driving.school.service.StudentExamAnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentExamAnswerServiceTest {

    @Mock
    private StudentExamAnswerRepository studentExamAnswerRepository;

    @InjectMocks
    private StudentExamAnswerService studentExamAnswerService;

    private StudentExamAnswer sampleAnswer;

    @BeforeEach
    void setUp() {
        sampleAnswer = new StudentExamAnswer();
        sampleAnswer.setId(100L);
        sampleAnswer.setAnswer("Odpowiedź testowa");
        sampleAnswer.setCorrectness(true);
        sampleAnswer.setAnswerType(1L);
    }

    @Nested
    @DisplayName("Metoda: save(StudentExamAnswer)")
    class SaveTests {

        @Test
        @DisplayName("Powinna zapisać odpowiedź egzaminacyjną w repozytorium i zwrócić ją")
        void shouldSave() {
            when(studentExamAnswerRepository.save(any(StudentExamAnswer.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            StudentExamAnswer saved = studentExamAnswerService.save(sampleAnswer);

            assertThat(saved).isEqualTo(sampleAnswer);
            verify(studentExamAnswerRepository, times(1)).save(sampleAnswer);
        }
    }

    @Nested
    @DisplayName("Metoda: findById(long)")
    class FindByIdTests {

        @Test
        @DisplayName("Powinna zwrócić Optional z istniejącą odpowiedzią, jeśli istnieje w repozytorium")
        void shouldReturnOptionalIfExists() {
            when(studentExamAnswerRepository.findById(100L))
                    .thenReturn(Optional.of(sampleAnswer));

            Optional<StudentExamAnswer> result = studentExamAnswerService.findById(100L);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(sampleAnswer);
            verify(studentExamAnswerRepository, times(1)).findById(100L);
        }

        @Test
        @DisplayName("Powinna zwrócić pusty Optional, jeśli odpowiedź o danym ID nie istnieje")
        void shouldReturnEmptyIfNotExists() {
            when(studentExamAnswerRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<StudentExamAnswer> result = studentExamAnswerService.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Metoda: findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Powinna zwrócić listę wszystkich odpowiedzi egzaminacyjnych")
        void shouldReturnAllAnswers() {
            List<StudentExamAnswer> answers = List.of(sampleAnswer, new StudentExamAnswer());
            when(studentExamAnswerRepository.findAll()).thenReturn(answers);

            List<StudentExamAnswer> result = studentExamAnswerService.findAll();

            assertThat(result).hasSize(2);
            verify(studentExamAnswerRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Metoda: deleteById(long)")
    class DeleteByIdTests {

        @Test
        @DisplayName("Powinna usunąć odpowiedź egzaminacyjną, jeśli istnieje w repozytorium")
        void shouldDelete() {
            doNothing().when(studentExamAnswerRepository).deleteById(100L);

            studentExamAnswerService.deleteById(100L);

            verify(studentExamAnswerRepository, times(1)).deleteById(100L);
        }
    }
}
