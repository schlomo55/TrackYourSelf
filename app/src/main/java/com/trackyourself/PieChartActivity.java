package com.trackyourself;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;

public class PieChartActivity extends AppCompatActivity {

    PieChart pieChart;
    DAOtracking daotracking;
    LocationCriteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_chart);
        daotracking = new DAOtracking(this);
        criteria = (LocationCriteria) getIntent().getParcelableExtra("criteria");



        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        HashMap<String,Float> locationsHistory  = daotracking.getLocationsHistory(criteria);
        int index = 0;
        for (Float percent:
             locationsHistory.values()) {
            yvalues.add(new Entry(percent, index));
        }
        for (String name:
             locationsHistory.keySet()) {
                xVals.add(name);

        }
        PieDataSet dataSet = new PieDataSet(yvalues, "locations Summart");
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
    }

}




