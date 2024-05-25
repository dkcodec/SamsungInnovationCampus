package com.example.careerup.Fragments;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.careerup.R;
import com.squareup.picasso.Picasso;

public class JobDetailsFragment extends Fragment {
    private ImageView pictureImageView;
    private AppCompatButton apply, apply2;
    private TextView title, companyTextView, country, city, remote, jobEmploymentType, descTextView;

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
            if(jobCity!= "null")
                city.setText(jobCity);
            else
                city.setText("");
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
                    // Replace the current fragment with ApplyFragment
                    ApplyFragment fragment = new ApplyFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("url", applyLink);
                    fragment.setArguments(bundle);

                    // Get the FragmentManager and start a FragmentTransaction
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    // Replace the current fragment with ApplyFragment
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null); // Optional, to allow back navigation
                    transaction.commit();
                }
            });


            apply2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Обработка нажатия на кнопку apply2
                    // Создаем Intent с действием ACTION_VIEW и передаем в него ссылку
                    ApplyFragment fragment = new ApplyFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("url", applyLink);
                    fragment.setArguments(bundle);

                    // получаю другой фрагмент
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    // Запускаю изменение фрагмента на фрагмент с webView
                    transaction.replace(R.id.fragment_container, fragment);

                    // для навигации назад
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
        return view;
    }
}
