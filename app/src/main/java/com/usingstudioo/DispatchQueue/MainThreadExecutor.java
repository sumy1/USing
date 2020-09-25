package com.usingstudioo.DispatchQueue;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

class MainThreadExecutor implements Executor {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public synchronized void execute(Runnable runnable) {
        handler.post(runnable);
    }

}
