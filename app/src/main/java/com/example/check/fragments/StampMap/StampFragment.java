package com.example.check.fragments.StampMap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.check.Adapter.stamp.TransportationAdapter;
import com.example.check.R;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.model.stampboard.StampBoard;
import com.example.check.model.stampboard.Transportation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StampFragment extends Fragment {

    private static final String TAG = "StampFragment";
    private RecyclerView stampRecyclerView;
    private TransportationAdapter adapter;
    private TextView currentTransportationTextView;
    private List<Transportation> allTransportations;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main2_stamp, container, false);

        stampRecyclerView = view.findViewById(R.id.stampRecyclerView);
        currentTransportationTextView = view.findViewById(R.id.currentTransportationTextView);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        stampRecyclerView.setLayoutManager(layoutManager);

        adapter = new TransportationAdapter(new ArrayList<>());
        stampRecyclerView.setAdapter(adapter);

        view.findViewById(R.id.backButton).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.walkButton).setOnClickListener(v -> updateTransportationType("뚜벅이"));
        view.findViewById(R.id.busButton).setOnClickListener(v -> updateTransportationType("버스"));

        loadData();

        return view;
    }

    private void loadData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int userId = 2; // 실제 사용자 ID로 교체하거나 SharedPreferences에서 가져오세요

        apiService.getStampBoard(userId).enqueue(new Callback<StampBoard>() {
            @Override
            public void onResponse(Call<StampBoard> call, Response<StampBoard> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StampBoard stampBoard = response.body();
                    allTransportations = stampBoard.getTransportation();
                    updateTransportationType(allTransportations.get(0).getType());
                } else {
                    Log.e(TAG, "Failed to load stamp board: " + response.message());
                    showToast("스탬프 보드 로딩 실패");
                }
            }

            @Override
            public void onFailure(Call<StampBoard> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                showToast("네트워크 오류");
            }
        });
    }

    private void updateTransportationType(String type) {
        for (Transportation transportation : allTransportations) {
            if (transportation.getType().equals(type)) {
                adapter.updateData(transportation.getVisited_libraries());
                currentTransportationTextView.setText(type);
                updateTransportationIcon(type);
                break;
            }
        }
    }

    private void updateTransportationIcon(String type) {
        int iconResId = getTransportationIconResourceId(type);
        currentTransportationTextView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
    }

    private int getTransportationIconResourceId(String type) {
        switch (type.toLowerCase()) {
            case "자전거":
                return R.drawable.icon_bike;
            case "뚜벅이":
                return R.drawable.icon_walk;
            case "버스":
                return R.drawable.icon_bus;
            default:
                return R.drawable.icon_bike; // Default icon
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}