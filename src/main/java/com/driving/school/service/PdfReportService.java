package com.driving.school.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfReportService {

    public byte[] generatePdfReport() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            InputStream fontStream = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf");
            PDType0Font font = PDType0Font.load(document, fontStream);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(font, 24);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Raport Szkoły Jazdy z polskimi znakami.");
            contentStream.endText();
            contentStream.close();
            document.save(bos);
        } catch (IOException e) {
            return new byte[0];
        }
        return bos.toByteArray();
    }
}
