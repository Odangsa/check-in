package com.example.check.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import com.example.check.R;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 검색 뷰 초기화
        SearchView searchView = view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 쿼리 제출 시 처리
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색 텍스트 변경 시 처리 (필요한 경우)
                return false;
            }
        });

//        // "모두보기" 버튼 초기화
//        Button viewAllButton = view.findViewById(R.id.view_all_button);
//        viewAllButton.setOnClickListener(v -> {
//            // "모두보기" 버튼 클릭 시 처리
//            viewAllRecommendedBooks();
//        });
//
//        // 추천 도서 "더보기" 텍스트뷰 초기화
//        TextView moreInfo1 = view.findViewById(R.id.more_info_1);
//        TextView moreInfo2 = view.findViewById(R.id.more_info_2);

//        moreInfo1.setOnClickListener(v -> showBookDetails(1));
//        moreInfo2.setOnClickListener(v -> showBookDetails(2));
    }

    private void performSearch(String query) {
        // 검색 기능 구현
        // TODO: 검색 로직 추가
    }

    private void viewAllRecommendedBooks() {
        // 모든 추천 도서 보기 기능 구현
        // TODO: 모든 추천 도서 화면으로 이동하는 로직 추가
    }

    private void showBookDetails(int bookId) {
        // 특정 도서의 상세 정보 표시 기능 구현
        // TODO: 도서 상세 정보 화면으로 이동하는 로직 추가
    }
}