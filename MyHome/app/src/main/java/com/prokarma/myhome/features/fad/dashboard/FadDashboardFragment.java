package com.prokarma.myhome.features.fad.dashboard;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
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
public class FadDashboardFragment extends Fragment implements FavProvidersAdapter.IProviderClick {

    private FragmentFadDashboardBinding binder;
    public static final String FAD_DASHBOARD_TAG = "fad_dashboard_tag";

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
        return binder.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        binder.setHandlers(new FadDashboardClick());
        binder.mySavedDocs.setLayoutManager(new LinearLayoutManager(getActivity()));
        binder.mySavedDocs.setAdapter(new FavProvidersAdapter(ProfileManager.getFavoriteProviders(),
                getActivity(), this));
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
            Bundle bundle = new Bundle();
            bundle.putString(ProviderDetailsFragment.PROVIDER_ID_KEY, ProfileManager.getFavoriteProviders().get(position).getNpi());
            ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PROVIDER_DETAILS, bundle);
        }
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
                        MyFavoritesDialog dialog = new MyFavoritesDialog();
                        dialog.show(getChildFragmentManager(), "Favorites Dialog");
                    }
                    break;
            }
        }
    }
}
