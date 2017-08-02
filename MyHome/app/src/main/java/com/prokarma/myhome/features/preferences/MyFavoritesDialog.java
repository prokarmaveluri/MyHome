package com.prokarma.myhome.features.preferences;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.FragmentFavListDialogBinding;
import com.prokarma.myhome.features.fad.dashboard.FavProvidersAdapter;
import com.prokarma.myhome.features.profile.ProfileManager;

import timber.log.Timber;

/*
 * Fragment dialog to display providers list for the cluster.
 *
 * Created by cmajji on 05/30/17.
 */
public class MyFavoritesDialog extends DialogFragment implements FavProvidersAdapter.IProviderClick {

    private FragmentFavListDialogBinding binding;

    public MyFavoritesDialog() {
        // Required empty public constructor
    }

    public static MyFavoritesDialog newInstance() {
        MyFavoritesDialog fragment = new MyFavoritesDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogTheame);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav_list_dialog, container, false);

        binding.favList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.favList.setAdapter(new FavProvidersAdapter(ProfileManager.getFavoriteProviders(),
                getActivity(), this));
        binding.setHandlers(new FavDialogClick());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void setResults(int position, String npi) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        try {
            bundle.putInt("FAV_POSITION", position);
            bundle.putString("FAV_NPI", npi);
            intent.putExtras(bundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
        } catch (NullPointerException ex) {
        }
    }

    @Override
    public void providerClick(int position) {
        Timber.i("fav dialog click " + position);
        setResults(position, ProfileManager.getFavoriteProviders().get(position).getNpi());
    }

    public class FavDialogClick {
        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.dialog_close:
                    dismiss();
                    break;
            }
        }
    }
}
