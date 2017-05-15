package com.dignityhealth.myhome.features.fad;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.app.NavigationActivity;
import com.dignityhealth.myhome.databinding.FragmentFadBinding;
import com.dignityhealth.myhome.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by cmajji on 4/26/17.
 * <p>
 * Fragment for Find a doctor, display list of doctors with search feature.
 */

public class FadFragment extends BaseFragment implements FadInteractor.View,
        TextView.OnEditorActionListener {

    private FragmentFadBinding binding;
    private ProvidersAdapter adapter;
    private FadInteractor.Presenter presenter;
    private List<ProvidersResponse.Provider> providerList = new ArrayList<>();

    public static final String FAD_TAG = "fad_tag";

    public static FadFragment newInstance() {
        return new FadFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fad, container, false);

        adapter = new ProvidersAdapter(providerList, getActivity());
        binding.providersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.providersList.setAdapter(adapter);

        ((NavigationActivity) getActivity()).setActionBarTitle("Find a Doctor");
        presenter = new FadPresenter(this, getActivity());

        binding.searchDoctor.setOnEditorActionListener(this);
        return binding.getRoot();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.FAD;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
        showViews(true);
    }

    @Override
    public void setPresenter(FadInteractor.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void showViews(boolean show) {

    }

    @Override
    public void showProgress(boolean inProgress) {
        if (inProgress) {
            binding.fadProgress.setVisibility(View.VISIBLE);
        } else {
            binding.fadProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void showErrorMessage(String message) {
        showErrorMessage(true, message);
    }

    @Override
    public void updateLocationSuggestions() {

    }

    @Override
    public void updateProviderList(List<ProvidersResponse.Provider> providers) {
        providerList.clear();
        providerList.addAll(providers);
        adapter.notifyDataSetChanged();
        showErrorMessage(false, "");
    }

    @Override
    public void providersListError() {

    }

    private void showViews() {

    }

    private void showErrorMessage(boolean show, String message) {

        if (show) {
            binding.providersList.setVisibility(View.GONE);
            binding.message.setVisibility(View.VISIBLE);
            binding.message.setText(message);
        } else {
            binding.providersList.setVisibility(View.VISIBLE);
            binding.message.setVisibility(View.GONE);
        }
    }

    /**
     * IME action listener for the search
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            if (binding.searchDoctor.getText().length() > 0) {
                showProgress(true);

                View view = getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                presenter.getProviderList(binding.searchDoctor.getText().toString(),
                        FadManager.getInstance().getLocation().getLat(),
                        FadManager.getInstance().getLocation().getLong(),
                        FadManager.getInstance().getLocation().getDisplayName(),
                        FadManager.getInstance().getLocation().getZipCode());
            } else {
                Toast.makeText(getActivity(), "Enter valid query", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return false;
    }
}
