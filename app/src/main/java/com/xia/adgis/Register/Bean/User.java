package com.xia.adgis.Register.Bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 *
 * Created by xiati on 2018/2/9.
 */

public class User extends BmobUser {

    private String userIcon;
    private String nickName;
    private String motto;
    private String sex;
    private String birthday;
    private String address;
    private String userIconUri;

    public String getUserIconUri() {
        return userIconUri;
    }

    public void setUserIconUri(String userIconUri) {
        this.userIconUri = userIconUri;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
