package com.example.careerup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.careerup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button btnReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Проверяю ЕСЛИ АВТОРИЗОВАН, то на главную страницу
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
//        Поля для считывания значений
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);

        textView = findViewById(R.id.loginNow);
//        Переход на страницу логина с регистрации
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Колесо загрузки
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
//                парсинг текста с полей в строку
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
//              Уведомления для пользователей если НЕ ввели почту
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_LONG).show();
                    return;
                }
//              Уведомления для пользователей если НЕ ввели пароль
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_LONG).show();
                    return;
                }
//              регистрация с офиц. доки FireBase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                              убераю колесо загрузки
                                progressBar.setVisibility(View.GONE);
                                // вывод уведомления о УДАЧНОЙ регистрации
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Authentication created.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // вывод уведомления о НЕ УДАЧНОЙ регистрации
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}