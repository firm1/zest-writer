package com.zestedesavoir.zestwriter.utils.api;

public interface ApiDownloaderListener{
    void onDownloadError();
    void onDownloadSuccess();
    void onDownloadCancelled();
    void onDownloadPaused();
    void onDownloadResumed();
}
