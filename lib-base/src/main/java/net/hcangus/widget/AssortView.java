package net.hcangus.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import net.hcangus.base.R;


/**
 * 字母滑动索引
 */
public class AssortView extends android.support.v7.widget.AppCompatButton {

	private Context context;
	private TextView header;
	private ListView mListView;
	private int interval;
	private SectionIndexer sectionIndex = null;

	// 分类
	private String[] assort;
	private Paint paint = new Paint();
	// 选择的索引
	private int selectIndex = -1;

	public AssortView(Context context) {
		super(context);
		this.context = context;
	}

	public AssortView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public AssortView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	private void init() {
		String st = context.getString(R.string.search_new);
		assort = new String[]{st, "A", "B", "C", "D", "E", "F", "G",
				"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z","#"};
	}

	public void setListView(ListView listView) {
		mListView = listView;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight();
		int width = getWidth();
		interval = height / assort.length;
		float density = getContext().getResources().getDisplayMetrics().density;

		for (int i = 0, length = assort.length; i < length; i++) {
			// 抗锯齿
			paint.setAntiAlias(true);
			// 默认粗体
			paint.setTypeface(Typeface.DEFAULT);
			// 白色字体
//			paint.setColor(getResources().getColor(R.color.c_ft4));
			paint.setColor(getCurrentTextColor());
			int TEXT_SIZE_NORMAL = 10;
			paint.setTextSize(TEXT_SIZE_NORMAL * density);
			if (i == selectIndex) {
				// 被选择的字母改变颜色和粗体
				paint.setColor(Color.parseColor("#646464"));//#3399ff
				paint.setFakeBoldText(true);
				int TEXT_SIZE_SELECT = 13;
				paint.setTextSize(TEXT_SIZE_SELECT * density);
			}

			// 计算字母的X坐标
			float xPos = width / 2 - paint.measureText(assort[i]) / 2;
			// 计算字母的Y坐标
			float yPos = interval * i + interval;
			canvas.drawText(assort[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int index = sectionForPoint(event.getY());
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (header == null) {
					header = (TextView) ((View) getParent()).findViewById(R.id.floating_header);
				}
				selectIndex = index;
				setHeaderTextAndscroll(event);
				header.setVisibility(View.VISIBLE);
				break;
			case MotionEvent.ACTION_MOVE:
				// 如果滑动改变
				selectIndex = index;
				setHeaderTextAndscroll(event);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				selectIndex = -1;
				header.setVisibility(View.INVISIBLE);
			default:
				break;
		}
		invalidate();

		return true;
	}

	private int sectionForPoint(float y) {
		int index = (int) (y / interval);
		if (index < 0) {
			index = 0;
		}
		if (index > assort.length - 1) {
			index = assort.length - 1;
		}
		return index;
	}

	private void setHeaderTextAndscroll(MotionEvent event) {
		if (mListView == null) {
			//check the mListView to avoid NPE. but the mListView shouldn't be null
			//need to check the call stack later
			return;
		}
		String headerString = assort[sectionForPoint(event.getY())];
		header.setText(headerString);
		ListAdapter adapter = mListView.getAdapter();
		if (sectionIndex == null) {
			if (adapter instanceof HeaderViewListAdapter) {
				sectionIndex = (SectionIndexer) ((HeaderViewListAdapter) adapter).getWrappedAdapter();
			} else if (adapter instanceof SectionIndexer) {
				sectionIndex = (SectionIndexer) adapter;
			} else {
				throw new RuntimeException("listview sets adapter does not implement SectionIndexer interface");
			}
		}
		String[] adapterSections = (String[]) sectionIndex.getSections();
		try {
			for (int i = adapterSections.length - 1; i > -1; i--) {
				if (adapterSections[i].equals(headerString)) {
					mListView.setSelection(sectionIndex.getPositionForSection(i));
					break;
				}
			}
		} catch (Exception e) {
			Log.e("setHeaderTextAndScroll", e.getMessage());
		}

	}
}
