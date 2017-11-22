package net.hcangus.loadretry;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.hcangus.base.R;
import net.hcangus.mvp.view.BaseView;


/**
 * SCRM 配合{@link LoadingAndRetryManager}使用
 * <pre>
 *     {@link #setRetryBtn(CharSequence)}设置按钮的文字
 *     {@link #setRetryBtn(View.OnClickListener)}设置按钮的点击事件
 *     {@link #setRetryImage(int)}设置图片,
 *     {@link #setRetryText(CharSequence)}设置显示的信息,
 *     {@link #setRetryText(int)}设置显示的信息,
 *     这些方法必须在{@link LoadingAndRetryManager#generate(Object, OnLoadingAndRetryListener, boolean)}后使用.
 * </pre>
 * Created by Administrator on 2017/3/25.
 */

public class DefaultLoadingRetryListener implements OnLoadingAndRetryListener {
	private final BaseView mvpView;
	private ImageView retryImage;
	private TextView retryText;
	private Button retryBtn;
	private ImageView emptyImage;
	private TextView emptyText;

	public DefaultLoadingRetryListener(@NonNull BaseView mvpView) {
		this.mvpView = mvpView;
	}

	@Override
	public void setRetryEvent(View retryView) {
		retryImage = (ImageView) retryView.findViewById(R.id.retry_image);
		retryText = (TextView) retryView.findViewById(R.id.retry_text);
	}

	public DefaultLoadingRetryListener setRetryImage(int resId) {
		retryImage.setImageResource(resId);
		return this;
	}

	public DefaultLoadingRetryListener setRetryText(int resId) {
		retryText.setText(resId);
		return this;
	}

	public DefaultLoadingRetryListener setRetryText(CharSequence text) {
		retryText.setText(text);
		return this;
	}

	public DefaultLoadingRetryListener setRetryBtn(CharSequence btnText) {
		retryBtn.setText(btnText);
		return this;
	}

	public DefaultLoadingRetryListener setRetryBtn(View.OnClickListener listener) {
		retryBtn.setOnClickListener(listener);
		return this;
	}

	public DefaultLoadingRetryListener setEmptyImage(@DrawableRes int res) {
		emptyImage.setImageResource(res);
		return this;
	}

	public DefaultLoadingRetryListener setEmptyText(@StringRes int res) {
		emptyText.setText(res);
		return this;
	}

	public DefaultLoadingRetryListener setEmptyText(CharSequence text) {
		emptyText.setText(text);
		return this;
	}

	@Override
	public void setLoadingEvent(View loadingView) {

	}

	@Override
	public void setEmptyEvent(View emptyView) {
		emptyImage = (ImageView) emptyView.findViewById(R.id.img_empty);
		emptyText = (TextView) emptyView.findViewById(R.id.tv_empty);
//		emptyView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mvpView.onRetry();
//			}
//		});
	}

	@Override
	public void setNoNetEvent(View noNetView) {
		retryBtn = (Button) noNetView.findViewById(R.id.retry_btn);
		retryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mvpView.onRetry();
			}
		});
	}

	@Override
	public int generateLoadingLayoutId() {
		return R.layout.base_loading;
	}

	@Override
	public int generateRetryLayoutId() {
		return R.layout.base_retry;
	}

	@Override
	public int generateEmptyLayoutId() {
		return R.layout.base_empty;
	}

	@Override
	public int generateNoNetLayoutId() {
		return R.layout.base_nonet;
	}
}
