package com.prokarma.myhome.features.faq;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import timber.log.Timber;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TealiumUtil.trackEvent(Constants.BILLPAY_EVENT, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View faqView = inflater.inflate(R.layout.faq, container, false);

        getActivity().setTitle(getString(R.string.bill_pay));
        webView = (WebView) faqView.findViewById(R.id.faq_webview);
        TextView error = (TextView) faqView.findViewById(R.id.faqError);
        final ProgressBar webProgress = (ProgressBar) faqView.findViewById(R.id.webProgress);
        webView.loadUrl(FAQ_URL);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Timber.d("billpay. override url = " + url);

                if (url.toLowerCase().endsWith(".pdf")) {
                    loadPdf(view, url);
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
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Timber.d("billpay. override request url = " + request.getUrl().toString());

                if (request.getUrl().toString().toLowerCase().endsWith(".pdf")) {

                    loadPdf(view, request.getUrl().toString());
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
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Timber.d("billpay. onPageFinished = " + url);
                super.onPageFinished(view, url);
                webProgress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                webProgress.setVisibility(View.GONE);
                Timber.d("billpay. onReceivedError = " + error);
            }

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description,
                                        final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webProgress.setVisibility(View.GONE);
                Timber.d("billpay. onReceivedError = " + description);
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

    private void loadPdf(View view, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "application/pdf");

        boolean foundAppToLoadPdf = true;
        try {
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                view.getContext().startActivity(intent);
            } else {
                foundAppToLoadPdf = false;
            }

        } catch (ActivityNotFoundException e) {
            //user does not have a pdf viewer installed
            foundAppToLoadPdf = false;
        }

        if (!foundAppToLoadPdf) {
            CommonUtil.showToast(getContext(), "No Application found to view PDF files.");
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAQ;
    }
}
