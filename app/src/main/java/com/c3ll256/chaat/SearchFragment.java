package com.c3ll256.chaat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;

import java.util.Date;
import java.util.Objects;

public class SearchFragment extends Fragment {
  private static final String url = "http://106.52.127.85:7001/api/users/";
  private static final String url1 = "http://106.52.127.85:7001/api/rooms/";
  private TextInputLayout textInputLayout;
  private Button button;
  private String inputId = "";

  public SearchFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_search, container, false);
    SharedPreferences sp = this.requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

    textInputLayout = rootView.findViewById(R.id.textFieldIdSearch);
    button = rootView.findViewById(R.id.add_button);

    Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        inputId = s.toString();
        setButtonClickable(s.length() != 0);
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    button.setOnClickListener(v -> {
      textInputLayout.setEnabled(false);
      setButtonClickable(false);
      RequestQueue queue = Volley.newRequestQueue(requireContext());
      StringRequest stringRequest = new StringRequest(Request.Method.GET,
          url + inputId,
          response -> {
            JSONObject res = JSON.parseObject(response);
            if (res.getBoolean("founded")) {
              StringRequest checkRequest = new StringRequest(Request.Method.GET,
                  url1 + sp.getString("user_id", null) + "?operate=isfriend&id=" +
                      inputId, response1 -> {
                JSONObject check = JSON.parseObject(response1);
                if (check.getBoolean("isfriend")) {
                  Toast.makeText(this.requireContext(), "Wrong ID or is already friend", Toast.LENGTH_SHORT).show();
                  textInputLayout.setEnabled(true);
                  setButtonClickable(true);
                } else {
                  org.json.JSONArray sendData = new org.json.JSONArray();
                  org.json.JSONObject sendBody = new org.json.JSONObject();
                  try {
                    sendData.put(sp.getString("user_id", null));
                    sendData.put(inputId);
                    sendBody.put("users", sendData);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  // 創建房間申請
                  JsonObjectRequest createRequest = new JsonObjectRequest(Request.Method.POST,
                      url1,
                      sendBody, System.out::println,
                      error -> {
                        showError();
                        error.printStackTrace();
                      });
                  showSuccess();
                  queue.add(createRequest);
                }
              }, error -> showError());
              queue.add(checkRequest);
            } else {
              showError();
            }
          }, error -> showError());

      queue.add(stringRequest);
    });

    setButtonClickable(false);
    // Inflate the layout for this fragment
    return rootView;
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
    Toast.makeText(this.requireContext(), "Wrong ID or is already friend", Toast.LENGTH_SHORT).show();
    Objects.requireNonNull(textInputLayout.getEditText()).setText("");
    textInputLayout.setEnabled(true);
    setButtonClickable(true);
  }

  private void showSuccess() {
    Toast.makeText(this.requireContext(), "Friend added", Toast.LENGTH_SHORT).show();
    Objects.requireNonNull(textInputLayout.getEditText()).setText("");
    textInputLayout.setEnabled(true);
    setButtonClickable(true);
  }
}