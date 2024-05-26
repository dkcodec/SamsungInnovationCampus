package com.example.careerup.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.example.careerup.Activities.Login;
import com.example.careerup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.util.ArrayList;

import data.JobAdapter;
import model.JobLS;

public class ObservedFragment extends Fragment implements JobAdapter.OnJobClickListener {
    private Context mContext;
    private JobAdapter jobAdapter;
    private ArrayList<JobLS> jobs;
    private RequestQueue requestQueue;
    private RecyclerView historyRecyclerView;
    // для показа загрузки пока данные не подтянулись в reyclerview
    private ProgressBar historyProgressBar;

    // -------- FireBase --------
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(mContext);
        database = FirebaseDatabase.getInstance("https://dijob-aafbe-default-rtdb.europe-west1.firebasedatabase.app");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // если не авторизован -> переход на логин
        if (currentUser == null) {
            Intent intent = new Intent(mContext, Login.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_observed, container, false);

        jobs = new ArrayList<>();

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyProgressBar = view.findViewById(R.id.historyProgressBar);

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        historyRecyclerView.setHasFixedSize(true);

        // Перенес инициализацию jobAdapter сюда
        jobAdapter = new JobAdapter(mContext, jobs, this);
        historyRecyclerView.setAdapter(jobAdapter);

        if (currentUser != null) {
            getJobsHistory();
        }

        return view;
    }

    // для подробной информации при клике на блок с работой откроется фрагмент с всей инфой
    @Override
    public void onJobClick(JobLS job) {
        Log.d("JobAdapterr", "onJobClick() called");

        JobDetailsFragment fragment = new JobDetailsFragment();

        Log.d("JobAdapter", "onJobClick: Starting...");

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


        Log.d("JobAdapter", "onJobClick: Fragment replaced successfully.");
    }

    // получаю данные из бд
    private void getJobsHistory() {
        historyProgressBar.setVisibility(View.VISIBLE);

        // парсю строку с почтой в вид для бд
        String emailKey = currentUser.getEmail().replace(".", ",");
        DatabaseReference userJobsRef = FirebaseDatabase.getInstance("https://dijob-aafbe-default-rtdb.europe-west1.firebasedatabase.app").getReference("jobs").child(emailKey);

        // получаю данные из fireBase
        userJobsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                // если подключено и запрос приходит, то делаю как в homefragment
                if (task.isSuccessful() && task.getResult() != null) {
                    jobs.clear();
                    for (DataSnapshot jobSnapshot : task.getResult().getChildren()) {
                        // метод добавляет все данные из бд в мой jobls и потом в job метод для оптимизации
                        JobLS job = jobSnapshot.getValue(JobLS.class);
                        if (job != null) {
                            jobs.add(job);
                        }
                    }
                    jobAdapter.notifyDataSetChanged();
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                historyProgressBar.setVisibility(View.GONE);
            }
        });
    }

}