package com.televisit.pharmacy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link PharmacyDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PharmacyDetailsFragment extends Fragment {

    public static final String PHARMACY_DETAILS_TAG = "pharmacy_details_tag";

    public PharmacyDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PharmacyListFragment.
     */
    public static PharmacyDetailsFragment newInstance() {
        PharmacyDetailsFragment fragment = new PharmacyDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_pharmacy_details, container, false);

        return view;
    }
}
