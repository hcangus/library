package net.hcangus.tips;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.hcangus.base.R;


/**
 * Created by Sai on 15/8/15.
 * 默认的SVProgress效果
 */
public class TipDefaultView extends LinearLayout {
    private int resBigLoading = R.mipmap.icon_tip_loading;
	private ImageView ivBigLoading, ivSmallLoading;
    private TipProgressBar circleProgressBar;
    private TextView tvMsg;

    private RotateAnimation mRotateAnimation;

    public TipDefaultView(Context context) {
        super(context);
        initViews();
        init();
    }

	public TipDefaultView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initViews();
		init();
	}

	private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tip_progress, this, true);
        ivBigLoading = (ImageView) findViewById(R.id.ivBigLoading);
        ivSmallLoading = (ImageView) findViewById(R.id.ivSmallLoading);
        circleProgressBar = (TipProgressBar) findViewById(R.id.circleProgressBar);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
    }

    private void init() {
        mRotateAnimation = new RotateAnimation(0f, 359f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(1000L);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    public void show() {
        clearAnimations();
        ivBigLoading.setImageResource(resBigLoading);
        ivBigLoading.setVisibility(View.VISIBLE);
        ivSmallLoading.setVisibility(View.GONE);
        circleProgressBar.setVisibility(View.GONE);
        tvMsg.setVisibility(View.GONE);
        //开启旋转动画
        ivBigLoading.startAnimation(mRotateAnimation);
    }

    public void show(CharSequence string) {
        if (string == null) {
            show();
            return;
        }
        showBaseStatus(resBigLoading, string);
        //开启旋转动画
        ivSmallLoading.startAnimation(mRotateAnimation);
    }

    public void showInfo(CharSequence string) {
		int resInfo = R.mipmap.icon_tip_info;
		showBaseStatus(resInfo, string);
    }

    public void showSuccess(CharSequence string) {
		int resSuccess = R.mipmap.icon_tip_success;
		showBaseStatus(resSuccess, string);
    }

    public void showError(CharSequence string) {
		int resError = R.mipmap.icon_tip_error;
		showBaseStatus(resError, string);
    }

    public TipProgressBar getCircleProgressBar() {
        return circleProgressBar;
    }

    public void setText(CharSequence string){
        tvMsg.setText(string);
    }

    public void showProgress(CharSequence string) {
        clearAnimations();
        tvMsg.setText(string);
        ivBigLoading.setVisibility(View.GONE);
        ivSmallLoading.setVisibility(View.GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        tvMsg.setVisibility(View.VISIBLE);
    }

    public void showBaseStatus(int res, CharSequence string) {
        clearAnimations();
        ivSmallLoading.setImageResource(res);
        tvMsg.setText(string);
        ivBigLoading.setVisibility(View.GONE);
        circleProgressBar.setVisibility(View.GONE);
        ivSmallLoading.setVisibility(View.VISIBLE);
        tvMsg.setVisibility(View.VISIBLE);
    }

    public void dismiss() {
        clearAnimations();
    }

    private void clearAnimations() {
        ivBigLoading.clearAnimation();
        ivSmallLoading.clearAnimation();
    }

}
