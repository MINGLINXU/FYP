package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity";

    EditText etEmail, etPassword, etName, etNumber;
    Button btnRegister;
    TextView tvLogin;
    ProgressBar progressBar;
    FirebaseAuth fbAuth;
    FirebaseFirestore fbFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etContactNumber);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        fbAuth = FirebaseAuth.getInstance();
        fbFirestore = FirebaseFirestore.getInstance();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String number = etNumber.getText().toString().trim();

                tvLogin.setEnabled(false);
                btnRegister.setEnabled(false);
                etName.setEnabled(false);
                etNumber.setEnabled(false);
                etEmail.setEnabled(false);
                etPassword.setEnabled(false);

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required");
                    tvLogin.setEnabled(true);
                    btnRegister.setEnabled(true);
                    etName.setEnabled(true);
                    etNumber.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    tvLogin.setEnabled(true);
                    btnRegister.setEnabled(true);
                    etName.setEnabled(true);
                    etNumber.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    etName.setError("Display name is required");
                    tvLogin.setEnabled(true);
                    btnRegister.setEnabled(true);
                    etName.setEnabled(true);
                    etNumber.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(number)) {
                    etNumber.setError("Contact number is required");
                    tvLogin.setEnabled(true);
                    btnRegister.setEnabled(true);
                    etName.setEnabled(true);
                    etNumber.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    return;
                }

                if (password.length() < 6) {
                    etPassword.setError("Password must contain more than 5 characters");
                    tvLogin.setEnabled(true);
                    btnRegister.setEnabled(true);
                    etName.setEnabled(true);
                    etNumber.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    return;
                }

                if (number.length() != 8) {
                    etNumber.setError("contact number must contain exactly 8 characters");
                    tvLogin.setEnabled(true);
                    btnRegister.setEnabled(true);
                    etName.setEnabled(true);
                    etNumber.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    return;
                }

                fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userID = fbAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fbFirestore.collection("users").document(userID);
                            Toast.makeText(RegisterActivity.this, "Account has been successfully created", Toast.LENGTH_SHORT).show();
                            Map<String, Object> user = new HashMap<>();
                            int numInt = Integer.parseInt(number);
                            user.put("Email", email);
                            user.put("Name", name);
                            user.put("Contact Number", numInt);
                            user.put("Role", "Member");
                            progressBar.setVisibility(View.VISIBLE);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), ClassifierActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            tvLogin.setEnabled(true);
                            btnRegister.setEnabled(true);
                            etName.setEnabled(true);
                            etNumber.setEnabled(true);
                            etEmail.setEnabled(true);
                            etPassword.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });
    }
}
