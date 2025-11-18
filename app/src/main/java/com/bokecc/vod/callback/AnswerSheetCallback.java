package com.bokecc.vod.callback;

/**
 * AnswerSheetCallback
 * @author Zhang
 */
public interface AnswerSheetCallback {

    /**
     * 判断题，选择判断正确选项
     */
    void onJudgeWrightOption();

    /**
     * 判断题，选择判断错误选项
     */
    void onJudgeWrongOption();

    /**
     * 判断题，选择判断错误选项
     */
    void onJudgeSkipOption();

    /**
     * 判断题，提交答案
     */
    void onSubmitJudgeAnswer();

    /**
     * 单选提，选择的选项
     * @param position 单选的选择选项
     */
    void onSingleChoiceOption(int position);

    /**
     * 多择提，选择的选项
     * @param position 点击index
     * @param select 是否选择
     */
    void onMultipleChoiceOptions(int position,boolean select);
}
