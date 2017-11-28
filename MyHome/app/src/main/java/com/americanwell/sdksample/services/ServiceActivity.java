/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.entity.provider.AvailableProviders;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.intake.TriageQuestionsActivity;
import com.americanwell.sdksample.widget.SwipeRefreshLayoutCheckChild;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Main activity for a specific Service
 */
@RequiresPresenter(ServicePresenter.class)
public class ServiceActivity extends BaseSampleNucleusActivity<ServicePresenter>
        implements SwipeRefreshLayout.OnRefreshListener,
        AvailableProvidersFragment.OnAvailableProvidersFragmentListener,
        ProviderAppointmentsFragment.OnProviderAppointmentsFragmentListener {

    private static final String LOG_TAG = ServicePresenter.class.getName();
    private static final String BUNDLE_PARCELABLE_EXTRAS = "parcelableExtras";
    private static final String EXTRA_SERVICE = "service";
    private static final String DIALOG_CHANGE_LANGUAGE = "changeLanguageDialog";

    @BindView(R.id.practice_info)
    TextView textView; // practice details

    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayoutCheckChild swipeRefreshLayout;

    @BindView(R.id.service_view_pager)
    ViewPager serviceViewPager;
    @BindView(R.id.service_tabs)
    TabLayout serviceTabs;

    @BindString(R.string.service_providers_available_now)
    String providersAvailableNow;
    @BindString(R.string.service_providers_appointments)
    String providersAppointments;

    private ServicePagerAdapter servicePagerAdapter;

    private MenuItem languageItem;
    private MenuItem providerItem;

    private AvailableProvidersFragment availableProvidersFragment;
    private ProviderAppointmentsFragment providerAppointmentsFragment;

    private DialogFragment changeLanguageDialog;

    // use this to make the intent to launch this activity.  provide the service here.
    public static Intent makeIntent(@NonNull final Context context,
                                    @NonNull final PracticeInfo service) {
        final Intent intent = new Intent(context, ServiceActivity.class);
        // Note: this is a sample implementation of a solution to a very specific issue
        // There are "known" issues with custom Parcelables being passed around as Intent extras
        // which manifest as benign ClassNotFoundException messages in the device logs
        // A workaround to avoid these log messages is to wrap the custom Parcelable in a Bundle
        // as shown below.  This sample application does not do this in all places, fyi.
        // reference: https://commonsware.com/blog/2016/07/22/be-careful-where-you-use-custom-parcelables.html
        final Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SERVICE, service);
        intent.putExtra(BUNDLE_PARCELABLE_EXTRAS, bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_service);

        if (savedInstanceState == null) {
            final Bundle bundle = getIntent().getBundleExtra(BUNDLE_PARCELABLE_EXTRAS);
            getPresenter().setServiceInfo((PracticeInfo) bundle.getParcelable(EXTRA_SERVICE));
        }

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(changeLanguageDialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_service, menu);
        languageItem = menu.findItem(R.id.action_filter_by_language);
        languageItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeLanguage();
                return false;
            }
        });

        List pastProviders = getPresenter().getPastProviders();
        if (pastProviders != null && !pastProviders.isEmpty()) {
            providerItem = menu.findItem(R.id.action_filter_by_provider_type);
            providerItem.setTitle(getPresenter().getShowAllProviders() ?
                    R.string.service_menu_past_providers : R.string.service_menu_all_providers);
            providerItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    getPresenter().changeProviderType();
                    invalidateOptionsMenu();
                    return false;
                }
            });
            providerItem.setVisible(true);
        }
        return true;
    }

    @Override
    public SwipeRefreshLayoutCheckChild getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private void changeLanguage() {
        changeLanguageDialog = new ProviderLanguageSpokenDialogFragment();
        changeLanguageDialog.show(getSupportFragmentManager(), DIALOG_CHANGE_LANGUAGE);
    }

    // the presenter will call this to update the practice info
    public void setPracticeInfo(final String practiceInfo) {
        textView.setText(practiceInfo);

        servicePagerAdapter = new ServicePagerAdapter(getSupportFragmentManager());
        serviceViewPager.setAdapter(servicePagerAdapter);
        serviceTabs.setupWithViewPager(serviceViewPager);

        serviceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getPresenter().setCurrentPage(position);
                if (providerItem != null) {
                    providerItem.setEnabled(position == 0 && getPresenter().isShowAvailableNow());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        serviceViewPager.setCurrentItem(getPresenter().getCurrentPage());
    }

    // tap to expand tap to collapse, low tech
    @OnClick(R.id.practice_info)
    public void onPracticeInfoClick() {
        if (textView.getMaxLines() != Integer.MAX_VALUE) {
            textView.setMaxLines(Integer.MAX_VALUE);
            textView.setEllipsize(null);
        }
        else {
            textView.setMaxLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    // the presenter will call this when the list of specialties has been fetched
    public void setShowFirstAvailable(boolean bShow) {
        if (availableProvidersFragment != null) {
            availableProvidersFragment.setShowFirstAvailable(bShow);
        }
    }

    @Override
    public void onRefresh() {
        DefaultLogger.d(LOG_TAG, "refreshing service");
        getPresenter().refreshService();
    }

    @Override
    protected void showBusy(final boolean show) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.postDelayed(new Runnable() { // http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(show);
                }
            }, PROGRESS_DIALOG_DELAY_MS);
        }
        else {
            super.showBusy(show);
        }
    }

    // the presenter will call this when a visit context has been created
    public void setVisitContext(final VisitContext visitContext) {
        final Intent intent = TriageQuestionsActivity.makeIntent(this, visitContext);
        startActivity(intent);
    }

    public void setAvailableProvidersListHeader(@StringRes int res) {
        if (availableProvidersFragment != null) {
            availableProvidersFragment.setListHeader(res);
        }
    }

    public void setAvailableProvidersListItems(final List<ProviderInfo> providers) {
        if (availableProvidersFragment != null) {
            availableProvidersFragment.setListItems(providers);
        }
    }

    public void setFutureAvailableProviders(final AvailableProviders availableProviders) {
        if (providerAppointmentsFragment != null) {
            providerAppointmentsFragment.setAvailableProviders(availableProviders);
        }
    }

    // we have server-side configurations as to which tabs to show per practice
    // in the case of this sample app, we're going to make the assumption that at least one
    // of them will always be true... in other words we're not handling the case where both
    // flags are false.
    class ServicePagerAdapter extends FragmentStatePagerAdapter {

        ServicePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0 && getPresenter().isShowAvailableNow()) {
                return AvailableProvidersFragment.newInstance();
            }
            else {
                return ProviderAppointmentsFragment.newInstance();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            if (position == 0 && getPresenter().isShowAvailableNow()) {
                availableProvidersFragment = (AvailableProvidersFragment) createdFragment;
            }
            else {
                providerAppointmentsFragment = (ProviderAppointmentsFragment) createdFragment;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return getPresenter().getPageCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0 && getPresenter().isShowAvailableNow()) {
                return providersAvailableNow;
            }
            else {
                return providersAppointments;
            }
        }
    }

}