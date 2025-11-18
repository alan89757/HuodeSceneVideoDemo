package com.bokecc.vod.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bokecc.vod.R;
import com.bokecc.sdk.mobile.entry.KnowledgeBean;

import java.util.List;

/**
 * KnowledgeExpandableAdapter
 *
 * @author Zhang
 */
@SuppressLint("DefaultLocale")
public class KnowledgeExpandableAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final KnowledgeBean bean;
    private int selectGroupPosition = -1, selectChildPosition = -1;

    public KnowledgeExpandableAdapter(Context context, @NonNull KnowledgeBean bean) {
        this.context = context;
        this.bean = bean;
    }

    @Override
    public int getGroupCount() {
        return bean.getCategory().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return bean.getCategory().get(groupPosition).getInfo().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return bean.getCategory().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return bean.getCategory().get(groupPosition).getInfo().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void setSelectGroupPosition(int selectGroupPosition) {
        this.selectGroupPosition = selectGroupPosition;
    }

    public void setSelectChildPosition(int selectChildPosition) {
        this.selectChildPosition = selectChildPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentHolder parentHolder;
        if (convertView == null) {
            parentHolder = new ParentHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_knowledge_parent, parent, false);
            parentHolder.parentName = convertView.findViewById(R.id.parentName);
            parentHolder.expandableStatue = convertView.findViewById(R.id.expandableStatue);
            convertView.setTag(parentHolder);
        } else {
            parentHolder = (ParentHolder) convertView.getTag();
        }
        parentHolder.expandableStatue.setImageResource(isExpanded ? R.mipmap.collapse_knowledge : R.mipmap.expand_knowledge);
        List<KnowledgeBean.Category.Info> info = bean.getCategory().get(groupPosition).getInfo();
        if (info==null||info.isEmpty()){
            parentHolder.expandableStatue.setVisibility(View.GONE);
        }else {
            parentHolder.expandableStatue.setVisibility(info.size()==1?View.GONE:View.VISIBLE);
        }
        String name = bean.getCategory().get(groupPosition).getName();
        int size = bean.getCategory().get(groupPosition).getInfo().size();
        parentHolder.parentName.setText(String.format("%s(%d)", name, size));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        List<KnowledgeBean.Category.Info> infoList = bean.getCategory().get(groupPosition).getInfo();
        boolean hasDesc = !TextUtils.isEmpty(infoList.get(childPosition).getDesc()) && infoList.get(childPosition).getDesc() != null;
        if (convertView == null) {
            childHolder = new ChildHolder();
            if (hasDesc) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_knowledge_child_with_desc, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_knowledge_child, parent, false);
            }
            childHolder.childRoot = convertView.findViewById(R.id.childRoot);
            childHolder.topLine = convertView.findViewById(R.id.topLine);
            childHolder.childOrder = convertView.findViewById(R.id.childOrder);
            childHolder.bottomLine = convertView.findViewById(R.id.bottomLine);
            childHolder.timeInfo = convertView.findViewById(R.id.timeInfo);
            childHolder.childDesc = convertView.findViewById(R.id.childDesc);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.topLine.setVisibility(childPosition == 0 ? View.INVISIBLE : View.VISIBLE);
        childHolder.bottomLine.setVisibility(childPosition == infoList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
        childHolder.childOrder.setText(String.valueOf(childPosition + 1));
        if (selectGroupPosition == groupPosition && selectChildPosition == childPosition) {
            childHolder.childOrder.setTextColor(Color.parseColor("#ff333333"));
            childHolder.childOrder.setBackgroundResource(R.drawable.shape_order_select);
            childHolder.childRoot.setBackgroundColor(Color.parseColor("#1AFFFFFF"));
        } else {
            childHolder.childOrder.setTextColor(Color.WHITE);
            childHolder.childOrder.setBackgroundResource(R.drawable.shape_order_normal);
            childHolder.childRoot.setBackground(null);
        }
        int startTime = infoList.get(childPosition).getStartTime();
        int endTime = infoList.get(childPosition).getEndTime();
        String time;
        String startTimeString = getMinuteSecondStr(startTime);
        String endTimeString = getMinuteSecondStr(endTime);
        if (endTime != 0) {
            time = startTimeString + "~" + endTimeString;
        } else {
            time = startTimeString;
        }
        childHolder.timeInfo.setText(time);
        if (hasDesc && childHolder.childDesc != null) {
            childHolder.childDesc.setVisibility(View.VISIBLE);
            childHolder.childDesc.setText(infoList.get(childPosition).getDesc());
        }
        return convertView;
    }


    /**
     * 格式化时间为分秒的形式
     */
    private String getMinuteSecondStr(int time) {
        int minute = time / 60;
        int second = time % 60;
        return addZeroOnTime(minute) + ":" + addZeroOnTime(second);
    }

    /**
     * 格式化时间，十位为0的时候，十位补0。如0，则返回"00"
     */
    private String addZeroOnTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return String.valueOf(time);
        }
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ParentHolder {
        TextView parentName;
        ImageView expandableStatue;
    }

    static class ChildHolder {
        LinearLayout childRoot;
        View topLine, bottomLine;
        TextView childOrder;
        TextView timeInfo;
        TextView childDesc;
    }
}
