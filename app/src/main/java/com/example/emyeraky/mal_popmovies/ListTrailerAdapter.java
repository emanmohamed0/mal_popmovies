package com.example.emyeraky.mal_popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Emy Eraky on 8/30/2016.
 */
public class ListTrailerAdapter extends BaseAdapter {
    Context context;
    String [] text;
    public ListTrailerAdapter(Context context , String [] text){
        this.context=context;
        this.text=text;

    }
    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int i) {
        return text[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.trailer_list,null);
        }

        TextView textView=(TextView)view.findViewById(R.id.text_Trailer);
        textView.setText(text[i]);
        return view;
    }
}
