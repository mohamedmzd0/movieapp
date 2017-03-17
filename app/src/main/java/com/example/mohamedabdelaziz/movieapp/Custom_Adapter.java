package com.example.mohamedabdelaziz.movieapp;

import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;

        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;


        public class Custom_Adapter extends BaseAdapter{
            ArrayList<String> movielist ;
            Context context ;
            String url ;

            Custom_Adapter(Context context , ArrayList<String>movilist )
            {
                this.movielist=movilist ;
                this.context= context ;

    }

    @Override
    public int getCount() {
        return movielist.size();
    }

    @Override
    public Object getItem(int i) {
        return movielist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE) ;
        view = inflater.inflate(R.layout.grid_view_layout,null) ;
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView) ;
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+movielist.get(i)).into(imageView);
        return view;
    }
}
