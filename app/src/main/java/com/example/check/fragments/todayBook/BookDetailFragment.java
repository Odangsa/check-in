package com.example.check.fragments.todayBook;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.check.Adapter.detail_book.BookDetailLibraryAdapter;
import com.example.check.R;
import com.example.check.Utils.LocationUtil;
import com.example.check.api.ApiClient;
import com.example.check.api.ApiService;
import com.example.check.model.bookDetail.BookDetailModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    private static final String ARG_ISBN = "isbn";
    private LocationUtil locationUtil;

    private ImageView bookImageView;
    private TextView bookNameTextView, authorTextView, publisherTextView, publishYearTextView, descriptionTextView;
    private RecyclerView librariesRecyclerView;
    private BookDetailLibraryAdapter libraryAdapter;

    private ApiService apiService;
    private String isbn;

    public static BookDetailFragment newInstance(String isbn) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN, isbn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient().create(ApiService.class);
        locationUtil = new LocationUtil(requireContext());
        if (getArguments() != null) {
            isbn = getArguments().getString(ARG_ISBN);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isbn != null) {
            getCurrentLocation();
        } else {
            showToast("ISBN이 제공되지 않았습니다.");
        }
    }

    private void getCurrentLocation() {
        locationUtil.getCurrentLocation(requireActivity(), new LocationUtil.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                if (location != null) {
                    Log.d(TAG, "현재 위치: 위도 = " + location.getLatitude() + ", 경도 = " + location.getLongitude());
                    loadBookDetails(isbn, location);
                    if (libraryAdapter != null && libraryAdapter.getLibraries() != null) {
                        LocationUtil.updateLibrariesWithCurrentLocation(location, libraryAdapter.getLibraries());
                        libraryAdapter.notifyDataSetChanged();
                    }
                } else {
                    // 권한이 새로 부여되어 위치 조회를 다시 시도해야 하는 경우
                    getCurrentLocation();
                }
            }

            @Override
            public void onLocationError(String error) {
                Log.e(TAG, "위치 오류: " + error);
                showToast("위치 정보를 가져올 수 없습니다. " + error);
            }
        });
    }

    private void loadBookDetails(String isbn, Location location) {
        if (location == null) {
            Log.e(TAG, "위치 정보가 없습니다. 책 정보를 가져올 수 없습니다.");
            showToast("위치 정보를 확인할 수 없습니다. 다시 시도해 주세요.");
            return;
        }

        Call<BookDetailModel> call = apiService.getBookDetails(isbn, location.getLatitude(), location.getLongitude());

        call.enqueue(new Callback<BookDetailModel>() {
            @Override
            public void onResponse(Call<BookDetailModel> call, Response<BookDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "서버 응답: " + response.body().toString());
                    displayBookDetail(response.body());
                } else {
                    Log.e(TAG, "책 정보 가져오기 실패: " + response.code());
                    showToast("책 정보를 가져오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<BookDetailModel> call, Throwable t) {
                Log.e(TAG, "책 정보 가져오기 오류", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private void initializeViews(View view) {
        bookImageView = view.findViewById(R.id.bookImageView);
        bookNameTextView = view.findViewById(R.id.bookNameTextView);
        authorTextView = view.findViewById(R.id.authorTextView);
        publisherTextView = view.findViewById(R.id.publisherTextView);
        publishYearTextView = view.findViewById(R.id.publishYearTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        librariesRecyclerView = view.findViewById(R.id.librariesRecyclerView);

        if (librariesRecyclerView != null) {
            librariesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void displayBookDetail(BookDetailModel bookDetail) {
        if (getView() == null) {
            Log.e(TAG, "Fragment view is null");
            return;
        }

        if (bookImageView != null) {
            Glide.with(this)
                    .load(bookDetail.getBookimageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(bookImageView);
        }

        if (bookNameTextView != null) bookNameTextView.setText(bookDetail.getBookname());
        if (authorTextView != null) authorTextView.setText("▸ 저자 : " + bookDetail.getAuthors());
        if (publisherTextView != null) publisherTextView.setText("▸ 출판 : " + bookDetail.getPublisher());
        if (publishYearTextView != null) publishYearTextView.setText("▸ 발행 : " + bookDetail.getPublishyear());

        if (descriptionTextView != null) {
            String description = bookDetail.getDescription();
            if (description != null && !description.isEmpty()) {
                descriptionTextView.setText(description);
                descriptionTextView.setVisibility(View.VISIBLE);
                Log.d(TAG, "설명 설정: " + description);
            } else {
                Log.w(TAG, "설명이 null이거나 비어 있습니다");
                descriptionTextView.setVisibility(View.GONE);
            }
        } else {
            Log.e(TAG, "descriptionTextView is null");
        }

        if (librariesRecyclerView != null && bookDetail.getLibs() != null) {
            libraryAdapter = new BookDetailLibraryAdapter(bookDetail.getLibs());
            librariesRecyclerView.setAdapter(libraryAdapter);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationUtil.handlePermissionResult(requestCode, permissions, grantResults, new LocationUtil.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                getCurrentLocation();
            }

            @Override
            public void onLocationError(String error) {
                showToast(error);
                // 권한이 거부된 경우 사용자에게 추가 설명을 제공하거나
                // 앱의 기능이 제한됨을 알릴 수 있습니다.
                if (error.contains("권한이 거부되었습니다")) {
                }
            }
        });
    }
}