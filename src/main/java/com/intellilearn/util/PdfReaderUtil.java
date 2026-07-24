package com.intellilearn.util;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class PdfReaderUtil {

    public String extractText(String filePath) {

        File file = new File(filePath);

        try (PDDocument document = PDDocument.load(file)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();

            return pdfStripper.getText(document);

        } catch (IOException e) {
            throw new RuntimeException("Unable to read PDF file.", e);
        }
    }

}