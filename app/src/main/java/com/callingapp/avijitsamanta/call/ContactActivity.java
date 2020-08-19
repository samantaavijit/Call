package com.callingapp.avijitsamanta.call;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callingapp.avijitsamanta.call.Adopter.CallLogDetailsAdopter;
import com.callingapp.avijitsamanta.call.Adopter.GroupedListContactAdopter;
import com.callingapp.avijitsamanta.call.Adopter.SearchAdopter;
import com.callingapp.avijitsamanta.call.Helper.GeneralItem;
import com.callingapp.avijitsamanta.call.Helper.HeaderItem;
import com.callingapp.avijitsamanta.call.Helper.ItemClickListener;
import com.callingapp.avijitsamanta.call.Helper.ListItem;
import com.callingapp.avijitsamanta.call.Helper.Modal;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ContactActivity extends AppCompatActivity implements ItemClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FastScroller fastScroller;
    private SearchView searchView;
    private SearchAdopter searchAdopter;
    private List<Modal> searchList = new ArrayList<>();
    private HashMap<String, List<Modal>> hashMap = new HashMap<>();
    private GroupedListContactAdopter adopter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        closeKeyBoard();

        toolbar = findViewById(R.id.toolbar_contact_activity);
        toolbar.setTitle("Contacts");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // For back to parent Activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view_contact_activity);
        fastScroller = findViewById(R.id.fast_scroll);
        searchView = findViewById(R.id.search_view_contact_activity);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getContactDetails();

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

    public void closeKeyBoard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 1);
    }

    private void getContactDetails() {

        try {

            @SuppressLint("Recycle") Cursor cursor = Objects.requireNonNull(this).getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC");

            assert cursor != null;
            int name = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);


            while (cursor.moveToNext()) {
                String stringName = cursor.getString(name);
                String key = String.valueOf(stringName.charAt(0)).toUpperCase();


                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                @SuppressLint("Recycle") Cursor phoneCursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                assert phoneCursor != null;
                if (phoneCursor.moveToNext()) {
                    int aa = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                    Modal modal = new Modal(stringName, phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            , phoneCursor.getString(aa));

                    searchList.add(modal); // For Search View

                    if (hashMap.containsKey(key)) {  // For Header and display contact
                        hashMap.get(key).add(modal);
                    } else {
                        List<Modal> list = new ArrayList<>();
                        list.add(modal);
                        hashMap.put(key, list);
                    }
                }

            }

            List<ListItem> consolidatedList = new ArrayList<>();
            for (String hKEY : hashMap.keySet()) {
                HeaderItem headerItem = new HeaderItem();
                headerItem.setHeaderItem(hKEY);
                consolidatedList.add(headerItem);

                for (Modal modals : hashMap.get(hKEY)) {
                    GeneralItem generalItem = new GeneralItem();
                    generalItem.setGeneralItem(modals);
                    consolidatedList.add(generalItem);
                }
            }

            searchAdopter = new SearchAdopter(searchList, this, this);
            adopter = new GroupedListContactAdopter(consolidatedList, this);
            recyclerView.setAdapter(adopter);
            fastScroller.setRecyclerView(recyclerView);


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongItemClick(int position) {

    }
}
