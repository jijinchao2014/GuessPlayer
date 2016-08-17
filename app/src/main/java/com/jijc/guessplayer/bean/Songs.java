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
            {"__00001.m4a", "童话","100"},
            {"__00002.m4a", "同桌的你","100"},
            {"__00003.m4a", "七里香","150"},
            {"__00004.m4a", "传奇","150"},
            {"__00005.m4a", "大海","150"},
            {"__00006.m4a", "后来","200"},
            {"__00007.m4a", "你的背包","200"},
            {"__00008.m4a", "再见","300"},
            {"__00009.m4a", "老男孩","300"},
            {"__00010.m4a", "龙的传人","300"}
    };
    public static final int TOTAL_SONGS_COUNT=Songs.SONGS.length;
    public static final String[] SOUNDS = new String[]{
        "cancel.mp3","coin.mp3","enter.mp3"
    };
}
