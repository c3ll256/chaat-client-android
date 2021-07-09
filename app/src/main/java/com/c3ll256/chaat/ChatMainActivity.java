package com.c3ll256.chaat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMainActivity extends AppCompatActivity {
  public static final String ROOM_ID = "com.c3ll256.chaat.ROOM_ID";
  public static final String LAST_MESSAGE_ID = "com.c3ll256.chaat.LAST_MESSAGE_ID";
  public static final String USER_LIST = "com.c3ll256.chaat.USER_LIST";

  @SuppressLint("NonConstantResourceId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Fragment dialogsListFragment = new DialogsListFragment();
    Fragment searchFragment = new SearchFragment();
    Fragment settingFragment = new SettingFragment();

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .setReorderingAllowed(true)
          .add(R.id.fragment_container_list, dialogsListFragment, null)
          .add(R.id.fragment_container_list, settingFragment, null)
          .add(R.id.fragment_container_search, searchFragment, null)
          .hide(settingFragment)
          .commit();
    }
    setContentView(R.layout.activity_chat_main);
    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);

    JSONObject sendData = new JSONObject();
    JSONObject sendBody = new JSONObject();
    try {
      sendData.put("status", "online");
      sendData.put("last_changed", simpleDateFormat.format(new Date().getTime()));
      sendBody.put("operate", "update_status");
      sendBody.put("data", sendData);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    RequestQueue queue = Volley.newRequestQueue(this);
    JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT,
        "http://106.52.127.85:7001/api/users/" +
            sp.getString("user_id", null),
        sendBody, System.out::println,
        Throwable::printStackTrace);
    queue.add(updateRequest);

    bottomNavigationView.setSelectedItemId(R.id.page_chat);

    bottomNavigationView.setOnItemSelectedListener(item -> {
      switch (item.getItemId()) {
        case R.id.page_chat:
          getSupportFragmentManager().beginTransaction()
              .setReorderingAllowed(true)
              .hide(settingFragment)
              .show(searchFragment)
              .show(dialogsListFragment)
              .commit();
          break;
        case R.id.page_setting:
          getSupportFragmentManager().beginTransaction()
              .setReorderingAllowed(true)
              .hide(searchFragment)
              .hide(dialogsListFragment)
              .show(settingFragment)
              .commit();
          break;
      }
      return true;
    });
  }
}