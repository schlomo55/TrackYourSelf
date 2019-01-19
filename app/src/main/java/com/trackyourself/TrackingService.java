package com.trackyourself;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executor;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    private Time_Up time_up;
    private DAOtracking daoTracking;
    private LocationCallback mLocationCallback;
    private MyLocation myLocation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        time_up =new Time_Up(this);
        daoTracking = new DAOtracking(this);
        myLocation = new MyLocation();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    myLocation.setLongitude(location.getLongitude());
                    myLocation.setLatitude(location.getLatitude());

                    LocalDateTime now = LocalDateTime.now();
                    Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    String from = df.format(date);

                    if(daoTracking.saveOrUpdate(from,1,myLocation).equals(SaveResult.TIMES_UP)){

                    }
                    time_up.showNotification();
                }
            };
        };
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(60000); // every 1 minutes
        request.setFastestInterval(30000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request,mLocationCallback,null);
        }

    }
}