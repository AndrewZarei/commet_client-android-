package com.blockchain.commet.util;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static void getToasts(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
