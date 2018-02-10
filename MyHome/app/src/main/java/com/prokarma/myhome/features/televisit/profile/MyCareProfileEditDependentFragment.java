package com.prokarma.myhome.features.televisit.profile;

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
import com.americanwell.sdk.entity.consumer.DependentUpdate;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.AwsNetworkManager;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdateDependent;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.prokarma.myhome.utils.TealiumUtil;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyCareProfileEditDependentFragment extends BaseFragment implements AwsUpdateDependent {
    public static final String MY_PROFILE_TAG = "my_profile_tag";

    private View profileView;
    private TextInputLayout firstNameLayout;
    private TextInputEditText firstName;
    private TextInputLayout lastNameLayout;
    private TextInputEditText lastName;
    private Spinner gender;
    private TextInputLayout dateOfBirthLayout;
    private TextInputEditText dateOfBirth;
    private TextView genderLabel;
    private TextView genderValue;

    private ProgressBar progress;

    public static MyCareProfileEditDependentFragment newInstance() {
        return new MyCareProfileEditDependentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.profile_edit_dependent, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.my_personal_information));

        firstNameLayout = (TextInputLayout) profileView.findViewById(R.id.first_name_layout);
        firstName = (TextInputEditText) profileView.findViewById(R.id.first_name);
        lastNameLayout = (TextInputLayout) profileView.findViewById(R.id.last_name_layout);
        lastName = (TextInputEditText) profileView.findViewById(R.id.last_name);
        genderLabel = (TextView) profileView.findViewById(R.id.gender_label);
        genderValue = (TextView) profileView.findViewById(R.id.gender_value);

        gender = (Spinner) profileView.findViewById(R.id.gender);
        dateOfBirthLayout = (TextInputLayout) profileView.findViewById(R.id.dob_layout);
        dateOfBirth = (TextInputEditText) profileView.findViewById(R.id.dob);
        progress = (ProgressBar) profileView.findViewById(R.id.profile_edit_progress);

        updateDependentViews(AwsManager.getInstance().getPatient());

        if (AwsManager.getInstance().isDependent()) {

            //For Dependents: as per Zeplin 'Save' button is not shown and also iOS is not displaying it. QA requested to hide the button
            setHasOptionsMenu(false);

            disableEditing(firstName);
            disableEditing(lastName);
            disableEditing(dateOfBirth);

            genderValue.setVisibility(View.VISIBLE);
            gender.setVisibility(View.GONE);

            /*gender.setFocusable(false);
            gender.setClickable(false);
            gender.setOnTouchListener(null);
            gender.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);*/

        } else {
            setHasOptionsMenu(true);

            dateOfBirth.addTextChangedListener(new PhoneAndDOBFormatter(dateOfBirth, PhoneAndDOBFormatter.FormatterType.DOB));

            gender.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CommonUtil.hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        return profileView;
    }

    private void disableEditing(TextInputEditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PROFILE_DEPENDENT_SCREEN, null);
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

        if (consumer.getFirstName() != null) {
            firstName.setText(consumer.getFirstName());
        }
        if (consumer.getLastName() != null) {
            lastName.setText(consumer.getLastName());
        }

        if (consumer.getGender() != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (consumer.getGender().equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {

                    genderValue.setText(gender.getAdapter().getItem(i).toString());
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
            genderValue.setText("");
        }

        if (consumer.getDob() != null) {
            dateOfBirth.setText(DateUtil.convertDobtoReadable(consumer.getDob()));
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

        if (AwsManager.getInstance().isDependent()) {
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.profile_saved_dependent));
        } else {
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.profile_saved));
        }
        getActivity().onBackPressed();
    }

    @Override
    public void updateDependentFailed(String errorMessage) {
        Timber.e(errorMessage);
        CommonUtil.showToast(getActivity(), getActivity().getString(R.string.something_went_wrong));
        getActivity().onBackPressed();
    }
}
