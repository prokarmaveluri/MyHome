package com.prokarma.myhome.features.home;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.gson.Gson;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.app.OptionsActivity;
import com.prokarma.myhome.databinding.HomeBinding;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.MyAppointmentsRequest;
import com.prokarma.myhome.features.appointments.MyAppointmentsResponse;
import com.prokarma.myhome.features.fad.ProviderListDialog;
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class HomeFragment extends BaseFragment {

    public static final String HOME_TAG = "home_tag";
    protected static final String APPOINTMENT_KEY = "appointment_key";
    private static final int RECENT_PROVIDERS = 200;
    private HomeBinding binding;
    private int progressStatus = 0;
    Appointment appointment = null;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.welcome_home));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.HOME_SCREEN, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Read More contents
        doSpannableChangesReadMore(getString(R.string.db_text_one) + " ", getString(R.string.db_readmore),
                binding.txtDbDidyouknowFirst);
        doSpannableChangesReadMore(getString(R.string.db_text_two) + " ", getString(R.string.db_readmore),
                binding.txtDbDidyouknowSecond);

        binding.txtDbAppointViewall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.APPOINTMENTS);
            }
        });
        binding.appointmentItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.APPOINTMENTS);
                Bundle bundle = new Bundle();
                bundle.putParcelable(APPOINTMENT_KEY, appointment);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.APPOINTMENTS_DETAILS, bundle);
            }
        });
        binding.pinIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != appointment && null != appointment.facilityAddress)
                    CommonUtil.getDirections(getActivity(), appointment.facilityAddress);
            }
        });
        binding.btnDbScheduleAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.hideSoftKeyboard(getActivity());
                startListDialog(getActivity());
            }
        });
        binding.etxtDbFindcare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.FAD);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.FAD, null);
            }
        });

        binding.homeBillPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((NavigationActivity) getActivity()).setActivityTag(Constants.ActivityTag.FAQ);
                Intent intentFAQ = new Intent(getActivity(), OptionsActivity.class);
                final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                        R.anim.slide_in_right, R.anim.slide_out_left);
                ActivityCompat.startActivity(getActivity(), intentFAQ, options.toBundle());
            }
        });

        binding.homeMyCareSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).setActivityTag(Constants.ActivityTag.MY_CARE);
                Intent intentFAQ = new Intent(getActivity(), OptionsActivity.class);
                final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                        R.anim.slide_in_right, R.anim.slide_out_left);
                ActivityCompat.startActivity(getActivity(), intentFAQ, options.toBundle());
            }
        });

        if (ConnectionUtil.isConnected(getActivity())) {
            // Get Name from profile
            if (ProfileManager.getProfile() == null) {
                progressStatus = 1;
                Timber.i(getString(R.string.db_profile_alert_one));
                getProfileInfo(AuthManager.getInstance().getBearerToken());
            } else {
                Timber.i(getString(R.string.db_profile_alert_two));
                updateProfileViews();
            }
            // Get Appointment Info
            if (ProfileManager.getAppointments() == null) {
                progressStatus++;
                Timber.i(getString(R.string.db_appoint_alert_one));
                getMyAppointments();
            } else {
                Timber.i(getString(R.string.db_appoint_alert_two));
                updateAppointViews();

            }
            if (progressStatus == 0)
                coachmarkBookAppointment();
        } else {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            binding.appointmentItemLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.HOME;
    }

    private void startListDialog(Activity activity) {
        ArrayList<String> recentlyViewed = RecentlyViewedDataSourceDB.getInstance().getAllProviderEntry();
        if (recentlyViewed != null && recentlyViewed.size() > 0) {

            ArrayList<ProviderDetailsResponse> providers = new ArrayList<>();
            ProviderListDialog dialog = new ProviderListDialog();
            Bundle bundle = new Bundle();
            Gson gson = new Gson();

            for (String provider : recentlyViewed) {
                ProviderDetailsResponse providerObj = gson.fromJson(provider, ProviderDetailsResponse.class);
                providers.add(providerObj);
            }
            if (providers.size() <= 0)
                return;

            Fragment fragment = getChildFragmentManager().findFragmentByTag(getString(R.string.db_list_dilaog));
            if (fragment == null || !fragment.isVisible()) {
                bundle.putParcelableArrayList("PROVIDER_LIST", providers);
                bundle.putBoolean("PROVIDER_RECENT", true);
                bundle.putBoolean("PROVIDER_RECENT_HOME", true);
                dialog.setArguments(bundle);
                dialog.setTargetFragment(this, RECENT_PROVIDERS);
                dialog.show(getChildFragmentManager(), getString(R.string.db_list_dilaog));
            }
        } else {
            ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.FAD);
        }
    }

    private void getProfileInfo(String bearer) {
        Timber.i(getString(R.string.db_session_bearer) + " " + bearer);
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            progressStatus--;
            return;
        }
        Timber.i("Session bearer " + bearer);
        showLoading();
        NetworkManager.getInstance().getProfile(bearer).enqueue(new Callback<ProfileGraphqlResponse>() {
            @Override
            public void onResponse(Call<ProfileGraphqlResponse> call, Response<ProfileGraphqlResponse> response) {
                if (isAdded()) {

                    if (response.isSuccessful()) {
                        try {
                            Timber.d(getString(R.string.db_res_success) + "\n" + response);
                            ProfileManager.setProfile(response.body().getData().getUser());
                            updateProfileViews();
                        } catch (NullPointerException ex) {
                        }
                    } else {
                        Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                    }
                    if (progressStatus == 1) {
                        hideLoading();
                        coachmarkBookAppointment();
                    }
                    progressStatus--;
                }
            }

            @Override
            public void onFailure(Call<ProfileGraphqlResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e(getString(R.string.db_res_failed));
                    Timber.e(getString(R.string.db_res_throwable) + " = " + t);
                    hideLoading();
                    coachmarkBookAppointment();
                }
            }
        });
    }

    private void updateProfileViews() {
        if (ProfileManager.getProfile() != null) {
            if (ProfileManager.getProfile().preferredName != null &&
                    !ProfileManager.getProfile().preferredName.trim().isEmpty()) {
                binding.txtDbTitle.setText(getString(R.string.db_welcome_one) + " " + ProfileManager.getProfile().preferredName + "!");
            } else if (ProfileManager.getProfile().firstName != null &&
                    !ProfileManager.getProfile().firstName.trim().isEmpty()) {
                binding.txtDbTitle.setText(getString(R.string.db_welcome_one) + " " + ProfileManager.getProfile().firstName + "!");
            } else {
                binding.txtDbTitle.setText(getString(R.string.db_welcome_two));
            }
        }
    }

    public void getMyAppointments() {
        showLoading();
        binding.appointmentItemLayout.setVisibility(View.GONE);
        NetworkManager.getInstance().getMyAppointments(AuthManager.getInstance().getBearerToken(),
                new MyAppointmentsRequest()).enqueue(new Callback<MyAppointmentsResponse>() {
            @Override
            public void onResponse(Call<MyAppointmentsResponse> call, retrofit2.Response<MyAppointmentsResponse> response) {
                if (response.isSuccessful()) {
                    MyAppointmentsResponse myAppointmentsResponse = response.body();

                    if (myAppointmentsResponse.getData() != null && myAppointmentsResponse.getData().getUser() != null) {
                        ArrayList<Appointment> appointments = (ArrayList<Appointment>) myAppointmentsResponse.getData().getUser().getAppointments();
                        Timber.i("Appointments: " + Arrays.deepToString(appointments.toArray()));

                        try {
                            //Attempt to sort the appointments by startTime
                            Collections.sort(appointments);
                            ProfileManager.setAppointments(appointments);
                            updateAppointViews();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
                if (progressStatus == 1) {
                    hideLoading();
                    coachmarkBookAppointment();
                }
                progressStatus--;
            }

            @Override
            public void onFailure(Call<MyAppointmentsResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
                hideLoading();
                coachmarkBookAppointment();
            }
        });
    }

    private void showLoading() {
        binding.pgDbViewProgress.setVisibility(View.VISIBLE);

    }

    private void hideLoading() {
        binding.pgDbViewProgress.setVisibility(View.GONE);

    }

    private void updateAppointViews() {
        ArrayList<Appointment> appointments = ProfileManager.getAppointments();
        appointments = CommonUtil.getFutureAppointments(appointments);
        if (appointments != null && appointments.size() > 0) {
            try {
                //Attempt to sort the appointments by startTime
                Collections.sort(appointments);
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.appointmentItemLayout.setVisibility(View.VISIBLE);
            binding.viewAppointDivider.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder1 = new SpannableStringBuilder();
            SpannableString partOne = new SpannableString(getString(R.string.db_appoint_one) + " ");
            SpannableString partTwo = new SpannableString(String.valueOf(appointments.size()));
            partTwo.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf(appointments.size()).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString partThree = new SpannableString(" " + getString(R.string.db_appoint_two));
            SpannableString partFour = new SpannableString(" " + getString(R.string.db_appoint_three));
            builder1.append(partOne);
            builder1.append(partTwo);

            if (appointments.size() == 1) {
                builder1.append(partThree);
                binding.txtDbAppointViewall.setVisibility(View.GONE);
                binding.txtDbAppointContent.setText(builder1);
            } else {
                builder1.append(partFour);
                binding.txtDbAppointViewall.setVisibility(View.VISIBLE);
                binding.txtDbAppointContent.setText(builder1);
            }
            appointment = appointments.get(0);
            if (appointment.doctorName != null && !appointment.doctorName.isEmpty()) {
                binding.doctorName.setText(appointment.doctorName);
            }

            if (appointment.facilityName != null && !appointment.facilityName.isEmpty()) {
                binding.facility.setText(appointment.facilityName);
            }

            if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
                binding.date.setText(DateUtil.getDateWordsFromUTC(appointment.appointmentStart));
                binding.time.setText(DateUtil.getTime(appointment.appointmentStart) + " " + DateUtil.getReadableTimeZone(appointment));
            }

            hideLoading();
        } else {
            binding.appointmentItemLayout.setVisibility(View.GONE);
            binding.viewAppointDivider.setVisibility(View.GONE);
            binding.txtDbAppointViewall.setVisibility(View.GONE);
            binding.txtDbAppointContent.setText(getString(R.string.db_appoint_four));
        }
    }

    public void doSpannableChangesReadMore(String firstString, String secondString, final TextView txtReadmore) {
        SpannableStringBuilder builder1 = new SpannableStringBuilder();
        SpannableString partOne = new SpannableString(firstString);
        SpannableString partTwo = new SpannableString(secondString);
        partTwo.setSpan(new TextAppearanceSpan(getContext(), R.style.tradeGothicLTStdBd2_20_deepAqua),
                0, partTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                // this remove the underline
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View textView) {
                /*Toast.makeText(getActivity(), getString(R.string.db_readmore_click),
                        Toast.LENGTH_LONG).show();*/
                int id = textView.getId();
                switch (id) {
                    case R.id.txt_db_didyouknow_first:
                        NavigationActivity.setActivityTag(Constants.ActivityTag.HOME_DID_YOU_KNOW_SEC_1);
                        break;
                    case R.id.txt_db_didyouknow_second:
                        NavigationActivity.setActivityTag(Constants.ActivityTag.HOME_DID_YOU_KNOW_SEC_2);
                        break;
                }

                final ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                        R.anim.slide_in_right, R.anim.slide_out_left);
                Intent intent = new Intent(getActivity(), OptionsActivity.class);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        };
        partTwo.setSpan(span1, 0, partTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder1.append(partOne);
        builder1.append(partTwo);
        txtReadmore.setText(builder1);
        txtReadmore.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECENT_PROVIDERS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras() != null) {
                    ProviderDetailsResponse provider = data.getExtras().getParcelable("PROVIDER");
                    providerDetails(provider);
                }
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                if (getActivity() != null) {
                    ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.FAD);
                }
            }
        }
    }

    /**
     * @param provider
     */
    private void providerDetails(ProviderDetailsResponse provider) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ProviderDetailsFragment.PROVIDER_KEY, provider);
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
    }

    private void coachmarkBookAppointment() {
        boolean skip = AppPreferences.getInstance().getBooleanPreference(Constants.HOME_SKIP_COACH_MARKS);
        if (skip)
            return;

        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(binding.btnDbScheduleAppoint, getString(R.string.coachmark_home_book_appointment))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        coachmarkFindCare();
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private void coachmarkFindCare() {
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(binding.etxtDbFindcare, getString(R.string.coachmark_home_find_care))
                        .transparentTarget(true),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        coachmarkNavigationBarFad();
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private void coachmarkNavigationBarProfile() {
        BottomNavigationViewEx bottomNavigationView = ((NavigationActivity) getActivity()).getBottomNavigationView();
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(bottomNavigationView.getIconAt(3), getString(R.string.coachmark_home_navbar_profile)),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }

    private void coachmarkNavigationBarFad() {
        BottomNavigationViewEx bottomNavigationView = ((NavigationActivity) getActivity()).getBottomNavigationView();
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forView(bottomNavigationView.getIconAt(1), getString(R.string.coachmark_home_navbar_fad)),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        coachmarkNavigationBarProfile();
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        AppPreferences.getInstance().setBooleanPreference(Constants.HOME_SKIP_COACH_MARKS, true);
                    }
                }
        );
    }
}
