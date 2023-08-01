package com.example.usersapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
CircleImageView prof_img;
TextView prof_email, prof_uname, prof_pass;
ImageView edit_img, del_img;
Button logout_btn;
String email;
byte[] imageData;
String email1;
DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        prof_img = findViewById(R.id.prof_img);
        prof_email = findViewById(R.id.prof_email);
        prof_uname = findViewById(R.id.prof_uname);
        prof_pass = findViewById(R.id.prof_pass);
        edit_img = findViewById(R.id.edit_img);
        del_img = findViewById(R.id.del_img);
        logout_btn = findViewById(R.id.logout_btn);
        dbHelper = new DbHelper(this);
        email = getIntent().getStringExtra("email");
        email1 = getIntent().getStringExtra("email");
        imageData = getIntent().getByteArrayExtra("image");
        getUserDetails();
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, MainActivity.class));
                finishAffinity();
            }
        });
        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Users user = new Users();
                Users.setImage(Users.getImage()); // Get the user image data
                Intent intent = new Intent(Profile.this, UpdateProfile.class);
                intent.putExtra("key_users", user);
                startActivity(intent);
            }
        });
        del_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure want to delete?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       boolean b = dbHelper.deleteUserHelper(Users.getEmail());
                       if (b) {
                           Toast.makeText(Profile.this, "Profile deleted...", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(Profile.this, MainActivity.class));
                           finishAffinity();
                       }
                    }
                });
                builder.show();
            }
        });
    }

    private void getUserDetails() {
        ArrayList<Users> al = dbHelper.getLoggedInUserDetails(email);
        if (!al.isEmpty()) {
            Users user = al.get(0);
            prof_email.setText(Users.getEmail());
            prof_uname.setText(Users.getName());
            prof_pass.setText(Users.getPassword());
            byte[] imageData = Users.getImage();
//            if (imageData != null) {}
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            prof_img.setImageBitmap(bitmap);
        }
    }

}