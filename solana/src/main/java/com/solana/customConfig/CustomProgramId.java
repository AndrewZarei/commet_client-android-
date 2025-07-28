package com.solana.customConfig;

import com.solana.Config;

public class CustomProgramId {

    public static String getContactProgramId(){
        if (Config.network.equals("Main")){
            return "7CaaDsKDwQ74HKZhxpozb6n8SxaAPDYRvJPrxkLtm67S";//(new)
//            return "CUdVSkaXMoAnr7NLSKjD25Zk5gpTrhtuXJ4Fxaw7nxRA";
        } else {
            return "8jrX8mjgipdGVWJH5o1PeDrEgNJHNyBVTJycbKhm3yc4";
        }
    }

    public static String getProfileProgramId(){
        if (Config.network.equals("Main")){
            return "5nFHkukP8jGuQEzpMH1H6hk5i6HUt6KamZw2NuSr97Ru";
        } else {
            return "HwE7A7DcLcscNfJBf3kZt9TABvfRWuD8RcT4kVhYPM4M";
        }
    }

    public static String getConversationProgramId(){
        if (Config.network.equals("Main")){
            return "9ANy7cbrSBGFMMjHURsnpKSiqm9rdPFGZokzS62FvLFU";
        } else {
            return "7jRAvySXHGTJMKBQNfNxmemLnW1gnCPm8Bv1WWwzUN5Y";
        }
    }
}