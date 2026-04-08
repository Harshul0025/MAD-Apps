package com.example.photogalleryapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> images;

    public ImageAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.grid_item_image);
        String path = images.get(i);

        // Reset image to avoid showing old content while loading
        imageView.setImageDrawable(null);

        if (path.startsWith("content://")) {
            imageView.setImageURI(android.net.Uri.parse(path));
        } else {
            // Using setImageURI for file paths is also more efficient for basic usage
            imageView.setImageURI(android.net.Uri.fromFile(new java.io.File(path)));
        }

        return view;
    }
}