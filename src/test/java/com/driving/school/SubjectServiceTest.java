package com.driving.school;

import com.driving.school.model.Subject;
import com.driving.school.repository.SubjectRepository;
import com.driving.school.service.SubjectService;
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
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    private Subject sampleSubject;

    @BeforeEach
    void setUp() {
        sampleSubject = new Subject();
        sampleSubject.setId(1L);
        sampleSubject.setTitle("Sample Subject");
        sampleSubject.setContent("Content of subject");
        sampleSubject.setOrderIndex(1);
    }

    @Nested
    @DisplayName("Metoda: save(Subject)")
    class SaveSubjectTests {

        @Test
        @DisplayName("Powinna wywołać renumberSubject, a następnie zapisać subject w repozytorium")
        void shouldSaveSubject() {
            when(subjectRepository.findAllByOrderByOrderIndex()).thenReturn(List.of(sampleSubject));
            when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));

            subjectService.save(sampleSubject);

            verify(subjectRepository, times(1)).findAllByOrderByOrderIndex();
            verify(subjectRepository, atLeastOnce()).save(sampleSubject);
        }
    }

    @Nested
    @DisplayName("Metoda: update(Subject)")
    class UpdateSubjectTests {

        @Test
        @DisplayName("Powinna wywołać renumberSubject i renumberAllSubject")
        void shouldUpdateSubjectAndRenumberAll() {
            when(subjectRepository.findAllByOrderByOrderIndex()).thenReturn(List.of(sampleSubject));

            subjectService.update(sampleSubject);

            verify(subjectRepository, atLeastOnce()).findAllByOrderByOrderIndex();
            verify(subjectRepository, atLeastOnce()).save(sampleSubject);
        }
    }

    @Nested
    @DisplayName("Metoda: findById(Long)")
    class FindByIdTests {

        @Test
        @DisplayName("Powinna zwrócić Subject, jeśli istnieje w repozytorium")
        void shouldReturnSubjectIfExists() {
            when(subjectRepository.findById(1L)).thenReturn(Optional.of(sampleSubject));

            Subject found = subjectService.findById(1L);

            assertThat(found).isEqualTo(sampleSubject);
        }

        @Test
        @DisplayName("Powinna zwrócić nowy Subject, jeśli nie istnieje w repozytorium")
        void shouldReturnNewSubjectIfNotFound() {
            when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

            Subject notFound = subjectService.findById(999L);

            assertThat(notFound.getId()).isNull();
            assertThat(notFound.getTitle()).isNull();
            assertThat(notFound.getContent()).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: delete(Subject)")
    class DeleteSubjectTests {

        @Test
        @DisplayName("Powinna usunąć subject, a następnie wywołać renumberAllSubject")
        void shouldDeleteSubject() {
            doNothing().when(subjectRepository).delete(sampleSubject);
            doReturn(new ArrayList<>()).when(subjectRepository).findAllByOrderByOrderIndex();

            subjectService.delete(sampleSubject);

            verify(subjectRepository, times(1)).delete(sampleSubject);
            verify(subjectRepository, atLeastOnce()).findAllByOrderByOrderIndex();
        }
    }

    @Nested
    @DisplayName("Metoda: renumberAllSubject()")
    class RenumberAllSubjectTests {

        @Test
        @DisplayName("Powinna ustawić orderIndex kolejno w rosnącej kolejności")
        void shouldRenumberAllSubjects() {
            Subject sub1 = new Subject();
            sub1.setId(101L);
            sub1.setOrderIndex(5);

            Subject sub2 = new Subject();
            sub2.setId(102L);
            sub2.setOrderIndex(10);

            List<Subject> allSubjects = List.of(sub1, sub2);
            when(subjectRepository.findAllByOrderByOrderIndex()).thenReturn(allSubjects);

            subjectService.renumberAllSubject();

            assertThat(sub1.getOrderIndex()).isEqualTo(1);
            assertThat(sub2.getOrderIndex()).isEqualTo(2);

            verify(subjectRepository, times(1)).save(sub1);
            verify(subjectRepository, times(1)).save(sub2);
        }
    }

    @Nested
    @DisplayName("Metoda: renumberSubject(...)")
    class RenumberSubjectTests {

        @Test
        @DisplayName("Jeśli subject ma orderIndex == -1, ustawia go na (liczba istniejących + 1)")
        void shouldSetOrderIndexIfMinusOne() {
            sampleSubject.setOrderIndex(-1);
            Subject other1 = new Subject();
            other1.setOrderIndex(1);
            Subject other2 = new Subject();
            other2.setOrderIndex(2);
            Subject other3 = new Subject();
            other3.setOrderIndex(3);

            when(subjectRepository.findAllByOrderByOrderIndex())
                    .thenReturn(List.of(other1, other2, other3));

            subjectService.save(sampleSubject);

            assertThat(sampleSubject.getOrderIndex()).isEqualTo(4);
            verify(subjectRepository, atLeastOnce()).save(sampleSubject);
        }

        @Test
        @DisplayName("Jeśli subject ma orderIndex > 0, wszystkie subjekty z takim samym (lub większym) orderIndex są przesunięte o 1")
        void shouldShiftOthersIfIndexGiven() {
            sampleSubject.setOrderIndex(2);

            Subject s1 = new Subject();
            s1.setId(10L);
            s1.setOrderIndex(1);
            Subject s2 = new Subject();
            s2.setId(11L);
            s2.setOrderIndex(2);
            Subject s3 = new Subject();
            s3.setId(12L);
            s3.setOrderIndex(3);

            when(subjectRepository.findAllByOrderByOrderIndex())
                    .thenReturn(List.of(s1, s2, s3));

            subjectService.save(sampleSubject);

            assertThat(s2.getOrderIndex()).isEqualTo(3);
            assertThat(s3.getOrderIndex()).isEqualTo(4);
            verify(subjectRepository, times(1)).save(s2);
            verify(subjectRepository, times(1)).save(s3);
            verify(subjectRepository, atLeastOnce()).save(sampleSubject);
        }
    }
}

