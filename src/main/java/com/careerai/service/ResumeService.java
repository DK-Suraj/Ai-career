package com.careerai.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeService {

    @Autowired
    private OpenRouterService openRouterService;

    public String analyzeResume(MultipartFile file) {

        try {

            if(file.isEmpty()){
                return "Please upload a resume.";
            }

            PDDocument document = PDDocument.load(file.getInputStream());

            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(document);

            document.close();

            if(text == null || text.isBlank()){
                return "Resume text could not be read.";
            }

            String prompt =
                    "Analyze this resume:\n\n" +
                    text +
                    "\n\nGive:\n" +
                    "1. Skills detected\n" +
                    "2. Best job roles\n" +
                    "3. Missing skills\n" +
                    "4. Learning roadmap\n" +
                    "5. Career advice";

            String aiResponse = openRouterService.askAI(prompt);

            System.out.println("AI Response: " + aiResponse);

            return aiResponse;

        } catch (Exception e) {

            e.printStackTrace();

            return "Resume analysis failed.";
        }
    }
}
