package com.example.check.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.check.model.bookDetail.LibraryModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class LocationUtil {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private static final String TAG = "LocationUtil"; // 로그 태그 추가

    public interface LocationCallback {
        void onLocationResult(Location location);
        void onLocationError(String error);
    }

    public LocationUtil(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(Activity activity, LocationCallback callback) {
        if (!hasLocationPermission()) {
            requestLocationPermission(activity);
            callback.onLocationError("위치 권한이 필요합니다.");
            return;
        }

        try {
            getLocationWithPermission(activity, callback);
        } catch (SecurityException e) {
            callback.onLocationError("위치 권한이 거부되었습니다: " + e.getMessage());
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getLocationWithPermission(Activity activity, LocationCallback callback) throws SecurityException {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // 위치 조회 성공 로그 추가
                            Log.d(TAG, "Current Location: Latitude = " + location.getLatitude()
                                    + ", Longitude = " + location.getLongitude());
                            callback.onLocationResult(location);
                        } else {
                            Log.e(TAG, "Location is null");
                            callback.onLocationError("위치를 가져올 수 없습니다.");
                        }
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e(TAG, "Location query failed: " + e.getMessage());
                    callback.onLocationError("위치 조회 실패: " + e.getMessage());
                });
    }

    public static void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults, LocationCallback callback) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onLocationResult(null);  // null을 전달하여 위치 조회를 다시 시도하도록 함
            } else {
                callback.onLocationError("위치 권한이 거부되었습니다.");
            }
        }
    }

    public static void updateLibrariesWithCurrentLocation(Location currentLocation, List<LibraryModel> libraries) {
        for (LibraryModel library : libraries) {
            float[] results = new float[1];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                    library.getLatitude(), library.getLongitude(), results);
            library.setDistance(String.format("%.2f", results[0] / 1000)); // km로 변환
        }
    }
}
