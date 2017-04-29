package com.comm;

/**
 * Created by wangxuact on 3/20/17.
 */
public class Loop {

    String className;
    String methodName;
    String sig;

    public Loop (String str) {

        String[] words = str.split(" ");
        this.className = words[0];
        this.methodName = words[1];
        this.sig = words[2];
    }

    public boolean equal(String cName, String mName, String sig_) {
        if (Util.strEqual(className, cName) == false) return false;
        if (Util.strEqual(methodName, mName) == false) return false;
        if (Util.strEqual(this.sig, sig_) == false) return false;
        return true;
    }

}
