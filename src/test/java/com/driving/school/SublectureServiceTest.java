package com.driving.school;

import com.driving.school.model.Subject;
import com.driving.school.model.Sublecture;
import com.driving.school.repository.SubjectRepository;
import com.driving.school.repository.SublectureRepository;
import com.driving.school.service.SublectureService;
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
class SublectureServiceTest {

    @Mock
    private SublectureRepository sublectureRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SublectureService sublectureService;

    private Sublecture sampleSublecture;

    @BeforeEach
    void setUp() {
        sampleSublecture = new Sublecture();
        sampleSublecture.setId(1L);
        sampleSublecture.setTitle("Sublecture title");
        sampleSublecture.setContent("Sublecture content");
        sampleSublecture.setOrderIndex(1);
        sampleSublecture.setSubjects(new ArrayList<>());
    }

    @Nested
    @DisplayName("Metoda: save(Sublecture)")
    class SaveSublectureTests {

        @Test
        @DisplayName("Powinna wywołać renumberSublecture, ustawić sublecture w Subject, zapisać Subjecty i sublecture")
        void shouldSaveSublectureAndItsSubjects() {
            Subject s1 = new Subject();
            Subject s2 = new Subject();
            sampleSublecture.getSubjects().addAll(List.of(s1, s2));

            when(subjectRepository.findMaxOrderIndex()).thenReturn(10);

            when(sublectureRepository.findAllByOrderByOrderIndex())
                    .thenReturn(List.of(sampleSublecture)); // uproszczony scenariusz

            when(sublectureRepository.save(any(Sublecture.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            sublectureService.save(sampleSublecture);

            verify(sublectureRepository, times(1)).findAllByOrderByOrderIndex();

            verify(subjectRepository, times(2)).save(any(Subject.class));
            verify(sublectureRepository, atLeastOnce()).save(sampleSublecture);

            assertThat(s1.getOrderIndex()).isEqualTo(11);
            assertThat(s2.getOrderIndex()).isEqualTo(11);
        }
    }

    @Nested
    @DisplayName("Metoda: update(Sublecture)")
    class UpdateSublectureTests {

        @Test
        @DisplayName("Powinna wywołać renumberSublecture i renumberAllSubLectures")
        void shouldUpdateSublectureAndRenumberAll() {
            doReturn(List.of(sampleSublecture)).when(sublectureRepository).findAllByOrderByOrderIndex();

            sublectureService.update(sampleSublecture);

            verify(sublectureRepository, atLeastOnce()).findAllByOrderByOrderIndex();
            verify(sublectureRepository, atLeastOnce()).save(sampleSublecture);
        }
    }

    @Nested
    @DisplayName("Metoda: findById(Long)")
    class FindByIdTests {

        @Test
        @DisplayName("Powinna zwrócić Sublecture, jeśli istnieje w repozytorium")
        void shouldReturnSublectureIfExists() {
            when(sublectureRepository.findById(1L))
                    .thenReturn(Optional.of(sampleSublecture));

            Sublecture found = sublectureService.findById(1L);

            assertThat(found).isEqualTo(sampleSublecture);
        }

        @Test
        @DisplayName("Powinna zwrócić nowy Sublecture, jeśli nie istnieje w repozytorium")
        void shouldReturnNewSublectureIfNotFound() {
            when(sublectureRepository.findById(999L)).thenReturn(Optional.empty());

            Sublecture notFound = sublectureService.findById(999L);

            assertThat(notFound.getId()).isNull();
            assertThat(notFound.getTitle()).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: delete(Sublecture)")
    class DeleteSublectureTests {

        @Test
        @DisplayName("Powinna usunąć sublecture, a następnie wywołać renumberAllSubLectures")
        void shouldDeleteSublecture() {
            doNothing().when(sublectureRepository).delete(sampleSublecture);
            doReturn(List.of()).when(sublectureRepository).findAllByOrderByOrderIndex();

            sublectureService.delete(sampleSublecture);

            verify(sublectureRepository, times(1)).delete(sampleSublecture);
            verify(sublectureRepository, atLeastOnce()).findAllByOrderByOrderIndex();
        }
    }

    @Nested
    @DisplayName("Metoda: renumberAllSubLectures()")
    class RenumberAllSubLecturesTests {

        @Test
        @DisplayName("Powinna ustawić orderIndex według kolejności listy, zaczynając od 1")
        void shouldRenumberAll() {
            Sublecture s1 = new Sublecture();
            s1.setId(101L);
            s1.setOrderIndex(5);

            Sublecture s2 = new Sublecture();
            s2.setId(102L);
            s2.setOrderIndex(10);

            List<Sublecture> sublectures = List.of(s1, s2);
            when(sublectureRepository.findAllByOrderByOrderIndex()).thenReturn(sublectures);

            sublectureService.renumberAllSubLectures();

            assertThat(s1.getOrderIndex()).isEqualTo(1);
            assertThat(s2.getOrderIndex()).isEqualTo(2);
            verify(sublectureRepository, times(1)).save(s1);
            verify(sublectureRepository, times(1)).save(s2);
        }
    }

    @Nested
    @DisplayName("Metoda: renumberSublecture(...)")
    class RenumberSublectureTests {

        @Test
        @DisplayName("Jeśli sublecture ma orderIndex == -1, ustawia go na (liczba istniejących sublecture + 1)")
        void shouldSetOrderIndexIfMinusOne() {
            sampleSublecture.setOrderIndex(-1);
            when(sublectureRepository.findAllByOrderByOrderIndex())
                    .thenReturn(List.of(new Sublecture(), new Sublecture()));

            sublectureService.save(sampleSublecture);

            assertThat(sampleSublecture.getOrderIndex()).isEqualTo(3);
            verify(sublectureRepository, atLeastOnce()).save(sampleSublecture);
        }

        @Test
        @DisplayName("Jeśli sublecture ma orderIndex np. 1, to wszystkie sublectures z orderIndex >= 1 są przesunięte o 1 w górę")
        void shouldShiftOthersIfIndexSet() {
            sampleSublecture.setOrderIndex(1);
            Sublecture slA = new Sublecture();
            slA.setId(20L);
            slA.setOrderIndex(1);

            Sublecture slB = new Sublecture();
            slB.setId(21L);
            slB.setOrderIndex(2);

            when(sublectureRepository.findAllByOrderByOrderIndex()).thenReturn(List.of(slA, slB));

            sublectureService.save(sampleSublecture);

            assertThat(slA.getOrderIndex()).isEqualTo(2);
            assertThat(slB.getOrderIndex()).isEqualTo(3);
            verify(sublectureRepository, atLeastOnce()).save(slA);
            verify(sublectureRepository, atLeastOnce()).save(slB);
            verify(sublectureRepository, atLeastOnce()).save(sampleSublecture);
        }
    }
}

