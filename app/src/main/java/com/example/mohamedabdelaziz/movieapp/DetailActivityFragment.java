package com.example.mohamedabdelaziz.movieapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class DetailActivityFragment extends Fragment {

    TextView release ,name ;
    TextView details ;
    ImageView imageView ;
    String release_date ;
    String overview ;
    String url ;
    String title ;
    Button favourite ;
    ArrayList<String> trailers_list =new ArrayList<>();
    String id ;
    String rate;
    boolean check;
    ListView trailer_buttons ;
    StringBuffer content ;
    TextView review ;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle intent= getArguments() ;
        review =(TextView)view.findViewById(R.id.review) ;
        trailer_buttons=(ListView) view.findViewById(R.id.trailer_buttons) ;
        favourite = (Button) view.findViewById(R.id.favrt) ;
        try {

            url = (String) intent.get("url");
            overview = (String) intent.get("overview");
            release_date = (String) intent.get("date");
            title = (String) intent.get("title") ;
            id= intent.getString("id") ;
            rate=(String) intent.get("rate") ;

        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),"Coudn't Load Data", Toast.LENGTH_LONG).show();
            return  null;
        }
        release=(TextView)view.findViewById(R.id.date_release) ;
        release.setText("\n\n"+release_date+"\n\n"+rate);
        name=(TextView)view.findViewById(R.id.name) ;
        name.setText(title);
        name.setBackgroundColor(Color.rgb(123,209,195));
        details=(TextView)view.findViewById(R.id.details) ;
        details.setText(overview);
        imageView=(ImageView) view.findViewById(R.id.imageView) ;
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/"+url).into(imageView);
        trailer_buttons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), R.string.please_wait, Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent();
                intent1.setData(Uri.parse("https://www.youtube.com/watch?v="+trailers_list.get(i)));
                startActivity(intent1);
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!check) {
                    if(new moviedatabase(getContext()).insert_data(id,title,release_date,overview,url,rate))
                        Toast.makeText(getContext(),R.string.add_fav,Toast.LENGTH_SHORT).show();
                }
                else {
                    new moviedatabase(getContext()).delete_it(id);
                    Toast.makeText(getContext(),R.string.remove_fav,Toast.LENGTH_SHORT).show();
                }
                selectfavorite();
            }
        });
        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();
            selectfavorite();

        if(isNetworkAvailable()) {
            new asynmovie().execute(id);
            new reviewtask().execute(id);
        }
        else
        {
            review.setText("Connection TimeOut");
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void selectfavorite(){

        check =new moviedatabase(getContext()).is_exists(id);
        if (check)
            favourite.setBackgroundResource(R.drawable.loved);
        else
            favourite.setBackgroundResource(R.drawable.disloved);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId() ;
        if(id==android.R.id.home) {
            getArguments().clear();
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
    class asynmovie extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        trailers_list.clear();
        }

        protected String doInBackground(String... args) {

            HttpURLConnection httpURLConnection = null ;
            BufferedReader bufferedReader =null ;
            InputStream inputStream ;
            StringBuffer stringBuffer=new StringBuffer() ;
            try {
                URL myurl = new URL("https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=*******************************");
                httpURLConnection=(HttpURLConnection) myurl.openConnection() ;
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                if(inputStream==null)
                    return null ;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream)) ;
                String line ;
                while ((line = bufferedReader.readLine())!=null)
                    stringBuffer.append(line+"\n") ;
                String jsn = stringBuffer.toString() ;
                JSONObject jsonObject = new JSONObject(jsn);
                JSONArray result = jsonObject.getJSONArray("results");
                for (int i = 0; i < result.length() ; i++) {
                    JSONObject jsonObject1 = result.getJSONObject(i);
                    String key = jsonObject1.getString("key") ;
                    trailers_list.add(key) ;
                }

            }catch (Exception e)
            {
                Log.e(e.getMessage(),"background") ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < trailers_list.size(); i++) {
                strings.add("Trailer "+(i+1));
            }

            ArrayAdapter adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,strings) ;
            trailer_buttons.setAdapter(adapter);
            ViewGroup.LayoutParams L = trailer_buttons.getLayoutParams();
            L.height = adapter.getCount()*75;
            trailer_buttons.setLayoutParams(L);
            trailer_buttons.requestLayout();
        }
    }

    class reviewtask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            HttpURLConnection httpURLConnection = null ;
            BufferedReader bufferedReader =null ;
            InputStream inputStream ;
            StringBuffer stringBuffer=new StringBuffer("") ;
            try {
                URL myurl = new URL("https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+"832f13a97b5d2df50ecf0dbc8a0f46ae");
                httpURLConnection=(HttpURLConnection) myurl.openConnection() ;
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                if(inputStream==null)
                    return null ;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream)) ;
                String line=null ;
                while ((line = bufferedReader.readLine())!=null)
                    stringBuffer.append(line+"\n") ;

                String jsn = stringBuffer.toString() ;
                JSONObject jsonObject = new JSONObject(jsn);
                JSONArray result = jsonObject.getJSONArray("results");
                content = new StringBuffer();
                for (int i = 0; i < result.length() ; i++) {
                    JSONObject jsonObject1 = result.getJSONObject(i);
                    content.append( jsonObject1.getString("content")) ;
                }

            }catch (Exception e)
            {

            }
            return null ;
        }

        @Override
        protected void onPostExecute(String s) {
            review.setText(content.toString());
        }
    }
}
