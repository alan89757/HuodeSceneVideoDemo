package com.bokecc.vod.data;

import android.content.Context;
import io.objectbox.BoxStore;

public class MyObjectBox {
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        public Builder androidContext(Context c) {
            return this;
        }
        public BoxStore build() {
            return new BoxStore();
        }
    }
}