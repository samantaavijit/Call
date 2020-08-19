package com.callingapp.avijitsamanta.call;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import static com.callingapp.avijitsamanta.call.CallLogActivity.REQUEST_CODE;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);


        if (ActivityCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(PermissionActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(PermissionActivity.this
                , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                PermissionActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            String[] permission = {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE
                    , Manifest.permission.READ_PHONE_STATE};

            ActivityCompat.requestPermissions(PermissionActivity.this, permission, REQUEST_CODE);

        }else {
            startActivity(new Intent(PermissionActivity.this, CallLogActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PermissionActivity.this, "Permission Denied  ", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
            Toast.makeText(PermissionActivity.this, "All Permissions Granted ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PermissionActivity.this, CallLogActivity.class));
            finish();
        }

    }

}
