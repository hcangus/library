package net.hcangus.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import net.hcangus.base.R;

/**
 * Anydoor
 * Created by Administrator.
 */

public class SearchTitleBar extends TitleBar {

	public SearchEditText etSearch;

	public SearchTitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SearchTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		etSearch = (SearchEditText) findViewById(R.id.et_search);
		initAttr(context, attrs);

		setRightText("搜索");
		showRightView(false);
		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				showRightView(!TextUtils.isEmpty(s));
			}
		});
	}

	private void initAttr(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchTitleBar);
		ColorStateList hintColor = a.getColorStateList(R.styleable.SearchTitleBar_stb_hintColor);
		ColorStateList textColor = a.getColorStateList(R.styleable.SearchTitleBar_stb_textColor);
		int textSize = a.getDimensionPixelSize(R.styleable.SearchTitleBar_stb_textSize, -1);
		Drawable drawable = a.getDrawable(R.styleable.SearchTitleBar_stb_drawableLeft);
		CharSequence hint = a.getText(R.styleable.SearchTitleBar_stb_hint);
		Drawable background = a.getDrawable(R.styleable.SearchTitleBar_stb_background);
		boolean isAutoSearch = a.getBoolean(R.styleable.SearchTitleBar_stb_isAutoSearch, false);
		if (hint != null) {
			etSearch.setHint(hint);
		}
		if (hintColor != null) {
			etSearch.setHintTextColor(hintColor);
		}
		if (textColor != null) {
			etSearch.setTextColor(textColor);
		}
		if (textSize!=-1) {
			etSearch.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		}
		if (background != null) {
			etSearch.setBackgroundDrawable(background);
		}
		etSearch.setCompoundDrawablesWithIntrinsicBounds(
				drawable, null, null, null);
		etSearch.setAutoSearch(isAutoSearch);
		a.recycle();
	}

	@Override
	protected int getHeaderViewLayoutId() {
		return R.layout.layout_titlebar_search;
	}

	@Override
	public void setRightOnClickListener(OnClickListener l) {
		lyTitleBarRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etSearch != null && etSearch.getOnSearchClickListener() != null) {
					etSearch.getOnSearchClickListener().onSearchClick(etSearch.getText().toString().trim());
				}
			}
		});
	}

	public void setOnSearchClickListener(SearchEditText.OnSearchClickListener listener) {
		etSearch.setOnSearchClickListener(listener);
	}
}
