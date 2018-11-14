package com.example.mateu.dynamicclass_student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoExerciseActivity extends AppCompatActivity {

    String subjectCode;
    String chapterIndex;
    String studentUID;
    TextView descriptionTextView;
    EditText answerText;
    RadioButton rButton1, rButton2, rButton3, rButton4, rButton5;
    ImageView exerciseImage;
    TextInputLayout textInputBox;
    ExerciseInfo myExercise;
    View radioGroup;
    List<String> copyOfAnswer = new ArrayList<>();
    int exerciseType;
    String radioAnswer = "";
    int rightAnswers[] = {0, 0, 0, 0, 0};
    int wrongAnswers[] = {0, 0, 0, 0, 0};

    DatabaseReference studentExercises;
    String studentsDatabase_path = "Classes/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_exercise);

        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        chapterIndex = b.getString("chapterIndex");
        studentUID = b.getString("studentID");

        studentsDatabase_path = studentsDatabase_path + "/" + subjectCode + "/Chapters/" + chapterIndex + "/Students/" + studentUID + "/DoneExercises";
        studentExercises = FirebaseDatabase.getInstance().getReference(studentsDatabase_path);

        rButton1 = findViewById(R.id.alternative1);
        rButton2 = findViewById(R.id.alternative2);
        rButton3 = findViewById(R.id.alternative3);
        rButton4 = findViewById(R.id.alternative4);
        rButton5 = findViewById(R.id.alternative5);
        descriptionTextView = findViewById(R.id.descriptionView);
        answerText = findViewById(R.id.answerTextInput);
        exerciseImage = findViewById(R.id.imageView);
        radioGroup = findViewById(R.id.alternativeGroup);
        textInputBox = findViewById(R.id.answerInputLayout);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoExerciseActivity.this.finish();
            }
        });

        Button mAnswerButton = (Button) findViewById(R.id.doneButton);
        mAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answeredExercise();
            }
        });

        studentExercises.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(int i = 1; i <= 5; i++){
                    if (dataSnapshot.child(String.valueOf(i)).exists()){
                        if (dataSnapshot.child(String.valueOf(i)).child("right").exists()) {
                            rightAnswers[i-1] = Integer.valueOf(dataSnapshot.child(String.valueOf(i)).child("right").getValue().toString());
                        }
                        if (dataSnapshot.child(String.valueOf(i)).child("wrong").exists()) {
                            wrongAnswers[i-1] = Integer.valueOf(dataSnapshot.child(String.valueOf(i)).child("wrong").getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar os exercicios feitos", Toast.LENGTH_LONG).show();
            }
        });

        createExercise();
    }

    public void createExercise(){
        myExercise = ChapterActivity.myExerciseAdapter.getNextExercise();
        if (myExercise == null){
            DoExerciseActivity.this.finish();
        }
        if (myExercise.getImageURL() != null) {
            Glide.with(DoExerciseActivity.this).load(myExercise.getImageURL()).into(exerciseImage);
        }else{
            exerciseImage.setImageDrawable(null);
        }
        descriptionTextView.setText(myExercise.getDescriptionText());
        if (myExercise.getAnswer().size() < 5){
            radioGroup.setVisibility(View.INVISIBLE);
            textInputBox.setVisibility(View.VISIBLE);
            exerciseType = myExercise.getAnswer().size();
        }else{
            radioGroup.setVisibility(View.VISIBLE);
            textInputBox.setVisibility(View.INVISIBLE);
            exerciseType = 3;
            for (int i = 0; i < 5; i++){
                copyOfAnswer.add(myExercise.getAnswer().get(i));
            }
            Collections.shuffle(copyOfAnswer);
            rButton1.setText(copyOfAnswer.get(0));
            rButton2.setText(copyOfAnswer.get(1));
            rButton3.setText(copyOfAnswer.get(2));
            rButton4.setText(copyOfAnswer.get(3));
            rButton5.setText(copyOfAnswer.get(4));
        }

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.alternative1:
                if (checked)
                    radioAnswer = copyOfAnswer.get(0);
                break;
            case R.id.alternative2:
                if (checked)
                    radioAnswer = copyOfAnswer.get(1);
                break;
            case R.id.alternative3:
                if (checked)
                    radioAnswer = copyOfAnswer.get(2);
                break;
            case R.id.alternative4:
                if (checked)
                    radioAnswer = copyOfAnswer.get(3);
                break;
            case R.id.alternative5:
                if (checked)
                    radioAnswer = copyOfAnswer.get(4);
                break;
        }
    }

    public void createFeedbackDialog(Boolean wasCorrect){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DoExerciseActivity.this);

        Boolean canDoAnotherEx = ChapterActivity.myExerciseAdapter.hasMoreExercises();
        String message;
        if (wasCorrect) {
            message = "Parabens, voce acertou!!";
        }else{
            message = ("Que pena, voce errou!!");
        }
        if (canDoAnotherEx){
            message = message + "\n Desseja resolver o proximo exercicio?";
            builder1.setPositiveButton(
                    "SIM",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            createExercise();
                        }
                    });

            builder1.setNegativeButton(
                    "NAO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            DoExerciseActivity.this.finish();
                        }
                    });
        }else{
            ChapterActivity.myExerciseAdapter.isReady = false;
            message = message + "\n Voce terminou todos os exercicios dados pelo professor!!";
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            DoExerciseActivity.this.finish();
                        }
                    });
        }
        builder1.setMessage(message);

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void correctAnswer(){
        int difficulty = Integer.valueOf(myExercise.getDifficulty()) - 1;
        rightAnswers[difficulty] += 1;
        studentExercises.child(myExercise.getDifficulty()).child("right").setValue(rightAnswers[difficulty]);
        int exAux = ChapterActivity.myExerciseAdapter.getDoneExercises() - 1;
        studentExercises.getParent().child("DoingExercises").child(String.valueOf(exAux)).setValue(myExercise.exerciseKey);
        int axuToDoEx = ChapterActivity.myExerciseAdapter.getToDoExerciseOfDifficulty(difficulty + 1);
        studentExercises.getParent().child("ToDoExercises").child(String.valueOf(difficulty)).setValue(axuToDoEx);
        createFeedbackDialog(true);
    }

    public void wrongAnswer(){
        int difficulty = Integer.valueOf(myExercise.getDifficulty()) - 1;
        wrongAnswers[difficulty] += 1;
        studentExercises.child(myExercise.getDifficulty()).child("wrong").setValue(wrongAnswers[difficulty]);
        int exAux = ChapterActivity.myExerciseAdapter.getDoneExercises() - 1;
        studentExercises.getParent().child("DoingExercises").child(String.valueOf(exAux)).setValue(myExercise.exerciseKey);
        int axuToDoEx = ChapterActivity.myExerciseAdapter.getToDoExerciseOfDifficulty(difficulty + 1);
        studentExercises.getParent().child("ToDoExercises").child(String.valueOf(difficulty)).setValue(axuToDoEx);
        createFeedbackDialog(false);
    }

    public void answeredExercise(){
        if (exerciseType == 1){
            String answer = answerText.getText().toString();
            if (TextUtils.isEmpty(answer)) {
                Toast.makeText(getApplicationContext(), "Preencha a resposta!!", Toast.LENGTH_LONG).show();
            }else{
                ChapterActivity.myExerciseAdapter.doneExercise();
                if (answer.equals(myExercise.getAnswer().get(0))) {
                    correctAnswer();
                } else {
                    wrongAnswer();
                }
            }
        }else if (exerciseType == 2){
            if (TextUtils.isEmpty(answerText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Preencha a resposta!!", Toast.LENGTH_LONG).show();
            }else {
                long minValue = Long.valueOf(myExercise.getAnswer().get(0));
                long maxValue = Long.valueOf(myExercise.getAnswer().get(1));
                long answerValue = Long.valueOf(answerText.getText().toString());
                if (minValue <= answerValue && answerValue <= maxValue) {
                    correctAnswer();
                } else {
                    wrongAnswer();
                }
            }
        }else if (exerciseType == 3){
            if (radioAnswer.equals("")){
                Toast.makeText(getApplicationContext(), "Escolha uma alternativa!!", Toast.LENGTH_LONG).show();
            }else{
                ChapterActivity.myExerciseAdapter.doneExercise();
                if (radioAnswer.equals(myExercise.getAnswer().get(0))) {
                    correctAnswer();
                }else{
                    wrongAnswer();
                }
            }
        }
    }


}
