package com.bokecc.vod.callback;

import com.bokecc.sdk.mobile.entry.AnswerSheetInfo;

import java.util.List;

/**
 * 选择题相关回调
 * ChoiceSelectListener
 * @author Zhang
 */
public interface ChoiceSelectListener {
    /**
     * onChoiceSelectStateChange
     * @param selectedAnswer  已选择答案
     * @param currentPosition 当前选择的角标索引
     * @param prePosition 上次的选择位置
     * @param select 当前是否为选中状态
     */
    void onChoiceSelectStateChange(List<AnswerSheetInfo.Answer> selectedAnswer,int currentPosition ,
                                   int prePosition,boolean select);
}
