package com.example.careerup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;
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

public class JobPage extends Fragment {
    private TextView titleTextView, companyTextView, descTextView, qualificationsTextView, benifTextView;
    private ImageView imageView;


    public JobPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_page, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleTextView = view.findViewById(R.id.title);
        companyTextView = view.findViewById(R.id.companyTextView);
        descTextView = view.findViewById(R.id.descTextView);
        qualificationsTextView = view.findViewById(R.id.qualificationsTextView);
        benifTextView = view.findViewById(R.id.benifTextView);
        imageView = view.findViewById(R.id.pictureImageView);

        // Здесь вы можете получить данные о вакансии и установить их в соответствующие TextView и ImageView
        try {
            JSONObject jobData = new JSONObject(/* Ваш JSON-объект с данными о вакансии */);

            String title = jobData.optString("job_title");
            String company = jobData.optString("employer_name");
            String desc = jobData.optString("job_description");
            String qualifications = TextUtils.join("\n", jobData.optJSONArray("highlights").optJSONObject(0).optJSONArray("Qualifications"));
            String benefits = TextUtils.join("\n", jobData.optJSONArray("highlights").optJSONObject(2).optJSONArray("Benefits"));
            String imageUrl = jobData.optString("employer_logo");

            titleTextView.setText(title);
            companyTextView.setText(company);
            descTextView.setText(desc);
            qualificationsTextView.setText(qualifications);
            benifTextView.setText(benefits);

            // Загрузка изображения с помощью Picasso
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get().load(imageUrl).into(imageView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}