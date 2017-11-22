package net.hcangus.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import net.hcangus.base.R;

/**
 * Android
 * Created by Administrator
 */

public class SearchEditText extends android.support.v7.widget.AppCompatEditText implements
		OnFocusChangeListener, OnKeyListener, TextWatcher, TextView.OnEditorActionListener {
	private static final String TAG = "SearchEditText";
	/**
	 * 图标是否默认在左边
	 */
	private boolean isIconLeft = false;
	/**
	 * 是否点击软键盘搜索
	 */
	private boolean pressSearch = false;
	/**
	 * 软键盘搜索键监听
	 */
	private OnSearchClickListener listener;

	private int deleteResId;
	private Drawable drawableDel; // 搜索图标和删除按钮图标
	private boolean isAutoSearch;//文字改变时是否自动搜索
	private Rect rect; // 控件区域

	public void setOnSearchClickListener(OnSearchClickListener listener) {
		this.listener = listener;
	}

	public OnSearchClickListener getOnSearchClickListener() {
		return listener;
	}

	public interface OnSearchClickListener {

		void onSearchClick(String key);
	}

	public SearchEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}


	public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchEditText);
		isIconLeft = a.getBoolean(R.styleable.SearchEditText_isIconLeft, false);
		deleteResId = a.getResourceId(R.styleable.SearchEditText_drawable_delete, R.mipmap.ic_delete_text_gray);
		isAutoSearch = a.getBoolean(R.styleable.SearchEditText_isAutoSearch, false);
		a.recycle();
		init();
	}

	private void init() {
		setOnFocusChangeListener(this);
		addTextChangedListener(this);
		setOnKeyListener(this);
		setOnEditorActionListener(this);
	}

	public void setAutoSearch(boolean autoSearch) {
		isAutoSearch = autoSearch;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			if (listener != null) {
				listener.onSearchClick(getText().toString().trim());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		Drawable drawableLeft = drawables[0];
		if (isIconLeft) { // 如果是默认样式，直接绘制
			if (length() < 1) {
				drawableDel = null;
			}
			setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableDel, null);
			super.onDraw(canvas);
		} else { // 如果不是默认样式，需要将图标绘制在中间
			if (drawableLeft != null) {
				float textWidth = getPaint().measureText(getHint().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = drawableLeft.getIntrinsicWidth();
				float bodyWidth = textWidth + drawableWidth + drawablePadding;
				canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
			}
			super.onDraw(canvas);
		}
	}


	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// 被点击时，恢复默认样式
		if (!pressSearch && TextUtils.isEmpty(getText().toString())) {
			isIconLeft = isIconLeft || hasFocus;
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		pressSearch = event.getAction() == KeyEvent.ACTION_UP
				&& (keyCode == EditorInfo.IME_ACTION_SEARCH
				|| keyCode == KeyEvent.KEYCODE_SEARCH);
		if (pressSearch && listener != null) {
			listener.onSearchClick(getText().toString().trim());
			pressSearch = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction()== KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			return super.dispatchKeyEvent(event);
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 清空edit内容  删除按钮被按下时改变图标样式
		if (drawableDel != null && event.getAction() == MotionEvent.ACTION_UP) {
			int eventX = (int) event.getRawX();
			int eventY = (int) event.getRawY();
			Log.i(TAG, "eventX = " + eventX + "; eventY = " + eventY);
			if (rect == null) rect = new Rect();
			getGlobalVisibleRect(rect);
			rect.left = rect.right - drawableDel.getIntrinsicWidth() * 2;
			if (rect.contains(eventX, eventY)) {
				setText("");
				drawableDel = getResources().getDrawable(deleteResId);
			}
		} else {
			drawableDel = getResources().getDrawable(deleteResId);
		}

		return super.onTouchEvent(event);
	}


	@Override
	public void afterTextChanged(Editable arg0) {
		if (this.length() < 1) {
			drawableDel = null;
		} else {
			drawableDel = getResources().getDrawable(deleteResId);
		}
		if (isAutoSearch && listener != null) {
			listener.onSearchClick(arg0.toString().trim());
		}
	}


	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
								  int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2,
							  int arg3) {
	}
}