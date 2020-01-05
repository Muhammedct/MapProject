package com.example.mapmct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mapmct.MapSettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity{


    FirebaseAuth auth;
    EditText etUn, etPw;
    String un = "", pw = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUn = findViewById(R.id.etUn);
        etPw = findViewById(R.id.etPw);

        auth = FirebaseAuth.getInstance();



        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener()
        {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser currUser = firebaseAuth.getCurrentUser();
                if (currUser != null)
                {
                    startActivity(new Intent(MainActivity.this, MapSettingsActivity.class));
                    finish();
                }

            }
        });

    }
    public void doResetPassword(View v)
    {
        un = etUn.getText().toString();
        if (un.isEmpty())
        {
            Toast.makeText(MainActivity.this, "To Reset Password, You Must Enter Your E-Mail",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(un);
        Toast.makeText(MainActivity.this, "If E-Mail Is Registered, A Reset Mail Was Sent",
                Toast.LENGTH_SHORT).show();
    }
    public void doLogin(View v)
    {

        un = etUn.getText().toString();

        pw = etPw.getText().toString();

        if (un.isEmpty() || pw.isEmpty())
        {
            Toast.makeText(MainActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(un,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (!task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Welcome ! :)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void doRegister(View v)
    {

        un = etUn.getText().toString();

        pw = etPw.getText().toString();

        if (un.isEmpty() || pw.isEmpty())
        {
            Toast.makeText(MainActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register User
        auth.createUserWithEmailAndPassword(un,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (!task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,
                            task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Registration Completed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
