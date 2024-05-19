package com.example.MAU;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MAU.models.HelperUserClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends AppCompatActivity {

    EditText loginInp, emailInp, passwordInp;
    Button authButton;
    TextView clickToLogin;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailInp = findViewById(R.id.emailEditText);
        passwordInp = findViewById(R.id.passwordEditText);
        loginInp = findViewById(R.id.loginEditText);
        progressBar = findViewById(R.id.progressBar);
        authButton = findViewById(R.id.authButton);
        clickToLogin = findViewById(R.id.clickToLogin);

        firebaseAuth = FirebaseAuth.getInstance();


        clickToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String username = loginInp.getText().toString().trim();
                String email = emailInp.getText().toString().trim();
                String password = passwordInp.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailInp.setError("Неверный формат Email");
                    progressBar.setVisibility(View.GONE);
                } else if (password.length() < 6){
                    passwordInp.setError("Пароль должен быть больше 6 символов");
                    progressBar.setVisibility(View.GONE);
                } else if (username.length() < 6){
                    loginInp.setError("Логин должен быть больше 6 символов");
                    progressBar.setVisibility(View.GONE);
                }else {
                    registrationUser(email, password, username);
                }
            }
        });
    }

    private void registrationUser(String email, String password, String username) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    HelperUserClass newUser = new HelperUserClass(username, email, password);
                    usersRef.add(newUser)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Registration.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            })

            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}