package com.zestedesavoir.zestwriter.utils.api;

public interface ApiInstallerListener{
    default void onInstallStarting(){}
    default void onInstallEnding(){}
    void onInstallError();
    void onInstallSuccess();
}
