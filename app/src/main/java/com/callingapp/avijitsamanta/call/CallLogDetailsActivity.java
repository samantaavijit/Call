package com.callingapp.avijitsamanta.call;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callingapp.avijitsamanta.call.Adopter.CallLogDetailsAdopter;
import com.callingapp.avijitsamanta.call.Helper.Modal;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CallLogDetailsActivity extends AppCompatActivity implements CallLogDetailsAdopter.ItemClickedCallLog {

    private String number, photo, name;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageViewProfile, imageViewCall, imageViewVideoCall, imageViewMSG;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat currentDateFormater = new SimpleDateFormat("dd/MMMM/yyyy"); //28/April/2020

    private List<Modal> callList = new ArrayList<>();
    private CallLogDetailsAdopter adopter;
    private RecyclerView recyclerView;
    private TextView textViewNumber, textViewDelete, textViewMore;
    private String curDate;
    private boolean isDelete = false;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_details);

        number = getIntent().getStringExtra("number");
        photo = getIntent().getStringExtra("photo");
        name = getIntent().getStringExtra("name");

        toolbar = findViewById(R.id.toolbar_call_log_profile_activity);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // For back to parent Activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout_profile_call_log);
        imageViewProfile = findViewById(R.id.image_view_call_log_profile_activity);
        recyclerView = findViewById(R.id.recycler_view_call_log_details);
        textViewNumber = findViewById(R.id.text_view_mobile_number_call_log);
        textViewDelete = findViewById(R.id.text_view_delete);
        textViewMore = findViewById(R.id.text_view_more);
        imageViewCall = findViewById(R.id.image_call_profile_call_log);
        imageViewVideoCall = findViewById(R.id.image_video_call_profile_call_log);
        imageViewMSG = findViewById(R.id.image_msg_profile_call_log);

        if (photo != null) {
            Glide.with(this)
                    .load(photo)
                    .into(imageViewProfile);
        } else
            imageViewProfile.setImageResource(R.drawable.ic_user);
        toolbar.setTitle(name);
        textViewNumber.setText("Mobile " + number);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        curDate = currentDateFormater.format(new Date());

        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CallLogDetailsActivity.this, "It is More", Toast.LENGTH_SHORT).show();
            }
        });

        textViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDelete = !isDelete;
            }
        });

        getCallLogDetails(number);

        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String s = "tel:" + number;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(s));
                if (ActivityCompat.checkSelfPermission(CallLogDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        });

        imageViewMSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
            }
        });

    }

    private void getCallLogDetails(String number) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        @SuppressLint("Recycle") Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null
                , CallLog.Calls.DEFAULT_SORT_ORDER);
        assert cursor != null;
        int numberInt = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int callType = cursor.getColumnIndex(CallLog.Calls.TYPE);

        while (cursor.moveToNext()) {

            if (cursor.getString(numberInt).equals(number)) {   // current phone number == call details phone number
                int cType = 0;
                String stringCallType = "";
                Date dd = new Date(Long.valueOf(cursor.getString(date)));

                switch (Integer.parseInt(cursor.getString(callType))) {
                    case CallLog.Calls.MISSED_TYPE:
                        cType = R.drawable.ic_missed_call_log;
                        stringCallType = "Missed Call";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        cType = R.drawable.ic_incoming_call_log;
                        stringCallType = "Incoming Call, " + getDurationTime(cursor.getString(duration));
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        cType = R.drawable.ic_out_call_logs;
                        stringCallType = "Outgoing Call, " + getDurationTime(cursor.getString(duration));
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        cType = R.drawable.ic_reject;
                        stringCallType = "Rejected Call";
                        break;
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("h:m a");  // 10:47 am

                String callDate, callTime;
                callTime = timeFormat.format(dd).toLowerCase();
                if (currentDateFormater.format(dd).equals(curDate)) {
                    callDate = "Today " + callTime;
                } else if (currentDateFormater.format(dd).equals(getYesterdayDateString())) {
                    callDate = "Yesterday " + callTime;
                } else
                    callDate = getDayMonthFormat(dd) + " " + callTime;

                callList.add(new Modal(stringCallType, callDate, cType, isDelete));
            }

        }

        adopter = new CallLogDetailsAdopter(callList, this, this);
        recyclerView.setAdapter(adopter);

    }

    private String getYesterdayDateString() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return currentDateFormater.format(cal.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    private String getDayMonthFormat(Date date) {
        return new SimpleDateFormat("d-MMMM").format(date);
    }

    private String getDurationTime(String ss) {
        int time = Integer.parseInt(ss);
        if (time >= 60) {
            int min = time / 60;
            int sec = time % 60;
            return min + " mins " + sec + " secs";
        }
        return "0 mins " + ss + " secs";
    }

    @Override
    public void onItemClick(int position) {
        if (isDelete) {

        }
    }
}
