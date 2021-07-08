package com.c3ll256.chaat.datamodels;

import com.alibaba.fastjson.annotation.JSONField;

public class MessageSendRequest {
  @JSONField(name = "content")
  private String content;
  @JSONField(name = "sender_id")
  private String sender_id;
  @JSONField(name = "username")
  private String username;
  @JSONField(name = "room_id")
  private String room_id;
  @JSONField(name = "system")
  private Boolean system;
  @JSONField(name = "saved")
  private Boolean saved;
  @JSONField(name = "distributed")
  private Boolean distributed;
  @JSONField(name = "seen")
  private Boolean seen;
  @JSONField(name = "disable_actions")
  private Boolean disable_actions;
  @JSONField(name = "disable_reactions")
  private Boolean disable_reactions;

  public MessageSendRequest(String content, String sender_id, String username, String room_id) {
    this.content = content;
    this.sender_id = sender_id;
    this.username = username;
    this.room_id = room_id;
    this.system = false;
    this.saved = false;
    this.distributed = true;
    this.seen = false;
    this.disable_actions = false;
    this.disable_reactions = true;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSender_id() {
    return sender_id;
  }

  public void setSender_id(String sender_id) {
    this.sender_id = sender_id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRoom_id() {
    return room_id;
  }

  public void setRoom_id(String room_id) {
    this.room_id = room_id;
  }

  public Boolean getSystem() {
    return system;
  }

  public void setSystem(Boolean system) {
    this.system = system;
  }

  public Boolean getSaved() {
    return saved;
  }

  public void setSaved(Boolean saved) {
    this.saved = saved;
  }

  public Boolean getDistributed() {
    return distributed;
  }

  public void setDistributed(Boolean distributed) {
    this.distributed = distributed;
  }

  public Boolean getSeen() {
    return seen;
  }

  public void setSeen(Boolean seen) {
    this.seen = seen;
  }

  public Boolean getDisable_actions() {
    return disable_actions;
  }

  public void setDisable_actions(Boolean disable_actions) {
    this.disable_actions = disable_actions;
  }

  public Boolean getDisable_reactions() {
    return disable_reactions;
  }

  public void setDisable_reactions(Boolean disable_reactions) {
    this.disable_reactions = disable_reactions;
  }
}
