package com.c3ll256.chaat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.alibaba.fastjson.JSONException;
import com.bumptech.glide.Glide;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c3ll256.chaat.datamodels.Author;
import com.c3ll256.chaat.datamodels.Message;
import com.c3ll256.chaat.datamodels.MessageSendRequest;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import io.socket.client.Socket;
import io.socket.client.IO;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {

  private Socket mSocket;
  private String userId;
  private String userName;
  private String roomId;
  private String currentOldestMessageId;
  private String lastMessageId;
  private ArrayList<Author> users;
  private MessagesListAdapter<Message> adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);
    users = new ArrayList<>();
    MessagesList messagesList = findViewById(R.id.messagesList);
    MessageInput messageInput = findViewById(R.id.input);


    // 通過 intent 初始化數據
    Intent intent = getIntent();
    roomId = intent.getStringExtra(ChatMainActivity.ROOM_ID);
    lastMessageId = intent.getStringExtra(ChatMainActivity.LAST_MESSAGE_ID);

    // 從 SharedPreferences 獲取當前用戶 ID
    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
    userId = sp.getString("user_id", null);
    userName = sp.getString("user_name", null);

    // 解析房間用戶列表
    JSONArray jsonArray = JSONArray.parseArray(intent.getStringExtra(ChatMainActivity.USER_LIST));
    for (Object obj : jsonArray) {
      JSONObject jsonObject = (JSONObject) obj;
      users.add(new Author(
          jsonObject.getString("id"),
          jsonObject.getString("name"),
          jsonObject.getString("avatar")
      ));
    }

    // 初始化 Socket
    try {
      mSocket = IO.socket("http://106.52.127.85:7001/");
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    // 創建 Socket 監聽
    mSocket.on("chaat", args -> runOnUiThread(() -> {
      try {
        JSONObject data = JSON.parseObject(args[0].toString());
        adapter.addToStart(new Message(
            data.getString("_id"),
            data.getString("content"),
            findUserById(data.getString("sender_id")),
            parseTime(data.getString("time"))
        ), true);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }));
    // Socket 連接
    mSocket.connect();

    // 創建 MessagesListAdapter
    adapter = new MessagesListAdapter<>(userId, (imageView, url, payload) -> {
      imageView.setScaleType(ImageView.ScaleType.FIT_XY);
      Glide.with(MessageActivity.this).load(url).fitCenter().into(imageView);
    });

    // 給 MessagesListAdapter 添加滾動到頂部的監聽器
    adapter.setLoadMoreListener((page, totalItemsCount) -> {
      this.loadMoreMessage(currentOldestMessageId);
    });

    // 給 MessageInput 組件添加發送監聽器
    messageInput.setInputListener(input -> {
      MessageSendRequest messageSendRequest = new MessageSendRequest(
          input.toString(),
          userId,
          userName,
          roomId
      );
      mSocket.emit("chaat", JSON.toJSON(messageSendRequest));
      return true;
    });

    // 初始化數據
    this.initData();

    // 給組件設置 Adapter
    messagesList.setAdapter(adapter);
  }

  private void initData() {
    this.loadMoreMessage(lastMessageId + 1);
  }

  private void loadMoreMessage(String currentOldestId) {
    RequestQueue queue = Volley.newRequestQueue(this);

    StringRequest stringRequest = new StringRequest(Request.Method.GET,
        "http://106.52.127.85:7001/api/messages/" +
            roomId +
            "?currentmessage=" +
            (currentOldestId) +
            "&operate=old",
        response -> {
          ArrayList<Message> messages = new ArrayList<>();
          JSONArray res = JSON.parseArray(response);
          for (Object obj : res) {
            JSONObject jsonObject = (JSONObject) obj;
            messages.add(new Message(
                jsonObject.getString("_id"),
                jsonObject.getString("content"),
                findUserById(jsonObject.getString("sender_id")),
                parseTime(jsonObject.getString("time"))
            ));
          }
          if (messages.size() != 0) {
            currentOldestMessageId = messages.get(0).getId();
          }
          adapter.addToEnd(messages, true);
        }, Throwable::printStackTrace
    );

    queue.add(stringRequest);
  }

  private Author findUserById(String id) {
    for (Author author : users) {
      if (author.getId().equals(id))
        return author;
    }
    return null;
  }

  private Date parseTime(String time) {
    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date result = null;
    try {
      result = simpleDateFormat.parse(time);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return result;
  }
}