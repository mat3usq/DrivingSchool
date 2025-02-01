package com.driving.school.service;

import com.driving.school.model.*;
import com.driving.school.repository.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import com.lowagie.text.pdf.BaseFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
public class PdfReportService {
    private final SchoolUserRepository schoolUserRepository;
    private final InstructionEventRepository instructionEventRepository;
    private final PaymentRepository paymentRepository;
    private final ExamStatisticsRepository examStatisticsRepository;
    private final TestStatisticsRepository testStatisticsRepository;
    private final ResourceLoader resourceLoader;
    private final CourseRepository courseRepository;

    @Autowired
    public PdfReportService(SchoolUserRepository schoolUserRepository,
                            InstructionEventRepository instructionEventRepository,
                            PaymentRepository paymentRepository,
                            ExamStatisticsRepository examStatisticsRepository,
                            TestStatisticsRepository testStatisticsRepository,
                            ResourceLoader resourceLoader, CourseRepository courseRepository) {
        this.schoolUserRepository = schoolUserRepository;
        this.instructionEventRepository = instructionEventRepository;
        this.paymentRepository = paymentRepository;
        this.examStatisticsRepository = examStatisticsRepository;
        this.testStatisticsRepository = testStatisticsRepository;
        this.resourceLoader = resourceLoader;
        this.courseRepository = courseRepository;
    }

    public byte[] generatePdfReport() {
        List<SchoolUser> allUsers = schoolUserRepository.findAll();
        List<SchoolUser> admins = allUsers.stream()
                .filter(u -> "ADMIN".equals(u.getRoleName()))
                .collect(Collectors.toList());
        List<SchoolUser> instructors = allUsers.stream()
                .filter(u -> "INSTRUCTOR".equals(u.getRoleName()))
                .collect(Collectors.toList());
        List<SchoolUser> students = allUsers.stream()
                .filter(u -> "STUDENT".equals(u.getRoleName()))
                .collect(Collectors.toList());

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);
        Double totalPaymentsLastMonth = paymentRepository.sumPaymentsBetween(startDate, endDate);
        if (totalPaymentsLastMonth == null) {
            totalPaymentsLastMonth = 0.0;
        }

        Map<SchoolUser, InstructorStats> instructorStatsMap = getInstructorStats(instructors, startDate, endDate);

        List<ExamStatistics> examStatsList = examStatisticsRepository.findByCreatedAtBetween(startDate, endDate);
        int totalSolvedExams = examStatsList.stream()
                .mapToInt(ExamStatistics::getNumberOfSolvedExams)
                .sum();
        int totalPassedExams = examStatsList.stream()
                .mapToInt(ExamStatistics::getNumberOfPassedExams)
                .sum();

        List<TestStatistics> testStatsList = testStatisticsRepository.findByCreatedAtBetween(startDate, endDate);
        int totalSolvedTests = testStatsList.stream()
                .mapToInt(TestStatistics::getNumberOfQuestionsSolved)
                .sum();
        int totalPassedTests = testStatsList.stream()
                .mapToInt(TestStatistics::getNumberOfQuestionsAnsweredCorrectly)
                .sum();

        String examsChartPath = generateExamsChart(examStatsList);
        String passedExamsChartPath = generatePassedExamsChart(examStatsList);
        String solvedQuestionsChartPath = generateSolvedQuestionsChart(testStatsList);

        Resource resource = resourceLoader.getResource("classpath:templates/report.html");
        try (InputStream inputStream = resource.getInputStream()) {
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            template = template.replace("{{title}}", "Raport Szkoły Jazdy " + LocalDate.now());
            template = template.replace("{{adminTable}}", buildSimpleTable(admins));
            template = template.replace("{{instructorTable}}", buildInstructorTable(instructorStatsMap));
            template = template.replace("{{studentTable}}", buildStudentTable(students));
            template = template.replace("{{totalPaymentsLastMonth}}", String.format("%.2f PLN", totalPaymentsLastMonth));
            template = template.replace("{{totalSolvedExams}}", String.valueOf(totalSolvedExams));
            template = template.replace("{{totalPassedExams}}", String.valueOf(totalPassedExams));
            template = template.replace("{{totalSolvedTests}}", String.valueOf(totalSolvedTests));
            template = template.replace("{{totalPassedTests}}", String.valueOf(totalPassedTests));

            if (!examStatsList.isEmpty()) {
                template = template.replace("{{examsChart}}", "data:image/png;base64," + encodeImageToBase64(examsChartPath));
                template = template.replace("{{emptyInfoAboutExamsChart}}", "");
                template = template.replace("{{passedExamsChart}}", "data:image/png;base64," + encodeImageToBase64(passedExamsChartPath));
                template = template.replace("{{emptyInfoAboutPassedExamsChart}}", "");
            } else {
                template = template.replace("{{emptyInfoAboutExamsChart}}", "Brak danych do wyświetlenia wykresu rozwiązywanych i nierozwiązywanych egzaminów.");
                template = template.replace("{{emptyInfoAboutPassedExamsChart}}", "Brak danych do wyświetlenia wykresu rozwiązanych pytań w egzaminach.");
            }

            if(!testStatsList.isEmpty()) {
                template = template.replace("{{solvedQuestionsChart}}", "data:image/png;base64," + encodeImageToBase64(solvedQuestionsChartPath));
                template = template.replace("{{emptyInfoAboutSolvedQuestionsChart}}", "");
            }
            else template = template.replace("{{emptyInfoAboutSolvedQuestionsChart}}", "Brak danych do wyświetlenia wykresu rozwiązanych pytań w testach.");

            ITextRenderer renderer = new ITextRenderer();

            Resource fontResource = resourceLoader.getResource("classpath:fonts/Roboto-Regular.ttf");
            File tempFontFile = File.createTempFile("Roboto-Regular", ".ttf");
            try (InputStream fontStream = fontResource.getInputStream();
                 OutputStream os = new FileOutputStream(tempFontFile)) {
                fontStream.transferTo(os);
            }

            renderer.getFontResolver().addFont(tempFontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            tempFontFile.deleteOnExit();

            renderer.setDocumentFromString(template);
            renderer.layout();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            renderer.createPDF(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    private Map<SchoolUser, InstructorStats> getInstructorStats(List<SchoolUser> instructors, LocalDateTime startDate, LocalDateTime endDate) {
        Map<SchoolUser, InstructorStats> statsMap = new HashMap<>();

        for (SchoolUser instructor : instructors) {
            List<InstructionEvent> events = instructionEventRepository.findByInstructorAndStartTimeBetween(instructor, startDate, endDate);
            long eventCount = events.size();
            long totalDurationMinutes = events.stream()
                    .mapToLong(event -> Duration.between(event.getStartTime(), event.getEndTime()).toMinutes())
                    .sum();
            statsMap.put(instructor, new InstructorStats(eventCount, totalDurationMinutes));
        }

        return statsMap;
    }


    private String buildInstructorTable(Map<SchoolUser, InstructorStats> statsMap) {
        if (statsMap.isEmpty()) {
            return "<p>Brak danych</p>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr>")
                .append("<th>Imię</th>")
                .append("<th>Nazwisko</th>")
                .append("<th>Liczba Wydarzeń</th>")
                .append("<th>Całkowity Czas (godz. min)</th>")
                .append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        for (Map.Entry<SchoolUser, InstructorStats> entry : statsMap.entrySet()) {
            SchoolUser instructor = entry.getKey();
            InstructorStats stats = entry.getValue();
            sb.append("<tr>")
                    .append("<td>").append(escapeHtml(instructor.getName())).append("</td>")
                    .append("<td>").append(escapeHtml(instructor.getSurname())).append("</td>")
                    .append("<td>").append(stats.getEventCount()).append("</td>")
                    .append("<td>").append(formatDuration(stats.getTotalDurationMinutes())).append("</td>")
                    .append("</tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");
        return sb.toString();
    }


    private String buildSimpleTable(List<SchoolUser> admins) {
        if (admins.isEmpty()) {
            return "<p>Brak danych</p>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr><th>Imię</th><th>Nazwisko</th></tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        for (SchoolUser admin : admins) {
            sb.append("<tr><td>")
                    .append(escapeHtml(admin.getName()))
                    .append("</td><td>")
                    .append(escapeHtml(admin.getSurname()))
                    .append("</td></tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");
        return sb.toString();
    }


    private String buildStudentTable(List<SchoolUser> students) {
        if (students.isEmpty()) {
            return "<p>Brak Studentów</p>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr><th>Imię</th><th>Nazwisko</th><th>Aktywne kursy</th><th>Zakończone kursy</th></tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        for (SchoolUser student : students) {
            Map<String, String> coursesMap = getStudentCoursesInfo(student);
            sb.append("<tr><td>").append(escapeHtml(student.getName()))
                    .append("</td><td>").append(escapeHtml(student.getSurname()))
                    .append("</td><td>").append(escapeHtml(coursesMap.get("active")))
                    .append("</td><td>").append(escapeHtml(coursesMap.get("ended")))
                    .append("</td></tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");
        return sb.toString();
    }


    public Map<String, String> getStudentCoursesInfo(SchoolUser student) {
        List<Course> courses = courseRepository.findCoursesByStudent(student);
        Map<String, Long> activeCategories = courses.stream()
                .filter(course -> course.getEndAt() == null)
                .collect(Collectors.groupingBy(course -> course.getCategory().getNameCategory(), LinkedHashMap::new, Collectors.counting()));
        Map<String, Long> endedCategories = courses.stream()
                .filter(course -> course.getEndAt() != null)
                .collect(Collectors.groupingBy(course -> course.getCategory().getNameCategory(), LinkedHashMap::new, Collectors.counting()));
        String active = activeCategories.entrySet().stream()
                .map(entry -> "Kategoria " + entry.getKey() + " (" + entry.getValue() + ")")
                .collect(Collectors.joining(", "));
        String ended = endedCategories.entrySet().stream()
                .map(entry -> "Kategoria " + entry.getKey() + " (" + entry.getValue() + ")")
                .collect(Collectors.joining(", "));
        return Map.of("active", active, "ended", ended);
    }


    private String formatDuration(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d godz. %d min", hours, minutes);
    }


    private String generateExamsChart(List<ExamStatistics> examStatsList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        examStatsList.sort(Comparator.comparing(ExamStatistics::getCategory));


        for (ExamStatistics stats : examStatsList) {
            dataset.addValue(stats.getNumberOfPassedExams(), "Zdane Egzaminy", stats.getCategory());
            long unpassed = stats.getNumberOfSolvedExams() - stats.getNumberOfPassedExams();
            dataset.addValue(unpassed, "Niezdane Egzaminy", stats.getCategory());
        }

        JFreeChart barChart = ChartFactory.createStackedBarChart(
                "Zdane i Niezdane Egzaminy z Ostatniego Miesiąca",
                "Kategoria",
                "Liczba Egzaminów",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        barChart.setBackgroundPaint(Color.white);
        barChart.getTitle().setPaint(Color.black);

        NumberAxis rangeAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        rangeAxis.setLabelPaint(Color.BLACK);
        rangeAxis.setTickLabelPaint(Color.BLACK);

        barChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);
        barChart.getCategoryPlot().setDomainGridlinePaint(Color.GRAY);
        barChart.getCategoryPlot().setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));

        BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, new Color(34, 177, 76));
        renderer.setSeriesPaint(1, new Color(237, 28, 36));

        String filePath = "src/main/resources/data/chartImages/examsChart.png";
        try {
            BufferedImage chartImage = barChart.createBufferedImage(700, 600);
            ImageIO.write(chartImage, "png", new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }



    private String generatePassedExamsChart(List<ExamStatistics> examStatsList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (ExamStatistics stats : examStatsList) {
            dataset.addValue(stats.getNumberOfQuestionsAnsweredCorrectly(), "Poprawne Odpowiedzi", stats.getCategory());
            dataset.addValue(stats.getNumberOfQuestionsAnsweredInCorrectly(), "Niepoprawne Odpowiedzi", stats.getCategory());
            dataset.addValue(stats.getNumberOfQuestionsSkipped(), "Pominięte Pytania", stats.getCategory());
        }

        JFreeChart barChart = ChartFactory.createStackedBarChart(
                "Rozwiązane pytania z Egzaminów z Ostatniego Miesiąca",
                "Kategoria",
                "Liczba Pytań",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        barChart.setBackgroundPaint(Color.white);
        barChart.getTitle().setPaint(Color.black);

        NumberAxis rangeAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        rangeAxis.setLabelPaint(Color.BLACK);
        rangeAxis.setTickLabelPaint(Color.BLACK);

        barChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);
        barChart.getCategoryPlot().setDomainGridlinePaint(Color.GRAY);
        barChart.getCategoryPlot().setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));

        BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, new Color(34, 177, 76));
        renderer.setSeriesPaint(1, new Color(255, 127, 39));
        renderer.setSeriesPaint(2, new Color(136, 0, 21));

        String filePath = "src/main/resources/data/chartImages/passedExamsChart.png";
        try {
            BufferedImage chartImage = barChart.createBufferedImage(700, 600);
            ImageIO.write(chartImage, "png", new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }


    private String generateSolvedQuestionsChart(List<TestStatistics> testStatsList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (TestStatistics stats : testStatsList) {
            dataset.addValue(stats.getNumberOfQuestionsAnsweredCorrectly(), "Poprawne Odpowiedzi", stats.getTest().getDrivingCategory());
            dataset.addValue(stats.getNumberOfQuestionsAnsweredInCorrectly(), "Niepoprawne Odpowiedzi", stats.getTest().getDrivingCategory());
            dataset.addValue(stats.getNumberOfQuestionsSkipped(), "Pominięte Pytania", stats.getTest().getDrivingCategory());
        }

        JFreeChart barChart = ChartFactory.createStackedBarChart(
                "Rozwiązane Pytania z Testów z Ostatniego Miesiąca",
                "Kategoria",
                "Liczba Pytań",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        barChart.setBackgroundPaint(Color.white);
        barChart.getTitle().setPaint(Color.black);

        NumberAxis rangeAxis = (NumberAxis) barChart.getCategoryPlot().getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        rangeAxis.setLabelPaint(Color.BLACK);
        rangeAxis.setTickLabelPaint(Color.BLACK);

        barChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);
        barChart.getCategoryPlot().setDomainGridlinePaint(Color.GRAY);
        barChart.getCategoryPlot().setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));

        BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, new Color(153, 102, 255));
        renderer.setSeriesPaint(1, new Color(255, 157, 64));
        renderer.setSeriesPaint(2, new Color(34, 193, 194));

        String filePath = "src/main/resources/data/chartImages/solvedQuestionsChart.png";
        try {
            BufferedImage chartImage = barChart.createBufferedImage(700, 600);
            ImageIO.write(chartImage, "png", new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }


    private String encodeImageToBase64(String imagePath) {
        try {
            File file = new File(imagePath);
            BufferedImage image = ImageIO.read(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }


    private static class InstructorStats {
        private final long eventCount;
        private final long totalDurationMinutes;

        public InstructorStats(long eventCount, long totalDurationMinutes) {
            this.eventCount = eventCount;
            this.totalDurationMinutes = totalDurationMinutes;
        }

        public long getEventCount() {
            return eventCount;
        }

        public long getTotalDurationMinutes() {
            return totalDurationMinutes;
        }
    }
}
