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
import java.util.Random;

    public class GraphActivity extends AppCompatActivity {

        BarChart barChart;
        ArrayList<String> dates;
        Random random;
        ArrayList<BarEntry> barEntries;
        DAOtracking daOtracking;
        LocationCriteria locationCriteria;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_graph);
            barChart = findViewById(R.id.graphID);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            daOtracking= new DAOtracking(this);
            try {
                Date to = dateFormat.parse(getIntent().getStringExtra("to"));
                Date from = dateFormat.parse(getIntent().getStringExtra("from"));
                String place = getIntent().getStringExtra("location");
                locationCriteria = new LocationCriteria();
                locationCriteria.setFromDate(getIntent().getStringExtra("from"));
                locationCriteria.setToDate(getIntent().getStringExtra("to"));
                locationCriteria.setLocationName(place);

            } catch (ParseException e) {
                e.printStackTrace();
            }




            createBarGraph(locationCriteria);
            daOtracking = new DAOtracking(this);

        }
        public void createBarGraph(LocationCriteria locationCriteria){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");


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

            dates = new ArrayList<>();
            dates = getList(mDate1,mDate2);
            daOtracking.getLocationHistory(locationCriteria);
            barEntries = new ArrayList<>();
            float value = 0f;

            for(int j = 0; j< dates.size();j++){


                barEntries.add(new BarEntry(value,j));
            }

            BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
            BarData barData = new BarData( dates,barDataSet);
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


