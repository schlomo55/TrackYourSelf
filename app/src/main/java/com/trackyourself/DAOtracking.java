package com.trackyourself;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.trackyourself.SaveResult.*;


public class DAOtracking {

    private Context context;
    private SQLiteDatabase db = null;
    private static int NOT_EXISTS_FOR_DATE = -1;
    private static int RECORD_MINUTES = 0;
    private static int TIME_REMAIN = 1;
    public DAOtracking(Context context){
        this.context = context;
        try
        {
            db = context.openOrCreateDatabase("MyContacts",Context.MODE_PRIVATE, null);
            String sql = "CREATE TABLE IF NOT EXISTS locations (id integer primary key, name VARCHAR, " +
                    "latitude FLOAT,longitude FLOAT,date DATE,totalTime INT,remainTime INT DEFAULT -1);";
            db.execSQL(sql);
        }
        catch(Exception e){
            Log.d("debug", "Error Creating Database");
        }
    }


    public void addData(){
        ContentValues cv = new ContentValues();
        //work
        cv.put("name","עבודה");
        cv.put("latitude",34.86056327);
        cv.put("longitude",32.14771106);
        cv.put("date","2019/01/14");
        cv.put("totalTime",502);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","עבודה");
        cv.put("latitude",34.86056327);
        cv.put("longitude",32.14771106);
        cv.put("date","2019/01/15");
        cv.put("totalTime",567);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","עבודה");
        cv.put("latitude",34.86056327);
        cv.put("longitude",32.14771106);
        cv.put("date","2019/01/16");
        cv.put("totalTime",452);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","עבודה");
        cv.put("latitude",34.86056327);
        cv.put("longitude",32.14771106);
        cv.put("date","2019/01/17");
        cv.put("totalTime",495);
        db.insert("locations",null,cv);
        //pisga
        cv = new ContentValues();
        cv.put("name","פסגה");
        cv.put("latitude",31.8164276);
        cv.put("longitude",35.252466);
        cv.put("date","2019/01/16");
        cv.put("totalTime",90);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","פסגה");
        cv.put("latitude",31.8164276);
        cv.put("longitude",35.252466);
        cv.put("date","2019/01/19");
        cv.put("totalTime",126);
        db.insert("locations",null,cv);
        //home
        cv = new ContentValues();
        cv.put("name","הרצוג");
        cv.put("latitude",31.7638617);
        cv.put("longitude",35.2034345);
        cv.put("date","2019/01/14");
        cv.put("totalTime",185);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","הרצוג");
        cv.put("latitude",31.7638617);
        cv.put("longitude",35.2034345);
        cv.put("date","2019/01/15");
        cv.put("totalTime",680);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","הרצוג");
        cv.put("latitude",31.7638617);
        cv.put("longitude",35.2034345);
        cv.put("date","2019/01/16");
        cv.put("totalTime",971);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","הרצוג");
        cv.put("latitude",31.7638617);
        cv.put("longitude",35.2034345);
        cv.put("date","2019/01/17");
        cv.put("totalTime",611);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","הרצוג");
        cv.put("latitude",31.7638617);
        cv.put("longitude",35.2034345);
        cv.put("date","2019/01/18");
        cv.put("totalTime",1283);
        db.insert("locations",null,cv);
        cv = new ContentValues();
        cv.put("name","הרצוג");
        cv.put("latitude",31.7638617);
        cv.put("longitude",35.2034345);
        cv.put("date","2019/01/19");
        cv.put("totalTime",1294);
        db.insert("locations",null,cv);
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

    public HashMap<String,Float> getLocationsHistory(LocationCriteria criteria){
        HashMap<String, Float> locationsResult = new HashMap<>();
        String query = prepareSqlQuery(criteria.getFromDate(), criteria.getToDate());
        Cursor cr = db.rawQuery(query, null);
        if (cr.moveToFirst()) {
            int totalTimeOverall = getTotalTime();
            if(totalTimeOverall==0){
                return locationsResult;
            }
            do {
                String name = cr.getString(cr.getColumnIndex("name"));
                float totalTime = cr.getInt(1);
                locationsResult.put(name, totalTime/totalTimeOverall);
            } while (cr.moveToNext());
        }
        return locationsResult;

    }
    public SaveResult saveOrUpdate(String date, int minutes, MyLocation location){
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String locationName = location.getName();
        switch (locationExists(latitude,longitude)){
            case LOCATION_EXISTS:
                int[] recordInfo = getRecordInfo(latitude,longitude,date);
                if(recordInfo[RECORD_MINUTES] != NOT_EXISTS_FOR_DATE ) {//location exists for the specific date
                    if (locationName == null)
                        return updateRecord(recordInfo[RECORD_MINUTES], minutes, latitude, longitude, date,recordInfo[TIME_REMAIN]);
                }else{
                    String name  = getLocationName(latitude,longitude);
                    saveNewLocationReord(name,latitude,longitude,date);
                }
                break;
            case LOCATION_NOT_EXISTS:
                if(locationName != null)
                    return saveNewLocationReord(locationName,latitude,longitude,date);
        }
        return  LOCATION_EXISTS;
    }

    public List<String> getAllLocations(){
        List<String> locationsNames = new ArrayList<>();
        String query = "select name from locations group by name";
        Cursor cr = db.rawQuery(query,null);
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(0);
                locationsNames.add(name);
            } while (cr.moveToNext());
        }
        return locationsNames;
    }

    public SaveResult setRemain(int remainTime, String date,String locationName){
        ContentValues cv = new ContentValues();
        cv.put("remainTime", remainTime);
        db.update("locations",cv,"name='"+locationName+"' and date='"+date+"'",null);
        return UPDATE_SUCCESSFULLY;
    }
    private SaveResult updateRecord(int recordMinutes,int minutes,double latitude,double longitude,String date,int remainTime){
        ContentValues cv = new ContentValues();
        cv.put("totalTime", recordMinutes + minutes);
        if(remainTime!=-1)
            cv.put("remainTime",--remainTime);
        db.update("locations", cv, "abs(latitude-"+latitude+")<=0.007 and abs(longitude-"+longitude+")<=0.007"
                +" and date='" + date + "'", null);
        if(remainTime==0)
            return TIMES_UP;
        return UPDATE_SUCCESSFULLY;
    }
    private int getTotalTime(){
        String query = "select sum(totalTime) as totalTimeOverAll from locations";
        Cursor cr = db.rawQuery(query,null);
        int totalTime = 0;
        if (cr.moveToFirst()) {
            totalTime  = cr.getInt(0);
        }
        return totalTime;
    }
    private SaveResult saveNewLocationReord(String locationName,double latitude,double longitude,String date){
        ContentValues cv = new ContentValues();
        cv.put("name",locationName);
        cv.put("latitude",latitude);
        cv.put("longitude",longitude);
        cv.put("date",date);
        cv.put("totalTime",0);
        if(db.insert("locations",null,cv)!=-1){
            return SAVE_SUCCESSFULLY;
        }
        return SAVE_PROBLEM;
    }


    private String getLocationName(double latitude,double longitude){
        String query = "select name from locations where abs(latitude-"+latitude+")<=0.007 and " +
                "abs(longitude-"+longitude+")<=0.007";
        Cursor cr = db.rawQuery(query,null);
        cr.moveToFirst();
        return cr.getString(cr.getColumnIndex("name"));
    }

    private  SaveResult locationExists(double latitude,double longitude){
        String query = "select totalTime from locations where abs(latitude-"+latitude+")<=0.007 and " +
                "abs(longitude-"+longitude+")<=0.007";
        Cursor cr = db.rawQuery(query,null);
        if(cr.moveToFirst()){
            return LOCATION_EXISTS;
        }
        return LOCATION_NOT_EXISTS;
    }
    private int[] getRecordInfo(double latitude , double longitude,String date){
        String query = "select totalTime,remainTime from locations where abs(latitude-"+latitude+")<=0.007 and abs(longitude-"+longitude+")<=0.007"+
                " and date='" +date+"'";
        Cursor cr = db.rawQuery(query,null);
        if(cr.moveToFirst()){
            int totalTime = cr.getInt(cr.getColumnIndex("totalTime"));
            int remainTime = cr.getInt(cr.getColumnIndex("remainTime"));
            return new int[]{totalTime,remainTime};
        }
        return new int[]{NOT_EXISTS_FOR_DATE,-1};
    }
    private String prepareSqlQuery(String fromDate,String toDate){
        String query="select name,sum(totalTime) as totalForLocation from locations ";
        if(fromDate!=null){
            query+="where date>= '"+fromDate+"'";
        }
        if(toDate!=null){
            if(fromDate!=null){
                query+=" and date<= '"+toDate+"'";
            }
            else {
                query+="where date<= '"+ toDate+"'";
            }
        }
        query+=" group by name";
        return query;
    }
    private String prepareSqlQuery(String location, String fromDate,String toDate){
        String query ="select date,totalTime from locations where name = '" +location;
        query+="' and date>= '" +fromDate+"' and date<= '"+toDate+"' order by date";
        return query;

    }//need the group by???


}
