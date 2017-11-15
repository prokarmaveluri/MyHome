package com.televisit.cost;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdksample.SampleApplication;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.televisit.SDKUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareVisitCostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareVisitCostFragment extends BaseFragment {

    public static final String MY_CARE_COST_TAG = "my_care_cost_tag";

    private Button applyButton;
    private EditText couponText;
    private ProgressBar progressBar;
    private TextView costInfo;

    public MyCareVisitCostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareServicesFragment.
     */
    public static MyCareVisitCostFragment newInstance() {
        MyCareVisitCostFragment fragment = new MyCareVisitCostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_care_cost, container, false);

        applyButton = (Button) view.findViewById(R.id.apply_button);
        costInfo = (TextView) view.findViewById(R.id.costInfo);
        couponText = (EditText) view.findViewById(R.id.coupon_code_edit_text);
        progressBar = (ProgressBar) view.findViewById(R.id.cost_progress);
        ((NavigationActivity) getActivity()).setActionBarTitle("Payment");

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponText.getText().toString().length() > 0
                        && SDKUtils.getInstance().getVisit() != null)
                    applyCoupon(couponText.getText().toString());
            }
        });
        costInfo.setText(getString(R.string.visit_cost_desc) +
                SDKUtils.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_COST;
    }

    private void applyCoupon(String couponCode) {
        if (SDKUtils.getInstance().getVisit() == null)
            return;
        try {
            progressBar.setVisibility(View.VISIBLE);
            SampleApplication.getInstance().getAWSDK().getVisitManager().applyCouponCode(
                    SDKUtils.getInstance().getVisit(),
                    couponCode,
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null && isAdded() && getActivity() != null) {
                                if (getActivity() != null) {
                                    ((NavigationActivity) getActivity()).onBackPressed();

                                    ((NavigationActivity) getActivity()).loadFragment(
                                            Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );
        } catch (IllegalArgumentException ex) {
        }
    }
}
