package uk.co.healtht.healthtouch.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import uk.co.healtht.healthtouch.R;

public class WebViewFragment extends BaseFragment {

    public static final String ARG_URL = "web_url";

    private WebView webView;
    private View loadingSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        loadingSpinner = view.findViewById(R.id.loading_spinner);

        webView = (WebView) view.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                loadingSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                loadingSpinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                    startActivity(url);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        WebSettings webSettings = webView.getSettings();
        // Having zoom makes it to look as if it didn't belong to the App
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        String url = getArguments().getString(ARG_URL);
        webView.loadUrl(url);
    }

    @Override
    public boolean onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void reload() {
    }
}
