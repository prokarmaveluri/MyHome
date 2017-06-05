package com.dignityhealth.myhome.features.fad;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.FragmentListDialogBinding;

import java.util.ArrayList;

/*
 * Fragment dialog to display providers list for the cluster.
 *
 * Created by cmajji on 05/30/17.
 */
public class ProviderListDialog extends DialogFragment implements ProvidersAdapter.IProviderClick {

    private boolean recent = false;
    private FragmentListDialogBinding binding;
    private ArrayList<Provider> providerList = new ArrayList<>();

    public ProviderListDialog() {
        // Required empty public constructor
    }

    public static ProviderListDialog newInstance() {
        ProviderListDialog fragment = new ProviderListDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light);

        if (getArguments() != null) {
            providerList = getArguments().getParcelableArrayList("PROVIDER_LIST");
            recent = getArguments().getBoolean("PROVIDER_RECENT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_dialog, container, false);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = ProviderListFragment.newInstance(this);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("PROVIDER_LIST", providerList);
        bundle.putString("PROVIDER_MSG", "");
        bundle.putBoolean("PROVIDER_PAGINATION", false);
        fragment.setArguments(bundle);
        transaction.replace(binding.listFrame.getId(), fragment).commit();

        binding.setHandlers(new DialogClick());
        return binding.getRoot();
    }

    private void setResults(Provider provider) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        try {
            bundle.putParcelable("PROVIDER", provider);
            intent.putExtras(bundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
        } catch (NullPointerException ex) {
        }
    }

    @Override
    public void providerClick(int position) {
        setResults(providerList.get(position));
    }

    public class DialogClick {
        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.dialog_close:
                    dismiss();
                    break;
            }
        }
    }
}
