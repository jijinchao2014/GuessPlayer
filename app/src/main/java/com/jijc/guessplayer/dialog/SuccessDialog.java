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

/**
 * Description:
 * Created by jijc on 2016/8/16.
 * PackageName: com.jijc.guessplayer.dialog
 */
@SuppressLint("ValidFragment")
public class SuccessDialog extends DialogFragment implements View.OnClickListener{
    private View view;
    private RelativeLayout rl_shadow;
    private TextView tvNext;
    private TextView tvShare;
    private Context mContext;
    private int layout;


    public SuccessDialog(Context mContext,int layout) {
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
        rl_shadow.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.shadow_dialog);
        setCancelable(true);
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
                dismissAllowingStateLoss();
                break;
            case R.id.tv_next:
                break;
            case R.id.tv_share_wx:
                break;
        }
    }
}
