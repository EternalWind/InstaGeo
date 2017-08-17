package com.etern.locationscout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.ArrayList;

/**
 * Created by etern on 8/5/2017.
 */

public final class PhotoListAdapter extends BaseAdapter {
    private ArrayList<Photo> photos;
    private Context context;
    private int itemSize;

    public PhotoListAdapter(Context context, int itemSize, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
        this.setItemSize(itemSize);
    }

    public PhotoListAdapter(Context context, int itemSize) {
        this(context, itemSize, new ArrayList<Photo>());
    }

    @NonNull
    public Photo[] getItems() {
        return photos.toArray(new Photo[0]);
    }

    public void clear() {
        photos.clear();
        notifyDataSetChanged();
    }

    public void add(Photo photo) {
        photos.add(photo);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(context);
            convertView.setLayoutParams(new GridView.LayoutParams(getItemSize(), getItemSize()));
            convertView.setPadding(0, 0, 0, 0);
        }

        final ImageView imgView = (ImageView)convertView;
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Photo photo = (Photo)getItem(position);
        photo.getData()
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        byte[] bytes = (byte[])result;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        imgView.setImageBitmap(bitmap);
                    }
                })
                .fail(new FailCallback() {
                    @Override
                    public void onFail(Object result) {
                        // TODO: Remove the ImageView or display an image indicating loading failure.
                    }
                });

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
