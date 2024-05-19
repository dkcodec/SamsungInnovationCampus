package com.example.careerup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.JobAdapter;
import model.JobLS;

public class HomeFragment extends Fragment implements JobAdapter.OnJobClickListener {
    private Context mContext;
    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private ArrayList<JobLS> jobs;
    private RequestQueue requestQueue;

    // --------------- Pagination -----------
    private AppCompatButton prev, next;

    // ---------API----------------
    int page = 1;
    String query = "Frontend developer in new york";
    static boolean isFormated = false;

    // ------------- Серч -------------
    private TextInputEditText searchInput = null;
    private ImageButton buttonSearch;

    // ------------Кодировка для URL в ютф 8 чтоб сервер понимал
    public static String encodeURIComponent(String s) {
        String result;
        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "%21")
                    .replaceAll("\\%27", "%27")
                    .replaceAll("\\%28", "%28")
                    .replaceAll("\\%29", "%29")
                    .replaceAll("\\%7E", "%7E");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }
        isFormated = true;
        return result;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        jobs = new ArrayList<>();

        recyclerView = view.findViewById(R.id.RecyclerView); // Инициализация recyclerView

        // Нахожу кнопки
        prev = view.findViewById(R.id.prev);
        next = view.findViewById(R.id.next);

        // нахожу серч
        searchInput = view.findViewById(R.id.search_input);
        buttonSearch = view.findViewById(R.id.search_button);

//        // для проверки карточки в ресайкл вью
//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("RecyclerView", "Clicked");
//            }
//        });

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        recyclerView.setHasFixedSize(true);

        jobAdapter = new JobAdapter(mContext, jobs, this);
        recyclerView.setAdapter(jobAdapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = String.valueOf(searchInput.getText());
                isFormated = false;

                getJobs();

                Log.d("SEARCH_QUERY: ", query);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page > 1) {
                    page--;
                    getJobs();
                    Log.d("PAGE: ", String.valueOf(page));
                } else {
                    Toast.makeText(mContext, "This is the first page", Toast.LENGTH_SHORT).show();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                getJobs();
                Log.d("PAGE: ", String.valueOf(page));
            }
        });

        getJobs();

        return view;
    }

    private void getJobs() {
        requestQueue = Volley.newRequestQueue(mContext);

        if (!isFormated)
            query = encodeURIComponent(query);

        Log.println(Log.INFO, "HELLO: ", "HEllO " + query);

        String url = "https://jsearch.p.rapidapi.com/search?query=" + query + "&page=" + page + "&num_pages=1";
        Log.d("REQUEST URL: ", url);  // Log the request URL

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            jobs.clear(); // Clear the list
                            Log.d("RESPONSE: ", response.toString());  // Log the full response
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String title, employer_logo, employer_name, city, country, type, desc, apply;

                                title = jsonObject1.getString("job_title");
                                country = jsonObject1.getString("job_country");
                                city = jsonObject1.getString("job_city");
                                employer_logo = jsonObject1.getString("employer_logo");
                                employer_name = jsonObject1.getString("employer_name");
                                type = jsonObject1.getString("job_employment_type");
                                desc = jsonObject1.getString("job_description");
                                apply = jsonObject1.getString("job_apply_link");

                                JobLS job = new JobLS();
                                job.setJob_apply_link(apply);
                                if (city != "null")
                                    job.setJob_city(city);
                                else
                                    job.setJob_city("");
                                job.setJob_country(country);
                                job.setJob_title(title);
                                job.setEmployer_logo(employer_logo);
                                job.setEmployer_name(employer_name);
                                job.setJob_employment_type(type);
                                job.setJob_description(desc);

                                jobs.add(job);
                            }
                            jobAdapter.notifyDataSetChanged(); // Notify adapter of new data
                        } catch (JSONException e) {
                            Log.e("JSON ERROR: ", e.getMessage());  // Log JSON error
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR: ", error.getMessage());  // Log Volley error
                        Toast.makeText(mContext, "Fail to get data..", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-RapidAPI-Key", "f5b7ad29aemshd54e3b82f28e666p13f679jsn053d129bc9de");
                headers.put("X-RapidAPI-Host", "jsearch.p.rapidapi.com");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public void onJobClick(JobLS job) {
        Log.d("JobAdapterr", "onJobClick() called");
        JobDetailsFragment fragment = new JobDetailsFragment();

        Log.d("JobAdapter", "onJobClick: Starting...");

        // Создал Bundle для передачи данных в новый фрагмент
        Bundle bundle = new Bundle();
        bundle.putString("job_apply_link", job.getJob_apply_link());
        bundle.putString("job_title", job.getJob_title());
        bundle.putString("employer_name", job.getEmployer_name());
        bundle.putString("job_country", job.getJob_country());
        bundle.putString("job_city", job.getJob_city());
        bundle.putString("job_employment_type", job.getJob_employment_type());
        bundle.putString("job_description", job.getJob_description());
        bundle.putString("employer_logo", job.getEmployer_logo());
        bundle.putString("job_is_remote", String.valueOf(job.isJob_is_remote()));

        // Поставил аргументы в бандл
        fragment.setArguments(bundle);

        // заменил фрагмент на новый
        AppCompatActivity activity = (AppCompatActivity) mContext;
        activity.getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                .addToBackStack(null)
                .commit();


        Log.d("JobAdapter", "onJobClick: Fragment replaced successfully.");
    }

}
