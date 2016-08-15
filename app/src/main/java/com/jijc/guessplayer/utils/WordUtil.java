package com.jijc.guessplayer.utils;

import com.jijc.guessplayer.bean.SongBean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Description:文字处理工具类
 * Created by jijc on 2016/8/15.
 * PackageName: com.jijc.guessplayer.utils
 */
public class WordUtil {
    /**
     * 获取随机汉字
     * @return
     */
    public static char getRandomChar() {
        String str = "";
        int hightPos, lowPos;
        Random random = new Random();
        hightPos = 176 + Math.abs(random.nextInt(39));
        lowPos = 161 + Math.abs(random.nextInt(93));
        byte[] bytes = new byte[2];
        bytes[0] = Integer.valueOf(hightPos).byteValue();
        bytes[1] = Integer.valueOf(lowPos).byteValue();
        try {
            str = new String(bytes, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }

    /**
     * 生成所有待选文字
     * @return
     */
    public static String[] generateWords(int selecting_word, int selected_word, SongBean currentSong){
        //待选文字集合
        String[] words = new String[selecting_word];
        //将歌曲名存入集合
        for (int i=0;i<selected_word;i++){
            words[i]=currentSong.getNameCharArray()[i]+"";
        }
        //将随机文字加入集合
        for (int i=selected_word;i<selecting_word;i++){
            words[i]=getRandomChar()+"";
        }

        //对集合进行随机排序
//        Arrays.sort(words);
        words = getSort(words,selecting_word);

        return words;
    }

    /**
     * 将字符串数组进行随机排序
     * 原理：准备一个新数组，将原有的数组存到集合里，随机取集合里的一个数据填充到新的数组里，每取出一个数据
     *          就将该数据从集合里删除
     * @param words
     * @return 排号序的新数组
     */
    public static String[] getSort(String[] words,int selecting_word) {
        String[] temp = new String[selecting_word];
        //将数组转换成长度可变的集合
        ArrayList<String> list = new ArrayList<>();
        for (int i=0;i<words.length;i++){
            list.add(words[i]);
        }
        Random random = new Random();
        int newLength=selecting_word;
        String word ="";
        for (int i=0;i<selecting_word;i++){
            int num = random.nextInt(newLength--);
            word=list.remove(num);
            temp[i]=word;
        }
        return temp;
    }
}
