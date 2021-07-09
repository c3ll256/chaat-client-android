package com.c3ll256.chaat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c3ll256.chaat.datamodels.Message;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class LandingActivity extends AppCompatActivity {
  private TextInputLayout textField;
  private Button button;
  private String currentInputId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_landing);
    textField = findViewById(R.id.textField);
    button = findViewById(R.id.login_button);
    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);

    if (sp.getBoolean("logged_in", false)) {
      Intent intent = new Intent();
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setClass(this, ChatMainActivity.class);
      startActivity(intent);
    }

    button.setOnClickListener(v -> {
      setButtonClickable(false);
      textField.setEnabled(false);
      RequestQueue queue = Volley.newRequestQueue(v.getContext());

      StringRequest stringRequest = new StringRequest(Request.Method.GET,
          "http://106.52.127.85:7001/api/users/" +
              currentInputId,
          response -> {
            JSONObject jsonObject = JSONObject.parseObject(response);
            try {
              if (jsonObject.getString("status").equals("offline")) {
                // 寫入用戶到 SharedPreferences
                sp.edit().putString("user_id", jsonObject.getString("_id"))
                    .putString("user_name", jsonObject.getString("username"))
                    .putString("user_avatar", jsonObject.getString("avatar"))
                    .putBoolean("logged_in", true)
                    .apply();
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(this, ChatMainActivity.class);
                startActivity(intent);
              } else {
                textField.setEnabled(true);
                showError();
              }
            } catch (NullPointerException e) {
              e.printStackTrace();
              showError();
            }
          }, error -> {
        Log.e("That didn't work!", error.toString());
        showError();
      }
      );

      queue.add(stringRequest);
    });

    Objects.requireNonNull(textField.getEditText()).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
          setButtonClickable(false);
        } else {
          setButtonClickable(true);
          currentInputId = s.toString();
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    setButtonClickable(false);
  }

  private void setButtonClickable(boolean clickable) {
    if (clickable) {
      button.setClickable(true);
      button.setBackgroundColor(Color.parseColor("#1d3557"));
    } else {
      button.setClickable(false);
      button.setBackgroundColor(Color.GRAY);
    }
  }

  private void showError() {
    textField.setEnabled(true);
    Toast.makeText(this, "Wrong ID or ID is logged in", Toast.LENGTH_SHORT).show();
  }
}