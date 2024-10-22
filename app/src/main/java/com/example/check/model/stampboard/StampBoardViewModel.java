package com.example.check.model.stampboard;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StampBoardViewModel extends ViewModel {


    private MutableLiveData<Integer> currentTransportationIndex = new MutableLiveData<>(0);
    private MutableLiveData<StampBoard> stampBoard = new MutableLiveData<>();
    private ApiService apiService;

    public StampBoardViewModel() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<StampBoard> getStampBoard() {
        return stampBoard;
    }

    public LiveData<Integer> getCurrentTransportationIndex() {
        return currentTransportationIndex;
    }


    public void nextTransportation() {
        StampBoard board = stampBoard.getValue();
        if (board != null) {
            List<Transportation> transportations = board.getTransportation();
            int currentIndex = transportations.indexOf(getCurrentTransportation());

            // 현재 타입이 10개 이상인 경우에만 다음으로 이동
            if (currentIndex >= 0 && currentIndex < transportations.size() - 1) {
                Transportation current = transportations.get(currentIndex);
                if (current != null &&
                        current.getVisited_libraries() != null &&
                        current.getVisited_libraries().size() >= 10) {
                    currentTransportationIndex.setValue(currentIndex + 1);
                }
            }
        }
    }

    public Transportation getCurrentTransportation() {
        StampBoard board = stampBoard.getValue();
        Integer index = currentTransportationIndex.getValue();
        if (board != null && index != null) {
            List<Transportation> validTransportations = board.getTransportation().stream()
                    .filter(t -> t.getVisited_libraries() != null && !t.getVisited_libraries().isEmpty())
                    .collect(Collectors.toList());

            if (!validTransportations.isEmpty() && index < validTransportations.size()) {
                return validTransportations.get(index);
            }
        }
        return null;
    }

    public void setCurrentTransportation(Transportation transportation) {
        StampBoard board = stampBoard.getValue();
        if (board != null) {
            List<Transportation> validTransportations = board.getTransportation().stream()
                    .filter(t -> t.getVisited_libraries() != null && !t.getVisited_libraries().isEmpty())
                    .collect(Collectors.toList());

            int index = validTransportations.indexOf(transportation);
            if (index != -1) {
                currentTransportationIndex.setValue(index);
            }
        }
    }


    public void fetchStampBoard(int userId) {
        Log.d("StampBoardViewModel", "fetchStampBoard called with userId: " + userId);
        apiService.getStampBoard(userId).enqueue(new Callback<StampBoard>() {
            @Override
            public void onResponse(Call<StampBoard> call, Response<StampBoard> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StampBoard receivedStampBoard = response.body();
                    stampBoard.setValue(receivedStampBoard);
                    Log.i("StampBoardViewModel", "StampBoard fetched successfully: " + receivedStampBoard.toString());
                } else {
                    Log.e("StampBoardViewModel", "Error fetching StampBoard: " + response.code());
                    // 에러 상태를 나타내는 빈 StampBoard 설정
                    stampBoard.setValue(new StampBoard());
                }
            }

            @Override
            public void onFailure(Call<StampBoard> call, Throwable t) {
                Log.e("StampBoardViewModel", "Failed to fetch StampBoard", t);
                // 에러 상태를 나타내는 빈 StampBoard 설정
                stampBoard.setValue(new StampBoard());
            }
        });
    }


}