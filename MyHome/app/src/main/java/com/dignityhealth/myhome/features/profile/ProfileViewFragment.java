package com.dignityhealth.myhome.features.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.features.login.LoginActivity;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.RESTConstants;

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
    TextView dateOfBirth;
    TextView address;
    TextView phone;
    TextView email;

    TextView insuranceProvider;
    TextView memberId;
    TextView group;
    private Button logout;

    private final String placeholderText = "Not Available";

    public static ProfileViewFragment newInstance() {
        return new ProfileViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.profile_view, container, false);

        name = (TextView) profileView.findViewById(R.id.name);
        dateOfBirth = (TextView) profileView.findViewById(R.id.dob);
        address = (TextView) profileView.findViewById(R.id.address);
        phone = (TextView) profileView.findViewById(R.id.phone);
        email = (TextView) profileView.findViewById(R.id.email);

        insuranceProvider = (TextView) profileView.findViewById(R.id.provider);
        memberId = (TextView) profileView.findViewById(R.id.id);
        group = (TextView) profileView.findViewById(R.id.group);
        logout = (Button) profileView.findViewById(R.id.sign_out);

        if (ProfileManager.getProfile() == null) {
            //Get profile since we don't have it
            getProfileInfo("Bearer " + AuthManager.getBearerToken());
        } else {
            //We have a profile singleton; just update info.
            updateProfileViews(ProfileManager.getProfile());
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        setHasOptionsMenu(true);
        return profileView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.profile_view_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.edit_profile:
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROFILE_EDIT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE_VIEW;
    }

    private void getProfileInfo(String bearer) {
        NetworkManager.getInstance().getProfile(bearer).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(response.body());
                    updateProfileViews(response.body());
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Timber.e("Something failed! :/");
            }
        });
    }

    private void updateProfileViews(Profile profile) {
        if (profile.firstName != null || profile.lastName != null) {
            name.setText(CommonUtil.constructName(profile.firstName, profile.lastName));
        } else {
            name.setText(placeholderText);
        }

        if (profile.dateOfBirth != null) {
            Calendar myCalendar = Calendar.getInstance();
            try {
                myCalendar.setTime(Constants.SIMPLE_DATE_FORMAT_UTC.parse(profile.dateOfBirth));
                dateOfBirth.setText(Constants.SIMPLE_DATE_FORMAT.format(myCalendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                dateOfBirth.setText(placeholderText);
            }
        } else {
            dateOfBirth.setText(placeholderText);
        }

        if (profile.address != null &&
                (profile.address.line1 != null || profile.address.line2 != null || profile.address.city != null || profile.address.stateOrProvince != null || profile.address.zipCode != null)) {
            address.setText(CommonUtil.constructAddress(profile.address.line1, profile.address.line2, profile.address.city, profile.address.stateOrProvince, profile.address.zipCode));
        } else {
            address.setText(placeholderText);
        }

        if (profile.phoneNumber != null) {
            phone.setText(CommonUtil.constructPhoneNumber(profile.phoneNumber));
        } else {
            phone.setText(placeholderText);
        }

        if (profile.email != null) {
            email.setText(profile.email);
        } else {
            email.setText(placeholderText);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.providerName != null) {
            insuranceProvider.setText(profile.insuranceProvider.providerName);
        } else {
            insuranceProvider.setText(placeholderText);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.memberNumber != null) {
            memberId.setText(profile.insuranceProvider.memberNumber);
        } else {
            memberId.setText(placeholderText);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.groupNumber != null) {
            group.setText(profile.insuranceProvider.groupNumber);
        } else {
            group.setText(placeholderText);
        }
    }

    private void logout() {

        if (null == AuthManager.getIdTokenForSignOut()){
            Toast.makeText(getActivity(), "No valid session, please login again",
                    Toast.LENGTH_LONG).show();

            Intent intent = LoginActivity.getLoginIntent(getActivity());
            startActivity(intent);
            getActivity().finish();
            return;
        }

        NetworkManager.getInstance().logout(RESTConstants.LOGOUT_AUTH,
                AuthManager.getIdTokenForSignOut()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    getActivity().finishAffinity();
                    Toast.makeText(getActivity(), "signed out successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = LoginActivity.getLoginIntent(getActivity());
                    startActivity(intent);
                    getActivity().finish();
                    return;
                }
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
