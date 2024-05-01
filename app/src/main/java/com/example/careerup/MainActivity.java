package com.example.careerup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import model.JobLS;
import okhttp3.*;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // -----------Авторизация-----------
    FirebaseAuth auth;
    TextView logOut, userName;
    FirebaseUser user;
    // -------------Меню-------------
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ----------------Авторизаци--------------

        // подключаю авториацию и нахожу по ID элементы которые буду менять
        auth = FirebaseAuth.getInstance();
//        logOut = findViewById(R.id.logout);
//        userName = findViewById(R.id.userName);
        // считаваю авторизован пользовтель или нет
        user = auth.getCurrentUser();

        // если не авторизован -> переход на логин
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
//        else {
//            // устанваливаю поле userName почтой пользователя
////            userName.setText(user.getEmail());
//        }

//        //     Разлогирование при нажатии на logOut
//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // Разлогирование пользователся с помощью Firebase
//                FirebaseAuth.getInstance().signOut();
//
//                // переход на страницу Логин
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        // -------------------Меню--------------------
        Toolbar toolbar = findViewById(R.id.Toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.userN);

        userName.setText(user.getEmail());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.search);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.vacancies) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
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

//    public void getData(){
//        try {
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url("https://jsearch.p.rapidapi.com/search?query="+query+"&page="+page+"&num_pages=1")
//                .get()
//                .addHeader("X-RapidAPI-Key", "dce0f7fdd8msh5d7766376aa5b8fp1618abjsnb4d000b11660")
//                .addHeader("X-RapidAPI-Host", "jsearch.p.rapidapi.com")
//                .build();
//            Response response = client.newCall(request).execute();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


//        String url = "https://jsearch.p.rapidapi.com/search?query="+query+"&page="+page+"&num_pages=1";
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
//                null, new Response.Listener<JSONObject>() {
//            public void onResponse(JSONObject jsonObject) {
//                try {
//                    JSONArray jsonArray =jsonObject.getJSONArray("data");
//                    for (int i=0;i<jsonArray.length();i++){
//                        JSONObject jsonObject1 =jsonArray.getJSONObject(i);
//                        String title, pictureUrl, category, instructions;
//                        title= jsonObject1.getString("strDrink");
//                        pictureUrl=jsonObject1.getString("strDrinkThumb");
//                        category=jsonObject1.getString("strCategory");
//                        instructions=jsonObject1.getString("strInstructions");
//                        JobLS jobLS=new JobLS();
//                        jobLS.setTitle(title);
//                        jobLS.setCategory(category);
//                        jobLS.setInstructions(instructions);
//                        jobLS.setPictureUrl(pictureUrl);
//                        jobLS.add(cocktail);
//                        cocktailAdapter=new CocktailAdapter(MainActivity.this,cocktails);
//                        recyclerView.setAdapter(cocktailAdapter);
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                volleyError.printStackTrace();
//            }
//        }) ;
//        requestQueue.add(request);
//    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

