package com.zestedesavoir.zestwriter.utils.api;

public interface ApiDownloaderListener{
    void onDownloadError();
    void onDownloadSuccess();
    default void onDownloadCancelled(){}
    default void onDownloadPaused(){}
    default void onDownloadResumed(){}
}
