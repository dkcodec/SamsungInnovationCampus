package com.example.careerup.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.careerup.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        Button inst = view.findViewById(R.id.inst);
        Button website = view.findViewById(R.id.website);

        // работа с кнопками контактов
        inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем Intent с действием ACTION_VIEW и передаем в него ссылку
                ApplyFragment fragment = new ApplyFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", "https://www.instagram.com/kair_dm?igsh=MW5nc2tmaWEzbnU3Mg==");
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

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем Intent с действием ACTION_VIEW и передаем в него ссылку
                ApplyFragment fragment = new ApplyFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", "https://dkcodec.github.io/Web-Profile/");
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

        return view;
    }
}