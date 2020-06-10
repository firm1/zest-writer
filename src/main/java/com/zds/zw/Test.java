package com.zds.zw;

import com.zds.zw.utils.ZMD;

public class Test {
    public static void main(String[] args) {
        ZMD zmd = new ZMD();
        System.out.println(zmd.toHtml("[[i]]\n| bonjour"));
    }
}
