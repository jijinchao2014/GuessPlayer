package com.jijc.guessplayer.bean;

import android.widget.TextView;

/**
 * Description:存储每个按钮的一些信息
 * Created by jijc on 2016/8/12.
 * PackageName: com.jijc.guessplayer.bean
 */
public class WordBean {

    public WordBean(){}
    public WordBean(int position,String wordText,TextView tvSelected){
        this.position=position;
//        this.isVisible=isVisible;
        this.wordText=wordText;
        this.tvSelected=tvSelected;
    }

    public String wordText;
    public int position;
    public boolean isVisible;
    public TextView tvSelected;
}
