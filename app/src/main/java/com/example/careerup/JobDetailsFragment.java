package com.example.careerup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.careerup.R;
import com.squareup.picasso.Picasso;

public class JobDetailsFragment extends Fragment {
    private ImageView pictureImageView;
    private AppCompatButton apply, apply2;
    private TextView title, companyTextView, country, city, remote, jobEmploymentType, descTitleTextView, descTextView, responseTitleTextView, responseTextView, qualificationsTitleTextView, qualificationsTextView, benifTitleTextView, benifTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);

        pictureImageView = view.findViewById(R.id.pictureImageView);

        title = view.findViewById(R.id.title);
        companyTextView = view.findViewById(R.id.companyTextView);
        country = view.findViewById(R.id.country);
        city = view.findViewById(R.id.city);
        remote = view.findViewById(R.id.remote);
        jobEmploymentType = view.findViewById(R.id.jobEmploymentType);

        apply = view.findViewById(R.id.apply);
        apply2 = view.findViewById(R.id.apply2);

        descTitleTextView = view.findViewById(R.id.descTitleTextView);
        descTextView = view.findViewById(R.id.descTextView);

        // Получаем данные о работе из аргументов
        Bundle bundle = getArguments();
        if (bundle != null) {
            String applyLink = bundle.getString("job_apply_link");
            String jobTitle = bundle.getString("job_title");
            String employerName = bundle.getString("employer_name");
            String jobCountry = bundle.getString("job_country");
            String jobCity = bundle.getString("job_city");
            boolean jobRemote = bundle.getBoolean("job_is_remote");
            String jobEmploymenttype = bundle.getString("job_employment_type");
            String employerLogo = bundle.getString("employer_logo");
            String desc = bundle.getString("job_description");

            // Устанавливаем данные в TextView
            title.setText(jobTitle);
            companyTextView.setText(employerName);
            country.setText(jobCountry);
            city.setText(jobCity);
            if (jobRemote) {
                remote.setText("Remote");
            } else {
                remote.setText("On-site");
            }

            jobEmploymentType.setText(jobEmploymenttype);
            descTextView.setText(desc);

            if (employerLogo != null) {
                Picasso.get()
                        .load(employerLogo)
                        .placeholder(R.drawable.placeholder_job)
                        .fit()
                        .centerInside()
                        .into(pictureImageView);
            }


            // Работа с кнопками Apply
            apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Обработка нажатия на кнопку apply
                    // Создаем Intent с действием ACTION_VIEW и передаем в него ссылку
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(applyLink));

                    // Проверяем, есть ли приложение, которое может обработать Intent
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Если есть, запускаем Intent
                        startActivity(intent);
                    } else {
                        // Если нет, выводим уведомление, что нет приложения для открытия ссылки
                        Toast.makeText(getActivity(), "No application available to open the link", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            apply2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Обработка нажатия на кнопку apply2
                    // Создаем Intent с действием ACTION_VIEW и передаем в него ссылку
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(applyLink));

                    // Проверяем, есть ли приложение, которое может обработать Intent
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Если есть, запускаем Intent
                        startActivity(intent);
                    } else {
                        // Если нет, выводим уведомление, что нет приложения для открытия ссылки
                        Toast.makeText(getActivity(), "No application available to open the link", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return view;
    }
}
