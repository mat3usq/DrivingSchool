package com.driving.school;

import com.driving.school.model.Constants;
import com.driving.school.model.Course;
import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.MentorShipRepository;
import com.driving.school.service.MentorShipService;
import com.driving.school.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorShipServiceTest {

    @Mock
    private MentorShipRepository mentorShipRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MentorShipService mentorShipService;

    private SchoolUser sampleStudent;
    private SchoolUser sampleInstructor;
    private MentorShip sampleMentorShip;

    @BeforeEach
    void setUp() {
        sampleStudent = new SchoolUser();
        sampleStudent.setId(1L);
        sampleStudent.setName("StudentName");
        sampleStudent.setRoleName(Constants.STUDENT_ROLE);

        sampleInstructor = new SchoolUser();
        sampleInstructor.setId(2L);
        sampleInstructor.setName("InstructorName");
        sampleInstructor.setRoleName(Constants.INSTRUCTOR_ROLE);

        sampleMentorShip = new MentorShip();
        sampleMentorShip.setId(100L);
        sampleMentorShip.setStudent(sampleStudent);
        sampleMentorShip.setInstructor(sampleInstructor);
        sampleMentorShip.setStatus(Constants.PENDING);
    }

    @Nested
    @DisplayName("Metoda: findByStudentId")
    class FindByStudentIdTests {

        @Test
        @DisplayName("Powinna zwrócić listę mentorShip posortowaną po statusie dla danego studenta")
        void shouldReturnMentorShipsForStudent() {
            List<MentorShip> mentorShips = List.of(sampleMentorShip);
            when(mentorShipRepository.findByStudentIdOrderByStatusAsc(1L))
                    .thenReturn(mentorShips);

            List<MentorShip> result = mentorShipService.findByStudentId(1L);

            assertThat(result).isEqualTo(mentorShips);
            verify(mentorShipRepository, times(1))
                    .findByStudentIdOrderByStatusAsc(1L);
        }
    }

    @Nested
    @DisplayName("Metoda: findByInstructorId (bez paginacji)")
    class FindByInstructorIdTests {

        @Test
        @DisplayName("Powinna zwrócić listę mentorShip posortowaną po statusie dla danego instruktora")
        void shouldReturnMentorShipsForInstructor() {
            List<MentorShip> mentorShips = List.of(sampleMentorShip);
            when(mentorShipRepository.findByInstructorIdOrderByStatusAsc(2L))
                    .thenReturn(mentorShips);

            List<MentorShip> result = mentorShipService.findByInstructorId(2L);

            assertThat(result).isEqualTo(mentorShips);
            verify(mentorShipRepository, times(1))
                    .findByInstructorIdOrderByStatusAsc(2L);
        }
    }

    @Nested
    @DisplayName("Metoda: findByInstructorId (z paginacją)")
    class FindByInstructorIdWithPageableTests {

        @Test
        @DisplayName("Powinna zwrócić stronicowaną listę mentorShip dla instruktora")
        void shouldReturnPageOfMentorShips() {
            Pageable pageable = PageRequest.of(0, 2);
            Page<MentorShip> page = new PageImpl<>(List.of(sampleMentorShip));
            when(mentorShipRepository.findByInstructorIdOrderByStatusAsc(2L, pageable))
                    .thenReturn(page);

            Page<MentorShip> result = mentorShipService.findByInstructorId(2L, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).contains(sampleMentorShip);
        }
    }

    @Nested
    @DisplayName("Metoda: existsByStudentAndInstructor")
    class ExistsByStudentAndInstructorTests {

        @Test
        @DisplayName("Powinna zwrócić true/false w zależności od tego, czy MentorShip istnieje w repo")
        void shouldReturnBooleanIfMentorShipExists() {
            when(mentorShipRepository.existsByStudentAndInstructor(sampleStudent, sampleInstructor))
                    .thenReturn(true);

            boolean exists = mentorShipService.existsByStudentAndInstructor(sampleStudent, sampleInstructor);

            assertThat(exists).isTrue();
        }
    }

    @Nested
    @DisplayName("Metoda: createMentorShip(MentorShip)")
    class CreateMentorShipTests {

        @Test
        @DisplayName("Powinna zapisać nowy MentorShip w repozytorium")
        void shouldCreateNewMentorShip() {
            when(mentorShipRepository.save(any(MentorShip.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            MentorShip created = mentorShipService.createMentorShip(sampleMentorShip);

            assertThat(created).isEqualTo(sampleMentorShip);
            verify(mentorShipRepository, times(1)).save(sampleMentorShip);
        }
    }

    @Nested
    @DisplayName("Metoda: getMentorShipById")
    class GetMentorShipByIdTests {

        @Test
        @DisplayName("Powinna zwrócić Optional z MentorShip jeśli istnieje")
        void shouldReturnOptionalIfExists() {
            when(mentorShipRepository.findById(100L))
                    .thenReturn(Optional.of(sampleMentorShip));

            Optional<MentorShip> result = mentorShipService.getMentorShipById(100L);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(sampleMentorShip);
        }

        @Test
        @DisplayName("Powinna zwrócić pusty Optional, jeśli MentorShip nie istnieje")
        void shouldReturnEmptyIfNotExist() {
            when(mentorShipRepository.findById(999L))
                    .thenReturn(Optional.empty());

            Optional<MentorShip> result = mentorShipService.getMentorShipById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Metoda: getAllMentorShips")
    class GetAllMentorShipsTests {

        @Test
        @DisplayName("Powinna zwrócić wszystkie MentorShip z repozytorium")
        void shouldReturnAll() {
            List<MentorShip> all = List.of(sampleMentorShip);
            when(mentorShipRepository.findAll()).thenReturn(all);

            List<MentorShip> result = mentorShipService.getAllMentorShips();

            assertThat(result).containsExactly(sampleMentorShip);
        }
    }

    @Nested
    @DisplayName("Metoda: updateMentorShips")
    class UpdateMentorShipsTests {

        @Test
        @DisplayName("Powinna zaktualizować student i instruktora w MentorShip, jeśli istnieje")
        void shouldUpdateIfExists() {
            SchoolUser newStudent = new SchoolUser();
            newStudent.setId(10L);
            SchoolUser newInstructor = new SchoolUser();
            newInstructor.setId(20L);

            MentorShip details = new MentorShip();
            details.setStudent(newStudent);
            details.setInstructor(newInstructor);

            when(mentorShipRepository.findById(100L)).thenReturn(Optional.of(sampleMentorShip));
            when(mentorShipRepository.save(any(MentorShip.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            MentorShip updated = mentorShipService.updateMentorShips(100L, details);

            assertThat(updated.getStudent()).isEqualTo(newStudent);
            assertThat(updated.getInstructor()).isEqualTo(newInstructor);
            verify(mentorShipRepository, times(1)).save(sampleMentorShip);
        }

        @Test
        @DisplayName("Powinna rzucić RuntimeException, jeśli MentorShip nie istnieje")
        void shouldThrowIfNotExists() {
            when(mentorShipRepository.findById(999L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> mentorShipService.updateMentorShips(999L, sampleMentorShip))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("Metoda: deleteMentorShipById")
    class DeleteMentorShipByIdTests {

        @Test
        @DisplayName("Powinna usunąć MentorShip z repozytorium po ID")
        void shouldDeleteById() {
            doNothing().when(mentorShipRepository).deleteById(100L);

            mentorShipService.deleteMentorShipById(100L);

            verify(mentorShipRepository, times(1)).deleteById(100L);
        }
    }

    @Nested
    @DisplayName("Metoda: createMentorShipWithStatus")
    class CreateMentorShipWithStatusTests {

        @Test
        @DisplayName("Powinna stworzyć MentorShip w statusie PENDING, jeśli nie istnieje ACTIVE/PENDING między tymi userami")
        void shouldCreateIfNoActiveOrPendingExists() {
            when(mentorShipRepository.existsByStudentAndInstructorAndStatus(sampleStudent, sampleInstructor, Constants.ACTIVE))
                    .thenReturn(false);
            when(mentorShipRepository.existsByStudentAndInstructorAndStatus(sampleStudent, sampleInstructor, Constants.PENDING))
                    .thenReturn(false);
            when(mentorShipRepository.save(any(MentorShip.class))).thenAnswer(inv -> inv.getArgument(0));

            boolean result = mentorShipService.createMentorShipWithStatus(sampleStudent, sampleInstructor, Constants.PENDING);

            assertThat(result).isTrue();
            verify(mentorShipRepository, times(1)).save(any(MentorShip.class));
        }

        @Test
        @DisplayName("Powinna zwrócić false, jeśli istnieje ACTIVE lub PENDING Mentorship dla danej pary")
        void shouldReturnFalseIfExistsActiveOrPending() {
            when(mentorShipRepository.existsByStudentAndInstructorAndStatus(sampleStudent, sampleInstructor, Constants.ACTIVE))
                    .thenReturn(true);
            boolean result = mentorShipService.createMentorShipWithStatus(sampleStudent, sampleInstructor, Constants.PENDING);

            assertThat(result).isFalse();
            verify(mentorShipRepository, never()).save(any(MentorShip.class));
        }
    }

    @Nested
    @DisplayName("Metoda: getStudentCategories")
    class GetStudentCategoriesTests {

        @Test
        @DisplayName("Powinna zwrócić zbiory kategorii active i completed na podstawie MentorShip i Course")
        void shouldReturnActiveAndCompletedCategories() {
            sampleMentorShip.setStatus(Constants.ACTIVE);
            Course courseA = new Course();
            courseA.setId(200L);
            com.driving.school.model.Category catA = new com.driving.school.model.Category();
            catA.setNameCategory("A");
            courseA.setCategory(catA);

            Course courseB = new Course();
            courseB.setId(201L);
            com.driving.school.model.Category catB = new com.driving.school.model.Category();
            catB.setNameCategory("B");
            courseB.setCategory(catB);

            sampleMentorShip.getStudentCourses().add(courseA);
            sampleMentorShip.getStudentCourses().add(courseB);

            MentorShip completedMentorShip = new MentorShip();
            completedMentorShip.setStatus(Constants.COMPLETED);
            Course courseC = new Course();
            com.driving.school.model.Category catC = new com.driving.school.model.Category();
            catC.setNameCategory("C");
            courseC.setCategory(catC);
            completedMentorShip.getStudentCourses().add(courseC);

            when(mentorShipRepository.findByStudentIdOrderByStatusAsc(1L))
                    .thenReturn(List.of(sampleMentorShip, completedMentorShip));

            Map<String, Set<String>> result = mentorShipService.getStudentCategories(1L);

            assertThat(result).containsKeys("active", "completed");
            Set<String> activeSet = result.get("active");
            Set<String> completedSet = result.get("completed");

            assertThat(activeSet).containsExactlyInAnyOrder("A", "B");
            assertThat(completedSet).containsExactly("C");
        }
    }

    @Nested
    @DisplayName("Metoda: acceptStudent / finishMentorShip / backToActiveMentorShip")
    class ChangeMentorShipStatusTests {

        @Test
        @DisplayName("acceptStudent ustawia status na ACTIVE i zapisuje w repo")
        void shouldAcceptStudent() {
            when(mentorShipRepository.findById(100L))
                    .thenReturn(Optional.of(sampleMentorShip));

            mentorShipService.acceptStudent(100L);

            assertThat(sampleMentorShip.getStatus()).isEqualTo(Constants.ACTIVE);
            verify(mentorShipRepository, times(1)).save(sampleMentorShip);
        }

        @Test
        @DisplayName("finishMentorShip ustawia status na COMPLETED i endAt=now, po czym zapisuje w repo")
        void shouldFinishMentorShip() {
            when(mentorShipRepository.findById(100L))
                    .thenReturn(Optional.of(sampleMentorShip));
            mentorShipService.finishMentorShip(100L);

            assertThat(sampleMentorShip.getStatus()).isEqualTo(Constants.COMPLETED);
            assertThat(sampleMentorShip.getEndAt()).isNotNull();
            verify(mentorShipRepository, times(1)).save(sampleMentorShip);
        }

        @Test
        @DisplayName("backToActiveMentorShip ustawia status na ACTIVE i endAt=null")
        void shouldBackToActiveMentorShip() {
            sampleMentorShip.setEndAt(LocalDateTime.of(2020,1,1,12,0));
            when(mentorShipRepository.findById(100L))
                    .thenReturn(Optional.of(sampleMentorShip));

            mentorShipService.backToActiveMentorShip(100L);

            assertThat(sampleMentorShip.getStatus()).isEqualTo(Constants.ACTIVE);
            assertThat(sampleMentorShip.getEndAt()).isNull();
            verify(mentorShipRepository, times(1)).save(sampleMentorShip);
        }
    }


    @Nested
    @DisplayName("Metoda: studentCancelMentorshipWithInstructor")
    class StudentCancelMentorshipTests {

        @Test
        @DisplayName("Powinna usunąć MentorShip i wywołać notyfikację")
        void shouldDeleteAndNotify() {
            doNothing().when(mentorShipRepository).deleteById(100L);

            mentorShipService.studentCancelMentorshipWithInstructor(sampleMentorShip);

            verify(mentorShipRepository, times(1)).deleteById(100L);
            verify(notificationService, times(1))
                    .sendNotificationWhenStudentCancelMentorship(sampleStudent, sampleInstructor);
        }
    }







}
