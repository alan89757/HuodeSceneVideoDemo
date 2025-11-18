package com.bokecc.vod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.sdk.mobile.entry.AnswerSheetInfo;
import com.bokecc.vod.R;
import com.bokecc.vod.callback.ChoiceSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ChoiceAdapter
 *
 * @author Zhang
 */
public class ChoiceAdapter extends RecyclerView.Adapter<ChoiceAdapter.ChoiceHolder> {

    private final Context context;

    private final List<AnswerSheetInfo.Answer> contentList;

    private final List<AnswerSheetInfo.Answer> selectedAnswer;

    private boolean multiple;

    private int perSelectIndex = -1;

    private ChoiceSelectListener choiceSelectListener;

    public ChoiceAdapter(Context context, List<AnswerSheetInfo.Answer> contentList) {
        this.context = context;
        this.contentList = contentList;
        selectedAnswer = new ArrayList<>();
    }

    public void setChoiceSelectListener(ChoiceSelectListener choiceSelectListener) {
        this.choiceSelectListener = choiceSelectListener;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isMultiple() {
        return multiple;
    }

    @NonNull
    @Override
    public ChoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_choice, parent, false);
        return new ChoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChoiceHolder holder, int position) {
        holder.choiceItem.setText(contentList.get(position).getContent());
        holder.choiceItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentSelected;
                if (!isMultiple()) {
                    if (perSelectIndex == holder.getLayoutPosition()) {
                        return;
                    }
                    holder.choiceItem.setBackgroundResource(R.mipmap.answer_selected);
                    selectedAnswer.clear();
                    selectedAnswer.add(contentList.get(holder.getLayoutPosition()));
                    currentSelected = true;
                    if (perSelectIndex != -1) {
                        contentList.get(perSelectIndex).setSelect(false);
                    }
                    contentList.get(holder.getLayoutPosition()).setSelect(true);
                } else {
                    if (selectedAnswer.contains(contentList.get(holder.getLayoutPosition()))){
                        selectedAnswer.remove(contentList.get(holder.getLayoutPosition()));
                        contentList.get(holder.getLayoutPosition()).setSelect(false);
                        currentSelected = false;
                    } else {
                        selectedAnswer.add(contentList.get(holder.getLayoutPosition()));
                        contentList.get(holder.getLayoutPosition()).setSelect(true);
                        currentSelected = true;
                    }
                }
                if (choiceSelectListener != null) {
                    choiceSelectListener.onChoiceSelectStateChange(selectedAnswer, holder.getLayoutPosition(), perSelectIndex, currentSelected);
                }
                perSelectIndex = holder.getLayoutPosition();
            }
        });
    }

    public List<AnswerSheetInfo.Answer> getSelectedAnswer() {
        return selectedAnswer == null ? new ArrayList<AnswerSheetInfo.Answer>() : selectedAnswer;
    }

    @Override
    public int getItemCount() {
        return contentList == null ? 0 : contentList.size();
    }

    public static class ChoiceHolder extends RecyclerView.ViewHolder {

        public Button choiceItem;

        public ChoiceHolder(@NonNull View itemView) {
            super(itemView);
            choiceItem = itemView.findViewById(R.id.choiceItem);
        }
    }
}
