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
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
    private AppCompatButton prev, next;



    // ---------API----------------
    int page = 1;
    String query = "Frontend developer in new york";

    // ------------Кодировка для URL в ютф 8 чтоб сервер понимал
    public static String encodeURIComponent(String s) {
        String result;
        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }
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

//        // для проверки карточки в ресайкл вью
//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("RecyclerView", "Clicked");
//            }
//        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page > 0) {
                    page--;
                }
                else
                    if(page<10)
                        Toast.makeText(getContext(), "Это первая страница", Toast.LENGTH_LONG);
                    else
                        Toast.makeText(getContext(), "Это последняя страница", Toast.LENGTH_LONG);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        getJobs(this);

        recyclerView.setHasFixedSize(true);

        jobAdapter = new JobAdapter(mContext, jobs, this);
        recyclerView.setAdapter(jobAdapter);

        return view;
    }

    private void getJobs(JobAdapter.OnJobClickListener listener){
        requestQueue= Volley.newRequestQueue(mContext);

        query = encodeURIComponent(query);
        Log.println(Log.INFO,"HELLO: ","HEllO " + query);

        String url = "https://jsearch.p.rapidapi.com/search?query=" + query + "&page=" + page + "&num_pages=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        // Обработка ответа от сервера и добавление данных в список jobs
                        // Обновление RecyclerView после получения данных
                        try {
                            JSONArray jsonArray =response.getJSONArray("data");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1 =jsonArray.getJSONObject(i);

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
                                job.setJob_city(city);
                                job.setJob_country(country);
                                job.setJob_title(title);
                                job.setEmployer_logo(employer_logo);
                                job.setEmployer_name(employer_name);
                                job.setJob_employment_type(type);
                                job.setJob_description(desc);


                                jobs.add(job);

                                jobAdapter = new JobAdapter(mContext, jobs, listener);

                                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                                recyclerView.setAdapter(jobAdapter);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Fail to get data..", Toast.LENGTH_SHORT).show();
                        // Обработка ошибки
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-RapidAPI-Key","dce0f7fdd8msh5d7766376aa5b8fp1618abjsnb4d000b11660");
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
