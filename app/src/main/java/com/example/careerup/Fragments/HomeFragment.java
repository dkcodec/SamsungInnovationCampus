package com.example.careerup.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.careerup.R;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // для показа загрузки пока данные не подтянулись в reyclerview
    private ProgressBar progressBar;

    // --------- FireBase ---------
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // --------------- Pagination -----------
    private AppCompatButton prev, next;
    private TextView pageNumber;

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
        FirebaseApp.initializeApp(mContext);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        jobs = new ArrayList<>();

        // Инициализация recyclerView и прогрессбара для него
        recyclerView = view.findViewById(R.id.RecyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        // Нахожу кнопки ( ПАГИНАЦИЯ )
        prev = view.findViewById(R.id.prev);
        next = view.findViewById(R.id.next);
        pageNumber = view.findViewById(R.id.pageNumber);
        pageNumber.setText(String.valueOf(page));

        // нахожу серч
        searchInput = view.findViewById(R.id.search_input);
        buttonSearch = view.findViewById(R.id.search_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        recyclerView.setHasFixedSize(true);

        jobAdapter = new JobAdapter(mContext, jobs, this);
        recyclerView.setAdapter(jobAdapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = String.valueOf(searchInput.getText());
                // убираю форматирование текста
                isFormated = false;
                // обновляю номер страницы
                page = 1;

                // отобразил стр
                pageNumber.setText(String.valueOf(page));

                getJobs();
            }
        });

        // при клике уменьшаю перехожу на предидущую стр
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page > 1) {
                    page--;
                    pageNumber.setText(String.valueOf(page));
                    getJobs();
                } else {
                    Toast.makeText(mContext, R.string.firstPage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // при клике перехожу на некст стр
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page < 10) {
                    page++;
                    pageNumber.setText(String.valueOf(page));
                    getJobs();
                } else {
                    Toast.makeText(mContext, R.string.lastPage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        getJobs();

        return view;
    }

    // Работа с апишкой, кидаю запрос, получаю данные, вывожу их
    private void getJobs() {
        progressBar.setVisibility(View.VISIBLE);
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

                                String title, employer_logo, employer_name, city, country, type, desc, apply, id;

                                id = jsonObject1.getString("job_id");
                                title = jsonObject1.getString("job_title");
                                country = jsonObject1.getString("job_country");
                                city = jsonObject1.getString("job_city");
                                employer_logo = jsonObject1.getString("employer_logo");
                                employer_name = jsonObject1.getString("employer_name");
                                type = jsonObject1.getString("job_employment_type");
                                desc = jsonObject1.getString("job_description");
                                apply = jsonObject1.getString("job_apply_link");

                                JobLS job = new JobLS();
                                job.setJob_id(id);
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
                            // Обновляю данные
                            jobAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("JSON ERROR: ", e.getMessage());
                            throw new RuntimeException(e);
                        } finally {
                            // finally выполняется всегда после try catch
                            // Скрываю ProgressBar
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR: ", error.getMessage());  // Log Volley error
                        Toast.makeText(mContext, R.string.ERGettingData, Toast.LENGTH_SHORT).show();
                        // при ошибке тоже скрываю
                        progressBar.setVisibility(View.GONE);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-RapidAPI-Key", "de52d40859msh0a3d49d57b118a2p11ecc9jsnea162425407f");
                headers.put("X-RapidAPI-Host", "jsearch.p.rapidapi.com");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    // для подробной информации при клике на блок с работой откроется фрагмент с всей инфой
    @Override
    public void onJobClick(JobLS job) {
        Log.d("JobAdapterr", "onJobClick() called");

        // Сохраняю данные в Firebase RealTimeDataBase
        saveJobToFirebase(job, String.valueOf(currentUser.getEmail()));

        JobDetailsFragment fragment = new JobDetailsFragment();


        // Создал Bundle для передачи данных в новый фрагмент
        Bundle bundle = new Bundle();
        bundle.putString("job_id", job.getJob_id());
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
    }

    // сохранение данных для истории в FireBase 2ая база данных
    private void saveJobToFirebase(JobLS job, String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dijob-aafbe-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference usersRef = database.getReference("jobs");
        Log.d("REF", usersRef.toString());

        String emailKey = email.replace(".", ",");
        Log.d("EMAIL", emailKey);

        // создаю HashMap для хранения информации о работе
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("job_id", job.getJob_id());
        jobData.put("job_apply_link", job.getJob_apply_link());
        jobData.put("job_title", job.getJob_title());
        jobData.put("employer_name", job.getEmployer_name());
        jobData.put("job_country", job.getJob_country());
        jobData.put("job_city", job.getJob_city());
        jobData.put("job_employment_type", job.getJob_employment_type());
        jobData.put("job_description", job.getJob_description());
        jobData.put("employer_logo", job.getEmployer_logo());
        jobData.put("job_is_remote", job.isJob_is_remote());

        // сохраняю информацию о работе под job_id
        usersRef.child(emailKey).child(job.getJob_id()).setValue(jobData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseJOB", "Job saved successfully");
                    } else {
                        Log.e("FirebaseJOB", "Failed to save job", task.getException());
                    }
                });
    }

}
