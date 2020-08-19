package com.callingapp.avijitsamanta.call;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callingapp.avijitsamanta.call.Adopter.GroupedCallLogAdopter;
import com.callingapp.avijitsamanta.call.Adopter.SearchAdopter;
import com.callingapp.avijitsamanta.call.Helper.GeneralItem;
import com.callingapp.avijitsamanta.call.Helper.HeaderItem;
import com.callingapp.avijitsamanta.call.Helper.ItemClickListener;
import com.callingapp.avijitsamanta.call.Helper.ListItem;
import com.callingapp.avijitsamanta.call.Helper.Modal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class CallLogActivity extends AppCompatActivity implements ItemClickListener {

    private RecyclerView recyclerView;
    private boolean swipeBack;
    private int position, sPosition;
    private HashMap<String, List<Modal>> hashMap = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> sortHashMap = new HashMap<>();
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat currentDateFormater = new SimpleDateFormat("d-M-yyyy"); //28/April/2020
    private String curDate;
    int count = 0;
    private List<ListItem> listItems = new ArrayList<>();
    private GroupedCallLogAdopter adopter;
    private HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
    private List<Modal> searchList = new ArrayList<>();
    private SearchAdopter searchAdopter;
    public static final int REQUEST_CODE = 101;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        recyclerView = findViewById(R.id.recycler_view_call_log_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_call_log_activity);
        toolbar.setTitle("Phone");
        setSupportActionBar(toolbar);
        closeKeyBoard();
        TextView textViewContact = findViewById(R.id.text_view_toolbar_contact_call_log_activity);
        TextView textViewMore = findViewById(R.id.text_view_toolbar_more_call_log_activity);
        SearchView searchView = findViewById(R.id.search_view_call_log_activity);
        CircleImageView circleImageView = findViewById(R.id.circular_image_dial_pad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        assert windowmanager != null;
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;

        sPosition = (deviceWidth / 2) + 50;
        curDate = currentDateFormater.format(new Date());

        collectAllCallLogs();
        getContactDetails();

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                //startActivity(new Intent(CallLogActivity.this, DialActivity.class));
            }
        });

        textViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CallLogActivity.this, ContactActivity.class));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recyclerView.setAdapter(searchAdopter);
                List<Modal> modals = new ArrayList<>();
                for (Modal search : searchList) {
                    String name = search.getName().toLowerCase();
                    String mobile = search.getMobile().toLowerCase().trim();
                    String query = s.toLowerCase();

                    if (name.contains(query) || mobile.contains(query)) {
                        modals.add(search);
                    }
                    searchAdopter.setFilter(modals);
                }
                if (s == null || s.equals(""))
                    recyclerView.setAdapter(adopter);
                return true;
            }
        });
    }


    private void getContactDetails() {

        try {

            @SuppressLint("Recycle") Cursor cursor = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC");

            assert cursor != null;
            int name = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);


            while (cursor.moveToNext()) {
                String stringName = cursor.getString(name);
                // String key = String.valueOf(stringName.charAt(0)).toUpperCase();


                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                @SuppressLint("Recycle") Cursor phoneCursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                assert phoneCursor != null;
                if (phoneCursor.moveToNext()) {
                    int aa = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                    Modal modal = new Modal(stringName, phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            , phoneCursor.getString(aa));

                    searchList.add(modal);
                }

            }
            searchAdopter = new SearchAdopter(searchList, this, this);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void collectAllCallLogs() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return;

        }
        try {
            @SuppressLint("Recycle") Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    null, null
                    , CallLog.Calls.DEFAULT_SORT_ORDER);

            assert cursor != null;
            int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int callType = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int photo = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);

            while (cursor.moveToNext()) {
                int cType = 0;
                Date dd = new Date(Long.parseLong(cursor.getString(date)));

                switch (Integer.parseInt(cursor.getString(callType))) {
                    case CallLog.Calls.MISSED_TYPE:
                        cType = R.drawable.ic_missed_call_log;
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        cType = R.drawable.ic_incoming_call_log;
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        cType = R.drawable.ic_out_call_logs;
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        cType = R.drawable.ic_reject;
                        break;
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("h:m a");  // 10:47 am

                String callDate = currentDateFormater.format(dd);
                String mobileNumber = cursor.getString(number);
                String tKey = callDate + mobileNumber;
                Modal modal = new Modal(cursor.getString(name), mobileNumber,
                        callDate, timeFormat.format(dd).toLowerCase(), cType, cursor.getString(photo));
                if (hashMap.containsKey(callDate)) {

                    if (stringIntegerHashMap.containsKey(tKey)) {
                        stringIntegerHashMap.put(tKey, (stringIntegerHashMap.get(tKey)) + 1);
                    } else {
                        stringIntegerHashMap.put(tKey, 1);
                        Objects.requireNonNull(hashMap.get(callDate)).add(modal);
                    }

                } else {
                    List<Modal> list = new ArrayList<>();
                    list.add(modal);
                    hashMap.put(callDate, list);
                    sortHashMap.put(++count, callDate);
                    stringIntegerHashMap.put(tKey, 1);
                }
            }


            for (Integer ii : sortHashMap.keySet()) {
                HeaderItem headerItem = new HeaderItem();
                String stringKey = sortHashMap.get(ii);
                assert stringKey != null;
                if (stringKey.equals(curDate))
                    headerItem.setHeaderItem("Today");
                else if (stringKey.equals(getYesterdayDateString()))
                    headerItem.setHeaderItem("Yesterday");
                else
                    headerItem.setHeaderItem(getDayMonthFormat(currentDateFormater.parse(stringKey)));
                listItems.add(headerItem);

                for (Modal modals : Objects.requireNonNull(hashMap.get(sortHashMap.get(ii)))) {
                    GeneralItem generalItem = new GeneralItem();
                    generalItem.setGeneralItem(modals);
                    listItems.add(generalItem);
                }

            }

            adopter = new GroupedCallLogAdopter(listItems, this, this, stringIntegerHashMap);
            recyclerView.setAdapter(adopter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (swipeBack) {
                swipeBack = false;
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if (actionState == ACTION_STATE_SWIPE) {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setTouchListener(Canvas c,
                                      RecyclerView recyclerView,
                                      RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      int actionState, boolean isCurrentlyActive) {
            position = viewHolder.getAdapterPosition();

            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                    GeneralItem generalItem = (GeneralItem) listItems.get(position);
                    String mobile = generalItem.getGeneralItem().getMobile();
                    if (swipeBack) {
                        if (dX > sPosition) {// Open call activity
                            String s = "tel:" + mobile;
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(s));
                            if (ActivityCompat.checkSelfPermission(CallLogActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                return false;
                            }
                            startActivity(intent);
                        } else if (dX < -sPosition) { // Open message activity
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mobile, null)));
                        }
                    }
                    return false;
                }
            });
        }
    };


    private String getYesterdayDateString() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return currentDateFormater.format(cal.getTime());
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(CallLogActivity.this, CallLogDetailsActivity.class);
        GeneralItem generalItem = (GeneralItem) listItems.get(position);
        String number = generalItem.getGeneralItem().getMobile();
        String photo = generalItem.getGeneralItem().getImg();
        String name = generalItem.getGeneralItem().getName();
        intent.putExtra("number", number);
        intent.putExtra("photo", photo);
        intent.putExtra("name", name);
        startActivity(intent);

    }

    @Override
    public void onLongItemClick(int position) {

    }

    @SuppressLint("SimpleDateFormat")
    private String getDayMonthFormat(Date date) {
        return new SimpleDateFormat("d-MMMM").format(date);
    }

    public void closeKeyBoard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 1);
    }
}
