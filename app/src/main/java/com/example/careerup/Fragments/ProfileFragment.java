package com.example.careerup.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import model.JobLS;
import model.ProfileLS;

public class ProfileFragment extends Fragment {
    // -------- коды для открытие галереи и сохранения ------
    private static final int GALLERY_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_CODE = 1002;
    private StorageReference storageRef;

    private Context mContext;
    private TextView emailData;
    private CircleImageView avatarData;
    private EditText passwordData, mobileData, addressData, postalCodeData, nameProfile;
    private AppCompatButton edit;
    private boolean isSave = true;

    // --------- FireBase ---------
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // --------- RealTimeDB ----------
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://dijob-aafbe.firebaseio.com");
    private DatabaseReference userRef = database.getReference("users");
    private String emailKey = String.valueOf(user.getEmail()).replace(".", ",");
    private ProfileLS userProfile;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(mContext);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailData = view.findViewById(R.id.emailProfile);

        avatarData = view.findViewById(R.id.profile_image);
        nameProfile = view.findViewById(R.id.nameProfile);
        passwordData = view.findViewById(R.id.passData);
        mobileData = view.findViewById(R.id.mobileDATA);
        addressData = view.findViewById(R.id.addressDATA);
        postalCodeData = view.findViewById(R.id.postalCodeData);

        userProfile = new ProfileLS();

        // вношу дату которая уже есть
        setTextFromFireBase();

        edit = view.findViewById(R.id.btn_edit);

        // устанавливаю, что в начале нельзя редактировать поля (inital state)
        setFieldsEditable(false);

        // установил почту из firebase
        emailData.setText(user.getEmail());


        // установка аватара по клику
        avatarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    } else {
                        openGallery();
                    }
            }
        });

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
                    setUserProfile(userProfile);
                    saveProfileToFirebase(userProfile);
                }
            }
        });
        return view;
    }
    // рарешения для памяти и галереи
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(mContext, String.valueOf(R.string.ERPermission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // открытие галереи
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }
    // конверт и утсановка картинки в URI а от туда уже в Cloud Storage
    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://dijob-aafbe.appspot.com");
        storageRef = storage.getReference();

        StorageReference fileRef = storageRef.child(emailKey + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                if (userProfile != null) {
                                    userProfile.setProfile_avatar(imageUrl); // Обновление URL аватара в объекте userProfile
                                    // установка изображения
                                    Picasso.get().load(imageUrl).into(avatarData);
                                    Log.d("IMAGEURLL", imageUrl);
                                    saveProfileToFirebase(userProfile);
                                    Log.d("USERPROFILE", userProfile.toString());
                                } else {
                                    // Обработка ситуации, когда userProfile равен null
                                    Log.e("ProfileFragment", String.valueOf(R.string.ERUserProfile));
                                }
                            }
                        });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(mContext, String.valueOf(R.string.ERUploadImg), Toast.LENGTH_SHORT).show();
                });
    }

    // устанавливаю данные в поля
    public void setTextFromFireBase() {
        userRef.child(emailKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        ProfileLS profile = dataSnapshot.getValue(ProfileLS.class);
                        if (profile != null) {
                            emailData.setText(user.getEmail());
                            addressData.setText(profile.getProfile_address());
                            mobileData.setText(profile.getProfile_mobile());
                            nameProfile.setText(profile.getProfile_name());
                            postalCodeData.setText(profile.getProfile_postalCode());
                            if (profile.getProfile_avatar() != null && !profile.getProfile_avatar().isEmpty()) {
                                Picasso.get().load(profile.getProfile_avatar()).into(avatarData);
                            }
                        } else {
                            Log.e("firebase", String.valueOf(R.string.ERGettingDataProf));
                        }
                    }
                } else {
                    Log.e("firebase", String.valueOf(R.string.ERGettingData), task.getException());
                }
            }
        });
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
    // сохранение данных в userProfile
    private void setUserProfile(ProfileLS profile) {
        // проверка на null и инициализация объекта userProfile при необходимости
        if (userProfile == null) {
            userProfile = new ProfileLS();
        }

        userProfile.setProfile_email(user.getEmail());
        userProfile.setProfile_address(String.valueOf(addressData.getText()));
        userProfile.setProfile_mobile(String.valueOf(mobileData.getText()));
        userProfile.setProfile_postalCode(String.valueOf(postalCodeData.getText()));
        userProfile.setProfile_name(String.valueOf(nameProfile.getText()));
        // обновление URL аватара, если он был установлен
        if (profile.getProfile_avatar() != null) {
            userProfile.setProfile_avatar(profile.getProfile_avatar());
            Log.d("UPDATEAVATAR:", userProfile.getProfile_avatar());
        }
        Picasso.get()
                .load(profile.getProfile_avatar())
                .error(R.drawable.placeholder_job) // Set a default image in case of error
                .into(avatarData);
    }

    // Подключаение 3 бд для хранения пользовательской информации
    private void saveProfileToFirebase(ProfileLS profile) {
        Log.d("REF", userRef.toString());

        // создаю HashMap для хранения информации о работе
        Map<String, Object> profileData = new HashMap<>();
        if (!nameProfile.getText().toString().isEmpty())
            profileData.put("profile_name", profile.getProfile_name());
        if (!addressData.getText().toString().isEmpty())
            profileData.put("profile_address", profile.getProfile_address());
        if (!emailData.getText().toString().isEmpty())
            profileData.put("profile_email", profile.getProfile_email());
        if (!mobileData.getText().toString().isEmpty())
            profileData.put("profile_mobile", profile.getProfile_mobile());
        if (!postalCodeData.getText().toString().isEmpty())
            profileData.put("profile_postalCode", profile.getProfile_postalCode());
        if (profile.getProfile_avatar() != null && !profile.getProfile_avatar().isEmpty()) {
            profileData.put("profile_avatar", profile.getProfile_avatar());
        }

        // сохраняю информацию о работе под job_id
        userRef.child(emailKey).setValue(profileData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, R.string.ONsaved, Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseJOB", String.valueOf(R.string.ONsaved));
                    } else {
                        Toast.makeText(mContext, R.string.ERsaved, Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseJOB", String.valueOf(R.string.ERsaved), task.getException());
                    }
                });
    }
}
