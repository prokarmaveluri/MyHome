package com.prokarma.myhome.features.televisit.pharmacy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.pharmacy.PharmacyType;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;

import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmacyListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmacyListFragment extends Fragment implements TextView.OnEditorActionListener {

    private EditText pharmacySearch;
    private RecyclerView pharmacyList;
    private ProgressBar progressBar;
    public static final String PHARMACY_LIST_TAG = "pharmacy_list_tag";

    public PharmacyListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PharmacyListFragment.
     */
    public static PharmacyListFragment newInstance() {
        return new PharmacyListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.pharmacy));
        } else {
            getActivity().setTitle(getString(R.string.pharmacy));
        }

        View view = inflater.inflate(R.layout.fragment_pharmacy_list, container, false);

        NavigationActivity.eventBus.register(this);
        pharmacyList = (RecyclerView) view.findViewById(R.id.pharmacyList);
        progressBar = (ProgressBar) view.findViewById(R.id.search_progress);

        pharmacySearch = (EditText) view.findViewById(R.id.pharmacySearch);
        pharmacySearch.setOnEditorActionListener(this);
        searchCancelClickEvent();

        if (CommonUtil.hasLocationPermissionForApp(this.getContext())
                && CommonUtil.isGPSEnabled(this.getContext())
                && FadManager.getInstance().getLocation() != null
                && FadManager.getInstance().getLocation().getLat() != null
                && !FadManager.getInstance().getLocation().getLat().isEmpty()
                && FadManager.getInstance().getLocation().getLong() != null
                && !FadManager.getInstance().getLocation().getLong().isEmpty()) {

            Timber.d("Default search by user's current location = " + FadManager.getInstance().getLocation().toString());
            getPharmacies(
                    Float.valueOf(FadManager.getInstance().getLocation().getLat()),
                    Float.valueOf(FadManager.getInstance().getLocation().getLong()), 50,
                    AwsManager.getInstance().getPatient());

        } else if (ProfileManager.getProfile() != null
                && ProfileManager.getProfile().address != null
                && ProfileManager.getProfile().address.zipCode != null
                && !ProfileManager.getProfile().address.zipCode.isEmpty()) {

            pharmacySearch.setText(ProfileManager.getProfile().address.zipCode);
            getPharmaciesByZip(pharmacySearch.getText().toString().trim());

            Timber.d("Default search by user's profile address zipcode = " + pharmacySearch.getText().toString().trim());
        }

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtil.hideSoftKeyboard(getActivity());
    }

    private void searchCancelClickEvent() {
        pharmacySearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if ((int) event.getRawX() >= (pharmacySearch.getRight() -
                            pharmacySearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        pharmacySearch.setText("");

                        CommonUtil.hideSoftKeyboard(getActivity());

                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void getPharmaciesByZip(final String zipCode) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.no_network_msg));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        AwsManager.getInstance().getAWSDK().getConsumerManager().getPharmacies(
                AwsManager.getInstance().getConsumer(),
                PharmacyType.RETAIL,
                null,
                null,
                zipCode, new SDKValidatedCallback<List<Pharmacy>, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(List<Pharmacy> pharmacies, SDKError sdkError) {
                        if (sdkError == null) {

                            AwsManager.getInstance().setPharmacies(pharmacies);
                            setListAdapter(pharmacies);

                            SearchPharmacies object = new SearchPharmacies();
                            object.setPharmacies(pharmacies);
                            NavigationActivity.eventBus.post(object);
                        }

                        progressBar.setVisibility(View.GONE);

                        if (pharmacies == null || pharmacies.size() == 0) {
                            CommonUtil.showToast(getContext(), getString(R.string.no_pharmacies_found_for) + " " + zipCode);
                        }

                        if (null != getActivity()) {
                            CommonUtil.hideSoftKeyboard(getActivity());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void getPharmacies(@NonNull final float latitude,
                               @NonNull final float longitude,
                               @NonNull final int radius,
                               @NonNull final Consumer patient) {
        progressBar.setVisibility(View.VISIBLE);
        final boolean excludeMailOrder = true;

        AwsManager.getInstance().getAWSDK().getConsumerManager().getPharmacies(
                patient,
                latitude,
                longitude,
                radius,
                excludeMailOrder, new SDKValidatedCallback<List<Pharmacy>, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(List<Pharmacy> pharmacies, SDKError sdkError) {
                        progressBar.setVisibility(View.GONE);

                        if (sdkError == null) {

                            AwsManager.getInstance().setPharmacies(pharmacies);
                            setListAdapter(pharmacies);

                            SearchPharmacies object = new SearchPharmacies();
                            object.setPharmacies(pharmacies);
                            NavigationActivity.eventBus.post(object);
                        }

                        if (pharmacies == null || pharmacies.size() == 0) {
                            CommonUtil.showToast(getContext(), getString(R.string.no_pharmacies_found_for_current_location));
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (pharmacySearch.getText().toString().trim().length() == 5) {
                getPharmaciesByZip(pharmacySearch.getText().toString().trim());
            } else {
                CommonUtil.showToast(getContext(), getString(R.string.zip_invalid));
            }
            return true;
        }
        return false;
    }

    private void setListAdapter(List<Pharmacy> pharmacies) {
        if (null != getActivity() && isAdded() && pharmacies != null && pharmacies.size() > 0) {
            pharmacyList.setVisibility(View.VISIBLE);
            pharmacyList.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            pharmacyList.addItemDecoration(itemDecoration);
            pharmacyList.setAdapter(new PharmacyListAdapter(getContext(), pharmacies, new PharmacyListAdapter.IPharmacyClick() {
                @Override
                public void pharmacyClick(final Pharmacy pharmacy) {
                    if (isAdded()) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(PharmacyDetailsFragment.PHARMACY_KEY, pharmacy);
                        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_PHARMACY_DETAILS, bundle);
                    }
                }
            }));
        } else {
            pharmacyList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AwsManager.getInstance().setPharmacies(null);
    }

    public class SearchPharmacies {
        private List<Pharmacy> pharmacies;

        public List<Pharmacy> getPharmacies() {
            return pharmacies;
        }

        public void setPharmacies(List<Pharmacy> pharmacies) {
            this.pharmacies = pharmacies;
        }
    }
}
