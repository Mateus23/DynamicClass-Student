package com.example.mateu.dynamicclass_student;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinSubjectPopup extends DialogFragment {
    EditText subjectCodeTextView, subjectPasswordTextView;
    DatabaseReference classesReference;
    DataSnapshot classesSnapshot;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        classesReference = FirebaseDatabase.getInstance().getReference("Classes");

        classesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classesSnapshot = dataSnapshot;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.d("TEMOS AS MATERIAS", postSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.popup_join_subject, null))
                // Add action buttons
                .setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        tryToJoinSubject();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JoinSubjectPopup.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    tryToJoinSubject();
                }
            });
        }

        subjectCodeTextView = d.findViewById(R.id.subjectCode);
        subjectPasswordTextView = d.findViewById(R.id.subjectPassword);
    }

    public void tryToJoinSubject(){
        subjectCodeTextView.setError(null);
        subjectPasswordTextView.setError(null);

        // Store values at the time of the login attempt.
        String code = subjectCodeTextView.getText().toString().toUpperCase();
        String password = subjectPasswordTextView.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(code)) {
            subjectCodeTextView.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            subjectPasswordTextView.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (!classesSnapshot.child(code).exists()){
            subjectCodeTextView.setError("Codigo de turma nao encontrado");
            cancel = true;
        }else if (classesSnapshot.child(code).child("password").exists()){
            if(!password.equals(classesSnapshot.child(code).child("password").getValue())){
                subjectPasswordTextView.setError("Senha incorreta");
                cancel = true;
            }
        }else{
            cancel = true;
        }

        if (!cancel) {
            DatabaseReference newClassReference = classesReference.child(code);
            MySubjects.joinSubject(code, classesSnapshot.child(code).child("name").getValue().toString(), newClassReference);

        }
    }


}
