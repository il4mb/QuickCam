package com.ilhamb.quickcam.utilities;

import io.reactivex.rxjava3.annotations.Nullable;

public interface TODO {
    void onSuccess();
    void onCallBack(@Nullable int key, @Nullable String data);
}
