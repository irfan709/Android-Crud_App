package com.example.usersapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {
CircleImageView reg_user_img;
TextInputLayout reg_email, reg_username, reg_pass;
TextInputEditText reg_input_email, reg_input_uname, reg_input_pass;
Button reg_btn;
private static final int SELECT_PICTURE = 1;
private static final int REQUEST_PERMISSIONS = 2;
private byte[] selectedImage;
DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_user_img = findViewById(R.id.reg_user_img);
        reg_email = findViewById(R.id.reg_email);
        reg_username = findViewById(R.id.reg_username);
        reg_pass = findViewById(R.id.reg_pass);
        reg_btn = findViewById(R.id.reg_btn);
        reg_input_email = findViewById(R.id.reg_input_email);
        reg_input_uname = findViewById(R.id.reg_input_uname);
        reg_input_pass = findViewById(R.id.reg_input_pass);
        dbHelper = new DbHelper(this);
        reg_user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(reg_input_email.getText()).toString().trim();
                String username = Objects.requireNonNull(reg_input_uname.getText()).toString().trim();
                String pass = Objects.requireNonNull(reg_input_pass.getText()).toString().trim();
                if (!validateEmail() | !validateUsername() | !validatePassword()) {
                    return;
                }
                else {
                    boolean b1 = dbHelper.checkUserExists(email, username);
                    if (b1) {
                        Toast.makeText(Register.this, "User already exists...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        boolean b = dbHelper.registerUserHelper(email, username, pass, selectedImage);
                        if (b) {
                            Toast.makeText(Register.this, "Registered successfully!!", Toast.LENGTH_SHORT).show();
                            reg_input_email.setText("");
                            reg_input_uname.setText("");
                            reg_input_pass.setText("");
                            reg_user_img.setImageResource(R.drawable.ic_launcher_background);
                        }
                        else {
                            Toast.makeText(Register.this, "Failed to register...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
         || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        } else {
           openGallery();
        }
    }

    private void showPermissionExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Storage Permission")
                .setMessage("This app needs access to your device storage")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                    }
                })
                .setNegativeButton("deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionSettingsDialog();
                }
            }
        }
    }

    private void showPermissionSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Storage settings")
                .setMessage("To use this app you need to grant the storage permission")
                .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openAppSettings();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);
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
                    reg_user_img.setImageBitmap(bitmap);
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
    public boolean validateEmail() {
        String email = Objects.requireNonNull(reg_email.getEditText()).getText().toString().trim();
        if (email.isEmpty()) {
            reg_email.setError("Field can't be empty");
            reg_input_email.requestFocus();
            return false;
        }  else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            reg_email.setError("Please enter valid email");
            return false;
        }
        else {
            reg_email.setError(null);
            return true;
        }
    }
    public boolean validateUsername() {
        String username = Objects.requireNonNull(reg_username.getEditText()).getText().toString();
        if (username.isEmpty()) {
            reg_username.setError("Field can't be empty");
            reg_input_uname.requestFocus();
            return false;
        }
        else if (username.length() > 20) {
            reg_username.setError("Username length length too long");
            reg_input_uname.requestFocus();
            return false;
        }
        else {
            reg_username.setError(null);
            return true;
        }
    }
    public boolean validatePassword() {
        String pass = Objects.requireNonNull(reg_pass.getEditText()).getText().toString();
        if (pass.isEmpty()) {
            reg_pass.setError("Field can't be empty");
            reg_input_pass.requestFocus();
            return false;
        }
        else if (!pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=~`{}\\[\\]|:;\"'<>,.?\\\\/])[^\\s]{6,}$"
        )) {
            reg_pass.setError("Password too weak");
            reg_input_pass.requestFocus();
            return false;
        }
        else {
            reg_pass.setError(null);
            return true;
        }
    }
}