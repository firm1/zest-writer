package com.zestedesavoir.zestwriter.utils.api;

public interface ApiInstallerListener{
    void onInstallStarting();
    void onInstallEnding();
    void onInstallError();
    void onInstallSuccess();
}
