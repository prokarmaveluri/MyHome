package com.prokarma.myhome.features.faq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 7/28/17.
 */

public class FaqFragment extends BaseFragment {
    public static final String FAQ_TAG = "faq_tag";
    //    public static final String FAQ_URL = "https://www.dignityhealth.org/my-home/billing-help-and-faq";
    public static final String FAQ_URL = "https://dignityhealth.org/billpay/";

    private WebView webView;

    public static FaqFragment newInstance() {
        return new FaqFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View faqView = inflater.inflate(R.layout.faq, container, false);
        getActivity().setTitle(getString(R.string.bill_pay));
        webView = (WebView) faqView.findViewById(R.id.faq_webview);
        TextView error = (TextView) faqView.findViewById(R.id.faqError);
        webView.loadUrl(FAQ_URL);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
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
        return Constants.ActivityTag.FAQ;
    }
}
