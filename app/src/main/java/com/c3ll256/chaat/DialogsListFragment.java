package com.c3ll256.chaat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.c3ll256.chaat.datamodels.Author;
import com.c3ll256.chaat.datamodels.DefaultDialog;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;

public class DialogsListFragment extends Fragment {
  DialogsList dialogsListView;
  private DialogsListAdapter dialogsListAdapter;
  private ArrayList<DefaultDialog> dialogs;
  private static final String url = "http://106.52.127.85:7001/api/rooms/";

  private String userId;

  public DialogsListFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 獲取 UserID
    SharedPreferences sp = this.requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
    userId = sp.getString("user_id", null);

    // 創建 DialogsListAdapter
    dialogsListAdapter = new DialogsListAdapter<DefaultDialog>((imageView, url, payload) -> {
      imageView.setScaleType(ImageView.ScaleType.FIT_XY);
      Glide.with(this.requireActivity()).load(url).fitCenter().into(imageView);
    });

    // 設置按下監聽器
    dialogsListAdapter.setOnDialogClickListener((DialogsListAdapter.OnDialogClickListener<DefaultDialog>) dialog -> {
      // 按下事件
      Intent intent = new Intent(this.requireActivity(), MessageActivity.class);
      intent.putExtra(ChatMainActivity.ROOM_ID, dialog.getId());
      intent.putExtra(ChatMainActivity.LAST_MESSAGE_ID, dialog.getLastMessageId());
      intent.putExtra(ChatMainActivity.USER_LIST, JSONArray.toJSON(dialog.getUsers()).toString());
      this.requireActivity().startActivity(intent);
    });
    // 初始化數據
    initData();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_dialogs_list, container, false);
    dialogsListView = rootView.findViewById(R.id.dialogsList);
    // 綁定 DialogsListAdapter
    dialogsListView.setAdapter(dialogsListAdapter);
    // 綁定數據
    dialogsListAdapter.setItems(dialogs);
    return rootView;
  }

  private void initData() {
    dialogs = new ArrayList<>();
    this.getData();
  }

  private void getData() {
    RequestQueue queue = Volley.newRequestQueue(this.requireContext());

    StringRequest stringRequest = new StringRequest(Request.Method.GET,
        url + userId + "?operate=showrooms",
        response -> {
          JSONArray res = JSON.parseArray(response);
          for (Object obj : res) {
            JSONObject jsonObject = (JSONObject) obj;
            ArrayList<Author> users = new ArrayList<>();
            JSONArray usersData = jsonObject.getJSONArray("users");
            for (Object user : usersData) {
              JSONObject userJSONObject = (JSONObject) user;
              users.add(new Author(
                  userJSONObject.getString("_id"),
                  userJSONObject.getString("username"),
                  userJSONObject.getString("avatar")
              ));
            }
            dialogs.add(new DefaultDialog(
                jsonObject.getString("roomId"),
                jsonObject.getString("avatar"),
                jsonObject.getString("roomName"),
                users,
                jsonObject.getString("last_message_id"),
                0
            ));
          }
          dialogsListAdapter.notifyDataSetChanged();
        }, error -> Log.e("That didn't work!", error.toString())
    );

    queue.add(stringRequest);
  }
}