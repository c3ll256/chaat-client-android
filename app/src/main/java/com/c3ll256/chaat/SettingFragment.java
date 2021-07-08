package com.c3ll256.chaat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingFragment extends Fragment {
  private String userName;
  private String userId;
  private String avatar;

  public SettingFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences sp = this.requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
    this.userName = sp.getString("user_name", null);
    this.userId = sp.getString("user_id", null);
    this.avatar = sp.getString("user_avatar", null);
  }

  @SuppressLint({"CheckResult", "SetTextI18n"})
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
    ImageView avatarView = rootView.findViewById(R.id.setting_avatar);
    TextView userNameView = rootView.findViewById(R.id.setting_user_name);
    TextView userIdView = rootView.findViewById(R.id.setting_user_id);
    Button logOutView = rootView.findViewById(R.id.setting_logout);

    userNameView.setText(userName);
    userIdView.setText("ID: " + userId);

    if (avatar.equals("defualt_avatar.png")) {
      Glide.with(this.requireActivity())
          .load("https://chaat-avatar-1251621542.cos.ap-guangzhou.myqcloud.com/defualt_avatar.png").
          fitCenter()
          .override(256, 256)
          .into(avatarView);
    } else {
      Glide.with(this.requireActivity())
          .load(avatar).
          fitCenter().
          override(256, 256).
          into(avatarView);
    }

    logOutView.setOnClickListener(v -> {
      SharedPreferences sp = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
      sp.edit().clear().apply();
      @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      JSONObject sendData = new JSONObject();
      JSONObject sendBody = new JSONObject();
      try {
        sendData.put("status", "offline");
        sendData.put("last_changed", simpleDateFormat.format(new Date().getTime()));
        sendBody.put("operate", "update_status");
        sendBody.put("data", sendData);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      RequestQueue queue = Volley.newRequestQueue(this.requireContext());
      JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT,
          "http://106.52.127.85:7001/api/users/" +
              userId,
          sendBody, System.out::println,
          error -> Log.e("That didn't work!", error.toString()));
      queue.add(updateRequest);

      Intent intent = new Intent();
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setClass(requireActivity(), LandingActivity.class);
      startActivity(intent);
    });
    // Inflate the layout for this fragment
    return rootView;
  }
}