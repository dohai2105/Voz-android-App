package com.fun.voznoad;

import com.fun.voznoad.R;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewActivity extends FragmentActivity {
	private SharedPreferences sharedPreferences;

	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppThemeDark);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		sharedPreferences = getSharedPreferences("voz_data",MODE_PRIVATE);
		int fontSizePlus = Integer.parseInt(sharedPreferences.getString("fontsize", "0"));
		
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		String url = getIntent().getStringExtra("url");
		WebView webview = (WebView) findViewById(R.id.vozWebView);
		webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.getSettings().setDefaultFontSize(17+fontSizePlus);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				ProgressBar progressBar = (ProgressBar) findViewById(R.id.webviewProgress);
				progressBar.setVisibility(View.GONE);
			}
		});
		webview.loadUrl(url);
	}
}
