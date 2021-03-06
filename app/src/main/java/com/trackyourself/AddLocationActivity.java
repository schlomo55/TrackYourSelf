package com.trackyourself;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.trackyourself.SaveResult.*;


public class AddLocationActivity extends Activity implements View.OnClickListener,OnSuccessListener {


    private Button save;
    private TextView name;
    private DAOtracking daoTracking;
    private  MyLocation myLocation;
    private Spinner names;
    private  Button addRemain;
    private  TextView remainTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);
        daoTracking  = new DAOtracking(this);
        myLocation = new MyLocation();
        save = (Button) findViewById(R.id.saveLocation);
        name = (TextView) findViewById(R.id.locationName);
        names = (Spinner) findViewById(R.id.names);
        addRemain = (Button) findViewById(R.id.addRemain);
        remainTime = (TextView) findViewById(R.id.remainTime);
        List<String> locationsNames = daoTracking.getAllLocations();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,locationsNames);
        names.setAdapter(adapter);
        save.setOnClickListener(this);
        addRemain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveLocation:
                if(name.getText().toString().equals("")){
                    Toast.makeText(this, "Please Enter Location Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                getCurrentLocation(this);
                break;
            case R.id.addRemain:
                if(names.getSelectedItem()==null ||names.getSelectedItem().toString().equals("") || remainTime.getText().toString().equals("")){

                    Toast.makeText(this, "Please add valid data", Toast.LENGTH_SHORT).show();
                    break;
                }
                String name = names.getSelectedItem().toString();

                int time = Integer.parseInt(remainTime.getText().toString());
                LocalDateTime now = LocalDateTime.now();
                Date from = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd");


                if(daoTracking.setRemain(time,df.format(from),name)==UPDATE_SUCCESSFULLY){
                    remainTime.setText("");
                    Toast.makeText(this, "Add successfully", Toast.LENGTH_SHORT).show();
                }
        }


    }

    private void getCurrentLocation(Context context){
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnSuccessListener(this);
        }
    }

    @Override
    public void onSuccess(Object o) {
        Location location = (Location) o;
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
        myLocation.setName(name.getText().toString());

        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String from = df.format(date);

        switch (daoTracking.saveOrUpdate(from,0,myLocation)){
            case SAVE_SUCCESSFULLY:
                Toast.makeText(this, "Location Saved Successfully ", Toast.LENGTH_SHORT).show();
                break;
            case SAVE_PROBLEM:
                Toast.makeText(this, "There was a problem saving the record", Toast.LENGTH_SHORT).show();
                break;
            case LOCATION_EXISTS:
                Toast.makeText(this, "Location already exists in the system", Toast.LENGTH_SHORT).show();
        }

    }
}
