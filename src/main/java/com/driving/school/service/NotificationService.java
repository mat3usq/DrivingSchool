package com.driving.school.service;

import com.driving.school.model.*;
import com.driving.school.repository.MentorShipRepository;
import com.driving.school.repository.NotificationRepository;
import com.driving.school.repository.SchoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final SchoolUserRepository schoolUserRepository;
    private final NotificationRepository notificationRepository;
    private final MentorShipRepository mentorShipRepository;

    @Autowired
    public NotificationService(SchoolUserRepository schoolUserRepository,
                               NotificationRepository notificationRepository, MentorShipRepository mentorShipRepository) {
        this.schoolUserRepository = schoolUserRepository;
        this.notificationRepository = notificationRepository;
        this.mentorShipRepository = mentorShipRepository;
    }

    private void createNotification(SchoolUser schoolUser, String content) {
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setSchoolUser(schoolUser);
        notification.setStatus(Constants.NOTIFICATION_NOT_SEEN);
        schoolUser.setNumberOfNotifications(schoolUser.getNumberOfNotifications() + 1);
        notificationRepository.save(notification);
        schoolUserRepository.save(schoolUser);
    }

    public List<Notification> getNotificationsByUser(SchoolUser user) {
        List<Notification> originalNotifications = user.getNotifications();
        List<Notification> notificationsToReturn = originalNotifications.stream()
                .map(notification -> new Notification(
                        notification.getContent(),
                        notification.getStatus(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());

        originalNotifications.forEach(notification -> notification.setStatus(Constants.NOTIFICATION_SEEN));

        if (!originalNotifications.isEmpty()) {
            user.setNotifications(originalNotifications);
            user.setNumberOfNotifications(0);
            schoolUserRepository.save(user);
        }

        return notificationsToReturn;
    }

    public void sendNotificationWhenUserReceiveNewRole(SchoolUser schoolUser) {
        String roleName = schoolUser.getRoleName();
        String benefits = switch (roleName) {
            case Constants.ADMIN_ROLE ->
                    "zarządzania użytkownikami, dostępem do wszystkich zasobów oraz generowania raportów";
            case Constants.INSTRUCTOR_ROLE ->
                    "tworzenia i zarządzania materiałami edukacyjnymi, organizowania kursów oraz prowadzenia spotkań z uczniami";
            case Constants.STUDENT_ROLE ->
                    "dostępu do materiałów edukacyjnych, uczestnictwa w zajęciach praktycznych oraz monitorowania swoich postępów";
            default -> "korzystania z dostępnych funkcji w systemie";
        };

        String content = String.format(
                "🎉 Gratulacje, %s!\n\nOtrzymałeś nową rolę %s🚔. Twoje nowe uprawnienia pozwolą Ci na %s.\n\nŻyczymy powodzenia!",
                schoolUser.getName(),
                roleName,
                benefits
        );

        createNotification(schoolUser, content);
    }

    public void sendNotificationWhenUserReceivePayment(Payment payment) {
        SchoolUser user = payment.getSchoolUser();
        List<Category> categories = payment.getCategories();
        String categoryNames = categories.stream()
                .map(Category::getNameCategory)
                .collect(Collectors.joining(", "));

        String content = String.format(
                "💰 Gratulacje, %s!\n\nOtrzymałeś nową płatność o komenatrzu \"%s\".\nKwota: %.2f PLN\nKategorie: %s\nID Płatności: %d\n\nDziękujemy za korzystanie z naszego systemu!",
                user.getName(),
                payment.getComment(),
                payment.getSum(),
                categoryNames,
                payment.getId()
        );

        createNotification(user, content);
    }

    public void sendNotificationWhenStudentAssignsToInstructor(SchoolUser student, SchoolUser instructor) {
        String contentForStudent = String.format(
                "📩 Witaj, %s!\n\nTwoja prośba o współpracę została wysłana pomyślnie do instruktora %s <%s>. Oczekuj na odpowiedź ze strony instruktora.\n\n",
                student.getName(),
                instructor.getName(),
                instructor.getEmail()
        );

        String contentForInstructor = String.format(
                "📩 Witaj, %s!\n\nOtrzymałeś nową prośbę o współpracę od studenta %s <%s>. Prosimy o rozpatrzenie i odpowiedź na prośbę.\n\nDziękujemy za zaangażowanie!",
                instructor.getName(),
                student.getName(),
                student.getEmail()
        );

        createNotification(student, contentForStudent);
        createNotification(instructor, contentForInstructor);
    }

    public void sendNotificationWhenStudentCancelMentorship(SchoolUser student, SchoolUser instructor) {
        String contentForStudent = String.format(
                "📩 Witaj, %s!\n\nPomyślnie anulowałeś współpracę z instruktorem %s <%s>.\nJeśli zmienisz zdanie, zawsze możesz ponownie wysłać prośbę o współpracę.\n\nPozdrawiamy!",
                student.getName(),
                instructor.getName(),
                instructor.getEmail()
        );

        String contentForInstructor = String.format(
                "📩 Witaj, %s!\n\nStudent %s <%s> anulował współpracę z Tobą.\nJeśli masz pytania lub potrzebujesz dodatkowych informacji, skontaktuj się z administracją.\n\nDziękujemy za zaangażowanie!",
                instructor.getName(),
                student.getName(),
                student.getEmail()
        );

        createNotification(student, contentForStudent);
        createNotification(instructor, contentForInstructor);
    }

    public void sendNotificationWhenInstructorCreateMentorshipWithStudent(SchoolUser student, SchoolUser instructor) {
        String contentForInstructor = String.format(
                "📩 Witaj, %s!\n\nPomyślnie utworzyłeś współpracę ze studentem %s <%s>.\nMożesz teraz rozpocząć współpracę i wspierać swojego studenta tworzac kurs oraz nadzorowac postepy w nauce.\n\nDziękujemy za zaangażowanie!",
                instructor.getName(),
                student.getName(),
                student.getEmail()
        );

        String contentForStudent = String.format(
                "📩 Witaj, %s!\n\nInstruktor %s <%s> przypisał Cię do siebie.\nMożesz teraz rozpocząć współpracę i korzystać z wsparcia swojego nauczyciela.\n\nPowodzenia!",
                student.getName(),
                instructor.getName(),
                instructor.getEmail()
        );

        createNotification(student, contentForStudent);
        createNotification(instructor, contentForInstructor);
    }

    public void sendNotificationWhenInstructorAcceptMentorshipWithStudent(SchoolUser student, SchoolUser instructor) {
        String contentForStudent = String.format(
                "📩 Witaj, %s!\n\nInstruktor %s <%s> zaakceptował Twoją prośbę o współpracę.\nMożesz teraz rozpocząć współpracę i korzystać z wsparcia swojego nauczyciela.\n\nPowodzenia!",
                student.getName(),
                instructor.getName(),
                instructor.getEmail()
        );

        String contentForInstructor = String.format(
                "📩 Witaj, %s!\n\nZaakceptowałeś współpracę mentoringową ze studentem %s <%s>.\nMożesz teraz rozpocząć współpracę i wspierać swojego studenta.\n\nDziękujemy za zaangażowanie!",
                instructor.getName(),
                student.getName(),
                student.getEmail()
        );

        createNotification(student, contentForStudent);
        createNotification(instructor, contentForInstructor);
    }

    public void sendNotificationWhenInstructorCancelMentorshipWithStudent(SchoolUser student, SchoolUser instructor) {
        String contentForInstructor = String.format(
                "📩 Witaj, %s!\n\nPomyślnie anulowałeś współpracę ze studentem %s <%s>.\nJeśli zmienisz zdanie, zawsze możesz ponownie nawiązać współpracę.\n\nPozdrawiamy!",
                instructor.getName(),
                student.getName(),
                student.getEmail()
        );

        String contentForStudent = String.format(
                "📩 Witaj, %s!\n\nInstruktor %s <%s> anulował współpracę z Tobą.\nJeśli masz pytania lub potrzebujesz dodatkowych informacji, skontaktuj się z nim.\n\nDziękujemy za zrozumienie!",
                student.getName(),
                instructor.getName(),
                instructor.getEmail()
        );

        createNotification(instructor, contentForInstructor);
        createNotification(student, contentForStudent);
    }

    public void sendNotificationWhenInstructorFinishMentorshipWithStudent(SchoolUser student, SchoolUser instructor) {
        String contentForInstructor = String.format(
                "📩 Witaj, %s!\n\nZakończyłeś współpracę ze studentem %s <%s>.\nMamy nadzieję, że współpraca była owocna.\nJeśli masz uwagi lub sugestie, prosimy o kontakt z administracją.\n\nDziękujemy za Twój wkład!",
                instructor.getName(),
                student.getName(),
                student.getEmail()
        );

        String contentForStudent = String.format(
                "📩 Witaj, %s!\n\nInstruktor %s <%s> zakończył współpracę z Tobą.\nMamy nadzieję, że był to dla Ciebie wartościowy czas.\nJeśli masz pytania lub potrzebujesz dalszego wsparcia, skontaktuj się z ze swoim instruktorem.\n\nŻyczymy dalszych sukcesów!",
                student.getName(),
                instructor.getName(),
                instructor.getEmail()
        );

        createNotification(instructor, contentForInstructor);
        createNotification(student, contentForStudent);
    }

    public void sendNotificationWhenInstructorMakeMentorshipActiveAgainWithStudent(SchoolUser student, SchoolUser instructor) {
        String contentForInstructor = String.format(
                "📩 Witaj, %s!\n\nPomyślnie reaktywowałeś współpracę ze studentem %s <%s>.\nMożesz teraz kontynuować współpracę i wspierać swojego studenta.\n\nDziękujemy za zaangażowanie!",
                instructor.getName(),
                student.getName(),
                student.getEmail()
        );

        String contentForStudent = String.format(
                "📩 Witaj, %s!\n\nInstruktor %s <%s> reaktywował Waszą współpracę.\nMożesz teraz kontynuować współpracę i korzystać ze wsparcia swojego nauczyciela.\n\nPowodzenia!",
                student.getName(),
                instructor.getName(),
                instructor.getEmail()
        );

        createNotification(instructor, contentForInstructor);
        createNotification(student, contentForStudent);
    }

    public void sendNotificationWhenInstructorCreateCourse(Course course) {
        MentorShip mentorShip = course.getMentorShip();
        SchoolUser instructor = mentorShip.getInstructor();
        SchoolUser student = mentorShip.getStudent();

        String contentForInstructor = String.format(
                """
                        📩 Witaj, %s!

                        Pomyślnie utworzyłeś nowy kurs.

                        **Szczegóły Kursu:*
                        - Opis: %s
                        - Kategoria: %s
                        - Czas trwania: %.2f godzin

                        Możesz rowniez zarządzać sesjami jazdy, testami oraz komentarzami związanymi z tym kursem.

                        Dziękujemy za Twoje zaangażowanie!""",
                instructor.getName(),
                course.getDescription(),
                course.getCategory(),
                course.getDuration()
        );

        String contentForStudent = String.format(
                """
                        📩 Witaj, %s!

                        Instruktor %s <%s> własnie utworzył dla ciebie nowy kurs, który jest przypisany do waszej wspołpracy.

                        **Szczegóły Kursu:**
                        - Opis: %s
                        - Kategoria: %s
                        - Czas trwania: %.2f godzin

                        Możesz teraz uczestniczyć w sesjach jazdy, testach oraz dodawać komentarze związane z tym kursem.

                        Życzymy powodzenia!""",
                student.getName(),
                instructor.getName(),
                instructor.getEmail(),
                course.getDescription(),
                course.getCategory(),
                course.getDuration()
        );

        createNotification(instructor, contentForInstructor);
        createNotification(student, contentForStudent);
    }

    public void sendNotificationWhenInstructorCreateDrivingSession(DrivingSession drivingSession) {
        Course course = drivingSession.getCourse();
        MentorShip mentorShip = course.getMentorShip();
        SchoolUser instructor = mentorShip.getInstructor();
        SchoolUser student = mentorShip.getStudent();

        String contentForInstructor = String.format(
                """
                        📩 Witaj, %s!

                        Utworzyłeś nową sesję jazdy do kursu o komenatrzu: "%s" dla studenta %s <%s>.

                        **Szczegóły Sesji Jazdy:**
                        - Czas trwania: %.2f godzin
                        - Komentarz: "%s"

                        Możesz teraz zarządzać tą sesją jazdy oraz monitorować postępy studenta.

                        Dziękujemy za Twoje zaangażowanie!""",
                instructor.getName(),
                course.getDescription(),
                student.getName(),
                student.getEmail(),
                drivingSession.getDurationHours(),
                drivingSession.getInstructorComment()
        );

        String contentForStudent = String.format(
                """
                        📩 Witaj, %s!

                        Instruktor %s <%s> dodal nową sesję jazdy przypisaną do Twojego kursu o komentarzu: "%s".

                        **Szczegóły Sesji Jazdy:**
                        - Czas trwania: %.2f godzin
                        - Komentarz Instruktora: "%s"

                        Życzymy powodzenia!""",
                student.getName(),
                instructor.getName(),
                instructor.getEmail(),
                course.getDescription(),
                drivingSession.getDurationHours(),
                drivingSession.getInstructorComment()
        );

//        createNotification(instructor, contentForInstructor);
        createNotification(student, contentForStudent);
    }

    public void sendNotificationWhenInstructorCreateTestCourse(TestCourse testCourse) {
        Course course = testCourse.getCourse();
        MentorShip mentorShip = course.getMentorShip();
        SchoolUser instructor = mentorShip.getInstructor();
        SchoolUser student = mentorShip.getStudent();

        String contentForInstructor = String.format(
                """
                        📩 Witaj, %s!
                                                
                        Utworzyłeś nowy wyniki testu w kursie o komenatrzu: "%s" dla studenta %s <%s>.

                        **Szczegóły Testu:**
                        - Typ Testu: %s
                        - Wynik Testu: %.2f
                        - Komentarz: "%s"
                            
                        Możesz teraz zarządzać wynikami testów oraz monitorować postępy studenta.
                            
                        Dziękujemy za Twoje zaangażowanie!""",
                instructor.getName(),
                course.getDescription(),
                student.getName(),
                student.getEmail(),
                testCourse.getTestType(),
                testCourse.getTestResult(),
                testCourse.getInstructorComment()
        );

        String contentForStudent = String.format(
                """
                        📩 Witaj, %s!
                            
                        Instruktor %s <%s> dodał nowy wynik testu przypisany do Twojego kursu o komenatrzu: "%s".
                            
                        **Szczegóły Testu:**
                        - Typ Testu: %s
                        - Wynik Testu: %.2f
                        - Komentarz Instruktora: "%s"
                                               
                        Życzymy powodzenia!""",
                student.getName(),
                instructor.getName(),
                instructor.getEmail(),
                course.getDescription(),
                testCourse.getTestType(),
                testCourse.getTestResult(),
                testCourse.getInstructorComment()
        );

//        createNotification(instructor, contentForInstructor);
        createNotification(student, contentForStudent);
    }

    public void sendNotificationWhenInstructorCreateNewLecture(SchoolUser user) {
        List<SchoolUser> usersToNotify = schoolUserRepository.findUsersByCategoryName(user.getCurrentCategory());

        String content = String.format(
                """
                        📩 Witaj!
                                
                        Instruktor %s <%s> dodał nowy dział o kategorii "%s". Możesz teraz przeglądać nowy materiał w sekcji wykładów.
                                
                        Życzymy owocnej Nauki!
                        """,
                user.getName(),
                user.getEmail(),
                user.getCurrentCategory()
        );

        for (SchoolUser notifyUser : usersToNotify)
            createNotification(notifyUser, content);
    }

    public void sendNotificationWhenInstructorCreateNewEvent(InstructionEvent event) {
        List<MentorShip> mentorShips = mentorShipRepository.findAllByInstructorAndStatus(event.getInstructor(), Constants.ACTIVE);
        SchoolUser instructor = event.getInstructor();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
        String formattedStartTime = event.getStartTime().format(formatter);
        String formattedEndTime = event.getEndTime().format(formatter);

        for (MentorShip mentorShip : mentorShips) {
            SchoolUser student = mentorShip.getStudent();

            String contentForStudent = String.format(
                    """
                            📩 Witaj, %s!
    
                            Instruktor %s <%s> dodał nowe spotkanie.
    
                            **Szczegóły Wydarzenia:**
                            - Temat: "%s"
                            - Typ Wydarzenia: "%s"
                            - Rozpoczęcie: %s
                            - Zakończenie: %s
                            - Dostępne miejsca: %d
    
                            Zachęcamy do udziału w wydarzeniu. Prosimy o zapisanie się i punktualne stawienie się na umówione miejsce.
    
                            Życzymy powodzenia!
                            """,
                    student.getName(),
                    instructor.getName(),
                    instructor.getEmail(),
                    event.getSubject(),
                    event.getEventType(),
                    formattedStartTime,
                    formattedEndTime,
                    event.getAvailableEventSlots()
            );


            createNotification(student, contentForStudent);
        }
    }

}
