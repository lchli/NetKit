package com.lch.netkit.v2.filerequest;

import java.io.File;

/**
 * Created by bbt-team on 2017/8/15.
 */

public class FileResponse {

    private String reponseString;

    private File reponseFile;

    public String getReponseString() {
        return reponseString;
    }

    public void setReponseString(String reponseString) {
        this.reponseString = reponseString;
    }

    public File getReponseFile() {
        return reponseFile;
    }

    public void setReponseFile(File reponseFile) {
        this.reponseFile = reponseFile;
    }
}
