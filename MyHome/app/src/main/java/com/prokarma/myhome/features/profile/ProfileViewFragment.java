package com.prokarma.myhome.features.profile;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.SessionUtil;
import com.prokarma.myhome.utils.TealiumUtil;

import java.text.ParseException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class ProfileViewFragment extends BaseFragment {
    public static final String PROFILE_VIEW_TAG = "profile_view_tag";

    View profileView;
    TextView name;
    TextView preferredName;
    TextView gender;
    TextView dateOfBirth;
    TextView address;
    TextView phone;
    TextView email;

    TextView insurancePlan;
    TextView memberId;
    TextView group;
    private Button logout;
    ProgressBar progressBar;
    TextView errorText;
    LinearLayout viewProfile;

    public static ProfileViewFragment newInstance() {
        return new ProfileViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.profile_view, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.profile));

        name = (TextView) profileView.findViewById(R.id.name);
        preferredName = (TextView) profileView.findViewById(R.id.preferred_name);
        gender = (TextView) profileView.findViewById(R.id.gender);
        dateOfBirth = (TextView) profileView.findViewById(R.id.dob);
        address = (TextView) profileView.findViewById(R.id.address);
        phone = (TextView) profileView.findViewById(R.id.phone);
        email = (TextView) profileView.findViewById(R.id.email);

        insurancePlan = (TextView) profileView.findViewById(R.id.plan);
        memberId = (TextView) profileView.findViewById(R.id.id);
        group = (TextView) profileView.findViewById(R.id.group);
        logout = (Button) profileView.findViewById(R.id.sign_out);
        progressBar = (ProgressBar) profileView.findViewById(R.id.profile_view_progress);
        errorText = (TextView) profileView.findViewById(R.id.profile_unavailable);
        viewProfile = (LinearLayout) profileView.findViewById(R.id.viewProfile);

        if (ProfileManager.getProfile() == null) {
            viewProfile.setVisibility(View.INVISIBLE);
            Timber.i("Don't have a saved Profile. Retrieving profile now...");
            getProfileInfo(AuthManager.getInstance().getBearerToken());
        } else {
            viewProfile.setVisibility(View.VISIBLE);
            Timber.i("Already have a Profile Singleton. Updating the view...");
            updateProfileViews(ProfileManager.getProfile());
        }

//        viewProfile.setVisibility(View.INVISIBLE);
//        getProfileInfo(AuthManager.getInstance().getBearerToken());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SessionUtil.logout(getActivity(), progressBar);
                SessionUtil.signOutAlert(getActivity(), progressBar);
            }
        });

        return profileView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_view_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.edit_profile:
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROFILE_EDIT,
                        null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.PROFILE_SCREEN, null);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE_VIEW;
    }

    private void getProfileInfo(String bearer) {
        Timber.i("Session bearer " + bearer);
        viewProfile.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        NetworkManager.getInstance().getProfile(bearer).enqueue(new Callback<ProfileGraphqlResponse>() {
            @Override
            public void onResponse(Call<ProfileGraphqlResponse> call, Response<ProfileGraphqlResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        try {
                            Timber.d("Successful Response\n" + response);
                            ProfileManager.setProfile(response.body().getData().getUser());
                            updateProfileViews(response.body().getData().getUser());
                            viewProfile.setVisibility(View.VISIBLE);
                            errorText.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } catch (NullPointerException ex) {
                            Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                            viewProfile.setVisibility(View.GONE);
                            errorText.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                        viewProfile.setVisibility(View.GONE);
                        errorText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileGraphqlResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("Something failed! :/");
                    Timber.e("Throwable = " + t);
                    viewProfile.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateProfileViews(Profile profile) {
        if (!CommonUtil.isEmptyString(profile.firstName) || !CommonUtil.isEmptyString(profile.lastName)) {
            name.setText(CommonUtil.constructName(profile.firstName, profile.lastName));
        } else {
            name.setText(String.format(getString(R.string.not_available_postfix), getString(R.string.name)));
        }

        if (!CommonUtil.isEmptyString(profile.preferredName)) {
            preferredName.setText(profile.preferredName);
        } else {
            preferredName.setText(String.format(getString(R.string.not_available_postfix), getString(R.string.preferred_name_profile)));
        }

        if (!CommonUtil.isEmptyString(profile.gender) && !profile.gender.equalsIgnoreCase(getString(R.string.unknown))) {
            gender.setText(CommonUtil.capitalize(profile.gender));
        } else {
            gender.setText(String.format(getString(R.string.not_available_postfix), getString(R.string.gender_profile)));
        }

        if (!CommonUtil.isEmptyString(profile.dateOfBirth)) {
            Calendar myCalendar = Calendar.getInstance();
            try {
                myCalendar.setTime(DateUtil.getDateZ(profile.dateOfBirth));
                dateOfBirth.setText(DateUtil.convertDateToReadable(myCalendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                dateOfBirth.setVisibility(View.GONE);
            }
        } else {
            dateOfBirth.setText(String.format(getString(R.string.not_available_postfix), getString(R.string.date_of_birth_profile)));
        }

        if (profile.address != null &&
                (profile.address.line1 != null || profile.address.line2 != null || profile.address.city != null || profile.address.stateOrProvince != null || profile.address.zipCode != null)) {
            address.setText(CommonUtil.constructAddress(profile.address.line1, profile.address.line2, profile.address.city, profile.address.stateOrProvince, profile.address.zipCode));
        } else {
            address.setVisibility(View.GONE);
        }

        if (!CommonUtil.isEmptyString(profile.phoneNumber)) {
            phone.setText(CommonUtil.constructPhoneNumber(profile.phoneNumber).replaceAll("\\.", "-"));
        } else {
            phone.setText(String.format(getString(R.string.not_available_postfix), getString(R.string.phone_number_profile)));
        }

        if (!CommonUtil.isEmptyString(profile.email)) {
            email.setText(profile.email);
        } else {
            email.setText(String.format(getString(R.string.not_available_postfix), getString(R.string.email_profile)));
        }

        if (profile.insuranceProvider != null && !CommonUtil.isEmptyString(profile.insuranceProvider.insurancePlan)) {
            insurancePlan.setText(profile.insuranceProvider.insurancePlan);
            insurancePlan.setVisibility(View.VISIBLE);
        }

        if (profile.insuranceProvider != null && !CommonUtil.isEmptyString(profile.insuranceProvider.memberNumber)) {
            memberId.setText(profile.insuranceProvider.memberNumber);
            memberId.setVisibility(View.VISIBLE);
        }

        if (profile.insuranceProvider != null && !CommonUtil.isEmptyString(profile.insuranceProvider.groupNumber)) {
            group.setText(profile.insuranceProvider.groupNumber);
            group.setVisibility(View.VISIBLE);
        }

        Handler coachmarkHandler = new Handler();
        coachmarkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                coachmarkToolbar();
            }
        }, 1000);
    }

    private void coachmarkToolbar(){
        Toolbar toolbar = ((NavigationActivity) getActivity()).getToolbar();
        TapTargetView.showFor(
                getActivity(),
                TapTarget.forToolbarMenuItem(toolbar, R.id.edit_profile, "To Edit your profile, go here!"),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        Toast.makeText(getContext(), "You clicked it! YAY!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
