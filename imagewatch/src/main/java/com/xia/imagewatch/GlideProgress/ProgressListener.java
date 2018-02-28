package com.xia.imagewatch.GlideProgress;

/**
 * Created by xiati on 2018/2/10.
 */

public interface ProgressListener {
    /**
            * 当下载进度发生变化时，会回调此方法。
            * @param progress
     *          当前的下载进度，参数值的范围是0-100。
            */
    void onProgress(int progress);
}
