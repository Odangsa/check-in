package com.example.check.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import android.widget.Toast;

import com.example.check.MainActivity;
import com.example.check.R;
import com.example.check.fragments.stamp.AuthCodeActivity;
import com.example.check.model.stampboard.StampBoard;
import com.example.check.model.stampboard.StampBoardViewModel;
import com.example.check.model.stampboard.Transportation;

import java.util.ArrayList;
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

        // 초기 배경 설정
        stampBoardScrollView.setBackgroundResource(R.drawable.img_stamp_background);

        // Observer 설정
        viewModel.getStampBoard().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getCurrentTransportationIndex().observe(getViewLifecycleOwner(), index -> {
            Transportation currentTransportation = viewModel.getCurrentTransportation();
            if (currentTransportation != null) {
                updateTransportationType(currentTransportation);
                updateStampBoard(currentTransportation.getVisited_libraries());
            }
        });

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        nextButton.setOnClickListener(v -> viewModel.nextTransportation());

        try {
            Long userId = MainActivity.userId;
            viewModel.fetchStampBoard(userId);
        } catch (NumberFormatException e) {
            Log.e("StampBoardFragment", "Invalid user ID format: " + MainActivity.userId);
            // 에러 처리 (예: 기본값 사용 또는 사용자에게 알림)
        }
    }



    private void updateStampBoard(List<String> visitedLibraries) {
        stampBoardContainer.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        if (visitedLibraries == null || visitedLibraries.isEmpty()) {
            TextView emptyStateView = new TextView(getContext());
            emptyStateView.setText("아직 방문한 도서관이 없습니다.");
            stampBoardContainer.addView(emptyStateView);
            return;
        }

        int currentStamps = visitedLibraries.size();
        boolean showAddButton = shouldShowAddButton();

        // 스탬프를 2개씩 짝지어 표시
        int totalRows = (int) Math.ceil((currentStamps + (showAddButton ? 1 : 0)) / 2.0);

        for (int i = 0; i < totalRows; i++) {
            View stampRow = inflater.inflate(R.layout.layout_stamp_row, stampBoardContainer, false);

            TextView libraryName1 = stampRow.findViewById(R.id.libraryName1);
            TextView libraryName2 = stampRow.findViewById(R.id.libraryName2);
            ImageView stampImage1 = stampRow.findViewById(R.id.stampImage1);
            ImageView stampImage2 = stampRow.findViewById(R.id.stampImage2);

            // 첫 번째 칸 처리
            int firstIndex = i * 2;
            if (firstIndex < currentStamps) {
                // 일반 스탬프 표시
                libraryName1.setText(visitedLibraries.get(firstIndex));
                stampImage1.setImageResource(R.drawable.img_seoul_library);
            } else if (showAddButton) {
                // + 버튼 표시
                libraryName1.setText("새 스탬프");
                stampImage1.setImageResource(R.drawable.icon_add);
                View.OnClickListener addClickListener = v -> showStampRegistrationDialog();
                stampImage1.setOnClickListener(addClickListener);
                libraryName1.setOnClickListener(addClickListener);
                showAddButton = false; // 한 번만 표시
            } else {
                libraryName1.setVisibility(View.INVISIBLE);
                stampImage1.setVisibility(View.INVISIBLE);
            }

            // 두 번째 칸 처리
            int secondIndex = i * 2 + 1;
            if (secondIndex < currentStamps) {
                // 일반 스탬프 표시
                libraryName2.setText(visitedLibraries.get(secondIndex));
                stampImage2.setImageResource(R.drawable.img_seoul_library);
            } else if (showAddButton) {
                // + 버튼 표시
                libraryName2.setText("새 스탬프");
                stampImage2.setImageResource(R.drawable.icon_add);
                View.OnClickListener addClickListener = v -> showStampRegistrationDialog();
                stampImage2.setOnClickListener(addClickListener);
                libraryName2.setOnClickListener(addClickListener);
            } else {
                libraryName2.setVisibility(View.INVISIBLE);
                stampImage2.setVisibility(View.INVISIBLE);
            }

            stampBoardContainer.addView(stampRow);

            // 마지막 줄이 아닐 경우에만 화살표 추가
            if (i < totalRows - 1) {
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

    private boolean shouldShowAddButton() {
        StampBoard board = viewModel.getStampBoard().getValue();
        if (board == null) return false;

        Transportation currentTransportation = viewModel.getCurrentTransportation();
        if (currentTransportation == null) return false;

        int currentIndex = board.getTransportation().indexOf(currentTransportation);
        int currentStamps = currentTransportation.getVisited_libraries().size();

        // 우주선인 경우 항상 + 버튼 표시
        if ("우주선".equals(currentTransportation.getType())) {
            return true;
        }

        // 첫 번째 타입이거나 이전 타입이 10개 이상일 때 + 버튼 표시
        if (currentStamps < 10 && (currentIndex == 0 ||
                (currentIndex > 0 &&
                        board.getTransportation().get(currentIndex - 1).getVisited_libraries().size() >= 10))) {
            return true;
        }

        // 현재 타입이 10개 이상이고 다음 타입이 None인 경우
        if (currentStamps >= 10) {
            for (int i = currentIndex + 1; i < board.getTransportation().size(); i++) {
                Transportation nextTransportation = board.getTransportation().get(i);
                if ("None".equals(nextTransportation.getVisited_libraries())) {
                    // 다음 타입으로 이동하고 + 버튼 표시
                    viewModel.setCurrentTransportation(nextTransportation);
                    return true;
                }
            }
        }

        return false;
    }
    // + 버튼 표시 여부 결정
//    private boolean shouldShowAddButton() {
//        StampBoard board = viewModel.getStampBoard().getValue();
//        if (board == null) return false;
//
//        Transportation currentTransportation = viewModel.getCurrentTransportation();
//        int currentIndex = board.getTransportation().indexOf(currentTransportation);
//
//        // 현재 타입의 스탬프 개수
//        int currentStamps = currentTransportation.getVisited_libraries().size();
//
//        // 첫 번째 타입이거나 이전 타입이 10개 이상일 때 + 버튼 표시
//        if (currentStamps < 10 && (currentIndex == 0 ||
//                (currentIndex > 0 &&
//                        board.getTransportation().get(currentIndex - 1).getVisited_libraries().size() >= 10))) {
//            return true;
//        }
//
//        // 현재 타입이 10개 이상이고 다음 타입이 None인 경우
//        if (currentStamps >= 10) {
//            for (int i = currentIndex + 1; i < board.getTransportation().size(); i++) {
//                Transportation nextTransportation = board.getTransportation().get(i);
//                if ("None".equals(nextTransportation.getVisited_libraries())) {
//                    // 다음 타입으로 이동하고 + 버튼 표시
//                    viewModel.setCurrentTransportation(nextTransportation);
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }


    private void updateUI(StampBoard stampBoard) {
        // 스탬프보드가 null이거나 transportation이 null일 때의 초기 상태 처리
        if (stampBoard == null || stampBoard.getTransportation() == null || stampBoard.getTransportation().isEmpty()) {
            // 초기 상태: 뚜벅이 버튼만 표시
            List<Transportation> initialTransportation = new ArrayList<>();
            Transportation walkTransportation = new Transportation();
            walkTransportation.setType("뚜벅이");
            walkTransportation.setVisited_libraries(new ArrayList<>()); // 빈 리스트로 초기화
            initialTransportation.add(walkTransportation);

            updateTransportationButtons(initialTransportation);
            updateTransportationType(walkTransportation);
            updateStampBoard(new ArrayList<>()); // 빈 스탬프 리스트로 업데이트 (+ 버튼이 표시됨)
            return;
        }

        // 기존 로직: 유효한 transportation 필터링
        List<Transportation> validTransportations = stampBoard.getTransportation().stream()
                .filter(t -> !"None".equals(t.getVisited_libraries()) && t.getVisited_libraries() != null)
                .collect(Collectors.toList());

        // 아무 데이터도 없는 경우 초기 상태로 설정
        if (validTransportations.isEmpty()) {
            Transportation walkTransportation = new Transportation();
            walkTransportation.setType("뚜벅이");
            walkTransportation.setVisited_libraries(new ArrayList<>());
            validTransportations.add(walkTransportation);
        }

        // 이전 타입의 도서관이 10개 이상인 경우에만 다음 타입도 표시
        List<Transportation> displayTransportations = new ArrayList<>();
        for (int i = 0; i < validTransportations.size(); i++) {
            Transportation current = validTransportations.get(i);
            if (i == 0 || (i > 0 && validTransportations.get(i-1).getVisited_libraries().size() >= 10)) {
                displayTransportations.add(current);
            }
        }

        updateTransportationButtons(displayTransportations);

        // 현재 선택된 transportation 업데이트
        Transportation currentTransportation = viewModel.getCurrentTransportation();
        if (currentTransportation == null && !displayTransportations.isEmpty()) {
            // 현재 선택된 transportation이 없으면 첫 번째 것 선택
            currentTransportation = displayTransportations.get(0);
            viewModel.setCurrentTransportation(currentTransportation);
        }

        if (currentTransportation != null) {
            updateTransportationType(currentTransportation);
            updateStampBoard(currentTransportation.getVisited_libraries());
        }
    }

    private void updateTransportationButtons(List<Transportation> validTransportations) {
        transportationButtonsContainer.removeAllViews();

        for (int i = 0; i < validTransportations.size(); i++) {
            Transportation transportation = validTransportations.get(i);
            TextView button = createTransportationButton(transportation);

            // 버튼 사이 마진 설정
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
            params.setMarginEnd(dpToPx(10));
            button.setLayoutParams(params);

            transportationButtonsContainer.addView(button);
        }
    }

    private void updateTransportationType(Transportation transportation) {
        transportationType.setText(transportation.getType());
        int iconResId = getTransportationIconResId(transportation.getType());
        transportationType.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);

        // 배경 이미지 업데이트
        String type = transportation.getType().toLowerCase();
        int backgroundResId;
        switch (type) {
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
                backgroundResId = R.drawable.img_stamp_background;
                break;
        }
        stampBoardScrollView.setBackgroundResource(backgroundResId);
    }



    private TextView createTransportationButton(Transportation transportation) {
        TextView button = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(120), // 고정된 너비
                ViewGroup.LayoutParams.MATCH_PARENT);
        button.setLayoutParams(params);

        button.setText(transportation.getType());
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        button.setTextColor(getResources().getColor(R.color.navy));
        button.setTypeface(button.getTypeface(), Typeface.BOLD);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(dpToPx(10), 0, dpToPx(10), 0);
        button.setBackground(getResources().getDrawable(R.drawable.background_gray_stroke));

        int iconResId = getTransportationIconResId(transportation.getType());
        button.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        button.setCompoundDrawablePadding(dpToPx(5));

        button.setOnClickListener(v -> {
            viewModel.setCurrentTransportation(transportation);
        });

        return button;
    }


    private void showStampRegistrationDialog() {
        // 새로운 Activity로 전환
        Intent intent = new Intent(getActivity(), AuthCodeActivity.class);
        startActivity(intent);
    }

    private void registerNewStamp(String libraryName) {
        Transportation currentTransportation = viewModel.getCurrentTransportation();
        if (currentTransportation != null) {
            // TODO: 실제 API 연동 시 사용할 코드
        /*
        viewModel.registerStamp(libraryName, currentTransportation.getType(),
            new OnStampRegistrationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "스탬프가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    // 스탬프 보드 새로고침
                    viewModel.fetchStampBoard(Integer.parseInt(MainActivity.userId));
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        */

            // 임시 테스트용 토스트 메시지
            String message = String.format("%s 도서관에 %s 타입의 스탬프를 등록합니다",
                    libraryName, currentTransportation.getType());
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
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