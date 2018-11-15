package com.example.mateu.dynamicclass_student;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MySubjects extends AppCompatActivity {

    LinearLayout mScrollView;
    DatabaseReference databaseReference;
    String Database_Path = "Students";
    static String id;
    static DataSnapshot mySubjectsSnapshot;
    static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subjects);
        mScrollView = findViewById(R.id.scrollViewLayout);
        Bundle b = getIntent().getExtras();
        id = b.getString("id");
        Database_Path = Database_Path + "/" + id + "/Classes";
        Log.d("DATAPATH", Database_Path);
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySubjects.this.finish();
            }
        });

        Button mJoinSubject = (Button) findViewById(R.id.joinSubjectButton);
        mJoinSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openJoinSubjectPopup();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mScrollView.getChildCount() == 0) {
            Log.d("OLHA AQUI", "ENTROU NO SCROLLVIEWCOUNT CHANGE");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 1;
                    mySubjectsSnapshot = dataSnapshot;
                    Log.d("OLHA AQUI", "ENTROU NO DATA CHANGE");
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.d("OLHA AQUI", "ENTROU NO FOR DATA CHANGE");
                        Button b = new Button(MySubjects.this);
                        b.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        b.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                        final String code = postSnapshot.child("code").getValue().toString();
                        name = postSnapshot.child("name").getValue().toString();

                        String buttonText = code + " - " + name;
                        b.setText(buttonText);
                        b.setId(count);
                        count = count + 1;

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toSubjectActivity(code);
                            }
                        });

                        mScrollView.addView(b);
                        if(mScrollView.getChildCount() == 0){
                            findViewById(R.id.textNoSubjects).setVisibility(View.VISIBLE);
                        }else{
                            findViewById(R.id.textNoSubjects).setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if(mScrollView.getChildCount() == 0){
                findViewById(R.id.textNoSubjects).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.textNoSubjects).setVisibility(View.INVISIBLE);
            }
        }else{
            Log.d("OLHA AQUI", "NAO ENTROU NO SCROLLVIEWCOUNT CHANGE");
        }
    }

    public void openJoinSubjectPopup() {
        DialogFragment newFragment = new JoinSubjectPopup();
        newFragment.show(getSupportFragmentManager(), "JoinSubject");
    }

    public void toSubjectActivity(String subjectCode){
        Intent intent = new Intent(this, SubjectActivity.class);
        intent.putExtra("subjectCode", subjectCode);
        startActivity(intent);
    }

    public static void joinSubject(String subjectCode, String subjecName, DatabaseReference newClassReference){
        if (!mySubjectsSnapshot.hasChild(subjectCode)){
            DatabaseReference newStudentSubjectReference = FirebaseDatabase.getInstance().getReference("Students" + "/" + id + "/Classes/" + subjectCode);
            newStudentSubjectReference.child("name").setValue(subjecName);
            newStudentSubjectReference.child("code").setValue(subjectCode);
            newClassReference.child("Students").child(id).child("isActive").setValue(true);
            newClassReference.child("Students").child(id).child("name").setValue(name);
        }
    }

}
