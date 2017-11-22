package net.hcangus.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.hcangus.base.R;

/**
 * 标题栏
 * Created by Administrator on 2017/3/18.
 */

public class TitleBar extends RelativeLayout {
	protected RelativeLayout lyTitleBarLeft;
	protected ImageView ivTitleBarLeft;
	protected RelativeLayout lyTitleBarCenter;
	protected TextView tvTitleBarTitle;
	protected RelativeLayout lyTitleBarRight;
	protected ImageView ivTitleBarRight;
	protected TextView tvTitleBarRight;

	private String mTitle;

	public TitleBar(Context context) {
		this(context, null, 0);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(context, getHeaderViewLayoutId(), this);
		assignViews();
	}

	private void assignViews() {
		lyTitleBarLeft = (RelativeLayout) findViewById(R.id.ly_title_bar_left);
		ivTitleBarLeft = (ImageView) findViewById(R.id.iv_title_bar_left);
		lyTitleBarCenter = (RelativeLayout) findViewById(R.id.ly_title_bar_center);
		tvTitleBarTitle = (TextView) findViewById(R.id.tv_title_bar_title);
		lyTitleBarRight = (RelativeLayout) findViewById(R.id.ly_title_bar_right);
		ivTitleBarRight = (ImageView) findViewById(R.id.iv_title_bar_right);
		tvTitleBarRight = (TextView) findViewById(R.id.tv_title_bar_right);
	}

	protected int getHeaderViewLayoutId() {
		return R.layout.layout_titlebar;
	}

	public ImageView getLeftImageView() {
		return ivTitleBarLeft;
	}

	public TextView getTitleTextView() {
		return tvTitleBarTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
		if (tvTitleBarTitle != null) {
			tvTitleBarTitle.setText(title);
		}
	}

	public void setTitle(int resId) {
		setTitle(getContext().getString(resId));
	}

	public String getTitle() {
		return mTitle;
	}

	private LayoutParams makeLayoutParams(View view) {
		ViewGroup.LayoutParams lpOld = view.getLayoutParams();
		LayoutParams lp;
		if (lpOld == null) {
			lp = new LayoutParams(-2, -1);
		} else {
			lp = new LayoutParams(lpOld.width, lpOld.height);
		}
		return lp;
	}

	/**
	 * set customized view to left side
	 *
	 * @param view the view to be added to left side
	 */
	public void setCustomizedLeftView(View view) {
		ivTitleBarLeft.setVisibility(GONE);
		LayoutParams lp = makeLayoutParams(view);
		lp.addRule(CENTER_VERTICAL);
		lp.addRule(ALIGN_PARENT_LEFT);
		getLeftViewContainer().addView(view, lp);
	}

	/**
	 * set customized view to left side
	 *
	 * @param layoutId the xml layout file user_id
	 */
	public void setCustomizedLeftView(int layoutId) {
		View view = inflate(getContext(), layoutId, null);
		setCustomizedLeftView(view);
	}

	/**
	 * wether show left side
	 * @param isShow true for VISIBLE, false for GONE
	 */
	public void showLeftView(boolean isShow) {
		lyTitleBarLeft.setVisibility(isShow ? VISIBLE : GONE);
	}

	/**
	 * set left ImageView
	 * @param resId the drawable user_id
	 */
	public void setLeftImage(int resId) {
		ivTitleBarLeft.setImageResource(resId);
	}

	/**
	 * set customized view to center
	 *
	 * @param view the view to be added to center
	 */
	public void setCustomizedCenterView(View view) {
		tvTitleBarTitle.setVisibility(GONE);
		LayoutParams lp = makeLayoutParams(view);
		lp.addRule(CENTER_IN_PARENT);
		getCenterViewContainer().addView(view, lp);
	}

	/**
	 * set customized view to center
	 *
	 * @param layoutId the xml layout file user_id
	 */
	public void setCustomizedCenterView(int layoutId) {
		View view = inflate(getContext(), layoutId, null);
		setCustomizedCenterView(view);
	}

	/**
	 * set customized view to right side
	 *
	 * @param view the view to be added to right side
	 */
	public void setCustomizedRightView(View view) {
		ivTitleBarRight.setVisibility(GONE);
		tvTitleBarRight.setVisibility(GONE);
		LayoutParams lp = makeLayoutParams(view);
		lp.addRule(CENTER_VERTICAL);
		lp.addRule(ALIGN_PARENT_RIGHT);
		getRightViewContainer().addView(view, lp);
		lyTitleBarRight.setVisibility(VISIBLE);
	}

	/**
	 * et customized view to right side
	 *
	 * @param layoutId the xml layout file user_id
	 */
	public void setCustomizedRightView(int layoutId) {
		View view = inflate(getContext(), layoutId, null);
		setCustomizedRightView(view);
	}

	public void showRightView(boolean isShow) {
		lyTitleBarRight.setVisibility(isShow ? VISIBLE : GONE);
	}

	public void setRightImage(@DrawableRes int resId) {
		ivTitleBarRight.setImageResource(resId);
		ivTitleBarRight.setVisibility(VISIBLE);
		tvTitleBarRight.setVisibility(GONE);
		lyTitleBarRight.setVisibility(VISIBLE);
	}

	public void setRightText(String text) {
		tvTitleBarRight.setText(text);
		ivTitleBarRight.setVisibility(GONE);
		tvTitleBarRight.setVisibility(VISIBLE);
		lyTitleBarRight.setVisibility(VISIBLE);
	}

	public void setRightText(@StringRes int resId) {
		setRightText(getContext().getString(resId));
	}

	public RelativeLayout getLeftViewContainer() {
		return lyTitleBarLeft;
	}

	public RelativeLayout getCenterViewContainer() {
		return lyTitleBarCenter;
	}

	public RelativeLayout getRightViewContainer() {
		return lyTitleBarRight;
	}

	public void setLeftOnClickListener(OnClickListener l) {
		lyTitleBarLeft.setOnClickListener(l);
	}

	public void setCenterOnClickListener(OnClickListener l) {
		lyTitleBarCenter.setOnClickListener(l);
	}

	public void setRightOnClickListener(OnClickListener l) {
		lyTitleBarRight.setOnClickListener(l);
	}
}
