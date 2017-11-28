package com.prokarma.myhome.features.fad;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.FragmentListDialogBinding;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;

import java.util.ArrayList;

import timber.log.Timber;

/*
 * Fragment dialog to display providers list for the cluster.
 *
 * Created by cmajji on 05/30/17.
 */
public class ProviderListDialog extends DialogFragment implements ProvidersAdapter.IProviderClick {

    public static final String PROVIDER_LIST_DIALOG_TAG = "provider_list_dilaog";
    private boolean recent = false;
    private boolean fromHome = false;
    private boolean isMapCluster = false;
    private FragmentListDialogBinding binding;
    private ArrayList<ProviderDetailsResponse> providerList = new ArrayList<>();

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
        setStyle(STYLE_NO_FRAME, R.style.DialogTheame);

        if (getArguments() != null) {
            ArrayList<ProviderDetailsResponse> list = getArguments().getParcelableArrayList("PROVIDER_LIST");
            providerList.addAll(list);
            recent = getArguments().getBoolean("PROVIDER_RECENT");
            fromHome = getArguments().getBoolean("PROVIDER_RECENT_HOME");
            isMapCluster = getArguments().getBoolean("IS_MAP_CLUSTER");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_dialog, container, false);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        DialogListFragment fragment = new DialogListFragment();
        fragment.setListener(this);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("PROVIDER_LIST", providerList);
        bundle.putString("PROVIDER_MSG", "");
        bundle.putBoolean("PROVIDER_PAGINATION", false);
        bundle.putBoolean("PROVIDER_RECENT", recent);
        fragment.setArguments(bundle);
        transaction.replace(binding.listFrame.getId(), fragment).commit();

        if (fromHome) {
            binding.findCare.setVisibility(View.VISIBLE);
        } else {
            binding.findCare.setVisibility(View.GONE);
        }

        binding.title.setText(R.string.recent_providers);
        if (isMapCluster){
            binding.title.setText(R.string.provider_list);
        }

        binding.setHandlers(new DialogClick());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void setResults(ProviderDetailsResponse provider) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        try {
            bundle.putParcelable("PROVIDER", provider);
            intent.putExtras(bundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
        } catch (NullPointerException ex) {
            Timber.w(ex);
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
                case R.id.find_care:
                    setResults();
                    dismiss();
                    break;
            }
        }
    }

    private void setResults() {
        try {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_FIRST_USER, null);
        } catch (NullPointerException ex) {
            Timber.w(ex);
        }
    }
}
