package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationFragment extends Fragment {

    TextView notifyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notifyText = view.findViewById(R.id.notifications);

        if (getArguments() != null) {
            String message = getArguments().getString("message");
            notifyText.setText(message);
        }

        return view;
    }
}