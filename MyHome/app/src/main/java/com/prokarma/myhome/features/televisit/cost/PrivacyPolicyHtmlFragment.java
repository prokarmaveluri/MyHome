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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivacyPolicyHtmlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacyPolicyHtmlFragment extends BaseFragment {

    public static final String MY_CARE_PRIVACY_POLICY_TAG = "my_care_privacy_policy_tag";

    private WebView webview;
    private LinearLayout wholeLayout;
    private ProgressBar progressBar;

    public PrivacyPolicyHtmlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PrivacyPolicyHtmlFragment.
     */
    public static PrivacyPolicyHtmlFragment newInstance() {
        return new PrivacyPolicyHtmlFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PRIVACY_POLICY_SCREEN, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.my_care_privacy_policy));

        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        wholeLayout = (LinearLayout) view.findViewById(R.id.whole_layout);
        webview = (WebView) view.findViewById(R.id.webview);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        setHasOptionsMenu(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getVisitContext();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PRIVACY_POLICY;
    }

    private void loadWebview(String content) {

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("tel:")) {
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
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.getSettings().setBuiltInZoomControls(true);
                view.getSettings().setSupportZoom(true);
                progressBar.setVisibility(View.GONE);
            }
        });

        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(true);
        webview.setInitialScale(100);
        webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        webview.getSettings().setMinimumFontSize(30);

        webview.loadData(content, "text/html", "UTF-8");
    }


    private void getVisitContext() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        if (AwsManager.getInstance().getVisit() == null) {
            return;
        }

        try {
            webview.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            AwsManager.getInstance().getAWSDK().getVisitManager().getVisitContext(
                    AwsManager.getInstance().getPatient(),
                    AwsManager.getInstance().getVisit().getAssignedProvider(),
                    new SDKCallback<VisitContext, SDKError>() {
                        @Override
                        public void onResponse(VisitContext visitContext, SDKError sdkError) {
                            if (sdkError == null && isAdded()) {

                                String legalTextData = "";
                                if (visitContext.getLegalTexts() != null) {
                                    for (LegalText legalText : visitContext.getLegalTexts()) {
                                        if (legalText.isRequired()) {
                                            legalTextData = legalText.getLegalText();
                                            break;
                                        }
                                    }
                                }
                                loadWebview(legalTextData);

                            } else {
                                Timber.e("PrivacyPolicy. Something failed! :/");
                                Timber.e("SDK Error: " + sdkError);
                            }

                            webview.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("PrivacyPolicy. Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            webview.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );
        } catch (IllegalArgumentException ex) {
            Timber.e(ex);
            webview.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
