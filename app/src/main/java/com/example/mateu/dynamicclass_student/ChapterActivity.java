package com.example.mateu.dynamicclass_student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    DataSnapshot chapterSnapshot;
    String Database_Path = "Classes/";
    String subjectCode;
    String chapterIndex;
    String studentDatabase_path;
    DatabaseReference studentDatabaseReference;
    TextView chapterName;
    TextView chapterDescription;
    String studentUID;

    public static ExercisesAdapter myExerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        studentUID = MainActivity.myUID;

        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        chapterIndex = b.getString("chapterIndex");
        Database_Path = Database_Path + subjectCode + "/Chapters/" + chapterIndex;
        studentDatabase_path = Database_Path + "/Students/" + studentUID;

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        studentDatabaseReference = FirebaseDatabase.getInstance().getReference(studentDatabase_path);
        chapterName = findViewById(R.id.chapterName);
        chapterDescription = findViewById(R.id.descriptionText);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChapterActivity.this.finish();
            }
        });

        Button mButtonExercises = (Button) findViewById(R.id.buttonChapters);
        mButtonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonExercisesEvent(subjectCode, chapterIndex);
            }
        });

        studentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> doingList = new ArrayList<>();
                for (int i = 0; i < dataSnapshot.child("DoingExercises").getChildrenCount(); i++) {
                    doingList.add(dataSnapshot.child("DoingExercises").child(String.valueOf(i)).getValue().toString());
                }
                List<Integer> exercisesToDoList = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    if (dataSnapshot.child("ToDoExercises").child(String.valueOf(i)).exists()) {
                        exercisesToDoList.add(Integer.valueOf(dataSnapshot.child("ToDoExercises").child(String.valueOf(i)).getValue().toString()));
                    }
                }
                myExerciseAdapter = new ExercisesAdapter(subjectCode, chapterIndex, doingList, exercisesToDoList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar o capitulo", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chapterSnapshot = dataSnapshot;

                String name = chapterSnapshot.child("name").getValue().toString();
                String subjectTitle = "" + chapterIndex + " - " + name;

                Object descriptionObject = chapterSnapshot.child("description").getValue();
                String subjectDescriptionText;
                if (descriptionObject == null){
                    subjectDescriptionText = "";
                }else{
                    subjectDescriptionText = descriptionObject.toString();
                }

                chapterName.setText(subjectTitle);
                chapterDescription.setText(subjectDescriptionText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Nao foi possivel carregar o capitulo", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void buttonDeletEvent(){
        //TODO
    }

    public void buttonStudentsEvent(){
        //TODO
    }

    public void buttonExercisesEvent(String subjectCode, String chapterIndex){
        //TODO
        if (myExerciseAdapter.isReady && myExerciseAdapter.hasMoreExercises()) {
            Intent intent = new Intent(this, DoExerciseActivity.class);
            intent.putExtra("subjectCode", subjectCode);
            intent.putExtra("chapterIndex", chapterIndex);
            intent.putExtra("studentID", studentUID);
            startActivity(intent);
        }else if(myExerciseAdapter.isReady) {
            AlertDialog alertDialog = new AlertDialog.Builder(ChapterActivity.this).create();
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setTitle("Oops");
            alertDialog.setMessage("Voce ja fez todos os exercicios propostos para esse capitulo :) Espere seu professor lhe passar novos!!");
            alertDialog.show();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(ChapterActivity.this).create();
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setTitle("Falha");
            alertDialog.setMessage("Exercicios ainda nao carregados.");
            alertDialog.show();
        }
    }
}

