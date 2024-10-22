package com.example.check.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.TypedValue;
import android.graphics.Typeface;
import android.view.Gravity;

import com.example.check.R;
import com.example.check.model.stampboard.StampBoard;
import com.example.check.model.stampboard.StampBoardViewModel;
import com.example.check.model.stampboard.Transportation;

import java.util.List;
import java.util.stream.Collectors;

public class StampBoardFragment extends Fragment {

    private StampBoardViewModel viewModel;
    private TextView transportationType;
    private LinearLayout stampBoardContainer;
    private LinearLayout transportationButtonsContainer;
    private ImageButton backButton;
    private ImageButton nextButton;

    private ScrollView stampBoardScrollView; // 배경 이미지를 설정할 ScrollView 추가

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stamp_board, container, false);

        transportationType = view.findViewById(R.id.transportationType);
        stampBoardContainer = view.findViewById(R.id.stampBoardContainer);
        transportationButtonsContainer = view.findViewById(R.id.transportationButtonsContainer);
        backButton = view.findViewById(R.id.backButton);
        nextButton = view.findViewById(R.id.nextButton);
        stampBoardScrollView = view.findViewById(R.id.stampBoardScrollView); // ScrollView 초기화

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StampBoardViewModel.class);

        viewModel.getStampBoard().observe(getViewLifecycleOwner(), this::updateUI);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        nextButton.setOnClickListener(v -> viewModel.nextTransportation());

        viewModel.fetchStampBoard(2); // Assuming user ID is 2
    }


    private void updateUI(StampBoard stampBoard) {
        if (stampBoard == null || stampBoard.getTransportation().isEmpty()) {
            return;
        }

        // 유효한 transportation만 필터링
        List<Transportation> validTransportations = stampBoard.getTransportation().stream()
                .filter(t -> t.getVisited_libraries() != null && !t.getVisited_libraries().isEmpty())
                .collect(Collectors.toList());

        updateTransportationButtons(validTransportations);

        // 현재 선택된 transportation 업데이트
        Transportation currentTransportation = viewModel.getCurrentTransportation();
        if (currentTransportation != null && currentTransportation.getVisited_libraries() != null
                && !currentTransportation.getVisited_libraries().isEmpty()) {
            updateTransportationType(currentTransportation);
            updateStampBoard(currentTransportation.getVisited_libraries());
        }
    }

    private void updateTransportationButtons(List<Transportation> validTransportations) {
        transportationButtonsContainer.removeAllViews();

        // 버튼들을 담을 LinearLayout 생성
        LinearLayout buttonRow = new LinearLayout(getContext());
        buttonRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setWeightSum(3);
        buttonRow.setPadding(
                dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));

        for (int i = 0; i < Math.min(2, validTransportations.size()); i++) {
            Transportation transportation = validTransportations.get(i);
            TextView button = createTransportationButton(transportation, i == 0);
            buttonRow.addView(button);
        }

        // Next 버튼 추가
        if (validTransportations.size() > 2) {
            ImageButton nextBtn = createNextButton();
            buttonRow.addView(nextBtn);
        }

        transportationButtonsContainer.addView(buttonRow);
    }

    private void updateTransportationType(Transportation transportation) {
        // 타입 텍스트 업데이트
        transportationType.setText(transportation.getType());

        // 아이콘 업데이트
        int iconResId = getTransportationIconResId(transportation.getType());
        transportationType.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);

        // 배경 이미지 업데이트
        updateBackgroundImage(transportation.getType());
    }


    private void updateBackgroundImage(String transportationType) {
        int backgroundResId;
        switch (transportationType.toLowerCase()) {
            case "뚜벅이":
                backgroundResId = R.drawable.img_stamp_background;
                break;
            case "킥보드":
                backgroundResId = R.drawable.stamp_kickboard;
                break;
            case "자전거":
                backgroundResId = R.drawable.stamp_bicycle;
                break;
            case "버스":
                backgroundResId = R.drawable.stamp_bus;
                break;
            case "기차":
                backgroundResId = R.drawable.stamp_train;
                break;
            case "비행기":
                backgroundResId = R.drawable.stamp_airplane;
                break;
            case "우주선":
                backgroundResId = R.drawable.stamp_spaceship;
                break;
            default:
                backgroundResId = R.drawable.stamp_walk;
                break;
        }

        // ScrollView의 배경 이미지 설정
        stampBoardScrollView.setBackgroundResource(backgroundResId);
    }

    private TextView createTransportationButton(Transportation transportation, boolean isFirst) {
        TextView button = new TextView(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                dpToPx(50), 1f);
        if (isFirst) {
            params.setMarginEnd(dpToPx(5));
        } else {
            params.setMarginStart(dpToPx(5));
            params.setMarginEnd(dpToPx(5));
        }
        button.setLayoutParams(params);

        button.setText(transportation.getType());
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        button.setTextColor(getResources().getColor(R.color.navy));
        button.setTypeface(button.getTypeface(), Typeface.BOLD);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(dpToPx(10), 0, 0, 0);
        button.setBackground(getResources().getDrawable(R.drawable.background_gray_stroke));

        int iconResId = getTransportationIconResId(transportation.getType());
        button.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        button.setCompoundDrawablePadding(dpToPx(5));

        button.setOnClickListener(v -> {
            viewModel.setCurrentTransportation(transportation);
            updateStampBoard(transportation.getVisited_libraries());
        });

        return button;
    }


    private ImageButton createNextButton() {
        ImageButton nextBtn = new ImageButton(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                dpToPx(50), 1f);
        params.setMarginStart(dpToPx(5));
        nextBtn.setLayoutParams(params);

        nextBtn.setImageResource(R.drawable.icon_next);
        nextBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nextBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        nextBtn.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));

        nextBtn.setOnClickListener(v -> viewModel.nextTransportation());

        return nextBtn;
    }

    private void updateStampBoard(List<String> visitedLibraries) {
        stampBoardContainer.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        if (visitedLibraries.isEmpty()) {
            // "None" 상태 처리
            TextView emptyStateView = new TextView(getContext());
            emptyStateView.setText("아직 방문한 도서관이 없습니다.");
            stampBoardContainer.addView(emptyStateView);
            return;
        }

        for (int i = 0; i < visitedLibraries.size(); i += 2) {
            View stampRow = inflater.inflate(R.layout.layout_stamp_row, stampBoardContainer, false);

            TextView libraryName1 = stampRow.findViewById(R.id.libraryName1);
            TextView libraryName2 = stampRow.findViewById(R.id.libraryName2);
            ImageView stampImage1 = stampRow.findViewById(R.id.stampImage1);
            ImageView stampImage2 = stampRow.findViewById(R.id.stampImage2);

            libraryName1.setText(visitedLibraries.get(i));
            stampImage1.setVisibility(View.VISIBLE);

            if (i + 1 < visitedLibraries.size()) {
                libraryName2.setText(visitedLibraries.get(i + 1));
                stampImage2.setVisibility(View.VISIBLE);
            } else {
                libraryName2.setVisibility(View.INVISIBLE);
                stampImage2.setVisibility(View.INVISIBLE);
            }

            stampBoardContainer.addView(stampRow);

            if (i + 2 < visitedLibraries.size()) {
                ImageView arrowDown = new ImageView(getContext());
                arrowDown.setImageResource(R.drawable.icon_arrow_down_left);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.weight = 0.5f;
                arrowDown.setLayoutParams(params);
                stampBoardContainer.addView(arrowDown);
            }
        }
    }

    private void updateTransportationButtons(Integer currentIndex) {
        StampBoard stampBoard = viewModel.getStampBoard().getValue();
        if (stampBoard == null || stampBoard.getTransportation() == null) {
            return;
        }
        List<Transportation> transportations = stampBoard.getTransportation();

        TextView walkButton = getView().findViewById(R.id.walkButton);
        TextView busButton = getView().findViewById(R.id.busButton);

        // 첫 번째와 두 번째 transportation 타입으로 버튼 설정
        if (transportations.size() > 0) {
            setTransportationButton(walkButton, transportations.get(0));
        }
        if (transportations.size() > 1) {
            setTransportationButton(busButton, transportations.get(1));
        }

        // Next 버튼 가시성 설정
        nextButton.setVisibility(transportations.size() > 2 ? View.VISIBLE : View.GONE);
    }

    private void setTransportationButton(TextView button, Transportation transportation) {
        button.setText(transportation.getType());
        button.setCompoundDrawablesWithIntrinsicBounds(getTransportationIconResId(transportation.getType()), 0, 0, 0);
        button.setOnClickListener(v -> viewModel.setCurrentTransportation(transportation));
    }

    private TextView createTransportationButton(Transportation transportation) {
        TextView button = (TextView) getLayoutInflater().inflate(R.layout.layout_transportation_button, null);
        button.setText(transportation.getType());
        button.setOnClickListener(v -> viewModel.setCurrentTransportation(transportation));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 0, 10, 0);
        button.setLayoutParams(params);

        int iconResId = getTransportationIconResId(transportation.getType());
        button.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);

        return button;
    }

    private int getTransportationIconResId(String type) {
        switch (type.toLowerCase()) {
            case "뚜벅이": return R.drawable.icon_walk;
//            case "킥보드": return R.drawable.icon_kickboard;
            case "자전거": return R.drawable.icon_bike;
            case "버스": return R.drawable.icon_bus;
//            case "기차": return R.drawable.icon_train;
//            case "비행기": return R.drawable.icon_airplane;
//            case "우주선": return R.drawable.icon_spaceship;
            default: return R.drawable.icon_walk;
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}