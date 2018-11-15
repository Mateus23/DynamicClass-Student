package com.example.mateu.dynamicclass_student;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExercisesAdapter {

    private DatabaseReference  exercisesReference;
    private String database_path;
    private List<ExerciseInfo> exerciseList1 = new ArrayList<>();
    private List<ExerciseInfo> exerciseList2 = new ArrayList<>();
    private List<ExerciseInfo> exerciseList3 = new ArrayList<>();
    private List<ExerciseInfo> exerciseList4 = new ArrayList<>();
    private List<ExerciseInfo> exerciseList5 = new ArrayList<>();
    private List<List<ExerciseInfo>> myExercisesList = new ArrayList<>();
    private ExerciseInfo lastSentExercise;
    private int lastSentDifficulty;
    private List<Integer> toDoList = new ArrayList<>();
    public Boolean isReady = false;
    private int exercisesDonePreviously;


    public ExercisesAdapter(String subjectCode, String chapterIndex, final List<String> doneExercises, List<Integer> exercisesToDo){
        myExercisesList.add(exerciseList1);
        myExercisesList.add(exerciseList2);
        myExercisesList.add(exerciseList3);
        myExercisesList.add(exerciseList4);
        myExercisesList.add(exerciseList5);
        this.toDoList = exercisesToDo;
        exercisesDonePreviously = doneExercises.size();

        this.database_path = "Classes/" + subjectCode + "/Chapters/" + chapterIndex + "/Exercises";
        this.exercisesReference = FirebaseDatabase.getInstance().getReference(this.database_path);
        this.exercisesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 1; i <= 6; i++) {
                    if (dataSnapshot.child(String.valueOf(i)).exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child(String.valueOf(i)).getChildren()) {
                            if (!doneExercises.contains(postSnapshot.getKey())){
                                ExerciseInfo myExercise = postSnapshot.getValue(ExerciseInfo.class);
                                myExercise.exerciseKey = postSnapshot.getKey();
                                myExercisesList.get(i - 1).add(myExercise);
                            }
                        }
                    }
                    if (i == 6){
                        isReady = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void doneExercise(){
        if (lastSentExercise != null) {
            myExercisesList.get(lastSentDifficulty - 1).remove(lastSentExercise);
        }
        int numberOfEx = toDoList.get(lastSentDifficulty - 1);
        toDoList.set(lastSentDifficulty - 1, numberOfEx - 1);
        exercisesDonePreviously += 1;
    }

    private ExerciseInfo getExerciseOfDifficulty(int difficulty){
        if (difficulty == 0){
            return null;
        }
        ExerciseInfo exerciseToSend = myExercisesList.get(difficulty - 1).get(0);
        Collections.shuffle(myExercisesList.get(difficulty - 1));
        lastSentExercise = exerciseToSend;
        lastSentDifficulty = difficulty;
        return exerciseToSend;
    }

    public ExerciseInfo getNextExercise(){
        int nextDifficulty = 0;
        for(int i = 0; i < 5; i++){
            if (toDoList.get(i) > 0){
                nextDifficulty = i + 1;
                break;
            }
        }
        return getExerciseOfDifficulty(nextDifficulty);
    }

    public Boolean hasMoreExercises(){
        if (toDoList.size() < 5){
            return false;
        }
        int numberOfToDoEx = toDoList.get(0) + toDoList.get(1) + toDoList.get(2) + toDoList.get(3) + toDoList.get(4);
        return numberOfToDoEx > 0;
    }

    public int getDoneExercises(){
        return exercisesDonePreviously;
    }

    public int getToDoExerciseOfDifficulty(int difficulty){
        return toDoList.get(difficulty - 1);
    }

}
