package com.notes.iit.simplenotesmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListEditActivity extends AppCompatActivity {
    private SqliteHelper sqliteHelper;
    EditText description;
    TextView dateModified;
    TextView characterCount;

    FloatingActionButton addPhotoButton;
    ImageView thumbImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit);
        sqliteHelper=new SqliteHelper(this);
        initializeViews();
        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                characterCount.setText(String.valueOf(s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        };
        description.addTextChangedListener(mTextEditorWatcher);


        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void initializeViews() {
        description=(EditText)findViewById(R.id.description);
        dateModified=(TextView)findViewById(R.id.date);
        characterCount=(TextView)findViewById(R.id.characterCount);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String currentDateTime=simpleDateFormat.format(new Date());
        dateModified.setText(currentDateTime);
        addPhotoButton = (FloatingActionButton)findViewById(R.id.fab_photo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save:
                Date date=new Date();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String currentDateTime=simpleDateFormat.format(date);
                byte[] image = imageViewToByte(thumbImage);
                Note note=new Note(description.getText().toString(),currentDateTime, image);
                try{
                    sqliteHelper.addNote(note);
                    Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                }
                //sqliteHelper.addNote(note);
                dateModified.setText(currentDateTime);
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    private void selectImage(){
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ListEditActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }else if(items[i].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                }else if(items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            final ImageView image = new ImageView(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ListEditActivity.this);
            builder.setTitle("Image");
            if(requestCode == REQUEST_CAMERA){
                Bundle bundle  = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                image.setImageBitmap(bmp);

            }else if(requestCode == SELECT_FILE){
                Uri selectedImageUri = data.getData();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    image.setImageBitmap(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }


            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    thumbImage = image;
                    dialog.dismiss();
                }
            });

            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setView(image);
            builder.show();
        }
    }

    private byte[] imageViewToByte(ImageView image){
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
