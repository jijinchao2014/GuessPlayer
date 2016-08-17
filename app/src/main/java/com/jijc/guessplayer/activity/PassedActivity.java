package com.jijc.guessplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.jijc.guessplayer.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PassedActivity extends AppCompatActivity implements View.OnClickListener{

    @InjectView(R.id.btn_back)
    Button btnBack;
    @InjectView(R.id.tv_score)
    CheckedTextView tvScore;
    @InjectView(R.id.tv_wxchat)
    TextView tvWxchat;
    @InjectView(R.id.tv_credit)
    TextView tvCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passed);
        ButterKnife.inject(this);
        tvScore.setVisibility(View.GONE);

    }

    @OnClick({R.id.btn_back, R.id.tv_wxchat,R.id.tv_credit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_wxchat:
                break;
            case R.id.tv_credit:
                break;
        }
    }
}
