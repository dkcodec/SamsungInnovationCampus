package com.example.careerup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.careerup.Fragments.AboutFragment;
import com.example.careerup.Fragments.HomeFragment;
import com.example.careerup.Fragments.ObservedFragment;
import com.example.careerup.Fragments.ProfileFragment;
import com.example.careerup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.ProfileLS;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // -----------Авторизация-----------
    FirebaseAuth auth;
    TextView logOut, userName;
    FirebaseUser user;
    // -------------Меню-------------
    private DrawerLayout drawerLayout;

    // ----------- БД ----------------
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://dijob-aafbe.firebaseio.com");
    private DatabaseReference userRef = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ----------------Авторизаци--------------

        // подключаю авториацию и нахожу по ID элементы которые буду менять
        auth = FirebaseAuth.getInstance();
        // считаваю авторизован пользовтель или нет
        user = auth.getCurrentUser();

        String emailKey = String.valueOf(user.getEmail()).replace(".", ",");
        // если не авторизован -> переход на логин
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        // -------------------Меню--------------------
        Toolbar toolbar = findViewById(R.id.Toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.userN);


        userRef.child(emailKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DataSnapshot dataSnapshot = task.getResult();
                                                                        if (dataSnapshot != null && dataSnapshot.exists()) {
                                                                            ProfileLS profile = dataSnapshot.getValue(ProfileLS.class);
                                                                            if (profile != null) {
                                                                                userName.setText(profile.getProfile_name());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } );

        if (userName.getText().length() == 0) {
            userName.setText(user.getEmail());
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.vacancies);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.vacancies) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (id == R.id.profile){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else if (id == R.id.history){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ObservedFragment()).commit();
        } else if (id == R.id.logout) {
            // Выход из аккаунта при выборе пункта "Logout"
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        // Закрываем NavigationDrawer после выбора пункта меню
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

