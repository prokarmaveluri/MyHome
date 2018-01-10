package com.televisit.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
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

import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.EnviHandler;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.prokarma.myhome.utils.TealiumUtil;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsUpdateConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyCareProfileFragment extends BaseFragment implements AwsUpdateConsumer {
    public static final String MY_PROFILE_TAG = "my_profile_tag";

    View profileView;
    TextView welcomeText;
    TextInputLayout firstNameLayout;
    TextInputEditText firstName;
    TextInputLayout lastNameLayout;
    TextInputEditText lastName;
    Spinner gender;
    TextInputLayout dateOfBirthLayout;
    TextInputEditText dateOfBirth;
    TextInputEditText address;
    TextInputEditText address2;
    TextInputLayout cityLayout;
    TextInputEditText city;
    Spinner state;
    TextInputLayout zipLayout;
    TextInputEditText zip;
    TextInputLayout phoneLayout;
    TextInputEditText phone;
    TextInputLayout emailLayout;
    TextView email;
    TextView genderLabel;

    ProgressBar progress;

    public static MyCareProfileFragment newInstance() {
        return new MyCareProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.my_profile, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.my_personal_information));

        welcomeText = (TextView) profileView.findViewById(R.id.welcome_text);
        firstNameLayout = (TextInputLayout) profileView.findViewById(R.id.first_name_layout);
        firstName = (TextInputEditText) profileView.findViewById(R.id.first_name);
        lastNameLayout = (TextInputLayout) profileView.findViewById(R.id.last_name_layout);
        lastName = (TextInputEditText) profileView.findViewById(R.id.last_name);
        genderLabel = (TextView) profileView.findViewById(R.id.gender_label);
        gender = (Spinner) profileView.findViewById(R.id.gender);
        dateOfBirthLayout = (TextInputLayout) profileView.findViewById(R.id.dob_layout);
        dateOfBirth = (TextInputEditText) profileView.findViewById(R.id.dob);
        address = (TextInputEditText) profileView.findViewById(R.id.address);
        address2 = (TextInputEditText) profileView.findViewById(R.id.address2);
        cityLayout = (TextInputLayout) profileView.findViewById(R.id.city_layout);
        city = (TextInputEditText) profileView.findViewById(R.id.city);
        state = (Spinner) profileView.findViewById(R.id.state);
        zipLayout = (TextInputLayout) profileView.findViewById(R.id.zip_layout);
        zip = (TextInputEditText) profileView.findViewById(R.id.zip);
        phoneLayout = (TextInputLayout) profileView.findViewById(R.id.phone_layout);
        phone = (TextInputEditText) profileView.findViewById(R.id.phone);
        email = (TextView) profileView.findViewById(R.id.email);
        progress = (ProgressBar) profileView.findViewById(R.id.profile_edit_progress);

        dateOfBirth.addTextChangedListener(new PhoneAndDOBFormatter(dateOfBirth, PhoneAndDOBFormatter.FormatterType.DOB));

        phone.addTextChangedListener(new PhoneAndDOBFormatter(phone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER_DOTS));

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
            getProfileInfo(false);
        } else {
            Timber.i("Already have a Profile Singleton. Updating the view...");
            updateConsumerViews(ProfileManager.getProfile()); //, AwsManager.getInstance().getPatient());
        }

        setHasOptionsMenu(true);
        return profileView;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PROFILE_SCREEN, null);
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
                if (isValidConsumer()) {
                    sendUpdatedConsumer();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CommonUtil.hideSoftKeyboard(this.getActivity());
    }

    private void sendUpdatedConsumer() {
        progress.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getNewAddress();

        ConsumerUpdate update = AwsManager.getInstance().getAWSDK().getConsumerManager().getNewConsumerUpdate(AwsManager.getInstance().getPatient());

        //TODO change this once login actually works
        if (EnviHandler.isAttemptMutualAuth()) {
            //Not sure what to put here. We don't have users password....
        } else {
            update.setPassword(EnviHandler.getAmwellPassword());
        }

        //Comment this back in once login works
        update.setEmail(email.getText().toString().trim());
        update.setFirstName(firstName.getText().toString().trim());
        update.setLastName(lastName.getText().toString().trim());
        update.setGender(gender.getSelectedItem().toString().trim());
        update.setPhone(CommonUtil.stripPhoneNumber(phone.getText().toString().trim()));
        update.setDob(DateUtil.convertReadabletoDob(dateOfBirth.getText().toString().trim()));

        com.americanwell.sdk.entity.Address userAddress = AwsManager.getInstance().getAWSDK().getNewAddress();

        if (address.getText() != null) {
            userAddress.setAddress1(address.getText().toString());
        }

        if (address2.getText() != null) {
            userAddress.setAddress2(address2.getText().toString());
        }

        if (city.getText() != null) {
            userAddress.setCity(city.getText().toString());
        }

        if (state.getSelectedItemPosition() != 0) {
            userAddress.setState(AwsManager.getInstance().getState(state.getSelectedItem().toString()));
        }

        if (zip.getText() != null) {
            userAddress.setZipCode(zip.getText().toString());
        }

        update.setAddress(userAddress);

        AwsNetworkManager.getInstance().updateConsumer(update, this);
    }

    /**
     * Updates the view on screen to have the proper consumer values autopopulated
     *
     * @param profile the profile that we're using to autopopulate the EditTexts
     */
    private void updateConsumerViews(Profile profile) {

        if (profile == null) {
            return;
        }

        //Displaying data pulled from CIAM, and that is profile object info.

        //28877: as per Jak's comment made on DEC-19 9:38PM on bug id 28877
        /*
        Here is the expected behavior:
        - My Home Profile update is only CIAM update (Nothing related to MCN)
        - MCN Personal Info will be pulled from CIAM profile and displayed in the screen.
        - If User updates anything in MCN Personal Info - We will update that information with both CIAM and AmWell.
         */

        if (profile.firstName != null) {
            welcomeText.setText(getString(R.string.mcn_welcome_text_variable, profile.firstName));
            firstName.setText(profile.firstName);
        } else {
            welcomeText.setText(getString(R.string.mcn_welcome_text));
        }

        if (profile.lastName != null) {
            lastName.setText(profile.lastName);
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
            zip.setText(profile.address.zipCode.trim());
        }

        if (profile.phoneNumber != null) {
            //phone.setText(profile.phoneNumber.replaceAll("\\.", "-"));
            phone.setText(CommonUtil.constructPhoneNumberDots(profile.phoneNumber));
        }

        if (profile.email != null) {
            email.setText(profile.email);
        }
    }

    private boolean isValidConsumer() {
        boolean isValid = true;

        if (firstName.getText().toString().trim().isEmpty()) {
            isValid = false;
            firstNameLayout.setError(getString(R.string.first_name_required));
        } else {
            firstNameLayout.setError(null);
        }

        if (lastName.getText().toString().trim().isEmpty()) {
            isValid = false;
            lastNameLayout.setError(getString(R.string.last_name_required));
        } else {
            lastNameLayout.setError(null);
        }

        if (dateOfBirth.getText().toString().trim().isEmpty() || !DateUtil.isValidDateOfBirth(dateOfBirth.getText().toString().trim(), false)) {
            isValid = false;
            dateOfBirthLayout.setError(getString(R.string.date_of_birth_invalid));
        } else {
            dateOfBirthLayout.setError(null);
        }

        if (gender.getSelectedItemPosition() == 0) {
            genderLabel.setText(getString(R.string.gender_required));
            genderLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            genderLabel.setText(getString(R.string.gender));
            genderLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.text_darker));
        }

        if (state.getSelectedItemPosition() == 0) {
            isValid = false;
            cityLayout.setError(getString(R.string.state_required));
        } else {
            cityLayout.setError(null);
        }

        return isValid;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PROFILE;
    }

    @Override
    public void updateConsumerComplete(Consumer consumer) {

        //added following condition to avoid error: java.lang.IllegalStateException: Fragment MyCareProfileFragment{501feb4} not attached to Activity
        if (!isAdded()) {
            return;
        }

        AwsManager.getInstance().setPatient(consumer);
        AwsManager.getInstance().setConsumer(consumer);

        ProfileManager.updateProfileFromMcnData(AuthManager.getInstance().getBearerToken(), consumer);

        //refetch the saved data
        getProfileInfo(true);
    }

    @Override
    public void updateConsumerFailed(String errorMessage) {
        Timber.e(errorMessage);
        CommonUtil.showToast(getActivity(), getString(R.string.something_went_wrong));

        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private void getProfileInfo(final boolean fromSave) {

        NetworkManager.getInstance().getProfile(AuthManager.getInstance().getBearerToken()).enqueue(new Callback<ProfileGraphqlResponse>() {
            @Override
            public void onResponse(Call<ProfileGraphqlResponse> call, Response<ProfileGraphqlResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        Timber.d("ProfileFrag. getProfile. Successful Response\n" + response);
                        ProfileManager.setProfile(response.body().getData().getUser());
                    } catch (NullPointerException ex) {
                        Timber.e(getString(R.string.db_res_notsuccess) + "\n" + response);
                    }
                    if (!fromSave) {
                        updateConsumerViews(ProfileManager.getProfile());
                    }
                } else {
                    Timber.e("ProfileFrag. getProfile. Response, but not successful?\n" + response);
                }
                if (fromSave) {
                    navigateBack();
                }
            }

            @Override
            public void onFailure(Call<ProfileGraphqlResponse> call, Throwable t) {
                Timber.e("ProfileFrag. getProfile. Something failed! :/");
                Timber.e("Throwable = " + t);
                navigateBack();
            }
        });
    }

    private void navigateBack() {
        if (isAdded()) {
            if (AwsManager.getInstance().isDependent()) {
                CommonUtil.showToast(getActivity(), getString(R.string.profile_saved_dependent));
            } else {
                CommonUtil.showToast(getActivity(), getString(R.string.profile_saved));
            }
        }
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
