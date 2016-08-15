package com.jijc.guessplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jijc.guessplayer.R;
import com.jijc.guessplayer.bean.WordBean;

import java.util.List;

/**
 * Description:待选框和已选框区域通用适配器
 * Created by jijc on 2016/8/15.
 * PackageName: com.jijc.guessplayer.adapter
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

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
            if (type == 0) { //type=0表示待选区域
                view = mInflater.inflate(R.layout.item_word, parent, false);
            } else { //已选区域
                view = mInflater.inflate(R.layout.item_word_select, parent, false);
            }
            return new MyAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            boolean isVisible = mDatas.get(position).isVisible;
            if (isVisible) {
                holder.tv.setVisibility(View.VISIBLE);
                //每个文字框出现时的动画，如果是第一次加载才会有动画
                if (isLoadAnim) {
                    if (type == 0) {
                        mWordAnim = AnimationUtils.loadAnimation(mContext, R.anim.word_scale);
                        mWordAnim.setStartOffset(position * 100);
                        holder.tv.startAnimation(mWordAnim);
                    }else {
                        mDatas.get(position).tvSelected=holder.tv;
                    }
                    //如果说最后一个条目已经初始化了，证明第一次加载完成，关闭动画，防止每次选错答案后再次点击选择答案加载动画
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
                            return true;  //这点应注意，返回true表示只执行这个事件  true if the callback consumed the long click, false otherwise.
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
