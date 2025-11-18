package com.bokecc.vod.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Exercise {
    private int id;
    private String title;
    private int showTime;
    private int isJump;
    private int isPlay;
    private int backSecond;
    private List<ExeQuestion> exeQuestions = new ArrayList<>();

    public Exercise(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        title = jsonObject.getString("title");
        showTime = jsonObject.getInt("showTime");
        if (jsonObject.has("isJump")){
            isJump = jsonObject.getInt("isJump");
        }
        if (jsonObject.has("isPlay")){
            isPlay = jsonObject.getInt("isPlay");
        }
        if (jsonObject.has("backSecond")){
            backSecond = jsonObject.getInt("backSecond");
        }

        JSONArray exeQuestionArray = jsonObject.getJSONArray("questions");

        for (int i=0; i<exeQuestionArray.length(); i++) {
            ExeQuestion exeQuestion = new ExeQuestion(exeQuestionArray.getJSONObject(i));
            exeQuestions.add(exeQuestion);
        }

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public int getIsJump() {
        return isJump;
    }

    public void setIsJump(int isJump) {
        this.isJump = isJump;
    }

    public int getIsPlay() {
        return isPlay;
    }

    public void setIsPlay(int isPlay) {
        this.isPlay = isPlay;
    }

    public int getBackSecond() {
        return backSecond;
    }

    public void setBackSecond(int backSecond) {
        this.backSecond = backSecond;
    }

    public List<ExeQuestion> getExeQuestions() {
        return exeQuestions;
    }

    public void setExeQuestions(List<ExeQuestion> exeQuestions) {
        this.exeQuestions = exeQuestions;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", showTime=" + showTime +
                ", exeQuestions=" + exeQuestions +
                '}';
    }
}
