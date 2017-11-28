/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.appointments.AppointmentsActivity;
import com.americanwell.sdksample.documents.HealthDocumentsActivity;
import com.americanwell.sdksample.health.AllergiesActivity;
import com.americanwell.sdksample.health.MedicalHistoryActivity;
import com.americanwell.sdksample.health.MedicationsActivity;
import com.americanwell.sdksample.health.VitalsActivity;
import com.americanwell.sdksample.messages.InboxActivity;
import com.americanwell.sdksample.messages.SentMessagesActivity;
import com.americanwell.sdksample.profile.AddCreditCardActivity;
import com.americanwell.sdksample.profile.AddDependentActivity;
import com.americanwell.sdksample.profile.PharmacyActivity;
import com.americanwell.sdksample.profile.UpdateConsumerActivity;
import com.americanwell.sdksample.profile.UpdateDependentActivity;
import com.americanwell.sdksample.profile.UpdateInsuranceActivity;
import com.americanwell.sdksample.readiness.TechCheckActivity;
import com.americanwell.sdksample.services.ServiceActivity;
import com.americanwell.sdksample.visit.VisitReportsActivity;
import com.prokarma.myhome.R;
import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for application home screen with nav drawer
 */
@RequiresPresenter(HomePresenter.class)
public class HomeActivity extends BaseSampleNucleusActivity<HomePresenter>
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    public final static int REQUEST_ADD_DEPENDENT = 1000;
    public final static int REQUEST_UPDATE_CONSUMER = 1001;
    public final static int REQUEST_UPDATE_DEPENDENT = 1002;
    public final static int REQUEST_TECH_CHECK = 1003;

    private final static String DIALOG_ADD_SERVICE_KEY = "addServiceKeyDialog";
    private final static String DIALOG_CHOOSE_CONSUMER = "chooseConsumerDialog";
    private final static String DIALOG_SET_LOCALE = "setLocaleDialog";

    private ServicesAdapter adapter;

    boolean checkCache = true;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.services_grid)
    GridView servicesGrid;
    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout; // swipe to refresh services
    @BindView(R.id.empty_view)
    View emptyView;

    private DialogFragment addServiceKeyDialog;
    private DialogFragment setLocaleDialog;
    private DialogFragment chooseConsumerDialog;

    /**
     * return an instance of this activity
     *
     * @param context
     * @return
     */
    public static Intent makeIntent(final Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_home);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        adapter = new ServicesAdapter(this);
        servicesGrid.setAdapter(adapter);
        servicesGrid.setFocusable(false);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(addServiceKeyDialog);
        dismissDialog(setLocaleDialog);
        dismissDialog(chooseConsumerDialog);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            logOut();
        }
    }

    private void logOut() {
        getPresenter().logOut();
//        if (getResources().getBoolean(R.bool.use_third_party_auth)) {
//            startActivity(new Intent(this, ThirdPartyLoginActivity.class));
//        }
//        else {
//            startActivity(new Intent(this, LoginActivity.class));
//        }
        finish();
    }

    public void setShowTechCheck(boolean showTechCheck) {
        navigationView.getMenu().findItem(R.id.profile_tech_check).setVisible(showTechCheck);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_inbox: {
                startActivity(InboxActivity.makeIntent(this));
                break;
            }
            case R.id.nav_sent: {
                startActivity(SentMessagesActivity.makeIntent(this));
                break;
            }
            case R.id.nav_add_service_key: {
                addServiceKey();
                break;
            }
            case R.id.nav_set_locale: {
                setLocale();
                break;
            }
            case R.id.nav_appointments: {
                startActivity(AppointmentsActivity.makeIntent(this));
                break;
            }
            case R.id.nav_visit_reports: {
                startActivity(VisitReportsActivity.makeIntent(this));
                break;
            }
            case R.id.nav_log_out: {
                logOut();
                break;
            }

            // profile
            case R.id.profile_insurance: {
                startActivity(UpdateInsuranceActivity.makeIntent(this));
                break;
            }
            case R.id.profile_pharmacy: {
                startActivity(PharmacyActivity.makeIntent(this));
                break;
            }
            case R.id.profile_payment: {
                startActivity(AddCreditCardActivity.makeIntent(this));
                break;
            }
            case R.id.profile_update_consumer: {
                if (getPresenter().isDependent()) {
                    // if current consumer is a dependent, go to dependent-specific update activity
                    startActivityForResult(UpdateDependentActivity.makeIntent(this), REQUEST_UPDATE_DEPENDENT);
                }
                else {
                    startActivityForResult(UpdateConsumerActivity.makeIntent(this), REQUEST_UPDATE_CONSUMER);
                }
                break;
            }
            case R.id.profile_tech_check:
                startActivityForResult(TechCheckActivity.makeIntent(this),REQUEST_TECH_CHECK);
                break;
            case R.id.profile_dependents: {
                startActivityForResult(AddDependentActivity.makeIntent(this), REQUEST_ADD_DEPENDENT);
                break;
            }

            // health
            case R.id.health_conditions: {
                startActivity(MedicalHistoryActivity.makeIntent(this));
                break;
            }
            case R.id.health_allergies: {
                startActivity(AllergiesActivity.makeIntent(this));
                break;
            }
            case R.id.health_medications: {
                startActivity(MedicationsActivity.makeIntent(this));
                break;
            }
            case R.id.health_vitals: {
                startActivity(VitalsActivity.makeIntent(this));
                break;
            }
            case R.id.health_documents: {
                startActivity(HealthDocumentsActivity.makeIntent(this));
                break;
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_DEPENDENT:
                    getNavHeaderTextView().post(new Runnable() {
                        @Override
                        public void run() {
                            getPresenter().setNavHeader();
                        }
                    });
                    break;
                case REQUEST_UPDATE_CONSUMER:
                    getPresenter().setConsumerUpdated();
                    break;
                case REQUEST_UPDATE_DEPENDENT:
                    // when we update a dependent, we get back a new instance.  we need to pass this
                    // to the presenter for processing
                    final Consumer dependent = data.getParcelableExtra("dependent");
                    getPresenter().setDependentUpdated(dependent);
                    break;
                case REQUEST_TECH_CHECK:
                    getPresenter().setNavigationUpdate();
                    break;
                default:
                    break;
            }
        }
    }

    public void setNavHeaderText(final String text) {
        getNavHeaderTextView().setText(text);
    }

    private TextView getNavHeaderTextView() {
        final View headerLayout = navigationView.getHeaderView(0);
        return (TextView) headerLayout.findViewById(R.id.textView);
    }

    // if the logged in consumer has dependents we enable the "switch user" view
    public void setSwitchUserView(boolean visible) {
        getSwitchUserView().setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            getNavHeaderTextView().setOnClickListener(getChooseConsumerOnClickListener());
            getSwitchUserView().setOnClickListener(getChooseConsumerOnClickListener());
        }
    }

    private View getSwitchUserView() {
        final View headerLayout = navigationView.getHeaderView(0);
        return headerLayout.findViewById(R.id.switchUser);
    }

    // pop up the dialog to choose the consumer
    private View.OnClickListener getChooseConsumerOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseConsumerDialog = new ChooseConsumerDialogFragment();
                chooseConsumerDialog.show(getSupportFragmentManager(), DIALOG_CHOOSE_CONSUMER);
            }
        };
    }

    public void setShowInsuranceMenuItem(boolean show) {
        navigationView.getMenu().findItem(R.id.profile_insurance).setVisible(show);
    }

    // if we've switched to a dependent, we hide some nav menu items
    public void setShowMenuItems(boolean show) {
        navigationView.getMenu().findItem(R.id.nav_add_service_key).setVisible(show);
        navigationView.getMenu().findItem(R.id.profile_payment).setVisible(show);
    }

    public void setShowServiceKeyMenuItem(boolean show) {
        navigationView.getMenu().findItem(R.id.nav_add_service_key).setVisible(show);
    }

    public void setShowAddDependentMenuItem(boolean show) {
        navigationView.getMenu().findItem(R.id.profile_dependents).setVisible(show);
    }

    public void setShowSetLocale(boolean show) {
        navigationView.getMenu().findItem(R.id.nav_set_locale).setVisible(show);
    }

    // pop up a dialog for service keys
    private void addServiceKey() {
        addServiceKeyDialog = new AddServiceKeyDialogFragment();
        addServiceKeyDialog.show(getSupportFragmentManager(), DIALOG_ADD_SERVICE_KEY);
    }

    public void setServiceKeyAdded() {
        Toast.makeText(this, R.string.add_service_key_added, Toast.LENGTH_SHORT).show();
    }

    private void setLocale() {
        setLocaleDialog = new SetLocaleDialogFragment();
        setLocaleDialog.show(getSupportFragmentManager(), DIALOG_SET_LOCALE);
    }

    public void setServices(@NonNull final List<PracticeInfo> services) {
        adapter.clear();
        adapter.addAll(services);
        adapter.notifyDataSetChanged();
        servicesGrid.setVisibility(services.isEmpty() ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(services.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @OnItemClick(R.id.services_grid)
    public void onServiceItemClick(int position) {
        startActivity(ServiceActivity.makeIntent(this, adapter.getItem(position))); // open the selected service
    }

    /**
     * this is an example of an overridden "busy" view that uses the spinner in the list refresh
     * instead of the progress dialog
     *
     * @param show
     */
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

    @Override
    public void onRefresh() {
        checkCache = false;
        getPresenter().getServices();

        // loading the images in Adapter.getView() is asynchronous and there is no
        // callback for when last item in Adapter is called
        // Give 2000ms for final getView() to be called before reverting "checkCache"
        new Handler().postDelayed(new Runnable() {
                                      public void run() {
                                          checkCache = true;
                                      }
                                  },
                2000);
    }

    /**
     * modal dialog to accept the entry of a service key
     */
    public static class AddServiceKeyDialogFragment extends DialogFragment {
        @BindView(R.id.service_key_text)
        EditText serviceKey;
        @BindString(R.string.add_service_key_validate_blank)
        String serviceKeyBlank;

        private Unbinder unbinder;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            @SuppressLint("InflateParams")
            final View view = inflater.inflate(R.layout.dialog_add_service_key, null);
            unbinder = ButterKnife.bind(this, view);
            builder.setView(view)
                    .setCancelable(false)
                    .setPositiveButton(R.string.add_service_key_button_add, null)
                    .setNegativeButton(R.string.add_service_key_button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(serviceKey.getText().toString().trim())) {
                                serviceKey.setError(serviceKeyBlank);
                            }
                            else {
                                ((HomeActivity) getActivity()).getPresenter().addServiceKey(serviceKey.getText().toString().trim());
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
            return dialog;
        }

        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }
    }

    /**
     * modal dialog to set the preferred Locale
     */
    public static class SetLocaleDialogFragment extends DialogFragment {
        int selected = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final HomePresenter presenter = ((HomeActivity)getActivity()).getPresenter();
            final List<Locale> supportedLocales = presenter.getSupportedLocales();
            final Locale preferredLocale = presenter.getPreferredLocale();
            final Locale sysLocale = presenter.getSystemLocale();
            final ArrayList<String> options = new ArrayList<>();
            final Context ctx = getContext();

            for (int i = 0; i < supportedLocales.size(); i++) {
                Locale l = supportedLocales.get(i);
                options.add(new String(l.getCountry() + " - " + l.getLanguage() +
                        (sysLocale.equals(l) ? " " + ctx.getString(R.string.set_locale_system_locale)
                                : "")));

                if (preferredLocale != null && preferredLocale.equals(l)) {
                    selected = i;
                }
            }

            builder.setTitle(getContext().getString(R.string.set_locale_preferred_locale))
                    .setCancelable(false)
                    .setSingleChoiceItems(options.toArray(new String[]{}), selected, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selected = which;
                        }
                    })
                    .setPositiveButton(R.string.set_locale_button_apply, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Locale locale = supportedLocales.get(selected);
                            presenter.setPreferredLocale(locale);

                            // reload to apply Locale change
                            getActivity().finish();
                            Intent i = HomeActivity.makeIntent(ctx);
                            getActivity().startActivity(i);

                        }
                    })
                    .setNegativeButton(R.string.set_locale_button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            final AlertDialog dialog = builder.create();

            return dialog;
        }
    }

    public static class ChooseConsumerDialogFragment extends DialogFragment {
        @BindView(R.id.list_view)
        ListView listView;
        private ConsumersAdapter adapter;
        private AlertDialog dialog;
        private Unbinder unbinder;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            @SuppressLint("InflateParams")
            final View view = inflater.inflate(R.layout.dialog_choose_consumer, null);
            unbinder = ButterKnife.bind(this, view);
            builder.setView(view).setCancelable(true);
            adapter = new ConsumersAdapter(
                    getContext(),
                    ((HomeActivity) getActivity()).getPresenter().getConsumers());
            listView.setAdapter(adapter);
            dialog = builder.create();
            return dialog;
        }

        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }

        /**
         * Adapter used to build a dropdown list of consumers.
         */
        public class ConsumersAdapter extends ArrayAdapter<Consumer> {
            public ConsumersAdapter(final Context context, final List<Consumer> consumers) {
                super(context, 0, consumers);
            }

            private class ViewHolder {
                CheckedTextView checkedTextView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                ViewHolder viewHolder;
                if (convertView == null) {
                    view = View.inflate(getContext(), android.R.layout.simple_list_item_single_choice, null);
                    viewHolder = new ViewHolder();
                    viewHolder.checkedTextView = (CheckedTextView) view;
                    view.setTag(viewHolder);
                }
                else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                final Consumer consumer = getItem(position);
                viewHolder.checkedTextView.setText(consumer.getFullName());
                listView.setItemChecked(position, ((HomeActivity) getActivity()).getPresenter().isCurrentConsumer(consumer));
                viewHolder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HomeActivity) getActivity()).getPresenter().setConsumer(consumer);
                        dialog.dismiss();
                    }
                });
                return view;
            }
        }

    }

    // ArrayAdapter for handling Services
    public class ServicesAdapter extends ArrayAdapter<PracticeInfo> {
        public ServicesAdapter(final Context context) {
            super(context, 0);
        }

        public class ViewHolder {
            @BindView(R.id.text_view)
            TextView textView;
            @BindView(R.id.image_view)
            ImageView imageView;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_service, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final PracticeInfo practiceInfo = getItem(position);

            final ViewHolder finalHolder = viewHolder; // we need a final version for the picasso callback

            viewHolder.imageView.setImageBitmap(null); // clear out any existing image
            finalHolder.textView.setText(practiceInfo.getName());

            getPresenter().loadPracticeImage(
                    practiceInfo,
                    viewHolder.imageView,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            finalHolder.textView.setVisibility(View.GONE);
                            finalHolder.imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() { // if we don't have an image, show the text instead
                            finalHolder.textView.setVisibility(View.VISIBLE);
                            finalHolder.imageView.setVisibility(View.GONE);
                        }
                    },
                    checkCache
            );

            return view;
        }
    }
}
