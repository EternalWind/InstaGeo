package com.etern.locationscout;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by etern on 8/5/2017.
 */

public final class ImageListAdapter extends BaseAdapter {
    private ArrayList<Bitmap> images;
    private Context context;
    private int itemSize;

    public ImageListAdapter(Context context, int itemSize, ArrayList<Bitmap> imgs) {
        this.context = context;
        images = imgs;
        this.setItemSize(itemSize);
    }

    public ImageListAdapter(Context context, int itemSize) {
        this(context, itemSize, new ArrayList<Bitmap>());
    }

    public ArrayList<Bitmap> getItems() {
        return images;
    }

    public void clear() {
        images.clear();
        notifyDataSetChanged();
    }

    public void add(Bitmap bitmap) {
        images.add(bitmap);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgView = (ImageView)convertView;

        if (imgView == null) {
            imgView = new ImageView(context);
            imgView.setLayoutParams(new GridView.LayoutParams(getItemSize(), getItemSize()));
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setPadding(0, 0, 0, 0);
        }

        Bitmap img = (Bitmap)getItem(position);
        imgView.setImageBitmap(img);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helpers.log(context, "Clicking an image.");
            }
        });

        return imgView;
    }


    public int getItemSize() {
        return itemSize;
    }

    public void setItemSize(int itemSize) {
        this.itemSize = itemSize;
    }
}
