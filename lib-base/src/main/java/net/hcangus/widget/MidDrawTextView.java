package net.hcangus.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Anydoor
 * Created by Administrator.
 */

public class MidDrawTextView extends android.support.v7.widget.AppCompatTextView {

	public MidDrawTextView(Context context) {
		super(context);
	}

	public MidDrawTextView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public MidDrawTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		Drawable drawableLeft = drawables[0];
		if (drawableLeft != null) {
			float textWidth = 0;
			CharSequence text = getText();
			CharSequence hint = getHint();
			if (!TextUtils.isEmpty(text)) {
				textWidth = getPaint().measureText(text.toString());
			} else if (!TextUtils.isEmpty(hint)){
				textWidth = getPaint().measureText(hint.toString());
			}
			int drawablePadding = getCompoundDrawablePadding();
			int drawableWidth = drawableLeft.getIntrinsicWidth();
			float bodyWidth = textWidth + drawableWidth + drawablePadding;
			canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
		}
		super.onDraw(canvas);
	}
}
