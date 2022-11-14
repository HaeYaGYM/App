package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText textEmail, textPassword, textName, textBirthday, textNickname, textHeight, textWeight;
    private Button btnRegister;
    private String email, password, str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
    }

    private void Init() {
        textEmail = findViewById(R.id.textRegisterEmailAddress);
        textPassword = findViewById(R.id.textRegisterPassword);
        textName = findViewById(R.id.textRegisterPersonName);
        textBirthday = findViewById(R.id.textRegisterBirthday);
        textNickname = findViewById(R.id.textRegisterNickname);
        textHeight = findViewById(R.id.textRegisterHeight);
        textWeight = findViewById(R.id.textRegisterWeight);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), CommunityActivity.class));
        }

    }


    public void CreateUser(View view){
        email = textEmail.getText().toString().trim();
        password = textPassword.getText().toString().trim();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(getApplicationContext(), "이메일과 비밀번호를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 6){
            Toast.makeText(getApplicationContext(), "비밀번호를 6자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Test!", "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), CommunityActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Test!", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void GetUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
        }
    }
}