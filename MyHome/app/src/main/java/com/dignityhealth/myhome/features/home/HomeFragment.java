package com.dignityhealth.myhome.features.home;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.databinding.HomeBinding;
import com.dignityhealth.myhome.features.appointments.Appointment;
import com.dignityhealth.myhome.features.appointments.AppointmentResponse;
import com.dignityhealth.myhome.features.fad.FadFragment;
import com.dignityhealth.myhome.features.fad.Provider;
import com.dignityhealth.myhome.features.fad.ProviderListDialog;
import com.dignityhealth.myhome.features.fad.recent.RecentlyViewedDataSourceDB;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.features.profile.ProfileResponse;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.ConnectionUtil;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.DateUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class HomeFragment extends BaseFragment implements TextView.OnEditorActionListener {

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
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.dashboard));

        // Read More contents
        doSpannableChangesReadMore("The Power of Time Off: Why Vacations Are Essential. ", "Read More",
                binding.txtDbDidyouknowFirst);
        doSpannableChangesReadMore("A Little Bit of Color: Don't Let a Sunburn Get You Down This Summer. ", "Read More",
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
                Bundle bundle = new Bundle();
                bundle.putParcelable(APPOINTMENT_KEY, appointment);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.APPOINTMENTS_DETAILS, bundle);
            }
        });
        binding.imgDbAppointItemPinIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.getDirections(getActivity(), appointment.facilityAddress);
            }
        });
        binding.btnDbScheduleAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListDialog(getActivity());
            }
        });
        binding.etxtDbFindcare.setOnEditorActionListener(this);
        if (ConnectionUtil.isConnected(getActivity())) {
            // Get Name from profile
            if (ProfileManager.getProfile() == null) {
                Timber.i("Don't have a saved Profile. Retrieving profile now...");
                getProfileInfo(AuthManager.getInstance().getBearerToken());
            } else {
                Timber.i("Already have a Profile Singleton. Updating the view...");
                updateAppointViews();
            }

            if (ProfileManager.getAppointments() == null) {
                // Get Appointment Info
                getAppointmentInfo(AuthManager.getInstance().getBearerToken());
            } else {
                Timber.i("Already have a Profile Singleton. Updating the view...");
                updateProfileViews();
            }
        } else {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
        }
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NavigationActivity) getActivity()).getNavigationActionBar().show();
        ((NavigationActivity) getActivity()).hideHomeButton();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.HOME;
    }

    private void startListDialog(Activity activity) {
        ArrayList<String> recentlyViewed = RecentlyViewedDataSourceDB.getInstance().getAllProviderEntry();
        if (recentlyViewed != null && recentlyViewed.size() > 0) {

            ArrayList<Provider> providers = new ArrayList<>();
            ProviderListDialog dialog = new ProviderListDialog();
            Bundle bundle = new Bundle();
            Gson gson = new Gson();

            for (String provider : recentlyViewed) {
                Provider providerObj = gson.fromJson(provider, Provider.class);
                providers.add(providerObj);
            }
            if (providers.size() <= 0)
                return;

            bundle.putParcelableArrayList("PROVIDER_LIST", providers);
            bundle.putBoolean("PROVIDER_RECENT", true);
            dialog.setArguments(bundle);
            dialog.setTargetFragment(this, RECENT_PROVIDERS);
            dialog.show(getFragmentManager(), "List Dialog");
        } else {
            Toast.makeText(activity, getString(R.string.no_recent_providers),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getProfileInfo(String bearer) {
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
                    hideLoading();
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        ProfileManager.setProfile(response.body().result);
                        updateProfileViews();
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                    hideLoading();
                }
            }
        });
    }

    private void updateProfileViews() {
        if (ProfileManager.getProfile() != null && ProfileManager.getProfile().firstName != null) {
            binding.txtDbTitle.setText("Welcome home, " + ProfileManager.getProfile().firstName + "!");
        } else {
            binding.txtDbTitle.setText(getString(R.string.not_available));
        }
    }

    private void getAppointmentInfo(String bearer) {
        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        showLoading();

        Timber.i("Session bearer " + bearer);
        NetworkManager.getInstance().getAppointments(bearer).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (isAdded()) {
                    hideLoading();
                    if (response.isSuccessful()) {
                        AppointmentResponse result = response.body();
                        ProfileManager.setAppointments(result.result.appointments);
                        updateAppointViews();
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                    }
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                if (isAdded()) {
                    hideLoading();
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
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
            SpannableString partOne = new SpannableString("It looks like you have ");
            SpannableString partTwo = new SpannableString(String.valueOf(appointments.size()));
            partTwo.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf(appointments.size()).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString partThree = new SpannableString(" appointment scheduled");
            SpannableString partFour = new SpannableString(" appointments scheduled");
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
            binding.txtDbAppointContent.setText("No appointments scheduled!");
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
                Toast.makeText(getActivity(), "Read More Clicked!",
                        Toast.LENGTH_LONG).show();
            }
        };
        partTwo.setSpan(span1, 0, partTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder1.append(partOne);
        builder1.append(partTwo);
        txtReadmore.setText(builder1);
        txtReadmore.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH) {

            if (v.getText().toString().length() > 0) {
                FadFragment.currentSearchQuery = v.getText().toString();
                ((NavigationActivity) getActivity()).goToPage(Constants.ActivityTag.FAD);
            } else {
                Toast.makeText(getActivity(), getString(R.string.query_empty), Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return false;
    }
}
