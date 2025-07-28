package com.example.mysolana.customipfs;

public interface ProgressListener {
    void onProgressUpdate(long bytesUploaded, long totalBytes);
}
