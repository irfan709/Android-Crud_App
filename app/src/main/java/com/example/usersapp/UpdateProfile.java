package com.example.usersapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
CircleImageView up_img;
EditText up_uname, up_pass;
Button update_btn;
Serializable email;
private byte[] selectedImage;
DbHelper dbHelper;
private static final int SELECT_PICTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        up_img = findViewById(R.id.up_img);
        up_uname = findViewById(R.id.up_uname);
        up_pass = findViewById(R.id.up_pass);
        update_btn = findViewById(R.id.update_btn);
        dbHelper = new DbHelper(this);
        Users users = new Users();
        up_uname.setText(Users.getName());
        up_pass.setText(Users.getPassword());
        byte[] imageData = Users.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        up_img.setImageBitmap(bitmap);
        selectedImage = imageData;

        email = getIntent().getSerializableExtra("user");
        Users userss = (Users) getIntent().getSerializableExtra("key_users");
        up_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = up_uname.getText().toString();
                String pass = up_pass.getText().toString();
                if (name.isEmpty()) {
                    up_uname.setError("Field can't be empty");
                    return;
                }
                if (pass.isEmpty()) {
                    up_pass.setError("Field can't be empty");
                    return;
                }
                else {
                    boolean b = dbHelper.updateProfileHelper(Users.getEmail(), name, pass, selectedImage);
                    if (b) {
                        Toast.makeText(UpdateProfile.this, "Updated successfully!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfile.this, Profile.class);
                        intent.putExtra("email", Users.getEmail());
                        intent.putExtra("image", selectedImage);
                        startActivity(intent);
                        finishAffinity();

                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    up_img.setImageBitmap(bitmap);
                    selectedImage = getBytesFromBitmap(bitmap);
                }
            }
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}