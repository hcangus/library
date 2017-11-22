package net.hcangus.util;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;

/*
 * 倒计时功能
 */
public class TimerCountUtil extends CountDownTimer {

	private TextView bnt;

	public TimerCountUtil(long millisInFuture, long countDownInterval,
						  TextView bnt) {
		super(millisInFuture, countDownInterval);
		this.bnt = bnt;
	}

	public TimerCountUtil(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {
		bnt.setEnabled(true);
		bnt.setText("获取");
	}

	@Override
	public void onTick(long arg0) {
		bnt.setEnabled(false);
		bnt.setText(String.format(Locale.CHINA, "%d秒后重试", arg0 / 1000));
	}

}
