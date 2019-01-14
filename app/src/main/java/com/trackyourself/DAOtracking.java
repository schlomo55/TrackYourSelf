package com.trackyourself;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;

import java.util.Date;
import java.util.HashMap;


public class DAOtracking {

    private SQLiteDatabase db = null;
    private static int NOT_EXISTS = -1;
    public DAOtracking(){
        try
        {
            db = SQLiteDatabase.openOrCreateDatabase("MyContacts",, null);
            String sql = "CREATE TABLE IF NOT EXISTS locations (id integer primary key, name VARCHAR, latitude FLOAT,longitude FLOAT,date DATE,totalTime INT);";
            db.execSQL(sql);
        }
        catch(Exception e){
            Log.d("debug", "Error Creating Database");
        }

    }

    public HashMap<String, Integer> getLocationHistory(LocationCriteria criteria) {
        HashMap<String, Integer> locationResult = new HashMap<>();
        String query = prepareSqlQuery(criteria.getLocationName(), criteria.getFromDate(), criteria.getToDate());
        Cursor cr = db.rawQuery(query, null);
        if (cr.moveToFirst()) {
            do {
                String date = cr.getString(cr.getColumnIndex("date"));
                int totalTime = cr.getInt(cr.getColumnIndex("totalTime"));
                locationResult.put(date, totalTime);
            } while (cr.moveToNext());
        }
        return locationResult;
    }

    public HashMap<String,Double> getLocationsHistory(LocationCriteria criteria){
        HashMap<String, Double> locationsResult = new HashMap<>();
        String query = prepareSqlQuery(criteria.getFromDate(), criteria.getToDate());
        Cursor cr = db.rawQuery(query, null);
        if (cr.moveToFirst()) {
            int totalTimeOverall = cr.getInt(cr.getColumnIndex("totalTimeOverall"));
            do {
                String name = cr.getString(cr.getColumnIndex("name"));
                double totalTime = cr.getInt(cr.getColumnIndex("totalTime"))/totalTimeOverall;
                locationsResult.put(name, totalTime);
            } while (cr.moveToNext());
        }
        return locationsResult;

    }
    public void saveOrUpdate(String locationName,Date date,int minutes,double latitude,double longitude){
        int recordMinutes = getRecordMinutes(locationName,date);
        if(recordMinutes != NOT_EXISTS ){
            ContentValues cv = new ContentValues();
            cv.put("totalTime",recordMinutes+minutes);
            db.update("locations",cv,"name="+locationName,null);
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("name",locationName);
        cv.put("latitude",latitude);
        cv.put("longitude",longitude);
        cv.put("date",date.toString());
        cv.put("totalTime",minutes);
        db.insert("locations",null,cv);

    }
    private int getRecordMinutes(String locationName,Date date){
        String query = "select * from locations where name='"+locationName+"' and date="+date;
        Cursor cr = db.rawQuery(query,null);
        if(cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndex("totalTime"));
        }
        return NOT_EXISTS;
    }
    private String prepareSqlQuery(Date fromDate,Date toDate){
        String query="select name,totalTime,sum(totalTime) as totalTimeOverall from locations ";
        if(fromDate!=null){
            query+="where date>= "+fromDate;
        }
        if(toDate!=null){
            if(fromDate!=null){
                query+=" and date<= "+toDate;
            }
            else {
                query+="where date<= "+ toDate;
            }
        }
        query+=" group by name";
        return query;
    }
    private String prepareSqlQuery(String location, Date fromDate,Date toDate){
        String query ="select date,totalTime from locations where location = " +location;
        query+=" and date between " +fromDate+" and "+toDate+" order by date group by date,totalTime";
        return query;

    }//need the group by???


}
