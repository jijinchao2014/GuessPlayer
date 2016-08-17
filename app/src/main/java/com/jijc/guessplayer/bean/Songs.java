package com.jijc.guessplayer.bean;

/**
 * Description:模拟数据库
 * Created by jijc on 2016/8/13.
 * PackageName: com.jijc.guessplayer.bean
 */
public class Songs {
    public static final int SONG_FILENAME=0;
    public static final int SONG_SONGNAME=1;
    public static final int SONG_SCORE=2;
    public static final int SOUND_CANCEL=0;
    public static final int SOUND_COIN=1;
    public static final int SOUND_ENTER=2;
    public static final int TOTAL_SCORE=1080;
    public static final String[][] SONGS=new String[][]{
            {"__00000.m4a", "征服","100"},
            {"nzdwzdnm.mp3", "你知道我在等你吗","150"},
            {"bxrnzd.mp3", "不想让你知道","100"},
            {"__00001.m4a", "童话","100"},
            {"hxqbj.mp3", "火星情报局","100"},
            {"__00002.m4a", "同桌的你","100"},
            {"cbg.mp3", "丑八怪","300"},
            {"__00003.m4a", "七里香","150"},
            {"zcdmx.mp3", "最初的梦想","300"},
            {"__00004.m4a", "传奇","150"},
            {"xh.mp3", "心火","300"},
            {"__00005.m4a", "大海","150"},
            {"ynzj.mp3", "一念之间","300"},
            {"__00006.m4a", "后来","200"},
            {"__00010.m4a", "龙的传人","300"},
            {"lj.mp3", "老街","300"},
            {"__00007.m4a", "你的背包","200"},
            {"__00008.m4a", "再见","300"},
            {"__00009.m4a", "老男孩","300"},
            {"yy.mp3", "演员","300"},
            {"pbaq.mp3", "普遍爱情","300"}
    };
    public static final int TOTAL_SONGS_COUNT=Songs.SONGS.length;
    public static final String[] SOUNDS = new String[]{
        "cancel.mp3","coin.mp3","enter.mp3"
    };
}
