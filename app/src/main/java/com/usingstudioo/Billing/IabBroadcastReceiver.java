package com.usingstudioo.Billing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IabBroadcastReceiver extends BroadcastReceiver {
    /**
     * Listener interface for received broadcast messages.
     */
    public interface IabBroadcastListener {
        void receivedBroadcast();
    }

    /**
     * The Intent action that this Receiver should filter for.
     */
    public static final String ACTION = "com.android.vending.billing.PURCHASES_UPDATED";

    private final IabBroadcastListener mListener;

    public IabBroadcastReceiver(IabBroadcastListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null) {
            try {
                mListener.receivedBroadcast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
