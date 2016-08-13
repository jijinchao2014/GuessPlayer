package com.jijc.guessplayer.bean;

import android.text.TextUtils;

/**
 * Description:
 * Created by jijc on 2016/8/13.
 * PackageName: com.jijc.guessplayer.bean
 */
public class SongBean {
    public String fileName;
    public String songName;
    public int length;
    public char[] getNameCharArray(){
        if (!TextUtils.isEmpty(songName)){
            return songName.toCharArray();
        }
        return null;
    }

    public int getSongLength(){
        return songName.length();
    }
}
