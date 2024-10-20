package com.driving.school.components;

import com.driving.school.service.TestStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatisticsScheduler {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsScheduler.class);
    private final TestStatisticsService testStatisticsService;

    @Autowired
    public StatisticsScheduler(TestStatisticsService testStatisticsService) {
        this.testStatisticsService = testStatisticsService;
    }

    @Scheduled(cron = "0 * * * * *") // co pelna minute
//    @Scheduled(cron = "0 0 * * * *") // co pełną godzinę
//    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Warsaw") // Harmonogram: codziennie o północy (00:00)
    public void updateStatisticsDaily() {
        logger.info("Starting test statistics update.");
        long startTime = System.currentTimeMillis();

        try {
            testStatisticsService.updateAllTestStatistics();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Test statistics update completed successfully. Execution time: {} ms ({} seconds).", duration, duration / 1000.0);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.error("Error occurred during test statistics update.", e);
            logger.info("Execution time before error: {} ms ({} seconds).", duration, duration / 1000.0);
        }
    }
}
