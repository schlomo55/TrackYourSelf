package com.trackyourself;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;

import java.util.HashMap;

import static com.trackyourself.SaveResult.*;


public class DAOtracking {

    private Context context;
    private SQLiteDatabase db = null;
    private static int NOT_EXISTS = -1;
    public DAOtracking(Context context){
        this.context = context;
        try
        {
            db = context.openOrCreateDatabase("MyContacts",Context.MODE_PRIVATE, null);
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

    public HashMap<String,Float> getLocationsHistory(LocationCriteria criteria){
        HashMap<String, Float> locationsResult = new HashMap<>();
        String query = prepareSqlQuery(criteria.getFromDate(), criteria.getToDate());
        Cursor cr = db.rawQuery(query, null);
        if (cr.moveToFirst()) {
            int totalTimeOverall = cr.getInt(cr.getColumnIndex("totalTimeOverall"));
            if(totalTimeOverall==0){
                return locationsResult;
            }
            do {
                String name = cr.getString(cr.getColumnIndex("name"));
                float totalTime = cr.getInt(cr.getColumnIndex("totalForLocation"))/totalTimeOverall;
                locationsResult.put(name, totalTime);
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
                int recordMinutes = getRecordMinutes(latitude,longitude,date);
                if(recordMinutes != NOT_EXISTS ) {// mybe need to change the name??
                    if(locationName == null)
                        return updateRecord(recordMinutes,minutes,latitude,longitude,date);
                }
                break;
            case LOCATION_NOT_EXISTS:
                if(locationName != null)
                    return saveNewLocationReord(locationName,latitude,longitude,date);
        }
        return  LOCATION_EXISTS;
    }

    private SaveResult updateRecord(int recordMinutes,int minutes,double latitude,double longitude,String date){
        ContentValues cv = new ContentValues();
        cv.put("totalTime", recordMinutes + minutes);
        db.update("locations", cv, "abs(latitude-"+latitude+")<=0.007 and abs(longitude-"+longitude+")<=0.007"
                +" and date='" + date + "'", null);
        return UPDATE_SUCCESSFULLY;
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
    private  SaveResult locationExists(double latitude,double longitude){
        String query = "select totalTime from locations where abs(latitude-"+latitude+")<=0.007 and " +
                "abs(longitude-"+longitude+")<=0.007";
        Cursor cr = db.rawQuery(query,null);
        if(cr.moveToFirst()){
            return LOCATION_EXISTS;
        }
        return LOCATION_NOT_EXISTS;
    }
    private int getRecordMinutes(double latitude , double longitude,String date){
        String query = "select totalTime from locations where abs(latitude-"+latitude+")<=0.007 and abs(longitude-"+longitude+")<=0.007"+
                " and date='" +date+"'";
        Cursor cr = db.rawQuery(query,null);
        if(cr.moveToFirst()){
            int totalTime = cr.getInt(cr.getColumnIndex("totalTime"));
            return totalTime;
        }
        return NOT_EXISTS;
    }
    private String prepareSqlQuery(String fromDate,String toDate){
        String query="select name,sum(totalTime) as totalForLocation,sum(totalTime) as totalTimeOverall from locations ";
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
