package com.dmu.haeyagym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private TextView textEmail;
    private TextView textPassword;
    private Button btnLogin;
    private Button btnToRegister;

    private String email, password;

    private FirebaseAuth auth;      // Firebase 인증 객체

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();
    }

    private void Init() {
        textEmail = findViewById(R.id.textLoginEmailAddress);
        textPassword = findViewById(R.id.textLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), CommunityActivity.class));
        }
    }

    public void Login(View view) {
        email = textEmail.getText().toString().trim();
        password = textPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(getApplicationContext(), "이메일과 비밀번호를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                    startActivity(new Intent(getApplicationContext(), CommunityActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void ToRegister(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}