package com.mark.bestchristmaswallpaper;

import android.annotation.SuppressLint;
import android.app.admin.DelegatedAdminReceiver;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WallpaperaAdapter extends ArrayAdapter<DataHandler>{
    Context context;
    int resource;
    List<DataHandler> dataHandlers;
    public WallpaperaAdapter(@NonNull Context context, int resource, @NonNull List<DataHandler> list) {
        super(context, resource, list);
        this.context=context;
        this.resource=resource;
        this.dataHandlers=list;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater =LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view=layoutInflater.inflate(resource,null,false);
DataHandler datalist =dataHandlers.get(position);
        TextView textView =view.findViewById(R.id.title);
        ImageView imageView =view.findViewById(R.id.listImage);
        textView.setText(datalist.getTitle());
        Glide.with(context).load(datalist.getThumbnail()).centerCrop().error(R.drawable.no_image).placeholder(R.drawable.ic_loading)
                .into(imageView);

        return view;
    }
}
