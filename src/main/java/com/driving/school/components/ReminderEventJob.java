package com.driving.school.components;

import com.driving.school.model.InstructionEvent;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.InstructionEventRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.NotificationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ReminderEventJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ReminderEventJob.class);

    private final NotificationService notificationService;
    private final InstructionEventRepository instructionEventRepository;
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public ReminderEventJob(NotificationService notificationService,
                            InstructionEventRepository instructionEventRepository,
                            SchoolUserRepository schoolUserRepository) {
        this.notificationService = notificationService;
        this.instructionEventRepository = instructionEventRepository;
        this.schoolUserRepository = schoolUserRepository;
    }

    @Override
    @NonNull
    public void execute(@NonNull JobExecutionContext context) throws JobExecutionException {
        try {
            Long eventId = context.getJobDetail().getJobDataMap().getLong("eventId");
            Long userId = context.getJobDetail().getJobDataMap().getLong("userId");

            Optional<InstructionEvent> event = instructionEventRepository.findById(eventId);
            Optional<SchoolUser> user = schoolUserRepository.findById(userId);

            if (event.isPresent() && user.isPresent()) {
                notificationService.sendNotificationWhenStudentAssignedToMeetingButHourBefore(event.get(), user.get());
                logger.info("Notification sent for event ID: {} and user ID: {}", eventId, userId);
            } else {
                logger.warn("InstructionEvent or SchoolUser not found for IDs: {}, {}", eventId, userId);
            }
        } catch (Exception e) {
            logger.error("Error executing ReminderEventJob", e);
            throw new JobExecutionException("Error executing ReminderEventJob", e);
        }
    }
}
