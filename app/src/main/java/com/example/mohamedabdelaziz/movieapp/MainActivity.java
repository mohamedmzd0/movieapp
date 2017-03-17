package com.example.mohamedabdelaziz.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements listener{

    boolean two_panel=false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MainActivityFragment mainActivityFragment = new MainActivityFragment() ;
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainActivityFragment,"").commit();
        mainActivityFragment.start_send_data(this);

        if(findViewById(R.id.frame_detail)!=null)
            two_panel =true ;

    }

    @Override
    public void send_data(My_Movie_data datatype) {
        Bundle bundle = new Bundle();
        bundle.putString("url",datatype.url);
        bundle.putString("title",datatype.title);
        bundle.putString("overview",datatype.overview);
        bundle.putString("rate",datatype.rate);
        bundle.putString("date",datatype.release);
        bundle.putString("id",datatype.id);

        if(two_panel)
        {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment() ;
            detailActivityFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_detail,detailActivityFragment,"").commit();

        }else
        {
            Intent intent=new Intent(getApplicationContext(),DetailActivity.class) ;
            intent.putExtras(bundle) ;
            startActivity(intent);
        }
    }
}
