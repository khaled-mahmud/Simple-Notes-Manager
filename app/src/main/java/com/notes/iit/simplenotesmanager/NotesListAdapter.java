package com.notes.iit.simplenotesmanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class NotesListAdapter extends CursorAdapter {
    public NotesListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.noteTitle);
        TextView preview = (TextView) view.findViewById(R.id.notePreview);
        TextView date = (TextView) view.findViewById(R.id.datetime);
        ImageView thumbnail = (ImageView) view.findViewById(R.id.list_image);
        String description = cursor.getString(cursor.getColumnIndexOrThrow(SqliteHelper.KEY_DESCRIPTION));
        String datetime=cursor.getString(cursor.getColumnIndexOrThrow(SqliteHelper.KEY_MODIFIEDDATE));
        //byte datetime=cursor.getString(cursor.getColumnIndexOrThrow(SqliteHelper.KEY_MODIFIEDDATE));
        byte[] rawImage = cursor.getBlob(cursor.getColumnIndexOrThrow(SqliteHelper.KEY_IMAGE));
        Bitmap bitmap = null;
        if(rawImage != null){
            bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
        }

        String noteTitle=description.split("\n")[0];
        String notePreview="";
        if(description.contains("\n")) {
            notePreview = description.split("\n")[1];
        }
        title.setText(noteTitle);
        preview.setText(notePreview);
        date.setText(datetime);
        if(rawImage != null){
        thumbnail.setImageBitmap(bitmap);
        }else{
            thumbnail.setImageResource(R.drawable.no_images);
        }
    }
}
