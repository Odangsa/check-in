package com.example.check.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.check.R;

public class BBTIFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bbti, container, false);

        // 시작 버튼이 있다면 리스너를 설정합니다.
//        Button startButton = view.findViewById(R.id.startButton);
//        if (startButton != null) {
//            startButton.setOnClickListener(v -> startBBTITest());
//        }

        return view;
    }

    private void startBBTITest() {
        // TODO: BBTI 테스트를 시작하는 로직을 구현합니다.
        // 예: 새로운 Fragment로 전환하거나 Dialog를 표시합니다.
    }
}