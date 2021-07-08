package com.c3ll256.chaat.datamodels;

import com.stfalcon.chatkit.commons.models.IUser;

public class Author implements IUser {
  private final String id;
  private final String name;
  private final String avatar;

  public Author(String _id, String _name, String _avatar) {
    this.id = _id;
    this.name = _name;
    if (_avatar.equals("defualt_avatar.png"))
      this.avatar = "https://chaat-avatar-1251621542.cos.ap-guangzhou.myqcloud.com/defualt_avatar.png";
    else
     this.avatar = _avatar;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAvatar() {
    return avatar;
  }
}
