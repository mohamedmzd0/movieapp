package com.example.mohamedabdelaziz.movieapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivityFragment extends Fragment {
    GridView gridView ;
    ArrayList<String> arrayList =new ArrayList<>();
    ArrayList<My_Movie_data>data_list = new ArrayList<>() ;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String JsonStr =null ;
    JSONArray result ;
    String type ;
    SharedPreferences preferences ;
    listener listen ;
    ArrayList<My_Movie_data> array_values;
    boolean already_view_dialog =false ;
    public MainActivityFragment() {
    }
    void start_send_data(listener listen)
    {
        this.listen = listen ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false) ;
        gridView = (GridView) view.findViewById(R.id.movie_gridView) ;
        moviedatabase moviedata = new moviedatabase(getContext());
        final SQLiteDatabase database = moviedata.getWritableDatabase();
        setHasOptionsMenu(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                My_Movie_data my_movie_data =new My_Movie_data() ;
                my_movie_data.url= data_list.get(i).url;
                my_movie_data.title=data_list.get(i).title;
                my_movie_data.overview= data_list.get(i).overview;
                my_movie_data.rate=data_list.get(i).rate;
                my_movie_data.release=data_list.get(i).release;
                my_movie_data.id=data_list.get(i).id;
                listen.send_data(my_movie_data);
            }
        });
        return view;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    @Override
    public void onStart() {
        super.onStart();
        if(!isNetworkAvailable() && !already_view_dialog)
        {
            already_view_dialog=true ;
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.network_error)
                    .setMessage(R.string.cant_connect)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "Favourites Selects", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setIcon(R.drawable.networkfirewall)
                    .show();
            type="favourite";
        }else
        {
            preferences= PreferenceManager.getDefaultSharedPreferences(getContext()) ;
            type=preferences.getString("settings","popular") ;

        }

        if(type.equalsIgnoreCase("favourite")) {
            onrestore_favourite_data();
        }
        else
            new movie().execute();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int myid = item.getItemId() ;
        if(myid==R.id.action_settings) {
            startActivity(new Intent(getContext(), Settinga_activity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void onrestore_favourite_data()
    {
        arrayList.clear();
        data_list.clear();
        array_values = new moviedatabase(getContext()).restore_data() ;
        for (int i = 0; i < array_values.size() ; i++) {
            My_Movie_data dt = array_values.get(i);
            My_Movie_data dt2 =new My_Movie_data();
            dt2.id=dt.id;
            dt2.title=dt.title;
            dt2.overview=dt.overview;
            dt2.release=dt.release;
            dt2.rate=dt.rate;
            dt2.url=dt.url;
            data_list.add(dt2);
            arrayList.add(dt2.url);
        }
        Custom_Adapter myadapter = new Custom_Adapter(getContext(),arrayList) ;
        gridView.setAdapter(myadapter) ;
    }

    class movie extends AsyncTask<String, String, String> {
        private final String LOG = movie.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            data_list.clear();
            arrayList.clear();
        }
        protected String doInBackground(String... args) {

            try {

                URL url = new URL("http://api.themoviedb.org/3/movie/"+type+"?api_key=***************************");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                JsonStr = buffer.toString();
                urlConnection.disconnect();
                reader.close();
            } catch (Exception e) {
                Log.e(LOG, "Error ", e);
                return null;
            }
            String res = "";
            try {
                JSONObject jsonObject = new JSONObject(JsonStr);
                result = jsonObject.getJSONArray("results") ;
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonobject = result.getJSONObject(i);
                    My_Movie_data dt =new My_Movie_data() ;
                    dt.url = jsonobject.getString("poster_path");
                    dt.title = jsonobject.getString("original_title");
                    dt.release = jsonobject.getString("release_date");
                    dt.overview = jsonobject.getString("overview");
                    dt.rate = jsonobject.getString("vote_average");
                    dt.id= jsonobject.getString("id") ;
                    data_list.add(dt);
                    arrayList.add(dt.url);
                }
                Log.e(LOG, "Succes");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }
        protected void onPostExecute(String data) {
            Custom_Adapter myadapter = new Custom_Adapter(getContext(),arrayList) ;
            gridView.setAdapter(myadapter) ;
        }
    }
}
