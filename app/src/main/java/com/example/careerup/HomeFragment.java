package com.example.careerup;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import data.JobAdapter;
import model.JobLS;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private ArrayList<JobLS> jobs;
    private RequestQueue requestQueue;

    // ---------API----------------
    int page = 1;
    String query = "Frontend developer in new york";
    OkHttpClient client = new OkHttpClient();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        recyclerView =findViewById(R.id.RecyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cocktails=new ArrayList<>();
        requestQueue= Volley.newRequestQueue(this);
        getJobs();
    }

    private void getJobs(){
            try {
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://jsearch.p.rapidapi.com/search?query="+query+"&page="+page+"&num_pages=1")
                        .get()
                        .addHeader("X-RapidAPI-Key", "dce0f7fdd8msh5d7766376aa5b8fp1618abjsnb4d000b11660")
                        .addHeader("X-RapidAPI-Host", "jsearch.p.rapidapi.com")
                        .build();
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        String url = "https://www.thecocktaildb.com/api/json/v1/1/search.php?f="+letter;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONArray jsonArray =jsonObject.getJSONArray("drinks");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 =jsonArray.getJSONObject(i);
                        String title, pictureUrl, category, instructions;
                        title= jsonObject1.getString("strDrink");
                        pictureUrl=jsonObject1.getString("strDrinkThumb");
                        category=jsonObject1.getString("strCategory");
                        instructions=jsonObject1.getString("strInstructions");
                        CocktILS cocktail=new CocktILS();
                        cocktail.setTitle(title);
                        cocktail.setCategory(category);
                        cocktail.setInstructions(instructions);
                        cocktail.setPictureUrl(pictureUrl);
                        cocktails.add(cocktail);
                        cocktailAdapter=new CocktailAdapter(MainActivity.this,cocktails);
                        recyclerView.setAdapter(cocktailAdapter);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) ;
        requestQueue.add(request);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            query = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(query);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}