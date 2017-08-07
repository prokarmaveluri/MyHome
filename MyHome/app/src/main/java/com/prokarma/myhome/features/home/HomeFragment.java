package com.prokarma.myhome.features.home;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.google.gson.Gson;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.databinding.HomeBinding;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.AppointmentResponse;
import com.prokarma.myhome.features.fad.ProviderListDialog;
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.profile.ProfileResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;

import java.util.ArrayList;
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
        binding.relDbAppointItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.APPOINTMENTS);
                Bundle bundle = new Bundle();
                bundle.putParcelable(APPOINTMENT_KEY, appointment);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.APPOINTMENTS_DETAILS, bundle);
            }
        });
        binding.imgDbAppointItemPinIcon.setOnClickListener(new View.OnClickListener() {
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

        if (ConnectionUtil.isConnected(getActivity())) {
            // Get Name from profile
            if (ProfileManager.getProfile() == null) {
                Timber.i(getString(R.string.db_profile_alert_one));
                getProfileInfo(AuthManager.getInstance().getBearerToken());
            } else {
                Timber.i(getString(R.string.db_profile_alert_two));
                updateProfileViews();
            }
            // Get Appointment Info
            if (ProfileManager.getAppointments() == null) {
                Timber.i(getString(R.string.db_appoint_alert_one));
                getAppointmentInfo(AuthManager.getInstance().getBearerToken());
            } else {
                Timber.i(getString(R.string.db_appoint_alert_two));
                updateAppointViews();

            }
        } else {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            binding.relDbAppointItemLayout.setVisibility(View.GONE);
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

            bundle.putParcelableArrayList("PROVIDER_LIST", providers);
            bundle.putBoolean("PROVIDER_RECENT", true);
            bundle.putBoolean("PROVIDER_RECENT_HOME", true);
            dialog.setArguments(bundle);
            dialog.setTargetFragment(this, RECENT_PROVIDERS);
            dialog.show(getChildFragmentManager(), getString(R.string.db_list_dilaog));
        } else {
            ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.FAD);
        }
    }

    private void getProfileInfo(String bearer) {
        Timber.i(getString(R.string.db_session_bearer) + " " + bearer);
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        Timber.i("Session bearer " + bearer);
        showLoading();
        NetworkManager.getInstance().getProfile(bearer).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (isAdded()) {

                    if (response.isSuccessful()) {
                        Timber.d(getString(R.string.db_res_success) + "\n" + response);
                        ProfileManager.setProfile(response.body().result);
                        updateProfileViews();
                    } else {
                        Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e(getString(R.string.db_res_failed));
                    Timber.e(getString(R.string.db_res_throwable) + " = " + t);
                    hideLoading();
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

    private void getAppointmentInfo(String bearer) {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        showLoading();

        binding.relDbAppointItemLayout.setVisibility(View.GONE);
        NetworkManager.getInstance().getAppointments(bearer).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (isAdded()) {

                    if (response.isSuccessful()) {
                        try {
                            AppointmentResponse result = response.body();
                            ProfileManager.setAppointments(result.result.appointments);
                            updateAppointViews();
                        } catch (NullPointerException ex) {
                            Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                        }
                    } else {
                        Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                if (isAdded()) {
                    hideLoading();
                    Timber.e(getString(R.string.db_res_failed));
                    Timber.e(getString(R.string.db_res_throwable) + " = " + t);
                }
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

        if (appointments != null && appointments.size() > 0) {
            try {
                //Attempt to sort the appointments by startTime
                Collections.sort(appointments);
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.relDbAppointItemLayout.setVisibility(View.VISIBLE);
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
                binding.txtDbAppointItemDoctorName.setText(appointment.doctorName);
            }

            if (appointment.facilityName != null && !appointment.facilityName.isEmpty()) {
                binding.txtDbAppointItemFacility.setText(appointment.facilityName);
            }

            if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
                binding.txtDbAppointItemDate.setText(DateUtil.getDateWordsFromUTC(appointment.appointmentStart));
                binding.txtDbAppointItemTime.setText(DateUtil.getTime(appointment.appointmentStart));
            }


        } else {
            binding.relDbAppointItemLayout.setVisibility(View.GONE);
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
                        startWebView(Constants.DID_YOU_KNOW_SEC1);
                        break;
                    case R.id.txt_db_didyouknow_second:
                        startWebView(Constants.DID_YOU_KNOW_SEC2);
                        break;
                }
            }
        };
        partTwo.setSpan(span1, 0, partTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder1.append(partOne);
        builder1.append(partTwo);
        txtReadmore.setText(builder1);
        txtReadmore.setMovementMethod(LinkMovementMethod.getInstance());
    }

//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        if (actionId == EditorInfo.IME_ACTION_DONE ||
//                actionId == EditorInfo.IME_ACTION_SEARCH) {
//            CommonUtil.hideSoftKeyboard(getActivity());
//            if (v.getText().toString().length() > 0) {
//                FadFragment.currentSearchQuery = v.getText().toString();
//                ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.FAD);
//            } else {
//                Toast.makeText(getActivity(), getString(R.string.query_empty), Toast.LENGTH_LONG).show();
//            }
//            return true;
//        }
//        return false;
//    }

    private void startWebView(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
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
}
