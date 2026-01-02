package com.example.studylink;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String text;
    private List<String> options;
    private int selectedIndex;

    public Question(String text) {
        this.text = text;
        this.options = new ArrayList<>();
        this.selectedIndex = -1;
    }

    public Question(String text, List<String> options, int selectedIndex) {
        this.text = text;
        this.options = options;
        this.selectedIndex = selectedIndex;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
