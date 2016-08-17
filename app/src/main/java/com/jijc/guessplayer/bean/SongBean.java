package com.jijc.guessplayer.bean;

import android.text.TextUtils;

/**
 * Description:歌曲信息
 * Created by jijc on 2016/8/13.
 * PackageName: com.jijc.guessplayer.bean
 */
public class SongBean {
    public String fileName;
    public String songName;
    public int songIndex;
    public int songScore;
    public boolean isPassed;//是否过关了
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
