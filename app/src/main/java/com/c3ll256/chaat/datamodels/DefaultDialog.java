package com.c3ll256.chaat.datamodels;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.ArrayList;
import java.util.List;

public class DefaultDialog implements IDialog {
  private final String id;
  private final String dialogPhoto;
  private final String dialogName;
  private final ArrayList<Author> users;
  private final String lastMessage;
  private final int unreadCount;

  public DefaultDialog(String _id,
                       String _dialogPhoto,
                       String _dialogName,
                       ArrayList<Author> _users,
                       String _lastMessage,
                       int _unreadCount) {
    this.id = _id;
    this.dialogName = _dialogName;
    this.users = _users;
    this.lastMessage = _lastMessage;
    this.unreadCount = _unreadCount;
    if (_dialogPhoto.equals("defualt_avatar.png"))
      this.dialogPhoto = "https://chaat-avatar-1251621542.cos.ap-guangzhou.myqcloud.com/defualt_avatar.png";
    else
      this.dialogPhoto = _dialogPhoto;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getDialogPhoto() {
    return dialogPhoto;
  }

  @Override
  public String getDialogName() {
    return dialogName;
  }

  @Override
  public List<Author> getUsers() {
    return users;
  }

  @Override
  public IMessage getLastMessage() {
    return null;
  }

  public String getLastMessageId() {
    return lastMessage;
  }

  @Override
  public void setLastMessage(IMessage message) {

  }

  @Override
  public int getUnreadCount() {
    return unreadCount;
  }
}
