package com.solana.customConfig;

import com.solana.Config;

public class CustomContactPda {

    public static String[] getContactPda(){
        String[] a = new String[6];
        if (Config.network.equals("Main")){
            a[0] = "Ba8AcvH4G6DA1QKJLePQExfh4VzGLffPojGzeW9N6t8u";
            a[1] = "";
            a[2] = "";
            a[3] = "";
            a[4] = "";
            a[5] = "";
        } else {
            a[0] = "7WFmPNLNwZQFTgyhHiqgvo9DmGGdm5L4nUYNngTcWt1f";
            a[1] = "AgBLVPjUNf6ggFKn2nrRTjUKMtMpctcPjuVVvVUQn5tj";
            a[2] = "";
            a[3] = "";
            a[4] = "";
            a[5] = "";
        }
        return a;
    }

    public static String getContactPda_old(){
        if (Config.network.equals("Main")){
            return "D4VSz2XZviYv2fH4eR4W12Dv4b1XKRqM5XMaDPvKRALo";
        } else {
            return "J98ZB5pyfqah9xxue6HBYkj2xxsyga43vt3fG772iLoH";
        }
    }

}