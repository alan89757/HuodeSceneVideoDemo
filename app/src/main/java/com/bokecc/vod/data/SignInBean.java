package com.bokecc.vod.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignInBean implements Serializable, Parcelable {
    private int resImg;
    private String title;
    private String content;
    private String btnText;
    private List<Long> seconds;
    public SignInBean(){}
    protected SignInBean(Parcel in) {
        resImg = in.readInt();
        title = in.readString();
        content = in.readString();
        btnText = in.readString();
        seconds = new ArrayList();
        in.readList(seconds,Long.class.getClassLoader());
    }

    public static final Creator<SignInBean> CREATOR = new Creator<SignInBean>() {
        @Override
        public SignInBean createFromParcel(Parcel in) {
            return new SignInBean(in);
        }

        @Override
        public SignInBean[] newArray(int size) {
            return new SignInBean[size];
        }
    };

    public int getResImg() {
        return resImg;
    }

    public void setResImg(int resImg) {
        this.resImg = resImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public List<Long> getSeconds() {
        return seconds;
    }

    public void setSeconds(List<Long> seconds) {
        this.seconds = seconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resImg);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(btnText);
        dest.writeList(seconds);
    }
}
