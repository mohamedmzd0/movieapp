package com.example.mohamedabdelaziz.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class

moviedatabase extends SQLiteOpenHelper {

    String log= moviedatabase.class.getSimpleName() ;
    public moviedatabase(Context context) {
        super(context, Contract_class.table_name, null, Contract_class.version);
        Log.d(log,"constractor") ;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(Contract_class.create);
        Log.d(log,"Oncreate") ;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(Contract_class.update);
        Log.d(log,"upgrade") ;
        onCreate(sqLiteDatabase);
        Log.d(log,"oncreate again") ;
    }
    public boolean insert_data(String id,String title,String release_date,String overview,String url,String rate) {

        SQLiteDatabase sqLitewrite = this.getWritableDatabase() ;
        ContentValues contentValues = new ContentValues() ;
            contentValues.put("id",id);
            contentValues.put("title",title);
            contentValues.put("date",release_date);
            contentValues.put("overview",overview);
            contentValues.put("url",url);
            contentValues.put("rate",rate);
            sqLitewrite.insert(Contract_class.table_name,null,contentValues);
            sqLitewrite.close();
          return true;
    }
    public boolean  is_exists(String id)
    {
        SQLiteDatabase sqLiteread = this.getReadableDatabase() ;
        Cursor cursor = sqLiteread.rawQuery("SELECT * FROM "+Contract_class.table_name,null);
        cursor.moveToFirst();
        if(cursor.isAfterLast()){
            return false;
        }
        else {
            String chcek_id;
            cursor.moveToFirst();
            while (cursor.isAfterLast()!=true) {
                chcek_id = cursor.getString(0).toString() ;
                if (chcek_id.equals(id)) {
                    return true;
                }
                cursor.moveToNext();
            }
    }
        return false;
}
    public int delete_it(String id)
    {
        SQLiteDatabase sqLiteread = this.getReadableDatabase() ;
        String []array={id};
        return sqLiteread.delete(Contract_class.table_name,Contract_class.id_rw+" = ? ",array);
    }
    public ArrayList restore_data()
    {
        ArrayList<My_Movie_data> array_values = new ArrayList<>() ;

        SQLiteDatabase sql = this.getReadableDatabase() ;
        Cursor cursor = sql.rawQuery("SELECT * FROM "+Contract_class.table_name ,null) ;
        cursor.moveToFirst();
        while(cursor.isAfterLast()==false)
        {
            My_Movie_data dt =new My_Movie_data();
            dt.id=cursor.getString(cursor.getColumnIndex(Contract_class.id_rw));
            dt.title=cursor.getString(cursor.getColumnIndex(Contract_class.title_rw));
            dt.overview=cursor.getString(cursor.getColumnIndex(Contract_class.overview_rw));
            dt.release=cursor.getString(cursor.getColumnIndex(Contract_class.release_date_rw));
            dt.rate=cursor.getString(cursor.getColumnIndex(Contract_class.rate_rw));
            dt.url=cursor.getString(cursor.getColumnIndex(Contract_class.url_rw));
            array_values.add(dt);
            cursor.moveToNext();
        }
        return array_values;
    }

}