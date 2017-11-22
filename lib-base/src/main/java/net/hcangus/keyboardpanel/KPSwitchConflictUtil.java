/*
 * Copyright (C) 2015-2017 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hcangus.keyboardpanel;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jacksgong on 3/30/16.
 * <p/>
 * This util will help you control your panel and keyboard easily and exactly with
 * non-layout-conflict.
 * <p/>
 * This util just support the application layer encapsulation, more detail for how to resolve
 * the layout-conflict please Ref
 * {@link KPSwitchPanelLayoutHandler}、
 * <p/>
 * Any problems: https://github.com/Jacksgong/JKeyboardPanelSwitch
 */
public class KPSwitchConflictUtil {
	private static List<SubFocus> subFocusList = new ArrayList<>();
	/**
	 * Attach the action of {@code switchPanelKeyboardBtn} and the {@code focusView} to
	 * non-layout-conflict.
	 * <p/>
	 * You do not have to use this method to attach non-layout-conflict, in other words, you can
	 * attach the action by yourself with invoke methods manually: {@link #showPanel(View)}、
	 * {@link #showKeyboard(View, View)}、{@link #hidePanelAndKeyboard(View)}, and in the case of don't
	 * invoke this method to attach, and if your activity with the fullscreen-theme, please ensure your
	 * panel layout is {@link View#INVISIBLE} before the keyboard is going to show.
	 *
	 * @param panelLayout            the layout of panel.
	 * @param switchPanelKeyboardBtn the view will be used to trigger switching between the panel and
	 *                               the keyboard.
	 * @param focusView              the view will be focused or lose the focus.
	 * @param switchClickListener    the click listener is used to listening the click event for
	 *                               {@code switchPanelKeyboardBtn}.
	 * @see #attach(View, SwitchClickListener, SubPanelAndTrigger...)
	 */
	public static void attach(@NonNull final View panelLayout,
							  @Nullable View switchPanelKeyboardBtn,
							  @Nullable final EditText focusView,
							  @Nullable final SwitchClickListener switchClickListener) {
		final Activity activity = (Activity) panelLayout.getContext();

		if (switchPanelKeyboardBtn != null) {
			switchPanelKeyboardBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final boolean switchToPanel = switchPanelAndKeyboard(panelLayout, focusView);
					if (switchClickListener != null) {
						switchClickListener.onClickSwitch(switchToPanel);
					}
				}
			});
		}

		if (focusView != null && isHandleByPlaceholder(activity)) {
			focusView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					panelLayout.setVisibility(View.INVISIBLE);
				}
			});
		}
	}

	/**
	 * If you have multiple sub-panels in the {@code panelLayout},
	 * you can use this method to simply attach them to non-layout-conflict.
	 * otherwise you can use {@link #attach(View, View, EditText, SwitchClickListener)}.
	 *
	 * @param panelLayout         the layout of panel.
	 * @param switchClickListener the listener is used to listening whether the panel is showing or
	 *                            keyboard is showing with toggle the panel/keyboard state.
	 * @param subPanelAndTriggers the array of the trigger-toggle-view and
	 *                            the sub-panel which bound trigger-toggle-view.
	 */
	public static void attach(@NonNull View panelLayout,
							  @Nullable SwitchClickListener switchClickListener,
							  SubPanelAndTrigger... subPanelAndTriggers) {
		for (SubPanelAndTrigger subPanelAndTrigger : subPanelAndTriggers) {

			bindSubPanel(subPanelAndTrigger, subPanelAndTriggers, panelLayout, switchClickListener);
		}
	}

	public static void attach(@NonNull final View panelLayout,
							  @NonNull View switchPanelKeyboardBtn,
							  final SubFocus... subFocuses) {
		if (subFocuses != null) {
			subFocusList.addAll(Arrays.asList(subFocuses));
		}
		final Activity activity = (Activity) panelLayout.getContext();
		switchPanelKeyboardBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = (Activity) panelLayout.getContext();
				View focusView = activity.getCurrentFocus();
				for (SubFocus subFocuse : subFocusList) {
					if (subFocuse.focusView == focusView) {
						switchPanelAndKeyboard(panelLayout, focusView);
						break;
					}
				}
			}
		});
		if (subFocuses != null) {
			for (SubFocus subFocuse : subFocuses) {
				if (subFocuse.focusView != null && isHandleByPlaceholder(activity)) {
					subFocuse.focusView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							panelLayout.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}
	}

	public static void addSubFocus(@NonNull final View panelLayout,
								   @NonNull final View switchPanelKeyboardBtn,
								   SubFocus subFocus) {
		final Activity activity = (Activity) subFocus.focusView.getContext();
		subFocusList.add(subFocus);
		if (isHandleByPlaceholder(activity)) {
			subFocus.focusView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						switchPanelKeyboardBtn.setEnabled(true);
						panelLayout.setVisibility(View.INVISIBLE);
					}
					return false;
				}
			});
		}
	}

	public static void clear() {
		subFocusList.clear();
	}

	/**
	 * @see #attach(View, SwitchClickListener, SubPanelAndTrigger...)
	 */
	public static class SubPanelAndTrigger {
		/**
		 * The sub-panel view is the child of panel-layout.
		 */
		final View subPanelView;
		/**
		 * The trigger view is used for triggering the {@code subPanelView} VISIBLE state.
		 */
		final View triggerView;
		/**
		 * The edit will be focused or lose the focus.
		 */
		EditText focusView;
		/**
		 * Whether the eidt request focus when the {@link #subPanelView} show
		 */
		boolean isRequestFocus;

		public SubPanelAndTrigger(View subPanelView, View triggerView) {
			this.subPanelView = subPanelView;
			this.triggerView = triggerView;
		}

		public SubPanelAndTrigger(View subPanelView, View triggerView,
								  EditText focusView, boolean isRequestFocus) {
			this.subPanelView = subPanelView;
			this.triggerView = triggerView;
			this.focusView = focusView;
			this.isRequestFocus = isRequestFocus;
		}
	}

	public static class SubFocus {
		final EditText focusView;

		public SubFocus(EditText focusView) {
			this.focusView = focusView;
		}
	}


	/**
	 * To show the panel(hide the keyboard automatically if the keyboard is showing) with
	 * non-layout-conflict.
	 *
	 * @param panelLayout the layout of panel.
	 * @see KPSwitchPanelLayoutHandler
	 */
	public static void showPanel(final View panelLayout) {
		final Activity activity = (Activity) panelLayout.getContext();
        panelLayout.setVisibility(View.VISIBLE);
		if (activity.getCurrentFocus() != null) {
			KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
		}
	}

	/**
	 * To show the keyboard(hide the panel automatically if the panel is showing) with
	 * non-layout-conflict.
	 *
	 * @param panelLayout the layout of panel.
	 * @param focusView   the view will be focused.
	 */
	public static void showKeyboard(final View panelLayout, final View focusView) {
		final Activity activity = (Activity) panelLayout.getContext();

		KeyboardUtil.showKeyboard(focusView);
		if (isHandleByPlaceholder(activity)) {
			panelLayout.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * If the keyboard is showing, then going to show the {@code panelLayout},
	 * and hide the keyboard with non-layout-conflict.
	 * <p/>
	 * If the panel is showing, then going to show the keyboard,
	 * and hide the {@code panelLayout} with non-layout-conflict.
	 * <p/>
	 * If the panel and the keyboard are both hiding. then going to show the {@code panelLayout}
	 * with non-layout-conflict.
	 *
	 * @param panelLayout the layout of panel.
	 * @param focusView   the view will be focused or lose the focus.
	 * @return If true, switch to showing {@code panelLayout}; If false, hidePanelAndKeyboard.
	 */
	private static boolean switchPanelAndKeyboard(final View panelLayout, final View focusView) {
		boolean switchToPanel = panelLayout.getVisibility() != View.VISIBLE;
		if (!switchToPanel) {
			showKeyboard(panelLayout, focusView);
//			hidePanelAndKeyboard(panelLayout);
		} else {
			showPanel(panelLayout);
		}
		if (focusView != null) {
			if (switchToPanel) {
				focusView.requestFocus();
			}
		}
		return switchToPanel;
	}

	/**
	 * Hide the panel and the keyboard.
	 *
	 * @param panelLayout the layout of panel.
	 */
	public static void hidePanelAndKeyboard(View panelLayout) {
		Activity activity = (Activity) panelLayout.getContext();

		View focusView = activity.getCurrentFocus();
		if (focusView != null) {
			KeyboardUtil.hideKeyboard(focusView);
		}

		panelLayout.setVisibility(View.GONE);
	}

	/**
	 * This listener is used to listening the click event for a view which is received the click event
	 * to switch between Panel and Keyboard.
	 *
	 * @see #attach(View, View, EditText, SwitchClickListener)
	 */
	public interface SwitchClickListener {
		/**
		 * @param switchToPanel If true, switch to showing Panel; If false, switch to showing Keyboard.
		 */
		void onClickSwitch(boolean switchToPanel);
	}

	/**
	 * @param isFullScreen        Whether in fullscreen theme.
	 * @param isTranslucentStatus Whether in translucent status theme.
	 * @param isFitsSystem        Whether the root view(the child of the content view) is in
	 *                            {@code getFitSystemWindow()} equal true.
	 * @return Whether handle the conflict by show panel placeholder, otherwise, handle by delay the
	 * visible or gone of panel.
	 */
	static boolean isHandleByPlaceholder(boolean isFullScreen, boolean isTranslucentStatus,
										 boolean isFitsSystem) {
		return isFullScreen || (isTranslucentStatus && !isFitsSystem);
	}

	static boolean isHandleByPlaceholder(final Activity activity) {
		return isHandleByPlaceholder(ViewUtil.isFullScreen(activity),
				ViewUtil.isTranslucentStatus(activity), ViewUtil.isFitsSystemWindows(activity));
	}

	private static void bindSubPanel(final SubPanelAndTrigger subPanelAndTrigger,
									 final SubPanelAndTrigger[] subPanelAndTriggers,
									 final View panelLayout,
									 @Nullable final SwitchClickListener switchClickListener) {
		final Activity activity = (Activity) panelLayout.getContext();
		final View triggerView = subPanelAndTrigger.triggerView;
		final View boundTriggerSubPanelView = subPanelAndTrigger.subPanelView;
		View focusView = subPanelAndTrigger.focusView;

		triggerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean switchToPanel = null;
				if (panelLayout.getVisibility() == View.VISIBLE) {
					// panel is visible.
					if (boundTriggerSubPanelView.getVisibility() == View.VISIBLE) {
						//KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
						hidePanelAndKeyboard(panelLayout);
						triggerView.setSelected(false);
						switchToPanel = false;
					} else {
						// bound-trigger panel is invisible.
						// to show bound-trigger panel.
						showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggers);
					}
				} else {
					// panel is gone,to show panel.
					showPanel(panelLayout);
					switchToPanel = true;

					// to show bound-trigger panel.
					showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggers);
				}

				if (switchClickListener != null && switchToPanel != null) {
					switchClickListener.onClickSwitch(switchToPanel);
				}
			}
		});

//		if (focusView != null && isHandleByPlaceholder(activity)) {
//			focusView.setOnTouchListener(new View.OnTouchListener() {
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					if (event.getAction() == MotionEvent.ACTION_UP) {
//						panelLayout.setVisibility(View.INVISIBLE);
//					}
//					return false;
//				}
//			});
//		}
	}

	private static void showBoundTriggerSubPanel(final View boundTriggerSubPanelView,
												 final SubPanelAndTrigger[] subPanelAndTriggers) {
		// to show bound-trigger panel.
		for (SubPanelAndTrigger panelAndTrigger : subPanelAndTriggers) {
			if (panelAndTrigger.subPanelView != boundTriggerSubPanelView) {
				// other sub panel.
				panelAndTrigger.subPanelView.setVisibility(View.GONE);
				panelAndTrigger.triggerView.setSelected(false);
			} else {
				panelAndTrigger.subPanelView.setVisibility(View.VISIBLE);
				panelAndTrigger.triggerView.setSelected(true);
				if (panelAndTrigger.focusView != null && panelAndTrigger.isRequestFocus) {
					panelAndTrigger.focusView.requestFocus();
				}else if (panelAndTrigger.focusView != null) {
					panelAndTrigger.focusView.clearFocus();
				}
			}
		}
	}
}
