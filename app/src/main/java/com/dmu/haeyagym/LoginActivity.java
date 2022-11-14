package com.dmu.haeyagym;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int tabCount = 2;
    private LinearLayout btngoogle; // Google login button
    private FirebaseAuth auth;      // Firebase 인증 객체

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        firebaseInit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnListener();

    }


    private void firebaseInit() {
        auth = FirebaseAuth.getInstance();
    }

    public void init() {
        btngoogle = findViewById(R.id.google_login);
    }

    public void btnListener() {
        btngoogle.setOnClickListener(v -> {
            Toast.makeText(this, "클릭 !", Toast.LENGTH_SHORT).show();
            signIn();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d("fuck", "asd");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("fuck", account.toString());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.d("Exp", e.toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(getApplicationContext(), "로그인 성공 !", Toast.LENGTH_SHORT).show();
                        updateUI(user);
                        Log.d("fuck", "1");
                    }else {
                        Toast.makeText(getApplicationContext(), "로그인 실패 !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, CommunityActivity.class);
        startActivity(intent);
    }

}