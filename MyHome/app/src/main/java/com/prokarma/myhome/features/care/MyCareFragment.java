package com.prokarma.myhome.features.care;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 7/28/17.
 */

public class MyCareFragment extends BaseFragment {
    public static final String MY_CARE_TAG = "my_care_tag";
    public static final String MY_CARE_URL = "https://www.dignityhealth.org/my-home/my-care-dignity-health";

    private WebView webView;

    public static MyCareFragment newInstance() {
        return new MyCareFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View faqView = inflater.inflate(R.layout.faq, container, false);
        getActivity().setTitle(getString(R.string.my_care_title));
        webView = (WebView) faqView.findViewById(R.id.faq_webview);
        TextView error = (TextView) faqView.findViewById(R.id.faqError);
        final ProgressBar webProgress = (ProgressBar) faqView.findViewById(R.id.webProgress);
        webView.loadUrl(MY_CARE_URL);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webProgress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                webProgress.setVisibility(View.GONE);
            }
        });

        error.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        if (!ConnectionUtil.isConnected(getActivity())) {
            error.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
        return faqView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE;
    }
}
