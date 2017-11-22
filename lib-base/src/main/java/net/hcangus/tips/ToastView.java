package net.hcangus.tips;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import net.hcangus.base.R;


/**
 * Anydoor
 * Created by Administrator.
 */

public class ToastView {

	private Context context;
	private Toast toast;
	private View view_toast;
	private TipDefaultView tipDefaultView;

	public ToastView(Context context) {
		this.context = context;
		init();
	}

	private void init() {
		view_toast = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
		view_toast.setClickable(true);
		view_toast.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		tipDefaultView = (TipDefaultView) view_toast.findViewById(R.id.tipDefaultView);
	}

	private void showToast() {
		if (toast == null) {
			toast = new Toast(context);
			toast.setGravity(Gravity.FILL, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(view_toast);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
	}

	public void showBaseStatus(int res, CharSequence string) {
		tipDefaultView.showBaseStatus(res, string);
		showToast();
	}

	public void showInfo(CharSequence string) {
		tipDefaultView.showInfo(string);
		showToast();
	}

	public void showSuccess(CharSequence string) {
		tipDefaultView.showSuccess(string);
		showToast();
	}

	public void showError(CharSequence string) {
		tipDefaultView.showError(string);
		showToast();
	}

}
