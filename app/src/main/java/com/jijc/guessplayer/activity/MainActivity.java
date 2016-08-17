package com.jijc.guessplayer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import com.jijc.guessplayer.R;
import com.jijc.guessplayer.adapter.MyAdapter;
import com.jijc.guessplayer.bean.SongBean;
import com.jijc.guessplayer.bean.Songs;
import com.jijc.guessplayer.bean.WordBean;
import com.jijc.guessplayer.dialog.SuccessDialog;
import com.jijc.guessplayer.utils.MyPlayer;
import com.jijc.guessplayer.utils.WordUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECTING_WORD = 24; //待选文字区域文字个数
    private static final int SONG_SUCCESS = 0; //校验成功
    private static final int SONG_FAILL = 1; //校验失败
    private static final int SONG_UNKNOW = 2; //答案为完成
    private static final int DELETE_SUCCESS = 0; //删除成功
    private static final int DELETE_FAILL = 1; //删除失败
    private static final int TIP_SUCCESS = 0; //提示成功
    private static final int TIP_FAILL = 1; //提示失败
    private int fillNum; //已经填充的数量
    private int point;//已选框的指针（输入焦点所在的位置）
    private HashMap<Integer, WordBean> mapWord = new HashMap<>(); //存放已选区域的索引和待选区域的按钮以及对应关系
    private int currentSongIndex=0;  //当前播放歌曲的索引，初始值0
    private SongBean currentSong; //当前播放的歌曲
    private int selected_word; //答案的文字个数
    private boolean isStart;
    private List<WordBean> datas;//待选文字集合
    private List<WordBean> selects;//已选文字集合
    private int totalScore=Songs.TOTAL_SCORE;
    private int deleteScore;
    private int tipScore;
    private String[] words; //随机排好序的数组
    private ArrayList<Integer> passedList = new ArrayList<>();//通关歌曲的集合
    private int TIME = 1700;//刷新后提示时间 3000-300

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
    private TextView tv_new_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

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
        tv_new_msg = (TextView) findViewById(R.id.tv_new_msg);

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

        deleteScore=getResources().getInteger(R.integer.score_delete);
        tipScore=getResources().getInteger(R.integer.score_tip);

        currentSong = new SongBean();
        String[][] songs = Songs.SONGS;
        currentSong.fileName = songs[currentSongIndex][Songs.SONG_FILENAME];
        currentSong.songName = songs[currentSongIndex][Songs.SONG_SONGNAME];
        currentSong.songScore=Integer.parseInt(songs[currentSongIndex][Songs.SONG_SCORE]);
        currentSong.songIndex=currentSongIndex+1;
        //设置待选框个数
        selected_word = currentSong.getSongLength();

        words = WordUtil.generateWords(SELECTING_WORD, selected_word, currentSong);
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

    /**
     * 初始化各种监听
     */
    private void initListener() {
        //设置各种金币数量
        tvScore.setText(totalScore+"");
        tvDelete.setText(deleteScore+"");
        tvTip.setText(tipScore+"");
        //设置关卡数
        tvLevel.setText(currentSong.songIndex+"");

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
                MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_ENTER);
                Log.i("jijinc", "---------------------fillNum=" + fillNum);
                if (fillNum < selected_word) {
                    datas.get(position).isVisible = false;
                    adapter.notifyDataSetChanged();

                    for (int i = 0; i < selected_word; i++) {
                        if (TextUtils.isEmpty(selects.get(i).wordText)) {
                            selects.get(i).wordText = datas.get(position).wordText;
                            selectedAdapter.notifyDataSetChanged();
                            mapWord.put(i, datas.get(position));
                            point=i+1;
                            fillNum++;
                            break;
                        }
                    }

                }
                //进行歌曲校验
                validateSongs();
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
                MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_ENTER);
                for (int i=0;i<selected_word;i++){
                    if (selects.get(i).tvSelected!=null){
                        selects.get(i).tvSelected.setTextColor(Color.WHITE);
                    }
                }
//                Toast.makeText(MainActivity.this, "你点击了" + datas.get(position) + ",position=" + position, Toast.LENGTH_SHORT).show();
                selects.get(position).wordText = "";
                selectedAdapter.notifyDataSetChanged();
                WordBean wordBean = mapWord.get(position);//已选框文字原来的位置
                if (wordBean!=null){
                    wordBean.isVisible = true;
                    adapter.notifyDataSetChanged();
                    point=position;
                    fillNum--;
                    Log.i("jijinc", "---------------------fillNum=" + fillNum);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "你长按了 [" + datas.get(position).wordText + "] ,position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initListener();
        initAnim();
        playMusic();
        Log.i("jijinc","----------------onResume()");
    }

    @Override
    protected void onPause() {
        Log.i("jijinc","----------------onPause()");
        MyPlayer.getInstance().stop();
        if (ivPan != null) {
            ivPan.clearAnimation();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back: //返回按键
                MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_ENTER);
                finish();
                break;
            case R.id.btn_play: //播放按钮
                playMusic();
                break;
            case R.id.tv_score:
                break;
            case R.id.rl_delete: //删除待选文字
                handleDelOperate();
                break;
            case R.id.rl_tip: //提示
                handleTipOperate();
                break;
            case R.id.tv_share: //分享
                MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_ENTER);
                break;

        }
    }

    /**
     * 开始播放音乐
     */
    private void playMusic() {
        if (isStart) {
            barOutAnim();
        } else {
            btnPlay.setVisibility(View.GONE);
            barInAnim();
            MyPlayer.getInstance().play(this,currentSong.fileName);
        }
    }

    /**
     * 检查歌曲结果
     */
    private void validateSongs() {
        int result=checkSong();
        switch (result){
            case SONG_SUCCESS:
                //校验成功跳转页面
//                Toast.makeText(MainActivity.this, "恭喜你，奖励一头牛", Toast.LENGTH_SHORT).show();
                for (int i=0;i<selected_word;i++){
                    selects.get(i).tvSelected.setTextColor(Color.GREEN);
                }
                MyPlayer.getInstance().stop();
                barOutAnim();
                if (currentSongIndex<Songs.TOTAL_SONGS_COUNT-1){
                    SuccessDialog dialog = new SuccessDialog(this,R.layout.shadow_success,currentSong);
                    dialog.setOnButtonClickListener(new SuccessDialog.OnButtonClickListener() {
                        @Override
                        public void onShadowClick() {
                            fillNum=0;
                            if (passedList!=null){
                                if (!passedList.contains(currentSongIndex)){
                                    totalScore+=currentSong.songScore;
                                    passedList.add(currentSongIndex);
                                    tv_new_msg.setText(" + "+currentSong.songScore);
                                    tv_new_msg.setVisibility(View.VISIBLE);
                                    AnimatorSet set = new AnimatorSet();
                                    set.play(ObjectAnimator.ofFloat(tv_new_msg, "alpha", 0.5f, 1f));
                                    set.setDuration(300).start();

                                    handler.postDelayed(runnable, TIME); //隔TIME时间执行
                                    MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_COIN);
                                }
                            }
                            MainActivity.this.onResume();
                        }

                        @Override
                        public void onNextClick() {
                            if (passedList!=null){
                                if (!passedList.contains(currentSongIndex)){
                                    totalScore+=currentSong.songScore;
                                    passedList.add(currentSongIndex);
                                    tv_new_msg.setText(" + "+currentSong.songScore);
                                    tv_new_msg.setVisibility(View.VISIBLE);
                                    AnimatorSet set = new AnimatorSet();
                                    set.play(ObjectAnimator.ofFloat(tv_new_msg, "alpha", 0.5f, 1f));
                                    set.setDuration(300).start();

                                    handler.postDelayed(runnable, TIME); //隔TIME时间执行
                                    MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_COIN);
                                }
                            }

                            currentSongIndex++;
                            fillNum=0;
                            MainActivity.this.onResume();
                        }

                        @Override
                        public void onShareWXClick() {
                            if (passedList!=null){
                                if (!passedList.contains(currentSongIndex)){
                                    totalScore+=currentSong.songScore;
                                    passedList.add(currentSongIndex);
                                    tv_new_msg.setText(" + "+currentSong.songScore);
                                    tv_new_msg.setVisibility(View.VISIBLE);
                                    AnimatorSet set = new AnimatorSet();
                                    set.play(ObjectAnimator.ofFloat(tv_new_msg, "alpha", 0.5f, 1f));
                                    set.setDuration(300).start();

                                    handler.postDelayed(runnable, TIME); //隔TIME时间执行
                                    MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_COIN);
                                }
                            }
                            fillNum=0;
//                        MainActivity.this.onResume();
                        }
                    });
                    dialog.show(getSupportFragmentManager(),"");
                }else {
                    if (passedList!=null){
                        if (!passedList.contains(currentSongIndex)){
                            totalScore+=currentSong.songScore;
                            passedList.add(currentSongIndex);
                        }
                    }
                    fillNum=0;
                    startActivity(new Intent(this,PassedActivity.class));
                }

                break;
            case SONG_FAILL:
                //校验失败已选文字闪烁，
//                Toast.makeText(MainActivity.this, "Sorry，今晚你得吃一头牛", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(MainActivity.this, "还没填完，加油哦", Toast.LENGTH_SHORT).show();
                for (int i=0;i<selected_word;i++){
                    selects.get(i).tvSelected.setTextColor(Color.WHITE);
                }
                break;
        }
    }

    /**
     * 处理删除按钮的逻辑
     */
    private void handleDelOperate() {
        //弹出提示，如果确认删除执行删除操作
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("扣币提示").setMessage("排除一个待选文字将花费"+deleteScore+"金币，确认删除？");
        dialog.setNegativeButton("算了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_CANCEL);
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击确定后：
                //      1：判断金币数量，如果金币数量少于应扣数量，提示
                if (totalScore<deleteScore){
                    Toast.makeText(MainActivity.this, "金币数量不足，请充值！", Toast.LENGTH_SHORT).show();
                    return;
                }
              //        2.判断删除结果
                int result=deleteWord();
                switch (result){
                    case DELETE_SUCCESS:
                        //      3.删除成功后扣除相应金币
                        totalScore=totalScore-deleteScore;
                        tvScore.setText(totalScore+"");

                        MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_COIN);

                        tv_new_msg.setText(" - "+deleteScore);
                        tv_new_msg.setVisibility(View.VISIBLE);
                        AnimatorSet set = new AnimatorSet();
                        set.play(ObjectAnimator.ofFloat(tv_new_msg, "alpha", 0.5f, 1f));
                        set.setDuration(300).start();

                        handler.postDelayed(runnable, TIME); //隔TIME时间执行
                        break;
                    case DELETE_FAILL:
                        Toast.makeText(MainActivity.this, "正确答案就在眼前了，加油！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        dialog.show();

    }

    /**
     * 任意删除一个正确答案除外的文字
     */
    private int deleteWord() {

        //3.从剩下的待选区域中随机生成一个随机数，将这个随机数对应的索引的按钮隐藏
        Random random = new Random();
        //如果待选框中全部是正确答案，直接返回删除失败
        boolean isAllRight=true;
        for (int i=0;i<datas.size();i++){
            if (datas.get(i).isVisible&&!currentSong.songName.contains(datas.get(i).wordText)){
                isAllRight=false;
            }
        }
        if (isAllRight){
            return DELETE_FAILL;
        }

        int num=random.nextInt(SELECTING_WORD);
        while (true){
            if (datas.get(num).isVisible&&!currentSong.songName.contains(datas.get(num).wordText)){
                datas.get(num).isVisible=false;
                adapter.notifyDataSetChanged();
                return DELETE_SUCCESS;
            }else {
                num=random.nextInt(SELECTING_WORD);
            }

        }

    }

    /**
     * 处理提示按钮操作
     */
    private void handleTipOperate() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("扣币提示").setMessage("提示操作将花费您"+tipScore+"金币，确认操作？");
        dialog.setNegativeButton("算了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_CANCEL);
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击确定后：
                //      1：判断金币数量，如果金币数量少于应扣数量，提示
                if (totalScore<tipScore){
                    Toast.makeText(MainActivity.this, "金币数量不足，请充值！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //      2：判断提示操作，提示成功扣币
                int result = tipWord();
                switch (result){
                    case TIP_SUCCESS:
                        //      3.删除成功后扣除相应金币
                        totalScore=totalScore-tipScore;
                        tvScore.setText(totalScore+"");

                        MyPlayer.getInstance().playSounds(MainActivity.this,Songs.SOUND_COIN);

                        tv_new_msg.setText(" - "+tipScore);
                        tv_new_msg.setVisibility(View.VISIBLE);
                        AnimatorSet set = new AnimatorSet();
                        set.play(ObjectAnimator.ofFloat(tv_new_msg, "alpha", 0.5f, 1f));
                        set.setDuration(300).start();

                        handler.postDelayed(runnable, TIME); //隔TIME时间执行
                        break;
                    case TIP_FAILL:
                        Toast.makeText(MainActivity.this, "再没有别的提示了", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 提示一个正确答案
     * @return
     */
    private int tipWord() {
        //将正确答案添加到指定位置
        if (fillNum < selected_word) {
            for (int i=0;i<selected_word;i++){
                if (TextUtils.isEmpty(selects.get(i).wordText)){
                    point=i;
                    break;
                }
            }
            String name=currentSong.getNameCharArray()[point]+"";
            //将已选框中已经出现的文字隐藏
            for (int i=0;i<selected_word;i++){
                if (TextUtils.equals(selects.get(i).wordText,name)){
                    selects.get(i).wordText="";
                    fillNum--;
                }
            }

            if (TextUtils.isEmpty(selects.get(point).wordText)){
                selects.get(point).wordText=name;
                selectedAdapter.notifyDataSetChanged();
            }


            //将正确答案所在的位置隐藏
            int pos=-1;
            for (int i=0;i<words.length;i++){
                if (TextUtils.equals(words[i],name)){
                    pos=i;
                }
            }
            datas.get(pos).isVisible=false;
            adapter.notifyDataSetChanged();

            mapWord.put(point, datas.get(pos));
            fillNum++;
            validateSongs();
            return TIP_SUCCESS;

        }else {
            return TIP_FAILL;
        }
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
                MyPlayer.getInstance().stop();
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

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
//                handler.postDelayed(this, TIME);
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(tv_new_msg, "alpha", 0.8f, 0.3f));
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tv_new_msg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                set.setDuration(300).start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

}



