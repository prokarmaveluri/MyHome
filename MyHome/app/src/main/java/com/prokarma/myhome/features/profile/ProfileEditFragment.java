package com.prokarma.myhome.features.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.prokarma.myhome.utils.TealiumUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class ProfileEditFragment extends BaseFragment {
    public static final String PROFILE_EDIT_TAG = "profile_edit_tag";

    View profileView;
    TextInputLayout firstNameLayout;
    TextInputEditText firstName;
    TextInputLayout lastNameLayout;
    TextInputEditText lastName;
    TextInputEditText preferredName;
    Spinner gender;
    TextInputLayout dateOfBirthLayout;
    TextInputEditText dateOfBirth;
    TextInputEditText address;
    TextInputEditText address2;
    TextInputEditText city;
    Spinner state;
    TextInputLayout zipLayout;
    TextInputEditText zip;
    TextInputLayout phoneLayout;
    TextInputEditText phone;
    TextInputLayout emailLayout;
    TextView email;
    TextView genderLabel;

    TextInputEditText insurancePlan;
    TextInputEditText memberId;
    TextInputEditText group;
    ProgressBar progress;

    public static ProfileEditFragment newInstance() {
        return new ProfileEditFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.profile_edit, container, false);

        firstNameLayout = (TextInputLayout) profileView.findViewById(R.id.first_name_layout);
        firstName = (TextInputEditText) profileView.findViewById(R.id.first_name);
        lastNameLayout = (TextInputLayout) profileView.findViewById(R.id.last_name_layout);
        lastName = (TextInputEditText) profileView.findViewById(R.id.last_name);
        preferredName = (TextInputEditText) profileView.findViewById(R.id.preferred_name);
        genderLabel = (TextView) profileView.findViewById(R.id.gender_label);
        gender = (Spinner) profileView.findViewById(R.id.gender);
        dateOfBirthLayout = (TextInputLayout) profileView.findViewById(R.id.dob_layout);
        dateOfBirth = (TextInputEditText) profileView.findViewById(R.id.dob);
        address = (TextInputEditText) profileView.findViewById(R.id.address);
        address2 = (TextInputEditText) profileView.findViewById(R.id.address2);
        city = (TextInputEditText) profileView.findViewById(R.id.city);
        state = (Spinner) profileView.findViewById(R.id.state);
        zipLayout = (TextInputLayout) profileView.findViewById(R.id.zip_layout);
        zip = (TextInputEditText) profileView.findViewById(R.id.zip);
        phoneLayout = (TextInputLayout) profileView.findViewById(R.id.phone_layout);
        phone = (TextInputEditText) profileView.findViewById(R.id.phone);
        email = (TextView) profileView.findViewById(R.id.email);
        progress = (ProgressBar) profileView.findViewById(R.id.profile_edit_progress);

        insurancePlan = (TextInputEditText) profileView.findViewById(R.id.plan);
        memberId = (TextInputEditText) profileView.findViewById(R.id.id);
        group = (TextInputEditText) profileView.findViewById(R.id.group);

        dateOfBirth.addTextChangedListener(new PhoneAndDOBFormatter(dateOfBirth, PhoneAndDOBFormatter.FormatterType.DOB));

        phone.addTextChangedListener(new PhoneAndDOBFormatter(phone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER));

        gender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        if (ProfileManager.getProfile() == null) {
            Timber.i("Don't have a saved Profile. Retrieving profile now...");
            getProfileInfo(AuthManager.getInstance().getBearerToken());
        } else {
            Timber.i("Already have a Profile Singleton. Updating the view...");
            updateProfileViews(ProfileManager.getProfile());
        }

        setHasOptionsMenu(true);
        return profileView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.profile_edit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.save_profile:
                CommonUtil.hideSoftKeyboard(getActivity());
                if (isValidProfile()) {
                    Profile currentProfile = ProfileManager.getProfile();
                    sendUpdatedProfile(AuthManager.getInstance().getBearerToken(), getProfileValues(currentProfile));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfileInfo(String bearer) {
        progress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().getProfile(bearer).enqueue(new Callback<ProfileGraphqlResponse>() {
            @Override
            public void onResponse(Call<ProfileGraphqlResponse> call, Response<ProfileGraphqlResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        ProfileManager.setProfile(response.body().getData().getUser());
                        updateProfileViews(response.body().getData().getUser());
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                    }
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProfileGraphqlResponse> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("Something failed! :/");
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Sends the profile information to the server to update the values
     *
     * @param bearer         the bearer token needed to provide authentication
     * @param updatedProfile the updated profile information being attempted
     */
    private void sendUpdatedProfile(String bearer, Profile updatedProfile) {
        progress.setVisibility(View.VISIBLE);
        NetworkManager.getInstance().updateProfile(bearer, updatedProfile).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Timber.d("Successful Response\n" + response);
                        TealiumUtil.trackEvent(Constants.PROFILE_UPDATE_EVENT, null);
                        Toast.makeText(getActivity(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        Timber.e("Response, but not successful?\n" + response);
                        Toast.makeText(getActivity(), getString(R.string.profile_save_failed), Toast.LENGTH_LONG).show();
                    }

                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (isAdded()) {
                    Timber.e("Something failed! :/");
                    Toast.makeText(getActivity(), getString(R.string.profile_save_failed), Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Updates the view on screen to have the proper profile values autopopulated
     *
     * @param profile the profile that we're using to autopopulate the EditTexts
     */
    private void updateProfileViews(Profile profile) {
        if (profile.firstName != null) {
            firstName.setText(profile.firstName);
        }

        if (profile.lastName != null) {
            lastName.setText(profile.lastName);
        }

        if (profile.preferredName != null) {
            preferredName.setText(profile.preferredName);
        }

        if (profile.gender != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (profile.gender.equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
        }

//        if (profile.dateOfBirth != null && !profile.dateOfBirth.trim().isEmpty()) {
//            Date date = null;
//            try {
//                date = DateUtil.getDateNoTimeZone(profile.dateOfBirth);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            if (null != date)
//                myCalendar.setTime(date);
//            dateOfBirth.setText(DateUtil.convertUTCtoReadable(profile.dateOfBirth));
//        }

        if (profile.dateOfBirth != null) {
            dateOfBirth.setText(DateUtil.convertUTCtoReadable(profile.dateOfBirth));
        }

        if (profile.address != null && profile.address.line1 != null) {
            address.setText(profile.address.line1);
        }

        if (profile.address != null && profile.address.line2 != null) {
            address2.setText(profile.address.line2);
        }

        if (profile.address != null && profile.address.city != null) {
            city.setText(profile.address.city);
        }

        if (profile.address != null && profile.address.stateOrProvince != null) {

            //Loop through states until we find a match, then set state spinner selection
            for (int i = 0; i < state.getAdapter().getCount(); i++) {
                if (profile.address.stateOrProvince.equalsIgnoreCase(state.getAdapter().getItem(i).toString())) {
                    state.setSelection(i);
                    break;
                }
            }
        } else {
            state.setSelection(0);  //Placeholder is the first item in the array
        }

        if (profile.address != null && profile.address.zipCode != null) {
            zip.setText(profile.address.zipCode);
        }

        if (profile.phoneNumber != null) {
            phone.setText(profile.phoneNumber.replaceAll("\\.", "-"));
        }

        if (profile.email != null) {
            email.setText(profile.email);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.insurancePlan != null) {
            insurancePlan.setText(profile.insuranceProvider.insurancePlan);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.memberNumber != null) {
            memberId.setText(profile.insuranceProvider.memberNumber);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.groupNumber != null) {
            group.setText(profile.insuranceProvider.groupNumber);
        }
    }

    private boolean isValidProfile() {
        boolean isValid = true;

        if (firstNameLayout.getVisibility() == View.VISIBLE && firstName.getText().toString().trim().isEmpty()) {
            isValid = false;
            firstNameLayout.setError(getString(R.string.first_name_required));
        } else {
            firstNameLayout.setError(null);
        }

        if (lastNameLayout.getVisibility() == View.VISIBLE && lastName.getText().toString().trim().isEmpty()) {
            isValid = false;
            lastNameLayout.setError(getString(R.string.last_name_required));
        } else {
            lastNameLayout.setError(null);
        }

        if (dateOfBirth.getVisibility() == View.VISIBLE && (!DateUtil.isValidDateOfBirth(dateOfBirth.getText().toString().trim())) &&
                !dateOfBirth.getText().toString().trim().isEmpty()){
            isValid = false;
            dateOfBirthLayout.setError(getString(R.string.date_of_birth_invalid));
        } else {
            dateOfBirthLayout.setError(null);
        }

        if (zipLayout.getVisibility() == View.VISIBLE && (zip.getText().toString().trim().length() != 0 &&
                zip.getText().toString().trim().length() != 5)) {
            isValid = false;
            zipLayout.setError(getString(R.string.zip_invalid));
        } else {
            zipLayout.setError(null);
        }

        if (phoneLayout.getVisibility() == View.VISIBLE && phone.getText().toString().trim().length() != 0 &&
                !CommonUtil.isValidMobile(phone.getText().toString())) {
            isValid = false;
            phoneLayout.setError(getString(R.string.phone_number_invalid));
        } else {
            phoneLayout.setError(null);
        }

        return isValid;
    }

    /**
     * Grabs values from EditTexts and updates the current profile with said values
     *
     * @param currentProfile the current profile so far (before looking at the EditTexts)
     * @return the profile with updated information according to the EditTexts
     */
    private Profile getProfileValues(Profile currentProfile) {
        Profile profile = currentProfile;

        if (profile.address == null) {
            profile.address = new Address();
        }

        if (profile.insuranceProvider == null) {
            profile.insuranceProvider = new InsuranceProvider();
        }

        if (firstName.getText() != null) {
            profile.firstName = firstName.getText().toString().trim();
        }

        if (lastName.getText() != null) {
            profile.lastName = lastName.getText().toString().trim();
        }

        if (preferredName.getText() != null) {
            profile.preferredName = preferredName.getText().toString().trim();
        }

        if (gender.getSelectedItemPosition() != 0) {
            profile.gender = gender.getSelectedItem().toString().trim();
        }

        if (dateOfBirth.getText() != null && !dateOfBirth.getText().toString().trim().isEmpty()) {
            profile.dateOfBirth = DateUtil.convertReadableToUTC(dateOfBirth.getText().toString().trim());
        }

        if (address.getText() != null) {
            profile.address.line1 = address.getText().toString().trim();
        }

        if (address2.getText() != null) {
            profile.address.line2 = address2.getText().toString().trim();
        }

        if (city.getText() != null) {
            profile.address.city = city.getText().toString().trim();
        }

        if (state.getSelectedItemPosition() != 0) {
            profile.address.stateOrProvince = state.getSelectedItem().toString().trim();
        }

        if (zip.getText() != null) {
            profile.address.zipCode = zip.getText().toString().trim();
        }

        //Make sure to strip phone number of any non-digits
        if (phone.getText() != null) {
            profile.phoneNumber = CommonUtil.stripPhoneNumber(phone.getText().toString().trim());
        }

        if (insurancePlan.getText() != null) {
            profile.insuranceProvider.insurancePlan = insurancePlan.getText().toString().trim();
        }

        if (memberId.getText() != null) {
            profile.insuranceProvider.memberNumber = memberId.getText().toString().trim();
        }

        if (group.getText() != null) {
            profile.insuranceProvider.groupNumber = group.getText().toString().trim();
        }

        return profile;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE_EDIT;
    }
}
