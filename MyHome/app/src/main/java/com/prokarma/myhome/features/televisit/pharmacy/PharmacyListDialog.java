package com.prokarma.myhome.features.televisit.pharmacy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.FragmentListDialogPharmacyBinding;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Fragment dialog to display pharmacies list for the cluster.
 * <p>
 * Created by veluri on 12/19/17.
 */

public class PharmacyListDialog extends DialogFragment implements PharmacyListAdapter.IPharmacyClick {

    public static final String PHARMACY_LIST_DIALOG_TAG = "pharmacy_list_dilaog";
    private boolean isMapCluster = false;
    private FragmentListDialogPharmacyBinding binding;
    private ArrayList<Pharmacy> list = new ArrayList<>();

    public PharmacyListDialog() {
        // Required empty public constructor
    }

    public static PharmacyListDialog newInstance() {
        return new PharmacyListDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogTheame);

        if (getArguments() != null) {
            ArrayList<Pharmacy> listPharmacies = getArguments().getParcelableArrayList("PHARMACY_LIST");
            list.addAll(listPharmacies);
            isMapCluster = getArguments().getBoolean("IS_MAP_CLUSTER");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_dialog_pharmacy, container, false);

        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            PharmacyDialogListFrament fragment = new PharmacyDialogListFrament();
            fragment.setListener(this);

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("PHARMACY_LIST", list);

            fragment.setArguments(bundle);
            transaction.replace(binding.listFrame.getId(), fragment).commit();

        } catch (Exception ex) {
            Timber.e(ex);
            ex.printStackTrace();
        }

        binding.setHandlers(new PharmacyListDialog.DialogClick());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void pharmacyClick(Pharmacy pharmacy) {
        try {
            dismiss();

            if (getParentFragment() instanceof PharmacyMapFragment) {
                ((PharmacyMapFragment) getParentFragment()).launchPharmacyDetails(pharmacy);
            }
        } catch (NullPointerException ex) {
            Timber.w(ex);
        }
    }

    public class DialogClick {
        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.close:
                    dismiss();
                    break;
                case R.id.dialog_close:
                    dismiss();
                    break;
            }
        }
    }
}
