package com.trackyourself;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GraphActivity extends AppCompatActivity {

    private BarChart barChart;
    private  DAOtracking daOtracking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        barChart = findViewById(R.id.graphID);
        daOtracking= new DAOtracking(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");



        String name = getIntent().getStringExtra("name");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");
        LocationCriteria locationCriteria = new LocationCriteria();
        locationCriteria.setFromDate(fromDate);
        locationCriteria.setToDate(toDate);
        locationCriteria.setLocationName(name);



        createBarGraph(locationCriteria);


    }
    public void createBarGraph(LocationCriteria locationCriteria){



        Calendar mDate1 = Calendar.getInstance();
        Calendar mDate2 = Calendar.getInstance();
        mDate1.clear();
        mDate2.clear();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            mDate1.setTime(dateFormat.parse(locationCriteria.getFromDate()));
            mDate2.setTime(dateFormat.parse(locationCriteria.getToDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> dates = new ArrayList<>();
        dates = getList(mDate1,mDate2);
        HashMap<String,Integer> hashMap = daOtracking.getLocationHistory(locationCriteria);
        ArrayList<BarEntry> barEntries = new ArrayList<>();




        for (Map.Entry<String, Integer> time : hashMap.entrySet()) {
            int y =0;
            for(String list : dates)
                if (list.equals(time.getKey()))
                    break;
                else
                    y++;
            barEntries.add(new BarEntry(time.getValue(),y));

        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
        BarData barData = new BarData(dates,barDataSet);
        barChart.getAxisRight().setAxisMinValue(0);
        barChart.setData(barData);


    }

    public ArrayList<String> getList(Calendar startDate, Calendar endDate){
        ArrayList<String> list = new ArrayList<String>();
        while(startDate.compareTo(endDate)<=0){
            list.add(getDate(startDate));
            startDate.add(Calendar.DAY_OF_MONTH,1);
        }
        return list;
    }

    public String getDate(Calendar cld){
        String curDate = cld.get(Calendar.YEAR) + "/" + (cld.get(Calendar.MONTH) + 1) + "/"
                +cld.get(Calendar.DAY_OF_MONTH);
        try{
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(curDate);
            curDate =  new SimpleDateFormat("yyy/MM/dd").format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return curDate;
    }
}


