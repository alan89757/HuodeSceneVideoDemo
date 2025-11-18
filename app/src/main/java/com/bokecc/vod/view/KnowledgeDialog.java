package com.bokecc.vod.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bokecc.vod.R;
import com.bokecc.vod.adapter.KnowledgeExpandableAdapter;
import com.bokecc.sdk.mobile.entry.KnowledgeBean;

import java.util.List;

/**
 * KnowledgeDialog
 *
 * @author Zhang
 */
public class KnowledgeDialog extends Dialog implements ExpandableListView.OnChildClickListener, View.OnClickListener, ExpandableListView.OnGroupClickListener {

    private final Context context;

    private final KnowledgeBean knowledgeBean;

    private KnowledgeExpandableAdapter adapter;

    private KnowledgeCallback knowledgeCallback;

    public KnowledgeDialog(@NonNull Context context, KnowledgeBean knowledgeBean) {
        super(context, R.style.SetVideoDialog);
        this.context = context;
        this.knowledgeBean = knowledgeBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_knowledge, null, false);
        view.findViewById(R.id.dismiss).setOnClickListener(this);
        TextView title = view.findViewById(R.id.title);
        title.setText(knowledgeBean.getTitle());
        setContentView(view);
        ExpandableListView expandableListView = view.findViewById(R.id.expandableListView);
        expandableListView.setDividerHeight(0);
        adapter = new KnowledgeExpandableAdapter(context, knowledgeBean);
        expandableListView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            KnowledgeBean.Category category = (KnowledgeBean.Category) adapter.getGroup(i);
            if (category.getInfo() != null && !category.getInfo().isEmpty()) {
                if (category.getInfo().size() == 1) {
                    expandableListView.expandGroup(i);
                }
            }
        }
        setLayoutParams();
        expandableListView.setOnGroupClickListener(this);
        expandableListView.setOnChildClickListener(this);
    }

    public void setKnowledgeCallback(KnowledgeCallback knowledgeCallback) {
        this.knowledgeCallback = knowledgeCallback;
    }

    private void setLayoutParams() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.35);
        lp.height = (int) (d.heightPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.END);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        adapter.setSelectGroupPosition(groupPosition);
        adapter.setSelectChildPosition(childPosition);
        adapter.notifyDataSetChanged();
        KnowledgeBean.Category.Info info = knowledgeBean.getCategory().get(groupPosition).getInfo().get(childPosition);
        if (knowledgeCallback != null && info != null) {
            knowledgeCallback.onKnowledgeClick(info, knowledgeBean.isPauseStatus());
        }
        if (isShowing()) {
            dismiss();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dismiss) {
            if (isShowing()) {
                dismiss();
            }
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        if (knowledgeBean == null) {
            return true;
        } else {
            List<KnowledgeBean.Category.Info> infoList = knowledgeBean.getCategory().get(groupPosition).getInfo();
            if (infoList == null || infoList.isEmpty()) {
                return true;
            }
            return infoList.size() == 1;
        }
    }

    public interface KnowledgeCallback {
        /**
         * onKnowledgeClick
         *
         * @param info        info
         * @param pauseStatue pauseStatue
         */
        void onKnowledgeClick(KnowledgeBean.Category.Info info, boolean pauseStatue);
    }
}
