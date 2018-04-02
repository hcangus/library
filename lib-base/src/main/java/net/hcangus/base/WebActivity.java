package net.hcangus.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import net.hcangus.mvp.present.BasePresent;
import net.hcangus.ptr.PtrFrameLayout;
import net.hcangus.ptr.PtrHandler;
import net.hcangus.ptr.header.MaterialHeader;
import net.hcangus.util.DeviceUtil;

/**
 * Anydoor
 * Created by hcangus
 */

public class WebActivity extends BaseActivity {

	ProgressBar pbLoading;
	WebView webView;
	PtrFrameLayout ptrFrame;

	private String title;
	private String url;

	public static void startMe(Context context, String title, String url) {
		Intent intent = new Intent(context, WebActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		context.startActivity(intent);
	}

	@Override
	protected void onLeftClick(View leftContainer) {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onLeftClick(leftContainer);
		}
	}

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
	}

	@Override
	protected int getContentViewId() {
		return R.layout.activity_web;
	}

	@Override
	protected int getFragmentContentId() {
		return 0;
	}

	@Override
	protected void setupViews() {
		mTitleBar.setTitle(title);
		pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
		webView = (WebView) findViewById(R.id.webView);
		ptrFrame = (PtrFrameLayout) findViewById(R.id.ptrFrame);
		setWebView();

//		StoreHouseHeader header = new StoreHouseHeader(this);
//		header.setPadding(0, DeviceUtil.dp2px(this, 16), 0, 0);
//		header.initWithString(getString(R.string.app_name));
//		ptrFrame.setHeaderView(header);
//		ptrFrame.addPtrUIHandler(header);

		MaterialHeader materialHeader = new MaterialHeader(this);
		int[] colors = new int[]{ContextCompat.getColor(this, android.R.color.holo_red_light),
				ContextCompat.getColor(this, android.R.color.holo_blue_light),
				ContextCompat.getColor(this, android.R.color.holo_green_light),
				ContextCompat.getColor(this, android.R.color.holo_orange_light)};
		materialHeader.setColorSchemeColors(colors);
		materialHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		materialHeader.setPadding(0, DeviceUtil.dp2px(this, 14), 0, DeviceUtil.dp2px(this, 10));
		materialHeader.setPtrFrameLayout(ptrFrame);
		ptrFrame.setLoadingMinTime(1000);
		ptrFrame.setDurationToCloseHeader(1500);
		ptrFrame.setHeaderView(materialHeader);
		ptrFrame.addPtrUIHandler(materialHeader);
		ptrFrame.setPinContent(true);

		ptrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return !ViewCompat.canScrollVertically(webView, -1);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				webView.reload();
			}
		});
	}

	@Override
	protected BasePresent createPresent(Context context) {
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置webView
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView() {
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setLoadsImagesAutomatically(true);
		//打开页面时，自适应屏幕
		webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true);

		webView.setWebViewClient(new MWebViewClient());
		webView.setWebChromeClient(new MWebChromeView());
		webView.loadUrl(url);
	}

	/**
	 * 处理各种通知、请求事件
	 */
	private class MWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (pbLoading.isShown())
				pbLoading.setVisibility(View.GONE);
			ptrFrame.refreshComplete();
		}

	}

	/**
	 * 进度条及动画
	 */
	class MWebChromeView extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {// 加载完成
				if (pbLoading.isShown()) {
					pbLoading.setVisibility(View.GONE);
				}
				ptrFrame.refreshComplete();
			} else if (newProgress < 100) {// 正在加载
				if (!pbLoading.isShown() && !ptrFrame.isRefreshing()) {
					pbLoading.setVisibility(View.VISIBLE);
				}
				pbLoading.setProgress(newProgress);
			}
		}
	}
}
