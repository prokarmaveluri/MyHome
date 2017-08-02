package com.prokarma.myhome.features.fad.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.databinding.FragmentFadDashboardBinding;
import com.prokarma.myhome.features.fad.details.ProviderDetailsFragment;
import com.prokarma.myhome.features.preferences.MyFavoritesDialog;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.Constants;

import timber.log.Timber;

/**
 * Fad dashboard
 */
public class FadDashboardFragment extends BaseFragment implements FavProvidersAdapter.IProviderClick {

    private FragmentFadDashboardBinding binder;
    public static final String FAD_DASHBOARD_TAG = "fad_dashboard_tag";
    private static final int RECENT_PROVIDERS = 200;

    public FadDashboardFragment() {
        // Required empty public constructor
    }

    public static FadDashboardFragment newInstance() {
        FadDashboardFragment fragment = new FadDashboardFragment();
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
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.fad_title));
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_fad_dashboard, container, false);
        binder.mySavedDocs.setLayoutManager(new LinearLayoutManager(getActivity()));
        binder.mySavedDocs.setAdapter(new FavProvidersAdapter(ProfileManager.getFavoriteProviders(),
                getActivity(), this));
        binder.setHandlers(new FadDashboardClick());
        return binder.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void providerClick(int position) {
        Timber.i("fav dashboard click " + position);
        if (ProfileManager.getFavoriteProviders() != null) {
            providerDetails(ProfileManager.getFavoriteProviders().get(position).getNpi());
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD_DASH_BOARD;
    }

    public class FadDashboardClick {
        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.fadDashBoardFindcare:
                    if (getActivity() != null) {
                        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.FAD, null);
                    }
                    break;
                case R.id.fadDashBoardViewall:
                    if (null != ProfileManager.getFavoriteProviders() &&
                            ProfileManager.getFavoriteProviders().size() > 0) {
                        startFavProvidersDialog();
                    }
                    break;
            }
        }
    }

    private void startFavProvidersDialog() {
        MyFavoritesDialog dialog = new MyFavoritesDialog();
        dialog.setTargetFragment(this, RECENT_PROVIDERS);
        dialog.show(getChildFragmentManager(), "Favorites Dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECENT_PROVIDERS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras() != null) {
                    int position = data.getExtras().getInt("FAV_POSITION");
                    String npi = data.getExtras().getString("FAV_NPI");
                    providerDetails(npi);
                }
            }
        }
    }

    private void providerDetails(String npi) {
        Bundle bundle = new Bundle();
        bundle.putString(ProviderDetailsFragment.PROVIDER_ID_KEY, npi);
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
    }
}
