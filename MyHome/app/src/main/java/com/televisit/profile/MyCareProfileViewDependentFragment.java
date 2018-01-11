package com.televisit.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.consumer.Consumer;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.TealiumUtil;
import com.televisit.AwsManager;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyCareProfileViewDependentFragment extends BaseFragment {
    public static final String MY_PROFILE_TAG = "my_profile_tag";

    private TextView name;
    private TextView gender;
    private TextView dob;
    private LinearLayout viewProfile;
    private TextView profileUnavailable;

    public static MyCareProfileViewDependentFragment newInstance() {
        return new MyCareProfileViewDependentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.profile_view_dependent, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.my_personal_information));

        name = (TextView) profileView.findViewById(R.id.name);
        gender = (TextView) profileView.findViewById(R.id.gender);
        dob = (TextView) profileView.findViewById(R.id.dob);
        viewProfile = (LinearLayout) profileView.findViewById(R.id.viewProfile);
        profileUnavailable = (TextView) profileView.findViewById(R.id.profile_unavailable);

        updateDependentViews(AwsManager.getInstance().getPatient());

        setHasOptionsMenu(false);

        return profileView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PROFILE_DEPENDENT;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PROFILE_DEPENDENT_SCREEN, null);
    }

    private void updateDependentViews(Consumer consumer) {

        if (consumer == null) {
            viewProfile.setVisibility(View.GONE);
            profileUnavailable.setVisibility(View.VISIBLE);
        } else {
            viewProfile.setVisibility(View.VISIBLE);
            profileUnavailable.setVisibility(View.GONE);

            StringBuilder sbName = new StringBuilder();
            if (consumer.getFirstName() != null) {
                sbName.append(consumer.getFirstName());
            }
            if (consumer.getLastName() != null) {
                if (!sbName.toString().isEmpty()) {
                    sbName.append(" ");
                }
                sbName.append(consumer.getLastName());
            }
            name.setText(sbName.toString());

            if (consumer.getGender() != null) {
                gender.setText(consumer.getGender());
            }

            if (consumer.getDob() != null) {
                dob.setText(DateUtil.convertDobtoReadable(consumer.getDob()));
            }
        }
    }
}
