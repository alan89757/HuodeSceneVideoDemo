package com.bokecc.vod.data;

import android.content.Context;

import io.objectbox.BoxStore;

public class ObjectBox {
    private static BoxStore boxStore;
    private static boolean isInit =false;
    public static void init(Context context) {
        if (isInit){
            return;
        }
        isInit = true;
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
        DataSet.init(boxStore);
    }

    public static BoxStore get() { return boxStore; }
}
