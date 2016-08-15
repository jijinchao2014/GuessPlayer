package com.jijc.guessplayer.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jijc.guessplayer.adapter.MyAdapter;
import com.jijc.guessplayer.R;
import com.jijc.guessplayer.bean.SongBean;
import com.jijc.guessplayer.bean.Songs;
import com.jijc.guessplayer.bean.WordBean;
import com.jijc.guessplayer.utils.WordUtil;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECTING_WORD = 24; //待选文字区域文字个数
    private static final int SONG_SUCCESS = 0; //校验成功
    private static final int SONG_FAILL = 1; //校验失败
    private static final int SONG_UNKNOW = 2; //答案为完成
    private int fillNum; //已经填充的数量
    private HashMap<Integer, WordBean> mapWord = new HashMap<>(); //存放已选区域的索引和待选区域的按钮以及对应关系
    private int currentSongIndex;  //当前播放歌曲的索引，初始值0
    private SongBean currentSong; //当前播放的歌曲
    private int selected_word; //答案的文字个数
    private boolean isStart;
    private List<WordBean> datas;//待选文字集合
    private List<WordBean> selects;//已选文字集合

    private CheckedTextView tvScore;
    private Button btnBack;
    private TextView tvSong;
    private ImageButton btnPlay;
    private ImageView ivBar;
    private ImageView ivPan;
    private Animation mPanAnim; //盘片的动画
    private Animation mBarInAnim; //拨杆进入动画
    private Animation mBarOutAnim; //拨杆拨开动画
    private RecyclerView recyclerView; //待选文字区域
    private RecyclerView selectedRecyclerView; //已选文字区域
    private MyAdapter adapter;  //待选文字
    private MyAdapter selectedAdapter; //已选文字
    private LinearLayout llSuccess;
    private RelativeLayout rlDelete;
    private RelativeLayout rlTip;
    private TextView tvDelete;
    private TextView tvTip;
    private TextView tvShare;
    private TextView tvLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

        adapter = new MyAdapter(this, datas, 0);
        selectedAdapter = new MyAdapter(this, selects, 1);
        recyclerView.setAdapter(adapter);
        selectedRecyclerView.setAdapter(selectedAdapter);

        //设置布局
        recyclerView.setLayoutManager(new GridLayoutManager(this, 8));
        selectedRecyclerView.setLayoutManager(new GridLayoutManager(this, selected_word));
        //待选区域按钮点击事件
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {

                Log.i("jijinc", "---------------------fillNum=" + fillNum);
                if (fillNum < selected_word) {
                    datas.get(position).isVisible = false;
                    adapter.notifyDataSetChanged();

                    for (int i = 0; i < selected_word; i++) {
                        if (TextUtils.isEmpty(selects.get(i).wordText)) {
                            selects.get(i).wordText = datas.get(position).wordText;
                            selectedAdapter.notifyDataSetChanged();
                            mapWord.put(i, datas.get(position));
                            fillNum++;
                            break;
                        }
                    }

                }
                    //进行歌曲校验
                    int result=checkSong();
                    switch (result){
                        case SONG_SUCCESS:
                            //校验成功跳转页面
                            Toast.makeText(MainActivity.this, "恭喜你，奖励一头牛", Toast.LENGTH_SHORT).show();
                            for (int i=0;i<selected_word;i++){
                                selects.get(i).tvSelected.setTextColor(Color.GREEN);
                            }
                            llSuccess.setVisibility(View.VISIBLE);
                            break;
                        case SONG_FAILL:
                            //校验失败已选文字闪烁，
                            Toast.makeText(MainActivity.this, "Sorry，今晚你得吃一头牛", Toast.LENGTH_SHORT).show();
                            for (int i=0;i<selected_word;i++){
                                ValueAnimator colorAnim = ObjectAnimator.ofInt(selects.get(i).tvSelected, "textColor", Color.WHITE, Color.RED);
                                colorAnim.setEvaluator(new ArgbEvaluator());
                                colorAnim.setDuration(200);
                                colorAnim.setRepeatCount(4);
                                colorAnim.setRepeatMode(ValueAnimator.REVERSE);
                                colorAnim.start();
                            }

                            break;
                        case SONG_UNKNOW:
                            //没有选择完成
                            Toast.makeText(MainActivity.this, "还没填完，加油哦", Toast.LENGTH_SHORT).show();
                            for (int i=0;i<selected_word;i++){
                                selects.get(i).tvSelected.setTextColor(Color.WHITE);
                            }
                            break;
                    }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "你长按了 [" + datas.get(position).wordText + "] ,position=" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //已选区域按钮点击事件
        selectedAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                for (int i=0;i<selected_word;i++){
                    if (selects.get(i).tvSelected!=null){
                        selects.get(i).tvSelected.setTextColor(Color.WHITE);
                    }
                }
//                Toast.makeText(MainActivity.this, "你点击了" + datas.get(position) + ",position=" + position, Toast.LENGTH_SHORT).show();
                selects.get(position).wordText = "";
                selectedAdapter.notifyDataSetChanged();
                WordBean wordBean = mapWord.get(position);//已选框文字原来的位置
                wordBean.isVisible = true;
                adapter.notifyDataSetChanged();
                fillNum--;
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "你长按了 [" + datas.get(position).wordText + "] ,position=" + position, Toast.LENGTH_SHORT).show();
            }
        });

        initAnim();
    }


    private void initView() {
        tvScore = (CheckedTextView) findViewById(R.id.tv_score);
        btnBack = (Button) findViewById(R.id.btn_back);
        tvSong = (TextView) findViewById(R.id.tv_song);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        ivBar = (ImageView) findViewById(R.id.iv_bar);
        ivPan = (ImageView) findViewById(R.id.iv_pan);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selectedRecyclerView = (RecyclerView) findViewById(R.id.ll_container);
        llSuccess = (LinearLayout) findViewById(R.id.ll_success);
        rlDelete = (RelativeLayout) findViewById(R.id.rl_delete);
        rlTip = (RelativeLayout) findViewById(R.id.rl_tip);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        tvShare = (TextView) findViewById(R.id.tv_share);
        tvLevel = (TextView) findViewById(R.id.tv_level);

        tvScore.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        rlDelete.setOnClickListener(this);
        rlTip.setOnClickListener(this);
        tvShare.setOnClickListener(this);
    }

    /**
     * 初始化待选框和已选框文字
     */
    private void initData() {
        currentSong = new SongBean();
        String[][] songs = Songs.SONGS;
        currentSong.fileName = songs[currentSongIndex][Songs.SONG_FILENAME];
        currentSong.songName = songs[currentSongIndex][Songs.SONG_SONGNAME];
        //设置待选框个数
        selected_word = currentSong.getSongLength();

        String[] words = WordUtil.generateWords(SELECTING_WORD, selected_word, currentSong);
        datas = new ArrayList<>();
        WordBean word = null;
        for (int i = 0; i < SELECTING_WORD; i++) {
            word = new WordBean();
            word.position = i;
            word.wordText = words[i];
            word.isVisible = true;
            datas.add(word);
        }

        selects = new ArrayList<>();
        WordBean word1 = null;
        for (int i = 0; i < selected_word; i++) {
            word1 = new WordBean();
            word1.position = i;
            word1.wordText = "";
            word1.isVisible = true;
            selects.add(word1);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back: //返回按键
                finish();
                break;
            case R.id.btn_play: //播放按钮
                if (isStart) {
                    barOutAnim();
                } else {
                    btnPlay.setVisibility(View.GONE);
                    barInAnim();
                }
                break;
            case R.id.rl_delete: //删除待选文字
                break;
            case R.id.rl_tip: //提示
                break;
            case R.id.tv_share: //分享
                break;

        }
    }

    @Override
    protected void onPause() {
        if (ivPan != null) {
            ivPan.clearAnimation();
        }
        super.onPause();
    }

    /**
     * 检查歌曲名是否正确
     *
     * @return
     */
    private int checkSong() {
        StringBuilder sub = new StringBuilder();
        for (int i = 0; i < selected_word; i++) {
            sub.append(selects.get(i).wordText);
            if (TextUtils.isEmpty(selects.get(i).wordText)) {
                return SONG_UNKNOW;
            }
        }
        return TextUtils.equals(currentSong.songName, sub.toString()) ? SONG_SUCCESS : SONG_FAILL;
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.pan_set);
        mPanAnim.setInterpolator(new LinearInterpolator());
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                barOutAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        mPanAnim.setRepeatMode(-1);

        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.bar_set_45);
        mBarInAnim.setInterpolator(new LinearInterpolator());

        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.bar_set_d_45);
        mBarOutAnim.setInterpolator(new LinearInterpolator());


    }

    /**
     * 拨杆进入动画
     */
    private void barInAnim() {
        ivBar.startAnimation(mBarInAnim);
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivPan.startAnimation(mPanAnim);
                mPanAnim.setFillAfter(true);

                isStart = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 拨杆拨开动画
     */
    private void barOutAnim() {
        ivBar.startAnimation(mBarOutAnim);
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPanAnim.cancel();
                mPanAnim.setFillAfter(true);
                ivPan.clearAnimation();
                btnPlay.setVisibility(View.VISIBLE);
                isStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}



