package com.jijc.guessplayer.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.jijc.guessplayer.bean.Songs;

import java.io.IOException;

/**
 * Description:
 * Created by jijc on 2016/8/17.
 * PackageName: com.jijc.guessplayer.utils
 */
public class MyPlayer {
    private static MyPlayer myPlayer;
    public static MediaPlayer mediaPlayer;
    public static MediaPlayer[] mediaPlayers;
    private MyPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayers = new MediaPlayer[Songs.SOUNDS.length];
        mediaPlayers[Songs.SOUND_COIN] = new MediaPlayer();
        mediaPlayers[Songs.SOUND_CANCEL] = new MediaPlayer();
        mediaPlayers[Songs.SOUND_ENTER] = new MediaPlayer();

    }
    public static MyPlayer getInstance(){
        if (myPlayer==null){
            myPlayer = new MyPlayer();
        }
        return myPlayer;
    }

    /**
     * 播放歌曲
     * @param context
     * @param fileName
     */
    public void play(Context context,String fileName){
        mediaPlayer.reset();
        AssetManager assets = context.getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assets.openFd(fileName);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        if (mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }

    public void stop(){
        if (mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }

    public void playSounds(Context context,int index){
        mediaPlayers[index].reset();
        AssetManager assets = context.getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assets.openFd(Songs.SOUNDS[index]);
            mediaPlayers[index].setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
            mediaPlayers[index].prepare();
            mediaPlayers[index].start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
