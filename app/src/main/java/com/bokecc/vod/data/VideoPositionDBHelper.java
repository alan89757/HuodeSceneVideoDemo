package com.bokecc.vod.data;

import android.text.TextUtils;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

public class VideoPositionDBHelper {

    Box<VideoPosition> box;

    public VideoPositionDBHelper(BoxStore boxStore) {
        box = boxStore.boxFor(VideoPosition.class);
    }

    public VideoPosition getVideoPosition(String videoId) {
        if (TextUtils.isEmpty(videoId)){
            return null;
        }
        Query<VideoPosition> query = box.query()
                .equal(VideoPosition_.videoId, videoId, io.objectbox.query.QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build();
        VideoPosition videoPosition = query.findFirst();
        return videoPosition;
    }

    public void updateVideoPosition(VideoPosition videoPosition) {
        box.put(videoPosition);
    }
}
