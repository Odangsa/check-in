package com.example.check.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationUtil {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;

    public interface LocationCallback {
        void onLocationResult(Location location);
        void onLocationError(String error);
    }

    public LocationUtil(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(Activity activity, LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            callback.onLocationError("위치 권한이 필요합니다.");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            callback.onLocationResult(location);
                        } else {
                            callback.onLocationError("위치를 가져올 수 없습니다.");
                        }
                    }
                })
                .addOnFailureListener(activity, e -> callback.onLocationError("위치 조회 실패: " + e.getMessage()));
    }

    public static void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults, LocationCallback callback) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되었을 때 위치 조회 재시도
                // 이 메서드를 호출한 액티비티나 프래그먼트에서 getCurrentLocation을 다시 호출해야 합니다.
            } else {
                callback.onLocationError("위치 권한이 거부되었습니다.");
            }
        }
    }
}
