package com.example.careerup.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.careerup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private TextView emailData;
    private EditText passwordData, mobileData, addressData, postalCodeData, nameProfile;
    private AppCompatButton edit;
    private boolean isSave = true;

    // БАЗЫ ДАННЫХ
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    private FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private DatabaseReference userRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailData = view.findViewById(R.id.emailProfile);

        nameProfile = view.findViewById(R.id.nameProfile);
        passwordData = view.findViewById(R.id.passData);
        mobileData = view.findViewById(R.id.mobileDATA);
        addressData = view.findViewById(R.id.addressDATA);
        postalCodeData = view.findViewById(R.id.postalCodeData);

        edit = view.findViewById(R.id.btn_edit);

        // устанавливаю, что в начале нельзя редактировать пароль (inital state)
        setFieldsEditable(false);

        // установил почту из firebase
        emailData.setText(user.getEmail());

        // подключаю ВТОРУЮ БД
//        userRef = database.getReference("users");

        // при клике на кнопку меняю состояние профиля на редактирвоание либо сохранение
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSave) {
                    // Если была кнопка сохранить
                    isSave = false;
                    edit.setText(R.string.saveProfile);
                    setFieldsEditable(true);
                } else {
                    // если была кнопка редактировать
                    isSave = true;
                    edit.setText(R.string.editProfile);
                    // меняю на недоступно для редактирования
                    setFieldsEditable(false);
                    // обновляю пароль
                    if (!passwordData.getText().toString().isEmpty())
                        updatePassword();
                }
            }
        });
        return view;
    }

    // Правила для редактирования профиля
    private void setFieldsEditable(boolean isEditable) {
        passwordData.setFocusable(isEditable);
        passwordData.setFocusableInTouchMode(isEditable);
        passwordData.setInputType(isEditable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordData.setTextColor(isEditable? getResources().getColor(R.color.grwhite) : getResources().getColor(R.color.white));

        mobileData.setFocusable(isEditable);
        mobileData.setFocusableInTouchMode(isEditable);
        mobileData.setInputType(isEditable ? InputType.TYPE_CLASS_PHONE : InputType.TYPE_NULL);

        addressData.setFocusable(isEditable);
        addressData.setFocusableInTouchMode(isEditable);
        addressData.setInputType(isEditable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);

        postalCodeData.setFocusable(isEditable);
        postalCodeData.setFocusableInTouchMode(isEditable);
        postalCodeData.setInputType(isEditable ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_NULL);

        nameProfile.setFocusable(isEditable);
        nameProfile.setFocusableInTouchMode(isEditable);
        nameProfile.setInputType(isEditable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
    }
    // Смена пароля по FireBase
    private void updatePassword() {
        if (user != null) {
            // строка с новым паролем
            String newPassword = passwordData.getText().toString().trim();
            // обновляю пароль
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Если пароль успешно обновлен
                                // Переаутентификация пользователя с новым паролем
                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), newPassword);
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> reauthTask) {
                                                if (reauthTask.isSuccessful()) {
                                                    // Переаутентификация прла ЛОГ + ТОСТ
                                                    Log.d("Reauthentication", String.valueOf(R.string.reauthenticated));
                                                    Toast.makeText(getContext(), String.valueOf(R.string.reauthenticated), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Ошибка переаутентификации
                                                    Log.w("Reauthentication", String.valueOf(R.string.ERreauthenticated), reauthTask.getException());
                                                    Toast.makeText(getContext(), String.valueOf(R.string.ERreauthenticated), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // Ошибка при обновлении пароля
                                Log.w("UpdatePassword", String.valueOf(R.string.ERpass), task.getException());
                                Toast.makeText(getContext(), String.valueOf(R.string.ERpass), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // метод для сохранения данных профиля в Firebase Realtime Database
//    private void saveProfileData() {
//        if (user != null) {
//            String newName = nameProfile.getText().toString().trim();
//            String newMobile = mobileData.getText().toString().trim();
//            String newAddress = addressData.getText().toString().trim();
//            String newPostalCode = postalCodeData.getText().toString().trim();
//
//            // получаю уникальный идентификатор пользователя
//            String userId = user.getUid();
//
//            // Сохраняем данные профиля в базе данных Firebase
//            userRef.child(userId).child("name").setValue(newName);
//            userRef.child(userId).child("mobile").setValue(newMobile);
//            userRef.child(userId).child("address").setValue(newAddress);
//            userRef.child(userId).child("postalCode").setValue(newPostalCode);
//
//            Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show();
//        }
}
