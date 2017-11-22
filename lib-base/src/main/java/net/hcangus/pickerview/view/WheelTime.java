package net.hcangus.pickerview.view;

import android.content.Context;
import android.view.View;

import net.hcangus.base.R;
import net.hcangus.pickerview.TimePickerView.Type;
import net.hcangus.pickerview.adapter.NumericWheelAdapter;
import net.hcangus.pickerview.lib.WheelView;
import net.hcangus.pickerview.listener.OnItemSelectedListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class WheelTime {
	private View view;
	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private WheelView wv_hours;
	private WheelView wv_mins;
	private WheelView wv_seconds;
	private int gravity;

	private Type type;
	private static final int DEFAULT_START_YEAR = 1900;
	private static final int DEFAULT_END_YEAR = 2100;
	private static final int DEFAULT_START_MONTH = 1;
	private static final int DEFAULT_END_MONTH = 12;
	private static final int DEFAULT_START_DAY = 1;
	private static final int DEFAULT_END_DAY = 31;

	private int startYear = DEFAULT_START_YEAR;
	private int endYear = DEFAULT_END_YEAR;
	private int startMonth = DEFAULT_START_MONTH;
	private int endMonth = DEFAULT_END_MONTH;
	private int startDay = DEFAULT_START_DAY;
	private int endDay = DEFAULT_END_DAY; //表示31天的
	private int startHour = 0;
	private int endHour = 23;
	private int startMinute = 0;
	private int endMinute = 59;
	private int currentYear;
	private int currentMonth;//从 1 开始
	private int currentDay;//从 1 开始
	private int currentHour;
	private int currentMin;


	// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
	private int textSize = 18;
	//文字的颜色和分割线的颜色
	int textColorOut;
	int textColorCenter;
	int dividerColor;
	// 条目间距倍数
	float lineSpacingMultiplier = 1.6F;

	private WheelView.DividerType dividerType;

	public WheelTime(View view) {
		super();
		this.view = view;
		type = Type.ALL;
		setView(view);
	}

	public WheelTime(View view, Type type, int gravity, int textSize) {
		super();
		this.view = view;
		this.type = type;
		this.gravity = gravity;
		this.textSize = textSize;
		setView(view);
	}

	public void setPicker(int year, int month, int day) {
		this.setPicker(year, month, day, 0, 0, 0);
	}

	public void setPicker(int year, final int month, int day, int h, int m, int s) {
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
		String[] months_little = {"4", "6", "9", "11"};

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		final Context context = view.getContext();
		currentYear = year;
		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear));// 设置"年"的显示数据
		/*wv_year.setLabel(context.getString(R.string.pickerview_year));// 添加文字*/
		wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
		wv_year.setGravity(gravity);
		// 月
		currentMonth = month + 1;
		wv_month = (WheelView) view.findViewById(R.id.month);
		if (startYear == endYear) {//开始年等于终止年
			wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth));
			wv_month.setCurrentItem(month + 1 - startMonth);
		} else if (year == startYear) {
			wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));
			wv_month.setCurrentItem(month + 1 - startMonth);
		} else if (year == endYear) {
			wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
			wv_month.setCurrentItem(month);
		} else {
			wv_month.setAdapter(new NumericWheelAdapter(1, 12));
			wv_month.setCurrentItem(month);
		}
	 /*   wv_month.setLabel(context.getString(R.string.pickerview_month));*/

		wv_month.setGravity(gravity);
		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		currentDay = day;
		if (startYear == endYear && startMonth == endMonth) {
			if (list_big.contains(String.valueOf(month + 1))) {
				if (endDay > 31) {
					endDay = 31;
				}
				wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				if (endDay > 30) {
					endDay = 30;
				}
				wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					if (endDay > 29) {
						endDay = 29;
					}
					wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
				} else {
					if (endDay > 28) {
						endDay = 28;
					}
					wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
				}
			}
			wv_day.setCurrentItem(day - startDay);
		} else if (year == startYear && month + 1 == startMonth) {
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(startDay, 31));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(startDay, 30));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					wv_day.setAdapter(new NumericWheelAdapter(startDay, 29));
				} else {
					wv_day.setAdapter(new NumericWheelAdapter(startDay, 28));
				}
			}
			wv_day.setCurrentItem(day - startDay);
		} else if (year == endYear && month + 1 == endMonth) {
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month + 1))) {
				if (endDay > 31) {
					endDay = 31;
				}
				wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				if (endDay > 30) {
					endDay = 30;
				}
				wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					if (endDay > 29) {
						endDay = 29;
					}
					wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
				} else {
					if (endDay > 28) {
						endDay = 28;
					}
					wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
				}
			}
			wv_day.setCurrentItem(day - 1);
		} else {
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 29));
				} else {
					wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
			wv_day.setCurrentItem(day - 1);
		}
		wv_day.setGravity(gravity);

		//时
		wv_hours = (WheelView) view.findViewById(R.id.hour);
		currentHour = h;
		//分
		wv_mins = (WheelView) view.findViewById(R.id.min);
		currentMin = m;

		if (startYear == endYear && startMonth == endMonth && startDay == endDay) {
			wv_hours.setAdapter(new NumericWheelAdapter(startHour, endHour));
			wv_hours.setCurrentItem(h - startHour);
			if (startHour == endHour) {
				wv_mins.setAdapter(new NumericWheelAdapter(startMinute, endMinute));
				wv_mins.setCurrentItem(m - startMinute);
			} else if (h == startHour) {
				wv_mins.setAdapter(new NumericWheelAdapter(startMinute, 59));
				wv_mins.setCurrentItem(m - startMinute);
			} else if (h == endHour) {
				wv_mins.setAdapter(new NumericWheelAdapter(0, endMinute));
				wv_mins.setCurrentItem(m);
			} else {
				wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
				wv_mins.setCurrentItem(m);
			}
		} else if (year == startYear && month + 1 == startMonth && day == startDay) {
			wv_hours.setAdapter(new NumericWheelAdapter(startHour, 23));
			wv_hours.setCurrentItem(h - startHour);
			if (h == startHour) {
				wv_mins.setAdapter(new NumericWheelAdapter(startMinute, 59));
				wv_mins.setCurrentItem(m - startMinute);
			} else {
				wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
				wv_mins.setCurrentItem(m);
			}
		} else if (year == endYear && month + 1 == endMonth && day == endDay) {
			wv_hours.setAdapter(new NumericWheelAdapter(0, endHour));
			wv_hours.setCurrentItem(h);
			if (h == endHour) {
				wv_mins.setAdapter(new NumericWheelAdapter(0, endMinute));
			} else {
				wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
			}
			wv_mins.setCurrentItem(m);
		} else {
			wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
			wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
			wv_hours.setCurrentItem(h);
			wv_mins.setCurrentItem(m);
		}
		wv_hours.setGravity(gravity);
		//分
		wv_mins.setGravity(gravity);

		//秒
		wv_seconds = (WheelView) view.findViewById(R.id.second);
		wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
	   /* wv_seconds.setLabel(context.getString(R.string.pickerview_seconds));// 添加文字*/
		wv_seconds.setCurrentItem(s);
		wv_seconds.setGravity(gravity);

		// 添加"年"监听
		OnItemSelectedListener wheelListener_year = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				currentYear = index + startYear;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (startYear == endYear) {
					//重新设置月份
					wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth));
					if (currentMonth < startMonth) {
						wv_month.setCurrentItem(0);
						currentMonth = startMonth;
					} else if (currentMonth > endMonth) {
						wv_month.setCurrentItem(endMonth - startMonth);
						currentMonth = endMonth;
					} else {
						wv_month.setCurrentItem(currentMonth - startMonth);
					}

					if (startMonth == endMonth) {
						//重新设置日
						setReDay(startDay, endDay, list_big, list_little);
					} else if (currentMonth == startMonth) {
						//重新设置日
						setReDay(startDay, 31, list_big, list_little);
					} else {
						//重新设置日
						setReDay(1, 31, list_big, list_little);
					}
				} else if (currentYear == startYear) {//等于开始的年
					//重新设置月份
					wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));

					if (currentMonth < startMonth) {
						wv_month.setCurrentItem(0);
						currentMonth = startMonth;
					} else {
						wv_month.setCurrentItem(currentMonth - startMonth);
					}

					if (currentMonth == startMonth) {
						//重新设置日
						setReDay(startDay, 31, list_big, list_little);
					} else {
						//重新设置日
						setReDay(1, 31, list_big, list_little);
					}

				} else if (currentYear == endYear) {
					//重新设置月份
					wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
					if (currentMonth > endMonth) {
						wv_month.setCurrentItem(endMonth - 1);
						currentMonth = endMonth;
					} else {
						wv_month.setCurrentItem(currentMonth - 1);
					}

					if (currentMonth == endMonth) {
						//重新设置日
						setReDay(1, endDay, list_big, list_little);
					} else {
						//重新设置日
						setReDay(1, 31, list_big, list_little);
					}

				} else {
					//重新设置月份
					wv_month.setAdapter(new NumericWheelAdapter(1, 12));
					wv_month.setCurrentItem(currentMonth - 1);
					//重新设置日
					setReDay(1, 31, list_big, list_little);
				}

			}
		};
		// 添加"月"监听
		OnItemSelectedListener wheelListener_month = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				currentMonth = index + 1;
				if (startYear == endYear) {
					currentMonth = index + startMonth;
					if (startMonth == endMonth) {
						//重新设置日
						setReDay(startDay, endDay, list_big, list_little);
					} else if (startMonth == currentMonth) {

						//重新设置日
						setReDay(startDay, 31, list_big, list_little);
					} else if (endMonth == currentMonth) {
						setReDay(1, endDay, list_big, list_little);
					} else {
						setReDay(1, 31, list_big, list_little);
					}
				} else if (currentYear == startYear) {
					currentMonth = index + startMonth;
					if (currentMonth == startMonth) {
						//重新设置日
						setReDay(startDay, 31, list_big, list_little);
					} else {
						//重新设置日
						setReDay(1, 31, list_big, list_little);
					}

				} else if (currentYear == endYear) {
					if (currentMonth == endMonth) {
						//重新设置日
						setReDay(1, endDay, list_big, list_little);
					} else {
						setReDay(1, 31, list_big, list_little);
					}

				} else {
					//重新设置日
					setReDay(1, 31, list_big, list_little);
				}
			}
		};
		//添加“日”监听
		OnItemSelectedListener wheelListener_day = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(int index) {
				currentDay = index + 1;
				if (currentYear == startYear && currentMonth == startMonth) {
					currentDay = startDay + index;
				}
				setReHour(currentYear, currentMonth, currentDay);
				setReMinute(currentYear, currentMonth, currentDay);
			}
		};
		//添加“时”监听
		OnItemSelectedListener wheelListener_hour = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(int index) {
				currentHour = index;
				if (currentYear == startYear && currentMonth == startMonth
						&& currentDay == startDay) {
					currentHour = index + startHour;
				}
				setReMinute(currentYear, currentMonth, currentDay);
			}
		};
		//添加“分”监听
		OnItemSelectedListener wheelListener_min = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(int index) {
				currentMin = index;
				if (currentYear == startYear && currentMonth == startMonth
						&& currentDay == startDay
						&& currentHour == startHour) {
					currentMin = index + startMinute;
				}
			}
		};
		wv_year.setOnItemSelectedListener(wheelListener_year);
		wv_month.setOnItemSelectedListener(wheelListener_month);
		wv_day.setOnItemSelectedListener(wheelListener_day);
		wv_hours.setOnItemSelectedListener(wheelListener_hour);
		wv_mins.setOnItemSelectedListener(wheelListener_min);

		switch (type) {
			case ALL:
			   /* textSize = textSize * 3;*/
				break;
			case YEAR_MONTH_DAY:
               /* textSize = textSize * 4;*/
				wv_hours.setVisibility(View.GONE);
				wv_mins.setVisibility(View.GONE);
				wv_seconds.setVisibility(View.GONE);
				break;
			case HOURS_MINS:
                /*textSize = textSize * 4;*/
				wv_year.setVisibility(View.GONE);
				wv_month.setVisibility(View.GONE);
				wv_day.setVisibility(View.GONE);
				wv_seconds.setVisibility(View.GONE);
				break;
			case MONTH_DAY_HOUR_MIN:
               /* textSize = textSize * 3;*/
				wv_year.setVisibility(View.GONE);
				wv_seconds.setVisibility(View.GONE);
				break;
			case YEAR_MONTH:
               /* textSize = textSize * 4;*/
				wv_day.setVisibility(View.GONE);
				wv_hours.setVisibility(View.GONE);
				wv_mins.setVisibility(View.GONE);
				wv_seconds.setVisibility(View.GONE);
			case YEAR_MONTH_DAY_HOUR_MIN:
               /* textSize = textSize * 4;*/

				wv_seconds.setVisibility(View.GONE);
		}
		setContentTextSize();
	}


	private void setReDay(int startD, int endD,
						  List<String> list_big, List<String> list_little) {

		if (list_big
				.contains(String.valueOf(currentMonth))) {
			if (endD > 31) {
				endD = 31;
			}
			wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
		} else if (list_little.contains(String.valueOf(currentMonth))) {
			if (endD > 30) {
				endD = 30;
			}
			wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
		} else {
			if ((currentYear % 4 == 0 && currentYear % 100 != 0)
					|| currentYear % 400 == 0) {
				if (endD > 29) {
					endD = 29;
				}
				wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
			} else {
				if (endD > 28) {
					endD = 28;
				}
				wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
			}
		}
		if (currentDay < startD) {
			wv_day.setCurrentItem(0);
			currentDay = startD;
		} else if (currentDay > endD) {
			wv_day.setCurrentItem(startD - endD);
			currentDay = endD;
		} else {
			wv_day.setCurrentItem(currentDay - startD);
		}

		setReHour(currentYear, currentMonth, currentDay);
		setReMinute(currentYear, currentMonth, currentDay);
	}

	public void setReHour(int year, int month, int day) {
		int start;
		int end;
		if (startYear == endYear && startMonth == endMonth && startDay == endDay) {
			start = startHour;
			end = endHour;
		} else if (year == startYear && month == startMonth && day == startDay) {
			start = startHour;
			end = 23;
		} else if (year == endYear && month == endMonth && day == endDay) {
			start = 0;
			end = endHour;
		} else {
			start = 0;
			end = 23;
		}
		wv_hours.setAdapter(new NumericWheelAdapter(start, end));
		if (currentHour < start) {
			wv_hours.setCurrentItem(0);
			currentHour = start;
		} else if (currentHour > end) {
			wv_hours.setCurrentItem(end - start);
			currentHour = end;
		} else {
			wv_hours.setCurrentItem(currentHour - start);
		}
	}

	public void setReMinute(int year, int month, int day) {
		int start;
		int end;
		if (startYear == endYear && startMonth == endMonth && startDay == endDay) {
			if (startHour == endHour) {
				start = startMinute;
				end = endMinute;
			} else if (currentHour == startHour) {
				start = startMinute;
				end = 59;
			} else if (currentHour == endHour) {
				start = 0;
				end = endMinute;
			} else {
				start = 0;
				end = 59;
			}
		} else if (year == startYear && month == startMonth && day == startDay) {
			if (currentHour == startHour) {
				start = startMinute;
				end = 59;
			} else {
				start = 0;
				end = 59;
			}
		} else if (year == endYear && month == endMonth && day == endDay) {
			if (currentHour == endHour) {
				start = 0;
				end = endMinute;
			} else {
				start = 0;
				end = 59;
			}
		} else {
			start = 0;
			end = 59;
		}
		wv_mins.setAdapter(new NumericWheelAdapter(start, end));
		if (currentMin < start) {
			wv_mins.setCurrentItem(0);
			currentMin = start;
		} else if (currentMin > end) {
			wv_mins.setCurrentItem(end - start);
			currentMin = end;
		} else {
			wv_mins.setCurrentItem(currentMin - start);
		}
	}

	private void setContentTextSize() {
		wv_day.setTextSize(textSize);
		wv_month.setTextSize(textSize);
		wv_year.setTextSize(textSize);
		wv_hours.setTextSize(textSize);
		wv_mins.setTextSize(textSize);
		wv_seconds.setTextSize(textSize);
	}

	private void setTextColorOut() {
		wv_day.setTextColorOut(textColorOut);
		wv_month.setTextColorOut(textColorOut);
		wv_year.setTextColorOut(textColorOut);
		wv_hours.setTextColorOut(textColorOut);
		wv_mins.setTextColorOut(textColorOut);
		wv_seconds.setTextColorOut(textColorOut);
	}

	private void setTextColorCenter() {
		wv_day.setTextColorCenter(textColorCenter);
		wv_month.setTextColorCenter(textColorCenter);
		wv_year.setTextColorCenter(textColorCenter);
		wv_hours.setTextColorCenter(textColorCenter);
		wv_mins.setTextColorCenter(textColorCenter);
		wv_seconds.setTextColorCenter(textColorCenter);
	}

	private void setDividerColor() {
		wv_day.setDividerColor(dividerColor);
		wv_month.setDividerColor(dividerColor);
		wv_year.setDividerColor(dividerColor);
		wv_hours.setDividerColor(dividerColor);
		wv_mins.setDividerColor(dividerColor);
		wv_seconds.setDividerColor(dividerColor);
	}

	private void setDividerType() {

		wv_day.setDividerType(dividerType);
		wv_month.setDividerType(dividerType);
		wv_year.setDividerType(dividerType);
		wv_hours.setDividerType(dividerType);
		wv_mins.setDividerType(dividerType);
		wv_seconds.setDividerType(dividerType);

	}

	private void setLineSpacingMultiplier() {
		wv_day.setLineSpacingMultiplier(lineSpacingMultiplier);
		wv_month.setLineSpacingMultiplier(lineSpacingMultiplier);
		wv_year.setLineSpacingMultiplier(lineSpacingMultiplier);
		wv_hours.setLineSpacingMultiplier(lineSpacingMultiplier);
		wv_mins.setLineSpacingMultiplier(lineSpacingMultiplier);
		wv_seconds.setLineSpacingMultiplier(lineSpacingMultiplier);
	}

	public void setLabels(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
		if (label_year != null) {
			wv_year.setLabel(label_year);
		} else {
			wv_year.setLabel(view.getContext().getString(R.string.pickerview_year));
		}
		if (label_month != null) {
			wv_month.setLabel(label_month);
		} else {
			wv_month.setLabel(view.getContext().getString(R.string.pickerview_month));
		}
		if (label_day != null) {
			wv_day.setLabel(label_day);
		} else {
			wv_day.setLabel(view.getContext().getString(R.string.pickerview_day));
		}
		if (label_hours != null) {
			wv_hours.setLabel(label_hours);
		} else {
			wv_hours.setLabel(view.getContext().getString(R.string.pickerview_hours));
		}
		if (label_mins != null) {
			wv_mins.setLabel(label_mins);
		} else {
			wv_mins.setLabel(view.getContext().getString(R.string.pickerview_minutes));
		}
		if (label_seconds != null) {
			wv_seconds.setLabel(label_seconds);
		} else {
			wv_seconds.setLabel(view.getContext().getString(R.string.pickerview_seconds));
		}

	}


	/**
	 * 设置是否循环滚动
	 */
	public void setCyclic(boolean cyclic) {
		wv_year.setCyclic(cyclic);
		wv_month.setCyclic(cyclic);
		wv_day.setCyclic(cyclic);
		wv_hours.setCyclic(cyclic);
		wv_mins.setCyclic(cyclic);
		wv_seconds.setCyclic(cyclic);
	}

	public long getTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, currentYear);
		calendar.set(Calendar.MONTH, currentMonth - 1);
		calendar.set(Calendar.DAY_OF_MONTH, currentDay);
		calendar.set(Calendar.HOUR_OF_DAY, currentHour);
		calendar.set(Calendar.MINUTE, currentMin);
		calendar.set(Calendar.SECOND, wv_seconds.getCurrentItem());
//		calendar.set(Calendar.YEAR, wv_year.getCurrentItem() + startYear);
//		if (currentYear == startYear) {
//			calendar.set(Calendar.MONTH, wv_month.getCurrentItem() + startMonth-1);
//			if (wv_month.getCurrentItem() == 0) {
//				calendar.set(Calendar.DAY_OF_MONTH, wv_day.getCurrentItem() + startDay);
//				if (wv_day.getCurrentItem() == 0) {
//					calendar.set(Calendar.HOUR_OF_DAY, wv_hours.getCurrentItem() + startHour);
//					if (wv_hours.getCurrentItem() == 0) {
//						calendar.set(Calendar.MINUTE, wv_mins.getCurrentItem() + startMinute);
//					} else {
//						calendar.set(Calendar.MINUTE,wv_mins.getCurrentItem());
//					}
//				} else {
//					calendar.set(Calendar.HOUR_OF_DAY, wv_hours.getCurrentItem());
//					calendar.set(Calendar.MINUTE, wv_mins.getCurrentItem() + startMinute);
//				}
//				calendar.set(Calendar.SECOND, wv_seconds.getCurrentItem());
//			} else {
//				calendar.set(Calendar.DAY_OF_MONTH, wv_day.getCurrentItem() + 1);
//				calendar.set(Calendar.HOUR_OF_DAY, wv_hours.getCurrentItem());
//				calendar.set(Calendar.MINUTE, wv_mins.getCurrentItem());
//				calendar.set(Calendar.SECOND, wv_seconds.getCurrentItem());
//			}
//		} else {
//			calendar.set(Calendar.MONTH, wv_month.getCurrentItem());
//			calendar.set(Calendar.DAY_OF_MONTH, wv_day.getCurrentItem() + 1);
//			calendar.set(Calendar.HOUR_OF_DAY, wv_hours.getCurrentItem());
//			calendar.set(Calendar.MINUTE, wv_mins.getCurrentItem());
//			calendar.set(Calendar.SECOND, wv_seconds.getCurrentItem());
//		}
		return calendar.getTimeInMillis();
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}


	public void setRangDate(Calendar startDate, Calendar endDate) {

		if (startDate == null && endDate != null) {
			int year = endDate.get(Calendar.YEAR);
			int month = endDate.get(Calendar.MONTH) + 1;
			int day = endDate.get(Calendar.DAY_OF_MONTH);
			if (year > startYear) {
				this.endYear = year;
				this.endMonth = month;
				this.endDay = day;
			} else if (year == startYear) {
				if (month > startMonth) {
					this.endYear = year;
					this.endMonth = month;
					this.endDay = day;
				} else if (month == startMonth) {
					if (month > startDay) {
						this.endYear = year;
						this.endMonth = month;
						this.endDay = day;
					}
				}
			}
			endHour = endDate.get(Calendar.HOUR_OF_DAY);
			endMinute = endDate.get(Calendar.MINUTE);
		} else if (startDate != null && endDate == null) {
			int year = startDate.get(Calendar.YEAR);
			int month = startDate.get(Calendar.MONTH) + 1;
			int day = startDate.get(Calendar.DAY_OF_MONTH);
			if (year < endYear) {
				this.startMonth = month;
				this.startDay = day;
				this.startYear = year;
			} else if (year == endYear) {
				if (month < endMonth) {
					this.startMonth = month;
					this.startDay = day;
					this.startYear = year;
				} else if (month == endMonth) {
					if (day < endDay) {
						this.startMonth = month;
						this.startDay = day;
						this.startYear = year;
					}
				}
			}
			startHour = startDate.get(Calendar.HOUR_OF_DAY);
			startMinute = startDate.get(Calendar.MINUTE);
		} else if (startDate != null) {
			this.startYear = startDate.get(Calendar.YEAR);
			this.endYear = endDate.get(Calendar.YEAR);
			this.startMonth = startDate.get(Calendar.MONTH) + 1;
			this.endMonth = endDate.get(Calendar.MONTH) + 1;
			this.startDay = startDate.get(Calendar.DAY_OF_MONTH);
			this.endDay = endDate.get(Calendar.DAY_OF_MONTH);
			startHour = startDate.get(Calendar.HOUR_OF_DAY);
			startMinute = startDate.get(Calendar.MINUTE);
			endHour = endDate.get(Calendar.HOUR_OF_DAY);
			endMinute = endDate.get(Calendar.MINUTE);
		}
	}


	/**
	 * 设置间距倍数,但是只能在1.0-2.0f之间
	 */
	public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
		this.lineSpacingMultiplier = lineSpacingMultiplier;
		setLineSpacingMultiplier();
	}

	/**
	 * 设置分割线的颜色
	 */
	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		setDividerColor();
	}

	/**
	 * 设置分割线的类型
	 */
	public void setDividerType(WheelView.DividerType dividerType) {
		this.dividerType = dividerType;
		setDividerType();
	}

	/**
	 * 设置分割线之间的文字的颜色
	 */
	public void setTextColorCenter(int textColorCenter) {
		this.textColorCenter = textColorCenter;
		setTextColorCenter();
	}

	/**
	 * 设置分割线以外文字的颜色
	 */
	public void setTextColorOut(int textColorOut) {
		this.textColorOut = textColorOut;
		setTextColorOut();
	}
}
