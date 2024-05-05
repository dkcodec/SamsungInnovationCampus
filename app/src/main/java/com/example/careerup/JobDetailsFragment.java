package com.example.careerup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

        responseTitleTextView = view.findViewById(R.id.responseTitleTextView);
        responseTextView = view.findViewById(R.id.responseTextView);

        qualificationsTitleTextView = view.findViewById(R.id.qualificationsTitleTextView);
        qualificationsTextView = view.findViewById(R.id.qualificationsTextView);

        benifTitleTextView = view.findViewById(R.id.benifTitleTextView);
        benifTextView = view.findViewById(R.id.benifTextView);

        // Получаем данные о работе из аргументов
        Bundle bundle = getArguments();
        if (bundle != null) {
            String jobTitle = bundle.getString("job_title");
            String employerName = bundle.getString("employer_name");
            String jobCountry = bundle.getString("job_country");
            String jobCity = bundle.getString("job_city");
            boolean jobRemote = bundle.getBoolean("job_is_remote");
            String jobEmploymenttype = bundle.getString("job_employment_type");
            String employerLogo = bundle.getString("employer_logo");
            String desc = bundle.getString("job_description");
            String[] qualifications = bundle.getStringArray("qualifications");
            String[] responsibilities = bundle.getStringArray("responsibilities");
            String[] benefits = bundle.getStringArray("benefits");

            // Устанавливаем данные в TextView
            title.setText(jobTitle);
            companyTextView.setText(employerName);
            country.setText(jobCountry);
            city.setText(jobCity);
            if(jobRemote){
                remote.setText("Remote");
            } else{
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

            // Устанавливаем данные о квалификациях, обязанностях и преимуществах
            if (qualifications != null && qualifications.length > 0) {
                setTextViewFromArray(qualificationsTitleTextView, qualificationsTextView, "Qualifications", qualifications);
            }
            if (responsibilities != null && responsibilities.length > 0) {
                setTextViewFromArray(responseTitleTextView, responseTextView, "Responsibilities", responsibilities);
            }
            if (benefits != null && benefits.length > 0) {
                setTextViewFromArray(benifTitleTextView, benifTextView, "Benefits", benefits);
            }
        }
        return view;
    }
    private void setTextViewFromArray(TextView titleTextView, TextView contentTextView, String title, String[] contentArray) {
        // Проверяю, есть ли данные
        if (contentArray != null && contentArray.length > 0) {
            // Устанавливаю заголовок
            titleTextView.setText(title);

            // Создаю строку из массива данных
            StringBuilder stringBuilder = new StringBuilder();
            for (String item : contentArray) {
                stringBuilder.append("\u2022 ").append(item).append("\n");
            }

            // Устанавливаю данные в соответствующий TextView
            contentTextView.setText(stringBuilder.toString().trim());
        } else {
            // Если данных нет, скрываю соответствующие TextView
            titleTextView.setVisibility(View.GONE);
            contentTextView.setVisibility(View.GONE);
        }
    }

}
