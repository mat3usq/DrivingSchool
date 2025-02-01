
package com.driving.school;

import com.driving.school.model.Course;
import com.driving.school.model.DrivingSession;
import com.driving.school.repository.CourseRepository;
import com.driving.school.repository.DrivingSessionRepository;
import com.driving.school.service.DrivingSessionService;
import com.driving.school.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrivingSessionServiceTest {

    @Mock
    private DrivingSessionRepository drivingSessionRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DrivingSessionService drivingSessionService;

    private DrivingSession sampleDrivingSession;
    private Course sampleCourse;

    @BeforeEach
    void setUp() {
        sampleCourse = new Course();
        sampleCourse.setId(1L);
        sampleCourse.setSummaryDurationHours(0.0);

        sampleDrivingSession = new DrivingSession();
        sampleDrivingSession.setId(100L);
        sampleDrivingSession.setSessionDate(LocalDateTime.now());
        sampleDrivingSession.setDurationHours(2.5);
        sampleDrivingSession.setInstructorComment("Komentarz instruktora");
        sampleDrivingSession.setCourse(sampleCourse);
    }

    @Nested
    @DisplayName("Metoda: createDrivingSession")
    class CreateDrivingSessionTests {

        @Test
        @DisplayName("Powinna zapisać nową sesję jazdy oraz zaktualizować sumę godzin w kursie")
        void shouldCreateDrivingSessionAndUpdateCourseHours() {
            when(drivingSessionRepository.save(any(DrivingSession.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            assertThat(sampleCourse.getSummaryDurationHours()).isZero();

            drivingSessionService.createDrivingSession(sampleDrivingSession, sampleCourse);

            verify(drivingSessionRepository, times(1)).save(sampleDrivingSession);
            verify(courseRepository, times(1)).save(sampleCourse);
        }
    }

    @Nested
    @DisplayName("Metoda: getDrivingSessionById")
    class GetDrivingSessionByIdTests {

        @Test
        @DisplayName("Powinna zwrócić opcjonalną DrivingSession, jeśli istnieje w repozytorium")
        void shouldReturnOptionalWhenFound() {
            when(drivingSessionRepository.findById(100L))
                    .thenReturn(Optional.of(sampleDrivingSession));

            Optional<DrivingSession> result = drivingSessionService.getDrivingSessionById(100L);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(sampleDrivingSession);
        }

        @Test
        @DisplayName("Powinna zwrócić pusty Optional, jeśli sesja nie istnieje")
        void shouldReturnEmptyIfNotFound() {
            when(drivingSessionRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            Optional<DrivingSession> result = drivingSessionService.getDrivingSessionById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Metoda: getAllDrivingSessions")
    class GetAllDrivingSessionsTests {

        @Test
        @DisplayName("Powinna zwrócić listę wszystkich DrivingSession z repozytorium")
        void shouldReturnAllDrivingSessions() {
            List<DrivingSession> sessions = List.of(sampleDrivingSession, new DrivingSession());
            when(drivingSessionRepository.findAll()).thenReturn(sessions);

            List<DrivingSession> result = drivingSessionService.getAllDrivingSessions();

            assertThat(result).hasSize(2);
            verify(drivingSessionRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Metoda: updateDrivingSession")
    class UpdateDrivingSessionTests {

        @Test
        @DisplayName("Powinna zaktualizować istniejącą sesję jazdy i przeliczyć sumę godzin w kursie")
        void shouldUpdateSessionAndRecalculateSummaryHours() {
            when(drivingSessionRepository.findById(100L))
                    .thenReturn(Optional.of(sampleDrivingSession));

            DrivingSession updatedDetails = new DrivingSession();
            updatedDetails.setDurationHours(3.0);
            updatedDetails.setInstructorComment("Nowy komentarz");

            sampleCourse.setSummaryDurationHours(5.0);

            drivingSessionService.updateDrivingSession(100L, updatedDetails);

            assertThat(sampleDrivingSession.getDurationHours()).isEqualTo(3.0);
            assertThat(sampleDrivingSession.getInstructorComment()).isEqualTo("Nowy komentarz");

            verify(drivingSessionRepository, times(1)).save(sampleDrivingSession);
            verify(courseRepository, times(1)).save(sampleCourse);

        }

        @Test
        @DisplayName("Nie powinna nic robić, jeśli DrivingSession o danym ID nie istnieje")
        void shouldDoNothingIfNotExists() {
            when(drivingSessionRepository.findById(999L)).thenReturn(Optional.empty());

            DrivingSession updatedDetails = new DrivingSession();
            updatedDetails.setDurationHours(1.5);

            drivingSessionService.updateDrivingSession(999L, updatedDetails);

            verify(drivingSessionRepository, never()).save(any(DrivingSession.class));
            verify(courseRepository, never()).save(any(Course.class));
        }
    }

    @Nested
    @DisplayName("Metoda: deleteDrivingSession")
    class DeleteDrivingSessionTests {

        @Test
        @DisplayName("Powinna usunąć sesję jazdy i zaktualizować sumę godzin w kursie")
        void shouldDeleteSessionAndUpdateCourseHours() {
            sampleCourse.setSummaryDurationHours(10.0); // kurs ma 10h w sumie
            when(drivingSessionRepository.findById(100L))
                    .thenReturn(Optional.of(sampleDrivingSession));

            drivingSessionService.deleteDrivingSession(100L);

            verify(drivingSessionRepository, times(1)).delete(sampleDrivingSession);
            verify(courseRepository, times(1)).save(sampleCourse);

            assertThat(sampleCourse.getSummaryDurationHours()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Nie powinna nic robić, jeśli sesja o danym ID nie istnieje")
        void shouldDoNothingIfSessionNotExists() {
            when(drivingSessionRepository.findById(999L)).thenReturn(Optional.empty());

            drivingSessionService.deleteDrivingSession(999L);

            verify(drivingSessionRepository, never()).delete(any());
            verify(courseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Metoda: instructorCreateDrivingSession")
    class InstructorCreateDrivingSessionTests {

        @Test
        @DisplayName("Powinna wywołać createDrivingSession i powiadomić instruktora o nowej sesji")
        void shouldCreateAndNotify() {
            when(drivingSessionRepository.save(any(DrivingSession.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            drivingSessionService.instructorCreateDrivingSession(sampleDrivingSession, sampleCourse);

            verify(drivingSessionRepository, times(1)).save(sampleDrivingSession);
            verify(notificationService, times(1))
                    .sendNotificationWhenInstructorCreateDrivingSession(sampleDrivingSession);
            verify(courseRepository, times(1)).save(sampleCourse);

        }
    }
}
