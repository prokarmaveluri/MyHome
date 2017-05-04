package com.dignityhealth.myhome.features.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.Constants;

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
    TextView firstName;
    TextView lastName;
    TextView preferredName;
    TextView dateOfBirth;
    TextView address;
    TextView city;
    TextView state;
    TextView zip;
    TextView phone;
    TextView email;

    TextView insuranceProvider;
    TextView memberId;
    TextView group;

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

        firstName = (TextView) profileView.findViewById(R.id.first_name);
        lastName = (TextView) profileView.findViewById(R.id.last_name);
        preferredName = (TextView) profileView.findViewById(R.id.preferred_name);
        dateOfBirth = (TextView) profileView.findViewById(R.id.dob);
        address = (TextView) profileView.findViewById(R.id.address);
        city = (TextView) profileView.findViewById(R.id.city);
        state = (TextView) profileView.findViewById(R.id.state);
        zip = (TextView) profileView.findViewById(R.id.zip);
        phone = (TextView) profileView.findViewById(R.id.phone);
        email = (TextView) profileView.findViewById(R.id.email);

        insuranceProvider = (TextView) profileView.findViewById(R.id.provider);
        memberId = (TextView) profileView.findViewById(R.id.id);
        group = (TextView) profileView.findViewById(R.id.group);

        if(ProfileManager.getProfile() == null){
            //Get profile since we don't have it
            getProfileInfo("Bearer " + AuthManager.getBearerToken());
        } else {
            //We have a profile singleton; just update info.
            updateProfileViews(ProfileManager.getProfile());
        }

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
        if (profile.firstName != null) {
            firstName.setText(profile.firstName);
        } else {
            firstName.setText(placeholderText);
        }

        if (profile.lastName != null) {
            lastName.setText(profile.lastName);
        } else {
            lastName.setText(placeholderText);
        }

        if (profile.preferredName != null) {
            preferredName.setText(profile.preferredName);
        } else {
            preferredName.setText(placeholderText);
        }

        if (profile.dateOfBirth != null) {
            dateOfBirth.setText(profile.dateOfBirth);
        } else {
            dateOfBirth.setText(placeholderText);
        }

        if (profile.address != null && profile.address.line2 != null && profile.address.line1 != null) {
            address.setText(profile.address.line1 + "\n" + profile.address.line2);
        } else if (profile.address != null && profile.address.line1 != null) {
            address.setText(profile.address.line1);
        } else {
            address.setText(placeholderText);
        }

        if (profile.address != null && profile.address.city != null) {
            city.setText(profile.address.city);
        } else {
            city.setText(placeholderText);
        }

        if (profile.address != null && profile.address.stateOrProvince != null) {
            state.setText(profile.address.stateOrProvince);
        } else {
            state.setText(placeholderText);
        }

        if (profile.address != null && profile.address.zipCode != null) {
            zip.setText(profile.address.zipCode);
        } else {
            zip.setText(placeholderText);
        }

        if (profile.phoneNumber != null) {
            phone.setText(profile.phoneNumber);
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
}
