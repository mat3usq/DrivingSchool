package com.driving.school;

import com.driving.school.model.CommentCourse;
import com.driving.school.model.Constants;
import com.driving.school.model.Course;
import com.driving.school.repository.CommentCourseRepository;
import com.driving.school.repository.CourseRepository;
import com.driving.school.service.CourseService;
import com.driving.school.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CommentCourseRepository commentCourseRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CourseService courseService;

    private Course sampleCourse;
    private CommentCourse sampleCommentCourse;

    @BeforeEach
    void setUp() {
        sampleCourse = new Course();
        sampleCourse.setId(1L);
        sampleCourse.setDescription("Opis kursu ...");
        sampleCourse.setDuration(10.0);
        sampleCourse.setPassed(Constants.COURSE_NOTSPECIFIED);

        sampleCommentCourse = new CommentCourse();
        sampleCommentCourse.setId(100L);
        sampleCommentCourse.setInstructorComment("Przykładowy komentarz");
    }

    @Nested
    @DisplayName("Metoda: createCourse")
    class CreateCourseTests {
        @Test
        @DisplayName("Powinna zapisać kurs w repozytorium")
        void shouldSaveCourse() {
            doReturn(sampleCourse).when(courseRepository).save(sampleCourse);

            courseService.createCourse(sampleCourse);

            verify(courseRepository, times(1)).save(sampleCourse);
        }
    }

    @Nested
    @DisplayName("Metoda: getCourseById")
    class GetCourseByIdTests {
        @Test
        @DisplayName("Powinna zwrócić Optional z kursem, jeśli istnieje w repozytorium")
        void shouldReturnCourseWhenExists() {
            when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

            Optional<Course> result = courseService.getCourseById(1L);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(sampleCourse);
        }

        @Test
        @DisplayName("Powinna zwrócić pusty Optional, jeśli kurs nie istnieje")
        void shouldReturnEmptyWhenCourseNotExist() {
            when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

            Optional<Course> result = courseService.getCourseById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Metoda: getAllCourses")
    class GetAllCoursesTests {
        @Test
        @DisplayName("Powinna zwrócić listę wszystkich kursów")
        void shouldReturnAllCourses() {
            List<Course> courses = List.of(sampleCourse, new Course());
            when(courseRepository.findAll()).thenReturn(courses);

            List<Course> result = courseService.getAllCourses();

            assertThat(result).hasSize(2);
            verify(courseRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Metoda: updateCourse")
    class UpdateCourseTests {

        @Test
        @DisplayName("Powinna zaktualizować istniejący kurs: opis, kategoria, czas trwania, passed itd.")
        void shouldUpdateCourseWhenCourseExists() {
            Course courseDetails = new Course();
            courseDetails.setDescription("Nowy opis");
            courseDetails.setDuration(5.0);
            courseDetails.setPassed(Constants.COURSE_PASSED);
            courseDetails.setCommentCourses(new ArrayList<>()); // brak istniejących komentarzy

            when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

            courseService.updateCourse(1L, courseDetails, null);

            assertThat(sampleCourse.getDescription()).isEqualTo("Nowy opis");
            assertThat(sampleCourse.getDuration()).isEqualTo(5.0);
            assertThat(sampleCourse.getEndAt()).isNotNull();
            verify(courseRepository, times(1)).save(sampleCourse);
        }

        @Test
        @DisplayName("Jeśli newCommentCourse ma treść, powinien zostać dodany do listy komentarzy i zapisany")
        void shouldAddNewCommentIfNotEmpty() {
            Course courseDetails = new Course();
            courseDetails.setCommentCourses(new ArrayList<>()); // pusto w "details"

            CommentCourse newComment = new CommentCourse();
            newComment.setInstructorComment("Instruktor: testowy komentarz");

            when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

            courseService.updateCourse(1L, courseDetails, newComment);

            verify(commentCourseRepository, times(1)).save(newComment);
            assertThat(sampleCourse.getCommentCourses()).hasSize(1);
            verify(courseRepository, times(1)).save(sampleCourse);
        }

        @Test
        @DisplayName("Jeśli newCommentCourse jest pusty lub null, nie powinien być dodawany do kursu")
        void shouldNotAddEmptyComment() {
            when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

            courseService.updateCourse(1L, sampleCourse, null);
            CommentCourse emptyComment = new CommentCourse();
            emptyComment.setInstructorComment("   ");

            courseService.updateCourse(1L, sampleCourse, emptyComment);

            verify(commentCourseRepository, never()).save(any(CommentCourse.class));
            verify(courseRepository, times(2)).save(sampleCourse);
            assertThat(sampleCourse.getCommentCourses()).isEmpty();
        }

        @Test
        @DisplayName("Powinna zaktualizować istniejące komentarze (instructorComment), jeśli pasują po ID")
        void shouldUpdateExistingComments() {
            CommentCourse existing = new CommentCourse();
            existing.setId(100L);
            existing.setInstructorComment("Stary komentarz");
            sampleCourse.getCommentCourses().add(existing);

            Course courseDetails = new Course();
            CommentCourse editedComment = new CommentCourse();
            editedComment.setId(100L);
            editedComment.setInstructorComment("Edytowany komentarz");
            courseDetails.setCommentCourses(List.of(editedComment));

            when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

            courseService.updateCourse(1L, courseDetails, null);

            assertThat(existing.getInstructorComment()).isEqualTo("Edytowany komentarz");
            verify(courseRepository, times(1)).save(sampleCourse);
        }

        @Test
        @DisplayName("Powinna ustawić endAt gdy passed to COURSE_FAILED, a usunąć endAt gdy COURSE_NOTSPECIFIED")
        void shouldHandlePassedStatusCorrectly() {
            Course courseDetails = new Course();
            courseDetails.setPassed(Constants.COURSE_FAILED);
            when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

            courseService.updateCourse(1L, courseDetails, null);

            assertThat(sampleCourse.getPassed()).isEqualTo(Constants.COURSE_FAILED);
            assertThat(sampleCourse.getEndAt()).isNotNull();  // bo FAILED => endAt = LocalDate.now()

            courseDetails.setPassed(Constants.COURSE_NOTSPECIFIED);

            courseService.updateCourse(1L, courseDetails, null);

            assertThat(sampleCourse.getPassed()).isEqualTo(Constants.COURSE_NOTSPECIFIED);
            assertThat(sampleCourse.getEndAt()).isNull(); // bo NOTSPECIFIED => endAt = null
        }

        @Test
        @DisplayName("Nie powinna robić nic, jeśli kurs o danym ID nie istnieje")
        void shouldDoNothingIfCourseDoesNotExist() {
            when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

            courseService.updateCourse(999L, sampleCourse, null);

            verify(courseRepository, never()).save(any());
            verify(commentCourseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Metoda: deleteCourse")
    class DeleteCourseTests {
        @Test
        @DisplayName("Powinna usunąć kurs, jeśli istnieje")
        void shouldDeleteIfExists() {
            when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

            courseService.deleteCourse(1L);

            verify(courseRepository, times(1)).delete(sampleCourse);
        }

        @Test
        @DisplayName("Nie powinna usuwać, jeśli kurs nie istnieje")
        void shouldNotDeleteIfNotExists() {
            when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

            courseService.deleteCourse(999L);

            verify(courseRepository, never()).delete(any(Course.class));
        }
    }

    @Nested
    @DisplayName("Metoda: instructorCreateNewCourse")
    class InstructorCreateNewCourseTests {
        @Test
        @DisplayName("Powinna wywołać createCourse i sendNotificationWhenInstructorCreateCourse")
        void shouldCallCreateAndNotify() {
            doNothing().when(notificationService).sendNotificationWhenInstructorCreateCourse(any(Course.class));

            courseService.instructorCreateNewCourse(sampleCourse);

            verify(courseRepository, times(1)).save(sampleCourse);
            verify(notificationService, times(1)).sendNotificationWhenInstructorCreateCourse(sampleCourse);
        }
    }
}
