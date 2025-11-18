package com.bokecc.vod.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bokecc.vod.R;
import com.bokecc.vod.adapter.DefinitionAdapter;
import com.bokecc.vod.data.DefinitionInfo;
import com.bokecc.vod.inter.SelectDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 清晰度
 * SelectDefinitionDialog
 * @author CC
 */
public class SelectDefinitionDialog extends Dialog {
    private final Context context;
    private final SelectDefinition selectDefinition;
    private final int currentDefinition;
    private final Map<String, Integer> definitions;

    public SelectDefinitionDialog(Context context, int currentDefinition, Map<String, Integer> definitions,SelectDefinition selectDefinition) {
        super(context, R.style.SetVideoDialog);
        this.context = context;
        this.selectDefinition = selectDefinition;
        this.currentDefinition = currentDefinition;
        this.definitions = definitions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_select_definition, null);
        setContentView(view);
        ListView lvDefinition = view.findViewById(R.id.lv_definition);
        List<DefinitionInfo> data = new ArrayList<>();
        if (definitions!=null){
            for (String key:definitions.keySet()){
                Integer integer = definitions.get(key);
                boolean  isSelected = integer == currentDefinition;
                DefinitionInfo definitionInfo = new DefinitionInfo(key, integer, isSelected);
                data.add(definitionInfo);
            }
        }

        final DefinitionAdapter definitionAdapter = new DefinitionAdapter(context, data);
        lvDefinition.setAdapter(definitionAdapter);
        lvDefinition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DefinitionInfo item = (DefinitionInfo) definitionAdapter.getItem(position);
                if (selectDefinition!=null && item!=null){
                    selectDefinition.selectedDefinition(item.getDefinitionText(),item.getDefinition());
                    dismiss();
                }
            }
        });
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.35);
        lp.height = (int) (d.heightPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.END);
    }

}
