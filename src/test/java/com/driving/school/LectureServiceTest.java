package com.driving.school;

import com.driving.school.model.*;
import com.driving.school.repository.LectureRepository;
import com.driving.school.repository.SubjectRepository;
import com.driving.school.repository.SublectureRepository;
import com.driving.school.service.LectureService;
import com.driving.school.service.NotificationService;
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
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private SublectureRepository sublectureRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LectureService lectureService;

    private Lecture sampleLecture;
    private Sublecture sampleSublecture;
    private Subject sampleSubject;
    private SchoolUser sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new SchoolUser();
        sampleUser.setId(1L);
        sampleUser.setRoleName(Constants.INSTRUCTOR_ROLE);
        sampleUser.setCurrentCategory("B");

        sampleLecture = new Lecture();
        sampleLecture.setId(100L);
        sampleLecture.setName("Lecture 1");
        sampleLecture.setContent("Lecture content");
        sampleLecture.setOrderIndex(1);
        sampleLecture.setCategory("B");
        sampleLecture.setSublectures(new ArrayList<>());

        sampleSublecture = new Sublecture();
        sampleSublecture.setId(200L);
        sampleSublecture.setTitle("Sublecture 1");
        sampleSublecture.setContent("Sublecture content");
        sampleSublecture.setOrderIndex(1);
        sampleSublecture.setLecture(sampleLecture);
        sampleSublecture.setSubjects(new ArrayList<>());

        sampleSubject = new Subject();
        sampleSubject.setId(300L);
        sampleSubject.setTitle("Subject 1");
        sampleSubject.setContent("Subject content");
        sampleSubject.setOrderIndex(1);
        sampleSubject.setSublecture(sampleSublecture);

        sampleSublecture.getSubjects().add(sampleSubject);
        sampleLecture.getSublectures().add(sampleSublecture);
    }



    @Nested
    @DisplayName("Metoda: save (Lecture, SchoolUser)")
    class SaveLectureTests {

        @Test
        @DisplayName("Powinna ustawić kategorię z usera, przeliczyć orderIndex, zapisać sublectures i subjects, a następnie wysłać notyfikację (jeśli INSTRUCTOR)")
        void shouldSaveLectureAndItsChildrenAndNotifyIfInstructor() {
            when(sublectureRepository.findMaxOrderIndex()).thenReturn(10);
            when(subjectRepository.findMaxOrderIndex()).thenReturn(20);

            when(lectureRepository.findAllByCategoryOrderByOrderIndex("B"))
                    .thenReturn(new ArrayList<>());

            lectureService.save(sampleLecture, sampleUser);

            assertThat(sampleLecture.getCategory()).isEqualTo("B");

            assertThat(sampleSublecture.getOrderIndex()).isEqualTo(11);
            assertThat(sampleSubject.getOrderIndex()).isEqualTo(21);

            verify(sublectureRepository, times(1)).save(sampleSublecture);
            verify(subjectRepository, times(1)).save(sampleSubject);

            verify(notificationService, times(1)).sendNotificationWhenInstructorCreateNewLecture(sampleUser);
        }

        @Test
        @DisplayName("Jeśli user nie jest INSTRUCTOR, to nie powinno być notyfikacji")
        void shouldNotSendNotificationIfNotInstructor() {
            sampleUser.setRoleName(Constants.STUDENT_ROLE);
            when(sublectureRepository.findMaxOrderIndex()).thenReturn(10);
            when(subjectRepository.findMaxOrderIndex()).thenReturn(20);
            when(lectureRepository.findAllByCategoryOrderByOrderIndex("B")).thenReturn(new ArrayList<>());

            lectureService.save(sampleLecture, sampleUser);

            verify(notificationService, never()).sendNotificationWhenInstructorCreateNewLecture(any());
        }
    }

    @Nested
    @DisplayName("Metoda: update(Lecture)")
    class UpdateTests {

        @Test
        @DisplayName("Powinna wywołać renumberLecture i renumberAllLectures")
        void shouldCallRenumberLectureAndAll() {


            lectureService.update(sampleLecture);

            verify(lectureRepository, atLeastOnce())
                    .findAllByCategoryOrderByOrderIndex(sampleLecture.getCategory());
        }
    }

    @Nested
    @DisplayName("Metoda: findById")
    class FindByIdTests {
        @Test
        @DisplayName("Powinna zwrócić wykład, jeśli istnieje w repozytorium")
        void shouldReturnLectureWhenExists() {
            when(lectureRepository.findById(100L)).thenReturn(Optional.of(sampleLecture));

            Lecture found = lectureService.findById(100L);

            assertThat(found).isEqualTo(sampleLecture);
        }

        @Test
        @DisplayName("Powinna zwrócić nowy pusty Lecture, jeśli nie istnieje w repozytorium")
        void shouldReturnNewLectureWhenNotFound() {
            when(lectureRepository.findById(999L)).thenReturn(Optional.empty());

            Lecture found = lectureService.findById(999L);

            assertThat(found.getId()).isNull();
            assertThat(found.getName()).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: delete(Lecture)")
    class DeleteTests {
        @Test
        @DisplayName("Powinna usunąć dany wykład i wywołać renumberAllLectures")
        void shouldDeleteLectureAndRenumber() {
            doNothing().when(lectureRepository).delete(sampleLecture);

            lectureService.delete(sampleLecture);

            verify(lectureRepository, times(1)).delete(sampleLecture);
            verify(lectureRepository, atLeastOnce())
                    .findAllByCategoryOrderByOrderIndex(sampleLecture.getCategory());
        }
    }

    @Nested
    @DisplayName("Metoda: renumberAllLectures")
    class RenumberAllLecturesTests {

        @Test
        @DisplayName("Powinna ustawić orderIndex kolejno w rosnącej kolejności")
        void shouldRenumberAllLecturesInCategory() {
            Lecture lecture1 = new Lecture();
            lecture1.setId(101L);
            lecture1.setOrderIndex(5);

            Lecture lecture2 = new Lecture();
            lecture2.setId(102L);
            lecture2.setOrderIndex(10);

            List<Lecture> lecturesInCategory = List.of(lecture1, lecture2);
            when(lectureRepository.findAllByCategoryOrderByOrderIndex("B"))
                    .thenReturn(lecturesInCategory);

            lectureService.renumberAllLectures(sampleLecture);

            assertThat(lecture1.getOrderIndex()).isEqualTo(1);
            assertThat(lecture2.getOrderIndex()).isEqualTo(2);

            verify(lectureRepository, times(1))
                    .findAllByCategoryOrderByOrderIndex("B");
            verify(lectureRepository, times(1)).save(lecture1);
            verify(lectureRepository, times(1)).save(lecture2);
        }
    }
}
