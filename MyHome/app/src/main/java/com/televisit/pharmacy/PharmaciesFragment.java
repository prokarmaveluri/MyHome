package com.televisit.pharmacy;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmaciesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmaciesFragment extends Fragment {

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
        PharmaciesFragment fragment = new PharmaciesFragment();
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
        getActivity().setTitle(getString(R.string.pharmacy));
        View view = inflater.inflate(R.layout.fragment_pharmacies, container, false);

        pharmacyTabs = (TabLayout) view.findViewById(R.id.pharmacyTabs);
        pharmacyPager = (ViewPager) view.findViewById(R.id.pharmacyPager);
        setPager();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setPager() {
        pharmacyPager.setAdapter(new PharmacyPagerAdapter(getChildFragmentManager()));
        pharmacyTabs.setupWithViewPager(pharmacyPager);
    }
}
