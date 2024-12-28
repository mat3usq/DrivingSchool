package com.driving.school.service;

import com.driving.school.model.Course;
import com.driving.school.model.MentorShip;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.MentorShipRepository;
import com.driving.school.repository.SchoolUserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PdfReportService {

    private final SchoolUserRepository schoolUserRepository;
    private final MentorShipRepository mentorShipRepository;
    private final float MARGIN_BOTTOM = 50;
    private final float MARGIN_LEFT = 50;
    private final float MARGIN_RIGHT = 50;
    private final float MARGIN_TOP = 50;

    @Autowired
    public PdfReportService(SchoolUserRepository schoolUserRepository,
                            MentorShipRepository mentorShipRepository) {
        this.schoolUserRepository = schoolUserRepository;
        this.mentorShipRepository = mentorShipRepository;
    }

    public byte[] generatePdfReport() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            InputStream fontStream = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf");
            PDType0Font font = PDType0Font.load(document, fontStream);
            PDPageContentStream cs = new PDPageContentStream(document, page);
            String mainTitle = "Raport Szkoły Jazdy " + LocalDate.now();
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float titleFontSize = 24;
            float titleWidth = (font.getStringWidth(mainTitle) / 1000) * titleFontSize;
            float startX = (pageWidth - titleWidth) / 2;
            float startY = pageHeight - MARGIN_TOP;

            cs.beginText();
            cs.setFont(font, titleFontSize);
            cs.newLineAtOffset(startX, startY);
            cs.showText(mainTitle);
            cs.endText();

            float nextY = startY - 50;
            float tableWidth = pageWidth - MARGIN_LEFT - MARGIN_RIGHT;
            float rowHeight = 20;
            float colWidth = tableWidth / 2;

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

            nextY = drawUsersTable("Lista Adminów", admins, document, page, cs, font, nextY, tableWidth, rowHeight, colWidth);
            nextY -= 20;
            nextY = drawUsersTable("Lista Instruktorów", instructors, document, page, cs, font, nextY, tableWidth, rowHeight, colWidth);
            nextY -= 20;
            nextY = drawStudentTable("Lista Studentów", students, document, page, cs, font, nextY);

            cs.close();
            document.save(bos);
        } catch (IOException e) {
            return new byte[0];
        }
        return bos.toByteArray();
    }

    private float drawUsersTable(String heading,
                                 List<SchoolUser> users,
                                 PDDocument document,
                                 PDPage page,
                                 PDPageContentStream cs,
                                 PDType0Font font,
                                 float startY,
                                 float tableWidth,
                                 float rowHeight,
                                 float colWidth) throws IOException {
        if (users.isEmpty()) {
            return startY;
        }
        float headingFontSize = 18;
        float headingWidth = (font.getStringWidth(heading) / 1000) * headingFontSize;
        float pageWidth = page.getMediaBox().getWidth();
        if (startY - rowHeight < MARGIN_BOTTOM) {
            cs.close();
            PDPage newPage = new PDPage();
            document.addPage(newPage);
            cs = new PDPageContentStream(document, newPage);
            startY = newPage.getMediaBox().getHeight() - MARGIN_TOP;
        }
        float startXHeading = (pageWidth - headingWidth) / 2;
        cs.beginText();
        cs.setFont(font, headingFontSize);
        cs.newLineAtOffset(startXHeading, startY);
        cs.showText(heading);
        cs.endText();
        startY -= (rowHeight + 10);
        startY = drawUsersTableHeader(cs, font, startY, tableWidth, rowHeight, colWidth);
        for (SchoolUser user : users) {
            if (startY - rowHeight < MARGIN_BOTTOM) {
                cs.close();
                PDPage newPage = new PDPage();
                document.addPage(newPage);
                cs = new PDPageContentStream(document, newPage);
                startY = newPage.getMediaBox().getHeight() - MARGIN_TOP;
                startY = drawUsersTableHeader(cs, font, startY, tableWidth, rowHeight, colWidth);
            }
            drawRow(cs, font, startY, tableWidth, rowHeight, colWidth, user.getName(), user.getSurname());
            startY -= rowHeight;
        }
        return startY;
    }

    private float drawUsersTableHeader(PDPageContentStream cs,
                                       PDType0Font font,
                                       float startY,
                                       float tableWidth,
                                       float rowHeight,
                                       float colWidth) throws IOException {
        float leftX = MARGIN_LEFT;
        float rightX = leftX + tableWidth;
        cs.moveTo(leftX, startY);
        cs.lineTo(rightX, startY);
        cs.stroke();
        cs.moveTo(leftX, startY - rowHeight);
        cs.lineTo(rightX, startY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX, startY);
        cs.lineTo(leftX, startY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth, startY);
        cs.lineTo(leftX + colWidth, startY - rowHeight);
        cs.stroke();
        cs.moveTo(rightX, startY);
        cs.lineTo(rightX, startY - rowHeight);
        cs.stroke();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + 5, startY - 15);
        cs.showText("Imię");
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth + 5, startY - 15);
        cs.showText("Nazwisko");
        cs.endText();
        return startY - rowHeight;
    }

    private void drawRow(PDPageContentStream cs,
                         PDType0Font font,
                         float currentY,
                         float tableWidth,
                         float rowHeight,
                         float colWidth,
                         String name,
                         String surname) throws IOException {
        float leftX = MARGIN_LEFT;
        float rightX = leftX + tableWidth;
        cs.moveTo(leftX, currentY);
        cs.lineTo(rightX, currentY);
        cs.stroke();
        cs.moveTo(leftX, currentY - rowHeight);
        cs.lineTo(rightX, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX, currentY);
        cs.lineTo(leftX, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth, currentY);
        cs.lineTo(leftX + colWidth, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(rightX, currentY);
        cs.lineTo(rightX, currentY - rowHeight);
        cs.stroke();
        String wrappedName = wrapText(name, font, 12, colWidth - 10);
        String wrappedSurname = wrapText(surname, font, 12, colWidth - 10);
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + 5, currentY - 15);
        cs.showText(wrappedName);
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth + 5, currentY - 15);
        cs.showText(wrappedSurname);
        cs.endText();
    }

    private float drawStudentTable(String heading,
                                   List<SchoolUser> students,
                                   PDDocument document,
                                   PDPage page,
                                   PDPageContentStream cs,
                                   PDType0Font font,
                                   float startY) throws IOException {
        if (students.isEmpty()) {
            return startY;
        }
        float pageWidth = page.getMediaBox().getWidth();
        float headingFontSize = 18;
        float rowHeight = 20;
        float headingWidth = (font.getStringWidth(heading) / 1000) * headingFontSize;
        float tableWidth = pageWidth - MARGIN_LEFT - MARGIN_RIGHT;
        float colWidth = tableWidth / 4;
        if (startY - rowHeight < MARGIN_BOTTOM) {
            cs.close();
            PDPage newPage = new PDPage();
            document.addPage(newPage);
            cs = new PDPageContentStream(document, newPage);
            startY = newPage.getMediaBox().getHeight() - MARGIN_TOP;
        }
        float startXHeading = (pageWidth - headingWidth) / 2;
        cs.beginText();
        cs.setFont(font, headingFontSize);
        cs.newLineAtOffset(startXHeading, startY);
        cs.showText(heading);
        cs.endText();
        startY -= (rowHeight + 10);
        startY = drawStudentTableHeader(cs, font, startY, tableWidth, rowHeight, colWidth);
        for (SchoolUser student : students) {
            if (startY - rowHeight < MARGIN_BOTTOM) {
                cs.close();
                PDPage newPage = new PDPage();
                document.addPage(newPage);
                cs = new PDPageContentStream(document, newPage);
                startY = newPage.getMediaBox().getHeight() - MARGIN_TOP;
                startY = drawStudentTableHeader(cs, font, startY, tableWidth, rowHeight, colWidth);
            }
            Map<String, String> coursesMap = getStudentCoursesInfo(student);
            drawStudentRow(cs, font, startY, tableWidth, rowHeight, colWidth,
                    student.getName(),
                    student.getSurname(),
                    coursesMap.get("active"),
                    coursesMap.get("ended"));
            startY -= rowHeight;
        }
        return startY;
    }

    private float drawStudentTableHeader(PDPageContentStream cs,
                                         PDType0Font font,
                                         float startY,
                                         float tableWidth,
                                         float rowHeight,
                                         float colWidth) throws IOException {
        float leftX = MARGIN_LEFT;
        float rightX = leftX + tableWidth;
        cs.moveTo(leftX, startY);
        cs.lineTo(rightX, startY);
        cs.stroke();
        cs.moveTo(leftX, startY - rowHeight);
        cs.lineTo(rightX, startY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX, startY);
        cs.lineTo(leftX, startY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth, startY);
        cs.lineTo(leftX + colWidth, startY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth * 2, startY);
        cs.lineTo(leftX + colWidth * 2, startY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth * 3, startY);
        cs.lineTo(leftX + colWidth * 3, startY - rowHeight);
        cs.stroke();
        cs.moveTo(rightX, startY);
        cs.lineTo(rightX, startY - rowHeight);
        cs.stroke();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + 5, startY - 15);
        cs.showText("Imię");
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth + 5, startY - 15);
        cs.showText("Nazwisko");
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth * 2 + 5, startY - 15);
        cs.showText("Aktywne kursy");
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth * 3 + 5, startY - 15);
        cs.showText("Zakończone kursy");
        cs.endText();
        return startY - rowHeight;
    }

    private void drawStudentRow(PDPageContentStream cs,
                                PDType0Font font,
                                float currentY,
                                float tableWidth,
                                float rowHeight,
                                float colWidth,
                                String name,
                                String surname,
                                String activeCourses,
                                String endedCourses) throws IOException {
        float leftX = MARGIN_LEFT;
        float rightX = leftX + tableWidth;
        cs.moveTo(leftX, currentY);
        cs.lineTo(rightX, currentY);
        cs.stroke();
        cs.moveTo(leftX, currentY - rowHeight);
        cs.lineTo(rightX, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX, currentY);
        cs.lineTo(leftX, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth, currentY);
        cs.lineTo(leftX + colWidth, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth * 2, currentY);
        cs.lineTo(leftX + colWidth * 2, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(leftX + colWidth * 3, currentY);
        cs.lineTo(leftX + colWidth * 3, currentY - rowHeight);
        cs.stroke();
        cs.moveTo(rightX, currentY);
        cs.lineTo(rightX, currentY - rowHeight);
        cs.stroke();

        String wrappedName = wrapText(name, font, 12, colWidth - 10);
        String wrappedSurname = wrapText(surname, font, 12, colWidth - 10);
        String wrappedActive = wrapText(activeCourses, font, 12, colWidth - 10);
        String wrappedEnded = wrapText(endedCourses, font, 12, colWidth - 10);

        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + 5, currentY - 15);
        cs.showText(wrappedName);
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth + 5, currentY - 15);
        cs.showText(wrappedSurname);
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth * 2 + 5, currentY - 15);
        cs.showText(wrappedActive);
        cs.endText();
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(leftX + colWidth * 3 + 5, currentY - 15);
        cs.showText(wrappedEnded);
        cs.endText();
    }

    private Map<String, String> getStudentCoursesInfo(SchoolUser student) {
        List<MentorShip> mentorShips = mentorShipRepository.findByStudentIdOrderByStatusAsc(student.getId());
        Set<String> active = new LinkedHashSet<>();
        Set<String> ended = new LinkedHashSet<>();
        for (MentorShip ms : mentorShips) {
            for (Course c : ms.getStudentCourses()) {
                if (c.getEndAt() == null) {
                    if (c.getCategory() != null && c.getCategory().getNameCategory() != null) {
                        active.add(c.getCategory().getNameCategory());
                    }
                } else {
                    if (c.getCategory() != null && c.getCategory().getNameCategory() != null) {
                        ended.add(c.getCategory().getNameCategory());
                    }
                }
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("active", active.isEmpty() ? "" : String.join(", ", active));
        result.put("ended", ended.isEmpty() ? "" : String.join(", ", ended));
        return result;
    }

    private String wrapText(String text, PDType0Font font, float fontSize, float maxWidth) throws IOException {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(" ");
        float spaceWidth = font.getStringWidth(" ") / 1000 * fontSize;
        float currentWidth = 0;
        for (String word : words) {
            float wordWidth = font.getStringWidth(word) / 1000 * fontSize;
            if (currentWidth + wordWidth > maxWidth) {
                sb.append("\n").append(word).append(" ");
                currentWidth = wordWidth + spaceWidth;
            } else {
                sb.append(word).append(" ");
                currentWidth += wordWidth + spaceWidth;
            }
        }
        return sb.toString().trim();
    }
}
