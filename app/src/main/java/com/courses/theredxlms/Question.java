package com.courses.theredxlms;

import java.io.Serializable;

public class Question implements Serializable {
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int correctOptionNumber;

    public Question(String questionText, String option1, String option2, String option3, String option4, int correctOptionNumber) {
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctOptionNumber = correctOptionNumber;
    }

    // Getters
    public String getQuestionText() { return questionText; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public int getCorrectOptionNumber() { return correctOptionNumber; }


    // --- THIS IS THE MISSING METHOD ---
    // This helper method returns the text of the correct answer
    public String getCorrectAnswerText() {
        switch (correctOptionNumber) {
            case 1:
                return option1;
            case 2:
                return option2;
            case 3:
                return option3;
            case 4:
                return option4;
            default:
                return "No correct answer specified"; // Fallback text
        }
    }
}
