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
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;

public class LibraryMapFragment extends Fragment implements LocationUtil.CustomLocationCallback {
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
                getCurrentLocation();
            }
        });
    }

    private void getCurrentLocation() {
        locationUtil.getCurrentLocation(requireActivity(), this);
    }

    @Override
    public void onLocationResult(Location location) {
        if (location != null && kakaoMap != null) {
            Log.d(TAG, "Location received:ㅇㅇㅁ어ㅗㅠㅁㄴ윤뮤윤ㅍ뮤오너ㅠ륲모ㅠㄹ올뮾ㅇ뉴ㅓㅠㅠㅇ뉴뮤륜ㅇ");
            Log.d(TAG, "  Latitude: " + location.getLatitude());
            Log.d(TAG, "  Longitude: " + location.getLongitude());
            Log.d(TAG, "  Provider: " + location.getProvider());
            Log.d(TAG, "  Accuracy: " + location.getAccuracy() + " meters");
            Log.d(TAG, "  Time: " + new java.util.Date(location.getTime()).toString());
            if (location.hasAltitude()) {
                Log.d(TAG, "  Altitude: " + location.getAltitude() + " meters");
            }
            if (location.hasBearing()) {
                Log.d(TAG, "  Bearing: " + location.getBearing() + " degrees");
            }
            if (location.hasSpeed()) {
                Log.d(TAG, "  Speed: " + location.getSpeed() + " m/s");
            }

            LatLng position = LatLng.from(location.getLatitude(), location.getLongitude());
            addCurrentLocationMarker(position);
            moveCamera(position);
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

    private void moveCamera(LatLng position) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(position);
        kakaoMap.moveCamera(cameraUpdate);

        // 줌 레벨 설정 (1~20 사이의 값, 숫자가 클수록 더 줌인됨)
        CameraUpdate zoomUpdate = CameraUpdateFactory.zoomTo(15);
        kakaoMap.moveCamera(zoomUpdate);
    }

    @Override
    public void onLocationError(String error) {
        Log.e(TAG, "Location error: " + error);
        // TODO: 사용자에게 오류 메시지를 표시하는 코드를 여기에 추가하세요.
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