package com.example.mateu.dynamicclass_student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyChapters extends AppCompatActivity {

    LinearLayout mScrollView;
    DatabaseReference databaseReference;
    String Database_Path = "Classes/";
    String subjectCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chapters);

        mScrollView = findViewById(R.id.scrollViewLayout);

        Bundle b = getIntent().getExtras();
        subjectCode = b.getString("subjectCode");
        Database_Path = Database_Path + subjectCode + "/Chapters";
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        TextView titleTextView = findViewById(R.id.subjectTextView);
        titleTextView.setText(subjectCode);

        Button mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyChapters.this.finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mScrollView.getChildCount() == 0) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 1;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Button b = new Button(MyChapters.this);
                        b.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        b.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                        final String index = postSnapshot.child("index").getValue().toString();
                        String name = postSnapshot.child("name").getValue().toString();

                        String buttonText = "Capitulo " + index + " - " + name;
                        b.setText(buttonText);
                        b.setId(count);
                        count = count + 1;

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toChapterActivity(subjectCode, index);
                            }
                        });

                        mScrollView.addView(b);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void toChapterActivity(String subjectCode, String chapterIndex){
        Intent intent = new Intent(this, ChapterActivity.class);
        intent.putExtra("subjectCode", subjectCode);
        intent.putExtra("chapterIndex", chapterIndex);
        startActivity(intent);
    }

}

