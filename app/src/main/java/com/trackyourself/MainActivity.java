package com.trackyourself;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.location.LocationManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class MainActivity extends Activity{

    private static final int PERMISSIONS_REQUEST = 100;
    DAOtracking daoTracking;
    LocationCriteria criteria;
//    private double[] locations = new double[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        daoTracking = new DAOtracking(this);

        super.onCreate(savedInstanceState);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            //check();
            startTrackerService();
            criteria = createDefaultCriteria();
            Intent pieChart = new Intent(this,PieChartActivity.class);
            pieChart.putExtra("criteria",criteria);
            startActivity(pieChart);
        } else {
         ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    //Start the TrackerService//
    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
    }

    private LocationCriteria createDefaultCriteria(){
        LocationCriteria criteria = new LocationCriteria();
        LocalDateTime now = LocalDateTime.now();
        Date toDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        now = now.minusDays(7);
        Date fromDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String from = df.format(fromDate);

        criteria.setLocationName("2019/01/17");
        criteria.setToDate(""+df.format(toDate));
        criteria.setFromDate(from);
        return criteria;
    }
//    private void check(){
//        LocalDateTime now = LocalDateTime.now();
//        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
//        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
//        String from = df.format(date);
//        getCurrentLocation(this);
//       // daoTracking.saveOrUpdate("home",from,0,locations[0],locations[1]);
//
//    }
//
//    private void getCurrentLocation(Context context){
//        LocationRequest request = new LocationRequest();
//        double[] locations = new double[2];
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
//        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permission == PackageManager.PERMISSION_GRANTED) {
//            client.getLastLocation().addOnSuccessListener(this);
//        }
//    }
//
//    @Override
//    public void onSuccess(Object o) {
//        Location location = (Location) o;
//        locations[0] = location.getLongitude();
//        locations[1] = location.getLatitude();
//    }
}