package com.televisit.profile;

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

import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.DependentUpdate;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsUpdateDependent;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyCareProfileDependentFragment extends BaseFragment implements AwsUpdateDependent {
    public static final String MY_PROFILE_TAG = "my_profile_tag";

    View profileView;
    TextInputLayout firstNameLayout;
    TextInputEditText firstName;
    TextInputLayout lastNameLayout;
    TextInputEditText lastName;
    Spinner gender;
    TextInputLayout dateOfBirthLayout;
    TextInputEditText dateOfBirth;
    TextView genderLabel;

    ProgressBar progress;

    public static MyCareProfileDependentFragment newInstance() {
        return new MyCareProfileDependentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.dependent_profile, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.my_personal_information));

        firstNameLayout = (TextInputLayout) profileView.findViewById(R.id.first_name_layout);
        firstName = (TextInputEditText) profileView.findViewById(R.id.first_name);
        lastNameLayout = (TextInputLayout) profileView.findViewById(R.id.last_name_layout);
        lastName = (TextInputEditText) profileView.findViewById(R.id.last_name);
        genderLabel = (TextView) profileView.findViewById(R.id.gender_label);
        gender = (Spinner) profileView.findViewById(R.id.gender);
        dateOfBirthLayout = (TextInputLayout) profileView.findViewById(R.id.dob_layout);
        dateOfBirth = (TextInputEditText) profileView.findViewById(R.id.dob);
        progress = (ProgressBar) profileView.findViewById(R.id.profile_edit_progress);

        dateOfBirth.addTextChangedListener(new PhoneAndDOBFormatter(dateOfBirth, PhoneAndDOBFormatter.FormatterType.DOB));

        gender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        updateDependentViews(AwsManager.getInstance().getPatient());

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
                if (isValidConsumer()) {
                    sendUpdatedDependent();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUpdatedDependent() {
        progress.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getNewAddress();

        DependentUpdate update = AwsManager.getInstance().getAWSDK().getConsumerManager().getNewDependentUpdate(AwsManager.getInstance().getPatient());

        //Comment this back in once login works
        update.setFirstName(firstName.getText().toString().trim());
        update.setLastName(lastName.getText().toString().trim());
        update.setGender(gender.getSelectedItem().toString().trim());
        update.setDob(DateUtil.convertReadabletoDob(dateOfBirth.getText().toString().trim()));

        AwsNetworkManager.getInstance().updateDependent(update, this);
    }

    /**
     * Updates the view on screen to have the proper consumer values autopopulated
     *
     * @param consumer the consumer that we're using to autopopulate the EditTexts
     */
    private void updateDependentViews(Consumer consumer) {
        if (consumer.getLastName() != null) {
            lastName.setText(consumer.getLastName());
        }

        if (consumer.getGender() != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (consumer.getGender().equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
        }

        if (consumer.getDob() != null) {
            dateOfBirth.setText(DateUtil.convertDobtoReadable(consumer.getDob()));
        }
    }

    private boolean isValidConsumer() {
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
                !dateOfBirth.getText().toString().trim().isEmpty()) {
            isValid = false;
            dateOfBirthLayout.setError(getString(R.string.date_of_birth_invalid));
        } else {
            dateOfBirthLayout.setError(null);
        }

        return isValid;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PROFILE_DEPENDENT;
    }

    @Override
    public void updateDependentComplete(Consumer consumer) {
        AwsManager.getInstance().setPatient(consumer);
        AwsManager.getInstance().setHasConsumer(false); //force a refresh of the dashboard

        Toast.makeText(getActivity(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void updateDependentFailed(String errorMessage) {
        Timber.e(errorMessage);
        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }
}
