package com.example.careerup.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.example.careerup.R;

import java.util.ArrayList;

import data.JobAdapter;
import model.JobLS;

public class ObservedFragment extends Fragment {
    private Context mContext;
    private JobAdapter jobAdapter;
    private ArrayList<JobLS> jobs;
    private RequestQueue requestQueue;
    private RecyclerView historyRecyclerView;
    // для показа загрузки пока данные не подтянулись в reyclerview
    private ProgressBar historyProgressBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_observed, container, false);

        jobs = new ArrayList<>();

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyProgressBar = view.findViewById(R.id.historyProgressBar);

//        historyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//
//        historyRecyclerView.setHasFixedSize(true);
//
//        jobAdapter = new JobAdapter(mContext, jobs, this);
//        historyRecyclerView.setAdapter(jobAdapter);
//
//        getJobsHistory();

        return view;
    }

}