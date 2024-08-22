package com.example.check.map;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.check.R;
import com.example.check.Utils.LocationUtil;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;

public class LibraryMapFragment extends Fragment implements LocationUtil.LocationCallback {
    private static final String TAG = "LibraryMapFragment";
    private MapView mapView;
    private KakaoMap kakaoMap;
    private LocationUtil locationUtil;
    private LabelLayer currentLocationLayer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        locationUtil = new LocationUtil(requireContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                Log.d(TAG, "onMapDestroy");
            }

            @Override
            public void onMapError(Exception error) {
                Log.e(TAG, "onMapError: ", error);
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap map) {
                kakaoMap = map;
                currentLocationLayer = kakaoMap.getLabelManager().getLayer();
            }
        });
    }

    private void getCurrentLocation() {
        locationUtil.getCurrentLocation(requireActivity(), this);
    }

    @Override
    public void onLocationResult(Location location) {
        if (location != null && kakaoMap != null) {
            Log.d(TAG, "Location received: " + location.getLatitude() + ", " + location.getLongitude());
            // ... 기존 코드 ...
        } else {
            Log.e(TAG, "Location is null");
        }
    }

    private void addCurrentLocationMarker(LatLng position) {
        currentLocationLayer.removeAll();

        LabelOptions labelOptions = LabelOptions.from(position)
                .setStyles(LabelStyle.from(R.drawable.icon_bookmark_black))
                .setClickable(false);

        currentLocationLayer.addLabel(labelOptions);
    }

    @Override
    public void onLocationError(String error) {
        Log.e(TAG, "Location error: " + error);
        // 사용자에게 오류 메시지를 표시하는 코드를 여기에 추가할 수 있습니다.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationUtil.handlePermissionResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.finish();
        }
    }
}