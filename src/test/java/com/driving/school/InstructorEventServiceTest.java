package com.driving.school;

import com.driving.school.model.Constants;
import com.driving.school.model.InstructionEvent;
import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.InstructionEventRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.InstructorEventService;
import com.driving.school.service.MentorShipService;
import com.driving.school.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorEventServiceTest {

    @Mock
    private InstructionEventRepository instructionEventRepository;
    @Mock
    private SchoolUserRepository schoolUserRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MentorShipService mentorShipService;

    @InjectMocks
    private InstructorEventService instructorEventService;

    private InstructionEvent sampleEvent;
    private SchoolUser sampleInstructor;
    private SchoolUser sampleStudent;

    @BeforeEach
    void setUp() {
        sampleInstructor = new SchoolUser();
        sampleInstructor.setId(10L);
        sampleInstructor.setRoleName(Constants.INSTRUCTOR_ROLE);
        sampleInstructor.setName("Jan");
        sampleInstructor.setSurname("Kowalski");

        sampleStudent = new SchoolUser();
        sampleStudent.setId(20L);
        sampleStudent.setRoleName(Constants.STUDENT_ROLE);
        sampleStudent.setName("Anna");
        sampleStudent.setSurname("Nowak");

        sampleEvent = new InstructionEvent();
        sampleEvent.setId(1L);
        sampleEvent.setSubject("Event subject");
        sampleEvent.setEventType("TRAINING");
        sampleEvent.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        sampleEvent.setEndTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        sampleEvent.setInstructor(sampleInstructor);
        sampleEvent.setEventCapacity(2);
        sampleEvent.setAvailableEventSlots(2);
        sampleEvent.setStudents(new ArrayList<>());
    }

    @Nested
    @DisplayName("Metoda: findById")
    class FindByIdTests {

        @Test
        @DisplayName("Powinna zwrócić zdarzenie, gdy istnieje w repozytorium")
        void shouldReturnEventIfExists() {
            when(instructionEventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

            InstructionEvent result = instructorEventService.findById(1L);

            assertThat(result).isEqualTo(sampleEvent);
        }

        @Test
        @DisplayName("Powinna zwrócić null, gdy nie istnieje w repozytorium")
        void shouldReturnNullIfNotExist() {
            when(instructionEventRepository.findById(anyLong())).thenReturn(Optional.empty());

            InstructionEvent result = instructorEventService.findById(999L);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: getInstructionEventById")
    class GetInstructionEventByIdTests {

        @Test
        @DisplayName("Powinna zwrócić Optional zawierający zdarzenie, jeśli istnieje")
        void shouldReturnOptionalWhenExists() {
            when(instructionEventRepository.findById(1L))
                    .thenReturn(Optional.of(sampleEvent));

            Optional<InstructionEvent> result = instructorEventService.getInstructionEventById(1L);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(sampleEvent);
        }

        @Test
        @DisplayName("Powinna zwrócić Optional.empty() jeśli zdarzenie nie istnieje")
        void shouldReturnEmptyIfNotExist() {
            when(instructionEventRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            Optional<InstructionEvent> result = instructorEventService.getInstructionEventById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Metoda: getAllInstructionEvents")
    class GetAllInstructionEventsTests {

        @Test
        @DisplayName("Powinna zwrócić listę wszystkich zdarzeń z repozytorium")
        void shouldReturnAllEvents() {
            List<InstructionEvent> eventList = List.of(sampleEvent, new InstructionEvent());
            when(instructionEventRepository.findAll()).thenReturn(eventList);

            List<InstructionEvent> result = instructorEventService.getAllInstructionEvents();

            assertThat(result).hasSize(2);
            verify(instructionEventRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Metoda: updateInstructionEvent")
    class UpdateInstructionEventTests {



        @Test
        @DisplayName("Powinna wyrzucić wyjątek, jeśli zdarzenie o danym ID nie istnieje")
        void shouldThrowIfEventNotExist() {
            when(instructionEventRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> instructorEventService.updateInstructionEvent(999L, sampleEvent)
            );
            assertThat(ex.getMessage()).contains("not found");
        }
    }

    @Nested
    @DisplayName("Metoda: deleteInstructionEvent")
    class DeleteInstructionEventTests {

        @Test
        @DisplayName("Powinna usunąć zdarzenie po ID")
        void shouldDeleteEvent() {
            doNothing().when(instructionEventRepository).deleteById(1L);

            instructorEventService.deleteInstructionEvent(1L);

            verify(instructionEventRepository, times(1)).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Metoda: findInstructionEventsByTimeRange")
    class FindInstructionEventsByTimeRangeTests {

        @Test
        @DisplayName("Powinna zwrócić wydarzenia w określonym przedziale czasu")
        void shouldReturnEventsWithinTimeRange() {
            LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2025, 1, 31, 23, 59);
            when(instructionEventRepository.findByStartTimeBetween(start, end))
                    .thenReturn(List.of(sampleEvent));

            List<InstructionEvent> result = instructorEventService.findInstructionEventsByTimeRange(start, end);

            assertThat(result).containsExactly(sampleEvent);
            verify(instructionEventRepository, times(1))
                    .findByStartTimeBetween(start, end);
        }
    }

    @Nested
    @DisplayName("Metoda: findInstructionEventsByTimeRangeAndInstructor")
    class FindInstructionEventsByTimeRangeAndInstructorTests {

        @Test
        @DisplayName("Powinna zwrócić wydarzenia instruktora w określonym przedziale czasu")
        void shouldReturnInstructorEventsWithinTimeRange() {
            Long instructorId = 10L;
            LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2025, 1, 31, 23, 59);

            when(instructionEventRepository.findByStartTimeBetweenAndInstructorId(start, end, instructorId))
                    .thenReturn(List.of(sampleEvent));

            List<InstructionEvent> result =
                    instructorEventService.findInstructionEventsByTimeRangeAndInstructor(start, end, instructorId);

            assertThat(result).contains(sampleEvent);
            verify(instructionEventRepository, times(1))
                    .findByStartTimeBetweenAndInstructorId(start, end, instructorId);
        }
    }

    @Nested
    @DisplayName("Metoda: addStudentToInstructionEvent")
    class AddStudentToInstructionEventTests {

        @Test
        @DisplayName("Powinna dodać studenta do wydarzenia, jeśli oba istnieją")
        void shouldAddStudent() {
            when(instructionEventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
            when(schoolUserRepository.findById(20L)).thenReturn(Optional.of(sampleStudent));

            assertThat(sampleEvent.getStudents()).isEmpty();

            instructorEventService.addStudentToInstructionEvent(1L, 20L);

            assertThat(sampleEvent.getStudents()).contains(sampleStudent);
            verify(instructionEventRepository, times(1)).save(sampleEvent);
        }

        @Test
        @DisplayName("Powinna rzucić wyjątek, gdy event lub student nie istnieją")
        void shouldThrowIfEventOrStudentNotExists() {
            when(instructionEventRepository.findById(1L)).thenReturn(Optional.empty());
            when(schoolUserRepository.findById(20L)).thenReturn(Optional.of(sampleStudent));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> instructorEventService.addStudentToInstructionEvent(1L, 20L));
            assertThat(ex.getMessage()).contains("not found");
        }
    }

    @Nested
    @DisplayName("Metoda: removeStudentFromInstructionEvent")
    class RemoveStudentFromInstructionEventTests {

        @Test
        @DisplayName("Powinna usunąć studenta z wydarzenia i zaktualizować availableEventSlots")
        void shouldRemoveStudentAndIncrementSlots() {
            sampleEvent.getStudents().add(sampleStudent);
            sampleEvent.setAvailableEventSlots(1);

            when(instructionEventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
            when(schoolUserRepository.findById(20L)).thenReturn(Optional.of(sampleStudent));

            instructorEventService.removeStudentFromInstructionEvent(1L, 20L);

            assertThat(sampleEvent.getStudents()).doesNotContain(sampleStudent);
            assertThat(sampleEvent.getAvailableEventSlots()).isEqualTo(2);
            verify(instructionEventRepository, times(1)).save(sampleEvent);
        }

        @Test
        @DisplayName("Powinna rzucić wyjątek, jeśli event albo student nie istnieją")
        void shouldThrowIfEventOrStudentNotExists() {
            when(instructionEventRepository.findById(1L)).thenReturn(Optional.empty());
            when(schoolUserRepository.findById(20L)).thenReturn(Optional.of(sampleStudent));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> instructorEventService.removeStudentFromInstructionEvent(1L, 20L));
            assertThat(ex.getMessage()).contains("not found");
        }
    }

    @Nested
    @DisplayName("Metoda: getEvents")
    class GetEventsTests {

        @Mock
        private Authentication authentication;



        @Test
        @DisplayName("Instructor powinien otrzymać wszystkie wydarzenia w swoim przedziale czasowym")
        void shouldReturnEventsForInstructor() {
            YearMonth yearMonth = YearMonth.of(2025, 1);
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"));

            doReturn(authorities).when(authentication).getAuthorities();

            LocalDateTime startOfMonth = LocalDateTime.of(2025,1,1,0,0);
            LocalDateTime endOfMonth = LocalDateTime.of(2025,1,31,23,59,59);

            when(instructionEventRepository
                    .findByStartTimeBetweenAndInstructorId(startOfMonth, endOfMonth, sampleInstructor.getId()))
                    .thenReturn(List.of(sampleEvent));

            List<InstructionEvent> result = instructorEventService.getEvents(sampleInstructor, yearMonth, authentication);

            assertThat(result).containsExactly(sampleEvent);
            assertThat(result.get(0).getIsAssigned()).isFalse();
        }

        @Test
        @DisplayName("Admin powinien otrzymać wydarzenia z dowolnego instruktora w przedziale czasowym")
        void shouldReturnAllEventsForAdmin() {
            YearMonth yearMonth = YearMonth.of(2025, 1);
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

            doReturn(authorities).when(authentication).getAuthorities();

            LocalDateTime startOfMonth = LocalDateTime.of(2025,1,1,0,0);
            LocalDateTime endOfMonth = LocalDateTime.of(2025,1,31,23,59,59);

            when(instructionEventRepository.findByStartTimeBetween(startOfMonth, endOfMonth))
                    .thenReturn(List.of(sampleEvent));

            List<InstructionEvent> result = instructorEventService.getEvents(sampleInstructor, yearMonth, authentication);

            assertThat(result).containsExactly(sampleEvent);
        }
    }
}
