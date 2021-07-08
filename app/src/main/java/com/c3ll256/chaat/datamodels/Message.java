package com.c3ll256.chaat.datamodels;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class Message implements IMessage {
  private final String id;
  private final String text;
  private final Author user;
  private final Date createAt;

  public Message(String id, String text, Author user, Date createAt) {
    this.id = id;
    this.text = text;
    this.user = user;
    this.createAt = createAt;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public IUser getUser() {
    return user;
  }

  @Override
  public Date getCreatedAt() {
    return createAt;
  }
}