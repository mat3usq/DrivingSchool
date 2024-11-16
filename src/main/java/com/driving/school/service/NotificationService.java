package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.Notification;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.NotificationRepository;
import com.driving.school.repository.SchoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private void createNotification(Notification notification){
        notificationRepository.save(notification);
    }

    public void sendNotificationWhenUserReceiveNewRole(SchoolUser schoolUser) {
        Notification n = new Notification();
        n.setSchoolUser(schoolUser);
        n.setStatus(Constants.NOTIFICATION_NOT_SEEN);

        String roleName = schoolUser.getRoleName();
        String benefits = switch (roleName) {
            case Constants.ADMIN_ROLE -> "zarządzania użytkownikami, dostępem do wszystkich zasobów oraz generowania raportów";
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
        n.setContent(content);

        createNotification(n);
    }
}
