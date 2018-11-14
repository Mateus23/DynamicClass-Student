package com.example.mateu.dynamicclass_student;

import java.util.List;

public class ExerciseInfo {

    private String name;
    private String difficulty;
    private String descriptionText;
    private String imageURL;
    private List<String> Answer;
    public String exerciseKey;

    public ExerciseInfo(){

    }

    public ExerciseInfo(List<String> Answer, String descriptionText, String difficulty, String name){
        this.name = name;
        this.difficulty = difficulty;
        this.descriptionText = descriptionText;
        this.Answer = Answer;
    }

    public ExerciseInfo(List<String> Answer, String descriptionText, String difficulty, String imageURL, String name){
        this.name = name;
        this.difficulty = difficulty;
        this.descriptionText = descriptionText;
        this.imageURL = imageURL;
        this.Answer = Answer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getAnswer() {
        return Answer;
    }

    public void setAnswer(List<String> answer) {
        this.Answer = answer;
    }
}
