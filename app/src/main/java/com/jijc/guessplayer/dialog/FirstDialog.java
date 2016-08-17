package com.jijc.guessplayer.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jijc.guessplayer.R;
import com.jijc.guessplayer.bean.SongBean;

/**
 * Description:
 * Created by jijc on 2016/8/16.
 * PackageName: com.jijc.guessplayer.dialog
 */
@SuppressLint("ValidFragment")
public class FirstDialog extends DialogFragment implements View.OnClickListener{
    private View view;
    private RelativeLayout rl_shadow;
    private TextView tvNext;
    private TextView tvShare;
    private Context mContext;
    private int layout;
    private TextView tvLevel;
    private TextView tvName;
    private TextView tvScore;


    public FirstDialog(Context mContext, int layout) {
        super();
        this.mContext = mContext;
        this.layout = layout;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(layout,container,false);
        rl_shadow = (RelativeLayout)view.findViewById(R.id.rl_shadow);
        tvNext = (TextView) view.findViewById(R.id.tv_next);
        tvShare = (TextView)view.findViewById(R.id.tv_share_wx);
        tvLevel = (TextView)view.findViewById(R.id.tv_success_level);
        tvName = (TextView)view.findViewById(R.id.tv_success_name);
        tvScore = (TextView)view.findViewById(R.id.tv_score);
        rl_shadow.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.shadow_dialog);
        setCancelable(false);//设置对话框点击返回按钮无效
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        WindowManager wm = (WindowManager)mContext
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        getDialog().getWindow().setLayout(width, getDialog().getWindow().getAttributes().height);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.rl_shadow:
//                dismissAllowingStateLoss();
                if (onButtonClickListener!=null){
                    onButtonClickListener.onShadowClick();
                }
                break;
            case R.id.tv_next:
                dismissAllowingStateLoss();
                if (onButtonClickListener!=null){
                    onButtonClickListener.onNextClick();
                }
                break;
            case R.id.tv_share_wx:
                dismissAllowingStateLoss();
                if (onButtonClickListener!=null){
                    onButtonClickListener.onShareWXClick();
                }
                break;
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener){
        this.onButtonClickListener=onButtonClickListener;
    }
    private OnButtonClickListener onButtonClickListener;
    public interface OnButtonClickListener{
        void onShadowClick();
        void onNextClick();
        void onShareWXClick();
    }
}
