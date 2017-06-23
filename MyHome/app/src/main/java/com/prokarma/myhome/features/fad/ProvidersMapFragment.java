package com.prokarma.myhome.features.fad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;

import java.util.ArrayList;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for Find a doctor, display list of doctors with search feature.
 */

public class ProvidersMapFragment extends Fragment implements
        ProvidersAdapter.IProviderClick {

    private LocationResponse location = null;
    private ArrayList<Provider> providerList = new ArrayList<>();

    public static final String FAD_TAG = "fad_map";

    public static ProvidersMapFragment newInstance() {
        return new ProvidersMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerList = getArguments().getParcelableArrayList("PROVIDER_LIST");
            location = FadManager.getInstance().getCurrentLocation();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_providers_map, container, false);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = MapViewFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("PROVIDER_LIST", providerList);
        fragment.setArguments(bundle);
        transaction.replace(R.id.mapFrame, fragment).commit();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void providerClick(int position) {

    }
}
