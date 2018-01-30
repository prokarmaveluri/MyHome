package com.televisit.pharmacy;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmaciesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmaciesFragment extends BaseFragment {

    private TabLayout pharmacyTabs;
    private ViewPager pharmacyPager;
    public static final String PHARMACIES_TAG = "pharmacies_tag";

    public PharmaciesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PharmacyListFragment.
     */
    public static PharmaciesFragment newInstance() {
        return new PharmaciesFragment();
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
            ((NavigationActivity) getActivity()).setActionBarLineVisibility(false);
        } else {
            getActivity().setTitle(getString(R.string.pharmacy));
        }

        View view = inflater.inflate(R.layout.fragment_pharmacies, container, false);

        pharmacyTabs = (TabLayout) view.findViewById(R.id.pharmacyTabs);
        pharmacyPager = (ViewPager) view.findViewById(R.id.pharmacyPager);
        setPager();

        for (int i = 0; i < pharmacyTabs.getTabCount(); i++) {
            if (pharmacyTabs.getTabAt(i).isSelected()) {
                pharmacyTabs.getTabAt(i).setContentDescription("Tab, " + pharmacyTabs.getTabAt(i).getText());
            }
            else {
                pharmacyTabs.getTabAt(i).setContentDescription("Not selected, Tab, " + pharmacyTabs.getTabAt(i).getText());
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PHARMACY_SCREEN, null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setPager() {
        pharmacyPager.setAdapter(new PharmacyPagerAdapter(getChildFragmentManager()));
        pharmacyTabs.setupWithViewPager(pharmacyPager);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_PHARMACY;
    }
}
