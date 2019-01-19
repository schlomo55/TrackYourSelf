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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST = 100;
    DAOtracking daoTracking;
    LocationCriteria criteria;
    ImageView showAllHistory;
    ImageButton addLocation,showSpecificLocation;;
    TextView fromDate,toDate,name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize//
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        daoTracking = new DAOtracking(this);
        showAllHistory = (ImageView) findViewById(R.id.showAllHistory);
        showSpecificLocation = (ImageButton) findViewById(R.id.showSpecificLocation);
        addLocation = (ImageButton) findViewById(R.id.addNewLocation);
        fromDate = (TextView) findViewById(R.id.fromDate);
        toDate = (TextView) findViewById(R.id.toDate);
        name = (TextView) findViewById(R.id.name);
        showAllHistory.setOnClickListener(this);
        addLocation.setOnClickListener(this);
        showSpecificLocation.setOnClickListener(this);
        //Check permission//
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
         ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        criteria.setToDate(df.format(toDate));
        criteria.setFromDate( df.format(fromDate));
        return criteria;
    }



    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.showAllHistory:
               startPieChart();
               break;
           case R.id.addNewLocation:
               startAddLocation();
               break;
           case R.id.showSpecificLocation:
                startSpecificLocation();
                break;
       }
    }

    private void startPieChart(){
        criteria = createDefaultCriteria();
        Intent pieChart = new Intent(this,PieChartActivity.class);
        pieChart.putExtra("fromDate",criteria.getFromDate());
        pieChart.putExtra("toDate",criteria.getToDate());
        pieChart.putExtra("criteria",criteria);
        startActivity(pieChart);
    }
    private void startAddLocation(){
        Intent addLocation = new Intent(this,AddLocationActivity.class);
        startActivity(addLocation);

    }

    private void startSpecificLocation(){
        Intent graphActivity = new Intent(this,GraphActivity.class);
//        fromDate.getText().toString()
//        toDate.getText().toString()
//        name.getText().toString()
        graphActivity.putExtra("fromDate","2019/01/18");
        graphActivity.putExtra("toDate","2019/01/19");
        graphActivity.putExtra("name","הרצוג");
        startActivity(graphActivity);
    }
}