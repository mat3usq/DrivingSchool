package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.Notification;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public NotificationService(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    private void createNotification(SchoolUser schoolUser, String content) {
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setSchoolUser(schoolUser);
        notification.setStatus(Constants.NOTIFICATION_NOT_SEEN);
        schoolUser.setNumberOfNotifications(schoolUser.getNumberOfNotifications() + 1);
        schoolUser.getNotifications().add(notification);
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
}
