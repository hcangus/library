package net.hcangus.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import net.hcangus.base.R;
import net.hcangus.itf.Action;
import net.hcangus.itf.ActionCallBack;
import net.hcangus.tips.TipDefaultView;

public class DialogUtil {

	private final Builder builder;

	private DialogUtil(Builder builder) {
		this.builder = builder;
	}

	public Dialog show() {
		Dialog dialog;
		if (builder.mView != null) {
			dialog = initDialog(builder.mView);
			if (builder.dismissIds != null) {
				for (int id : builder.dismissIds) {
					setDialogDismiss(id, builder.mView, dialog);
				}
			}
			if (builder.clickIds != null && builder.mClickListener != null) {
				for (int clickId : builder.clickIds) {
					View view = builder.mView.findViewById(clickId);
					if (view != null) {
						view.setOnClickListener(builder.mClickListener);
					}
				}
			}
		} else {
			View view = initTipView();
			dialog = initDialog(view);
			setDialogDismiss(R.id.btn_left, view, dialog);
			setDialogDismiss(R.id.btn_right, view, dialog);
		}
		return dialog;
	}

	public AlertDialog showAlert() {
		AlertDialog alertDialog;
		if (builder.mView != null) {
			alertDialog = initAletDialog(builder.mView);
			if (builder.dismissIds != null) {
				for (int id : builder.dismissIds) {
					setDialogDismiss(id, builder.mView, alertDialog);
				}
			}
			if (builder.clickIds != null && builder.mClickListener != null) {
				for (int clickId : builder.clickIds) {
					View view = builder.mView.findViewById(clickId);
					if (view != null) {
						view.setOnClickListener(builder.mClickListener);
					}
				}
			}
		} else {
			View view = initTipView();
			alertDialog = initAletDialog(view);
			setDialogDismiss(R.id.btn_left, view, alertDialog);
			setDialogDismiss(R.id.btn_right, view, alertDialog);
		}
		return alertDialog;
	}

	public static class Builder {
		public Context mContext;
		View mView;
		int[] dismissIds;//点击关闭Dialog的id
		int[] clickIds;//点击事件的id
		@StyleRes
		int styleRes = R.style.style_dialogTranslucent;
		int gravity = Gravity.CENTER;
		@StyleRes
		int animStyle = -1;
		int viewWidth;
		int viewHeight = -2;
		View.OnClickListener mClickListener;
		public CharSequence title;
		public CharSequence tips;
		public CharSequence left;
		public CharSequence right = "确定";
		@ColorRes
		int leftColor;
		@ColorRes
		int rightColor;
		ActionCallBack<Integer> callBack;
		boolean cancelable = false;

		public Builder(Context context) {
			this.mContext = context;
			viewWidth = DeviceUtil.getScreenWidth(context) * 3 / 4;
		}

		public Builder setCustomView(View view) {
			this.mView = view;
			return this;
		}

		public Builder setClickListener(View.OnClickListener listener, int... ids) {
			mClickListener = listener;
			clickIds = ids;
			return this;
		}

		public Builder setClickDismissId(int... ids) {
			this.dismissIds = ids;
			return this;
		}

		public Builder setTitle(CharSequence title) {
			this.title = title;
			return this;
		}

		public Builder setTips(CharSequence tips) {
			this.tips = tips;
			return this;
		}

		public Builder setTips(@StringRes int res) {
			this.tips = mContext.getString(res);
			return this;
		}

		public Builder setLeft(CharSequence left) {
			this.left = left;
			return this;
		}

		public Builder setRight(CharSequence right) {
			this.right = right;
			return this;
		}

		public Builder setLeftColor(@ColorRes int leftColor) {
			this.leftColor = leftColor;
			return this;
		}

		public Builder setRightColor(@ColorRes int rightColor) {
			this.rightColor = rightColor;
			return this;
		}

		public Builder setCallBack(ActionCallBack<Integer> callBack) {
			this.callBack = callBack;
			return this;
		}

		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		public Builder setStyle(@StyleRes int res) {
			this.styleRes = res;
			return this;
		}

		public Builder setAniStyle(@StyleRes int animStyle) {
			this.animStyle = animStyle;
			return this;
		}

		public Builder setGravity(int gravity) {
			this.gravity = gravity;
			return this;
		}

		public void setViewWidth(int viewWidth) {
			this.viewWidth = viewWidth;
		}

		public void setViewHeight(int viewHeight) {
			this.viewHeight = viewHeight;
		}

		public DialogUtil build() {
			return new DialogUtil(this);
		}
	}


	/**
	 * --- 显示提示框 ---
	 * <p>无标题，确定”按钮</p>
	 */
	public static void showTips(Context context, String tips) {
		new Builder(context).setTips(tips).build().show();
	}

	/**
	 * ---  显示加载框 ---
	 */
	public static Dialog showLoading(Context context, @Nullable String tip) {
		TipDefaultView view = new TipDefaultView(context);
		view.show(tip);
		return new Builder(context).setCustomView(view).build().show();
	}

	public static Dialog showLoadingCancelable(Context context, @Nullable String tip) {
		TipDefaultView view = new TipDefaultView(context);
		view.show(tip);
		return new Builder(context).setCustomView(view).setCancelable(true).build().show();
	}

	private View initTipView() {
		View view = LayoutInflater.from(builder.mContext).inflate(R.layout.dialog_tips, null);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);
		Button btn_left = (Button) view.findViewById(R.id.btn_left);
		Button btn_right = (Button) view.findViewById(R.id.btn_right);
		View view_split = view.findViewById(R.id.view_split);
		if (TextUtils.isEmpty(builder.title)) {
			tv_title.setVisibility(View.GONE);
		} else {
			tv_title.setText(builder.title);
		}
		if (TextUtils.isEmpty(builder.tips)) {
			tv_tip.setVisibility(View.GONE);
		} else {
			tv_tip.setText(builder.tips);
		}
		if (TextUtils.isEmpty(builder.left)) {
			btn_left.setVisibility(View.GONE);
			view_split.setVisibility(View.GONE);
			btn_right.setBackgroundResource(R.drawable.selector_white_b);
		} else {
			btn_left.setText(builder.left);
			if (builder.leftColor > 0) {
				btn_left.setTextColor(ContextCompat.getColor(builder.mContext, builder.leftColor));
			}
			if (builder.callBack != null) {
				btn_left.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						builder.callBack.handleAction(Action.Dialog.OnLeftClick);
					}
				});
			}
		}
		btn_right.setText(builder.right);
		if (builder.rightColor > 0) {
			btn_right.setTextColor(ContextCompat.getColor(builder.mContext, builder.rightColor));
		}
		if (builder.callBack != null) {
			btn_right.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					builder.callBack.handleAction(Action.Dialog.OnRightClick);
				}
			});
		}
		return view;
	}

	private Dialog initDialog(View contentView) {
		Dialog dialog = new Dialog(builder.mContext, builder.styleRes);//R.style.style_dialogTranslucent
		Window window = dialog.getWindow();
		if (window != null) {
			window.setGravity(builder.gravity);
			if (builder.animStyle != -1) {
				window.setWindowAnimations(builder.animStyle);// 添加动画
			}
			//设置大小
			WindowManager.LayoutParams layoutParams = window.getAttributes();
			layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			window.setAttributes(layoutParams);
		}
		dialog.setContentView(contentView);
		dialog.setCancelable(builder.cancelable);
		dialog.setCanceledOnTouchOutside(builder.cancelable);
		dialog.show();
		return dialog;
	}


	private AlertDialog initAletDialog(View contentView) {
		AlertDialog alertDialog = new AlertDialog.Builder(this.builder.mContext, this.builder.styleRes).create();
		alertDialog.setView(contentView, 40, 0, 40, 0);
		alertDialog.setCancelable(builder.cancelable);
		alertDialog.setCanceledOnTouchOutside(builder.cancelable);
		alertDialog.show();
		Window window = alertDialog.getWindow();
		if (window != null) {
			window.setGravity(builder.gravity);
			if (builder.animStyle != -1) {
				window.setWindowAnimations(builder.animStyle);// 添加动画
			}
			//设置大小
			WindowManager.LayoutParams layoutParams = window.getAttributes();
			layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			window.setAttributes(layoutParams);
		}
		return alertDialog;
	}

	private void setDialogDismiss(int resId, View view,
								  final Dialog dialog) {
		view.findViewById(resId).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				int l = v.getLeft();
				int t = v.getTop();
				int r = v.getRight();
				int b = v.getBottom();
				boolean isIn = x >= 0 && x <= r - l && y >= 0 && y <= b - t;
				if (event.getAction() == MotionEvent.ACTION_UP && isIn) {
					dialog.dismiss();
					v.performClick();
				}
				return false;
			}
		});
	}
}
