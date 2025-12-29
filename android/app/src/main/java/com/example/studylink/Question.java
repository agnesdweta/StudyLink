package com.example.studylink;

public class Question {
    private int id;
    private int examId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;

    public int getId() { return id; }
    public int getExamId() { return examId; }
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getAnswer() { return answer; }

    public void setId(int id) { this.id = id; }
    public void setExamId(int examId) { this.examId = examId; }
    public void setQuestion(String question) { this.question = question; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setAnswer(String answer) { this.answer = answer; }
}
