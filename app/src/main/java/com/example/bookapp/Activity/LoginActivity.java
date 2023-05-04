package com.example.bookapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText etmail, etpass;
    private TextView tvRegister;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth= FirebaseAuth.getInstance();

         etmail = findViewById(R.id.tvEmail);
         etpass = findViewById(R.id.tvPass);
         btnLogin= findViewById(R.id.btnLogin);
         tvRegister = findViewById(R.id.tvRegister);

         btnLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 login();
             }
         });
        /* tvRegister.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 register();
             }
         });*/
    }
    /*private void register(){
        intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }*/
    private void login(){
        String email, password;
        email = etmail.getText().toString();
        password= etpass.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Vui lòng nhập email !",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Vui lòng nhập password !",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Đăng nhập không thành công",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
