package com.prokarma.myhome.features.televisit.cost;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivacyPolicyPdfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacyPolicyPdfFragment extends BaseFragment {

    public static final String MY_CARE_PRIVACY_POLICY_TAG = "my_care_privacy_policy_tag";

    private WebView webView;
    private ProgressBar webProgress;
    private TextView error;

    public static PrivacyPolicyPdfFragment newInstance() {
        return new PrivacyPolicyPdfFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PRIVACY_POLICY_SCREEN, null);

        loadPdf();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View faqView = null;
        try {
            faqView = inflater.inflate(R.layout.faq, container, false);

            getActivity().setTitle(getString(R.string.my_care_privacy_policy));

            webView = (WebView) faqView.findViewById(R.id.faq_webview);
            error = (TextView) faqView.findViewById(R.id.faqError);
            webProgress = (ProgressBar) faqView.findViewById(R.id.webProgress);

        } catch (Exception ex) {
            Timber.e(ex);
            ex.printStackTrace();
        }
        return faqView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PRIVACY_POLICY;
    }

    private void loadPdf() {
        webView.loadUrl("https://docs.google.com/viewer?url=" + Constants.MY_CARE_PRIVACY_POLICY_URL);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.toLowerCase().endsWith(".pdf")) {
                    CommonUtil.loadPdf(getContext(), view, url);
                    return true;
                } else if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;

                } else if (url.toLowerCase().startsWith("http:") || url.toLowerCase().startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                if (request.getUrl().toString().toLowerCase().endsWith(".pdf")) {

                    CommonUtil.loadPdf(getContext(), view, request.getUrl().toString());
                    return true;

                } else if (request.getUrl().toString().startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(request.getUrl().toString()));
                    startActivity(intent);
                    return true;

                } else if (request.getUrl().toString().toLowerCase().startsWith("http:") || request.getUrl().toString().toLowerCase().startsWith("https:")) {
                    view.loadUrl(request.getUrl().toString());
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webProgress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                webProgress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description,
                                        final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webProgress.setVisibility(View.GONE);
            }
        });

        error.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        if (!ConnectionUtil.isConnected(getActivity())) {
            error.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
    }
}