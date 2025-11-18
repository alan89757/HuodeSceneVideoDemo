package com.bokecc.vod.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.sdk.mobile.entry.AnswerCommitResult;
import com.bokecc.sdk.mobile.entry.AnswerSheetInfo;
import com.bokecc.vod.R;
import com.bokecc.vod.utils.MultiUtils;

import java.util.List;

/**
 * StatisticsAdapter
 * @author Zhang
 */
public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsHolder> {

    private final Context context;
    private final List<AnswerCommitResult> resultList;
    private final AnswerSheetInfo answerSheetInfo;

    public StatisticsAdapter(Context context, AnswerSheetInfo answerSheetInfo,List<AnswerCommitResult> resultList) {
        this.context=context;
        this.answerSheetInfo=answerSheetInfo;
        this.resultList=resultList;
    }

    @NonNull
    @Override
    public StatisticsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_statistics,parent,false);
        return new StatisticsHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull StatisticsHolder holder, int position) {
        holder.selectScale.setText(String.format("%d%%", resultList.get(position).getScale()));
        holder.selectNum.setText(String.valueOf(resultList.get(position).getNum()));
        float i = resultList.get(position).getScale() / 100.00f;
        int height = (int) (MultiUtils.dipToPx(context,88)*i);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,height);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        holder.selectScaleView.setLayoutParams(params);
        AnswerSheetInfo.Answer answerInfoById = getAnswerInfoById(resultList.get(position).getId());
        if (answerInfoById!=null){
            holder.answerContent.setText(answerInfoById.getContent());
            if (answerInfoById.isRight()){
                holder.answerContent.setTextColor(Color.parseColor("#1BBD79"));
                holder.selectScaleView.setBackground(context.getResources().getDrawable(R.drawable.shape_statistics_right_bg));
            }else {
                holder.answerContent.setTextColor(Color.parseColor("#F55757"));
                holder.selectScaleView.setBackground(context.getResources().getDrawable(R.drawable.shape_statistics_wrong_bg));
            }
            holder.selectCurrentResult.setVisibility(answerInfoById.isSelect()?View.VISIBLE:View.GONE);
        }
    }

    private AnswerSheetInfo.Answer getAnswerInfoById(int id){
        for (AnswerSheetInfo.Answer answer:answerSheetInfo.getAnswers()){
            if (answer.getId()==id){
                return answer;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return resultList==null?0:resultList.size();
    }

    static class StatisticsHolder extends RecyclerView.ViewHolder{

        View selectScaleView;
        TextView selectScale,selectNum;
        ImageView selectCurrentResult;
        TextView answerContent;
        public StatisticsHolder(@NonNull View itemView) {
            super(itemView);
            selectScaleView=itemView.findViewById(R.id.selectScaleView);
            selectScale=itemView.findViewById(R.id.selectScale);
            selectNum=itemView.findViewById(R.id.selectNum);
            selectCurrentResult=itemView.findViewById(R.id.selectCurrentResult);
            answerContent=itemView.findViewById(R.id.answerContent);

        }
    }
}
