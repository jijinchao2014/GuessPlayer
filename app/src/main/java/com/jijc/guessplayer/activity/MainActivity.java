package com.jijc.guessplayer.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jijc.guessplayer.R;
import com.jijc.guessplayer.bean.SongBean;
import com.jijc.guessplayer.bean.Songs;
import com.jijc.guessplayer.bean.WordBean;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECTING_WORD = 24;
    private static final int SONG_SUCCESS=0; //校验成功
    private static final int SONG_FAILL=1; //校验失败
    private static final int SONG_UNKNOW=2; //答案为完成

    private int selected_word;
    private CheckedTextView tvScore;
    private Button btnBack;
    private TextView tvSong;
    private ImageButton btnPlay;
    private ImageView ivBar;
    private ImageView ivPan;
    private boolean isStart;
    private boolean isShow = true;

    private Animation mPanAnim;

    private LinearInterpolator mPanLin;
    private Animation mBarInAnim;
    private Animation mBarOutAnim;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<WordBean> datas;//待选文字集合
    private List<WordBean> selects;//已选文字集合
    private RecyclerView llContainer;
    private int fillNum; //已经填充的数量
    private HashMap<Integer, WordBean> mapWord = new HashMap<>();
    private MyAdapter adapter1;
    private int currentSongIndex;
    private SongBean currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

        adapter = new MyAdapter(this, datas, 0);
        adapter1 = new MyAdapter(this, selects, 1);
        recyclerView.setAdapter(adapter);
        llContainer.setAdapter(adapter1);

        //设置布局
        recyclerView.setLayoutManager(new GridLayoutManager(this, 8));
        llContainer.setLayoutManager(new GridLayoutManager(this, selected_word));
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
                            adapter1.notifyDataSetChanged();
                            mapWord.put(i, datas.get(position));
                            fillNum++;
                            break;
                        }
                    }

                }
//                if (fillNum == selected_word) {
                    //进行歌曲校验
                    int result=checkSong();
                    switch (result){
                        case SONG_SUCCESS:
                            //校验成功跳转页面
                            Toast.makeText(MainActivity.this, "恭喜你，奖励一头牛", Toast.LENGTH_SHORT).show();
                            for (int i=0;i<selected_word;i++){
                                selects.get(i).tvSelected.setTextColor(Color.GREEN);
                            }
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
                            Toast.makeText(MainActivity.this, "还没填完，加油哦", Toast.LENGTH_SHORT).show();
                            for (int i=0;i<selected_word;i++){
                                selects.get(i).tvSelected.setTextColor(Color.WHITE);
                            }
                            break;
                    }
//                    if (isShow) {
//                        Toast.makeText(MainActivity.this, "恭喜你，猜错了", Toast.LENGTH_SHORT).show();
//                        isShow = false;
//                    }
//                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "你长按了" + datas.get(position) + ",position=" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //已选区域按钮点击事件
        adapter1.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                for (int i=0;i<selected_word;i++){
                    if (selects.get(i).tvSelected!=null){
                        selects.get(i).tvSelected.setTextColor(Color.WHITE);
                    }
                }
//                Toast.makeText(MainActivity.this, "你点击了" + datas.get(position) + ",position=" + position, Toast.LENGTH_SHORT).show();
                selects.get(position).wordText = "";
                adapter1.notifyDataSetChanged();
                WordBean wordBean = mapWord.get(position);//已选框原来的位置按钮
                wordBean.isVisible = true;
                adapter.notifyDataSetChanged();
                fillNum--;
                isShow=true;
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "你长按了" + datas.get(position) + ",position=" + position, Toast.LENGTH_SHORT).show();
            }
        });


        initAnim();
    }

    /**
     * 检查歌曲名是否正确
     * @return
     */
    private int checkSong() {
        StringBuilder sub = new StringBuilder();
        for (int i=0;i<selected_word;i++){
            sub.append(selects.get(i).wordText);
            if (TextUtils.isEmpty(selects.get(i).wordText)){
                return SONG_UNKNOW;
            }
        }
        return TextUtils.equals(currentSong.songName,sub.toString())?SONG_SUCCESS:SONG_FAILL;
    }

    private void initView() {
        tvScore = (CheckedTextView) findViewById(R.id.tv_score);
        btnBack = (Button) findViewById(R.id.btn_back);
        tvSong = (TextView) findViewById(R.id.tv_song);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        ivBar = (ImageView) findViewById(R.id.iv_bar);
        ivPan = (ImageView) findViewById(R.id.iv_pan);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llContainer = (RecyclerView) findViewById(R.id.ll_container);
        tvScore.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
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


    private void initData() {
        currentSong = new SongBean();
        String[][] songs = Songs.SONGS;
        currentSong.fileName = songs[currentSongIndex][Songs.SONG_FILENAME];
        currentSong.songName = songs[currentSongIndex][Songs.SONG_SONGNAME];
        //设置待选框个数
        selected_word = currentSong.getSongLength();

        String[] words = generateWords();
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
     * 获取随机汉字
     * @return
     */
    private char getRandomChar() {
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
    private String[] generateWords(){
        //待选文字集合
        String[] words = new String[SELECTING_WORD];
        //将歌曲名存入集合
        for (int i=0;i<selected_word;i++){
            words[i]=currentSong.getNameCharArray()[i]+"";
        }
        //将随机文字加入集合
        for (int i=selected_word;i<SELECTING_WORD;i++){
            words[i]=getRandomChar()+"";
        }

        //对集合进行随机排序
//        Arrays.sort(words);
       words = getSort(words);

        return words;
    }

    private String[] getSort(String[] words) {
        String[] temp = new String[SELECTING_WORD];
        //将数组转换成长度可变的集合
        ArrayList<String> list = new ArrayList<>();
        for (int i=0;i<words.length;i++){
            list.add(words[i]);
        }
        Random random = new Random();
        int newLength=SELECTING_WORD;
        String word ="";
        for (int i=0;i<SELECTING_WORD;i++){
            int num = random.nextInt(newLength--);
            word=list.remove(num);
            temp[i]=word;
        }
        return temp;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_play:
                if (isStart) {
                    barOutAnim();
                } else {
                    btnPlay.setVisibility(View.GONE);
                    barInAnim();
                }
                break;

        }
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

    @Override
    protected void onPause() {
        if (ivPan != null) {
            ivPan.clearAnimation();
        }
        super.onPause();
    }
}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context mContext;
    private List<WordBean> mDatas;
    private LayoutInflater mInflater;
    private int type;
    private Animation mWordAnim;
    private boolean isLoadAnim = true;

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MyAdapter() {
    }

    public MyAdapter(Context context, List<WordBean> datas, int type) {
        this.mContext = context;
        this.mDatas = datas;
        this.type = type;
        mInflater = LayoutInflater.from(context);


    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (type == 0) {
            view = mInflater.inflate(R.layout.item_word, parent, false);
        } else {
            view = mInflater.inflate(R.layout.item_word_select, parent, false);
        }
        return new MyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        boolean isVisible = mDatas.get(position).isVisible;
        if (isVisible) {
            holder.tv.setVisibility(View.VISIBLE);
            if (isLoadAnim) {
                if (type == 0) {
                    mWordAnim = AnimationUtils.loadAnimation(mContext, R.anim.word_scale);
                    mWordAnim.setStartOffset(position * 100);
                    holder.tv.startAnimation(mWordAnim);
                }else {
                    mDatas.get(position).tvSelected=holder.tv;
                }
                if (position == mDatas.size() - 1) {
                    isLoadAnim = false;
                }
            }
            String c = mDatas.get(position).wordText;
            holder.tv.setText(c);
            //设置背景（点击水波纹效果）
//            holder.itemView.setBackgroundResource(R.drawable.recycler_bg);

            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });

            }
        } else {
            holder.tv.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

//    public void add(int pos) {
//        mDatas.add(pos, "InsertOne");
//        //如果想要动画效果必须使用这个方法，不能使用notifyDatasetChanged()
//        notifyItemInserted(pos);
//    }
//
//    public void delete(int pos) {
//        mDatas.remove(pos);
//        notifyItemRemoved(pos);
//    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_word);
        }
    }

}

