package com.example.mohamedabdelaziz.movieapp;


public class Contract_class {
    public static final String data_name = "Movie_data" ;
    public static final String table_name ="data" ;
    public static final int version = 2;
    public static final String id_rw ="id" ;
    public static final String title_rw ="title";
    public static final String  release_date_rw ="date";
    public static final String  overview_rw= "overview" ;
    public static final String url_rw="url";
    public static final String rate_rw = "rate" ;
    public static final String create ="CREATE TABLE "+table_name+" ( "+id_rw+" INTEGER , "+title_rw+" VARCHAR(50) , " +
            ""+release_date_rw+" VARCHAR(30) , "+overview_rw+" VARCHAR(100) , "+url_rw+" VARCHAR(50) ,"+rate_rw+" VARCHAR(20) )";
    public static final String update ="DROP TABEL IF EXISTS "+table_name ;


}
