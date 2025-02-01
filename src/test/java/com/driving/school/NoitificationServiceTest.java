package com.driving.school;

import com.driving.school.components.ReminderEventJob;
import com.driving.school.model.*;
import com.driving.school.repository.MentorShipRepository;
import com.driving.school.repository.NotificationRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private SchoolUserRepository schoolUserRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MentorShipRepository mentorShipRepository;
    @Mock
    private Scheduler scheduler;
    @Mock
    private Logger logger;

    @InjectMocks
    private NotificationService notificationService;

    private SchoolUser sampleUser;
    private Notification sampleNotification;
    private InstructionEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleUser = new SchoolUser();
        sampleUser.setId(1L);
        sampleUser.setName("TestUser");
        sampleUser.setNumberOfNotifications(0);

        sampleNotification = new Notification();
        sampleNotification.setId(100L);
        sampleNotification.setStatus(Constants.NOTIFICATION_NOT_SEEN);
        sampleNotification.setContent("Sample notification");
        sampleNotification.setSchoolUser(sampleUser);

        sampleEvent = new InstructionEvent();
        sampleEvent.setId(10L);
        sampleEvent.setSubject("New Meeting");
        sampleEvent.setEventType("TRAINING");
        sampleEvent.setAvailableEventSlots(2);
        sampleEvent.setEventCapacity(2);
        sampleEvent.setStartTime(LocalDateTime.now().plusDays(1));
        sampleEvent.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        sampleEvent.setInstructor(sampleUser); // user jako instruktor
    }

    @Nested
    @DisplayName("Metoda: getNotificationsByUser")
    class GetNotificationsByUserTests {

        @Test
        @DisplayName("Powinna zwrócić listę notyfikacji, ustawić status na SEEN i wyzerować liczbę powiadomień u użytkownika")
        void shouldReturnNotificationsAndMarkAsSeen() {
            sampleUser.setNotifications(List.of(sampleNotification));
            when(schoolUserRepository.save(any(SchoolUser.class))).thenAnswer(inv -> inv.getArgument(0));

            List<Notification> returned = notificationService.getNotificationsByUser(sampleUser);

            assertThat(returned).hasSize(1);
            assertThat(returned.get(0).getStatus()).isEqualTo(Constants.NOTIFICATION_NOT_SEEN);
            assertThat(sampleUser.getNotifications().get(0).getStatus()).isEqualTo(Constants.NOTIFICATION_SEEN);
            assertThat(sampleUser.getNumberOfNotifications()).isZero();
            verify(schoolUserRepository).save(sampleUser);
        }
    }

    @Nested
    @DisplayName("Metoda: scheduleReminderForEvent")
    class ScheduleReminderForEventTests {

        @Test
        @DisplayName("Powinna zaplanować przypomnienie 1 godzinę przed wydarzeniem, jeśli startTime > now() + 1h")
        void shouldScheduleReminder() throws SchedulerException {
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                    .thenReturn(new Date());

            notificationService.scheduleReminderForEvent(sampleEvent, sampleUser);

            verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
        }

        @Test
        @DisplayName("Jeśli reminderTime jest w przeszłości, wysyła powiadomienie natychmiast i nie planuje Jobu")
        void shouldSendNotificationImmediatelyIfTimePassed() throws SchedulerException {
            sampleEvent.setStartTime(LocalDateTime.now().plusMinutes(30)); // mało czasu => reminderTime = start-1h < now
            sampleEvent.setEndTime(LocalDateTime.now().plusMinutes(90));

            notificationService.scheduleReminderForEvent(sampleEvent, sampleUser);

            verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
        }
    }

    @Nested
    @DisplayName("Metoda: cancelReminderForEvent")
    class CancelReminderForEventTests {

        @Test
        @DisplayName("Powinna usunąć zaplanowany Job z schedulera, jeśli istnieje")
        void shouldCancelJobIfExists() throws SchedulerException {
            when(scheduler.deleteJob(any(JobKey.class))).thenReturn(true);

            notificationService.cancelReminderForEvent(10L, 1L);

            verify(scheduler, times(1)).deleteJob(any(JobKey.class));
        }
    }

    @Nested
    @DisplayName("Wybrane metody: sendNotificationWhenUserReceiveNewRole, sendNotificationWhenInstructorCreateNewLecture")
    class SelectedSendNotificationTests {

        @Test
        @DisplayName("Powinna wysłać powiadomienie o nowej roli ADMIN do użytkownika")
        void shouldNotifyNewRole() {
            sampleUser.setRoleName(Constants.ADMIN_ROLE);
            sampleUser.setName("TestUser");
            when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

            notificationService.sendNotificationWhenUserReceiveNewRole(sampleUser);

            verify(notificationRepository, times(1)).save(any(Notification.class));
            verify(schoolUserRepository, times(1)).save(sampleUser);
            assertThat(sampleUser.getNumberOfNotifications()).isEqualTo(1);
        }

        @Test
        @DisplayName("Powinna wysłać powiadomienie do wszystkich, gdy instruktor tworzy nowy lecture w swojej kategorii")
        void shouldNotifyAllUsersInCategory() {
            sampleUser.setCurrentCategory("B");
            SchoolUser otherUser = new SchoolUser();
            otherUser.setId(2L);
            otherUser.setCurrentCategory("B");
            otherUser.setName("Other");
            List<SchoolUser> usersInCategory = List.of(sampleUser, otherUser);
            when(schoolUserRepository.findUsersByCategoryName("B")).thenReturn(usersInCategory);

            notificationService.sendNotificationWhenInstructorCreateNewLecture(sampleUser);

            verify(notificationRepository, times(2)).save(any(Notification.class));
            verify(schoolUserRepository, times(2)).save(any(SchoolUser.class));
        }
    }
}
