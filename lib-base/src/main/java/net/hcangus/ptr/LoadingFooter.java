package net.hcangus.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import net.hcangus.base.R;

/**
 * Created by cundong on 2015/10/9.
 * <p/>
 * ListView/GridView/RecyclerView 分页加载时使用到的FooterView
 */
public class LoadingFooter extends RelativeLayout {

    protected State mState = State.Normal;
	private TextView tv_hint;
	private CircleProgressBar progressBar;

    public LoadingFooter(Context context) {
        super(context);
        init(context);
    }

    public LoadingFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {

        inflate(context, R.layout.loadmore, this);
		tv_hint = (TextView) findViewById(R.id.tv_hint);
		progressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        setOnClickListener(null);

        setState(State.Normal);
    }

    public State getState() {
        return mState;
    }

    /**
     * 设置状态
     */
    public void setState(State status) {
        if (mState == status) {
            return;
        }
        mState = status;

        switch (status) {

            case Normal:
                setOnClickListener(null);
                if (tv_hint != null) {
                    tv_hint.setVisibility(GONE);
                }

                if (progressBar != null) {
                    progressBar.setVisibility(GONE);
                }
                break;
            case Loading:
                setOnClickListener(null);
                if (tv_hint != null) {
                    tv_hint.setVisibility(GONE);
                }
                progressBar.setVisibility(VISIBLE);
                break;
            case TheEnd:
                setOnClickListener(null);
                if (progressBar != null) {
					progressBar.setVisibility(GONE);
                }

                if (tv_hint != null) {
					tv_hint.setText("--- 没有更多内容 ---");
					tv_hint.setVisibility(VISIBLE);
                }
                break;
            case Error:

                if (progressBar != null) {
					progressBar.setVisibility(GONE);
                }

                if (tv_hint != null) {
					tv_hint.setText("点击重试");
					tv_hint.setVisibility(VISIBLE);
                }
                break;
            default:

                break;
        }
    }

    public enum State {
		/**正常*/
        Normal,
		/**加载到最底了*/
		TheEnd,
		/**加载中..*/
		Loading,
		/**网络异常*/
		Error
    }
}