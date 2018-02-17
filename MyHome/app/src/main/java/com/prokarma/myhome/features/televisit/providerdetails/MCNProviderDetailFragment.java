package com.prokarma.myhome.features.televisit.providerdetails;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.Language;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import com.prokarma.myhome.views.CircularImageView;
import com.whinc.widget.ratingbar.RatingBar;

import timber.log.Timber;

/**
 * Created by veluri on 2/15/18.
 */

public class MCNProviderDetailFragment extends BaseFragment {
    public static final String MY_CARE_PROVIDER_DETAIL_TAG = "my_care_provider_detail_tag";

    private ProgressBar progressBar;
    private CircularImageView docImage;
    private TextView providerName;
    private TextView providerTitle;
    private RatingBar providerRating;
    private TextView providerNotes;
    private TextView providerType;
    private LinearLayout linearLanguages;

    public MCNProviderDetailFragment() {
    }

    public static MCNProviderDetailFragment newInstance() {
        return new MCNProviderDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.mcn_provider_details_title));
        View view = inflater.inflate(R.layout.fragment_my_care_provider_details, container, false);

        CommonUtil.setTitle(getActivity(), CommonUtil.isAccessibilityEnabled(getActivity()) ? getResources().getString(R.string.mcn_provider_details_title) : getResources().getString(R.string.mcn_provider_details_title), true);

        progressBar = (ProgressBar) view.findViewById(R.id.details_progress);
        docImage = (CircularImageView) view.findViewById(R.id.doc_image);
        providerName = (TextView) view.findViewById(R.id.provider_name);
        providerTitle = (TextView) view.findViewById(R.id.provider_title);
        providerRating = (RatingBar) view.findViewById(R.id.provider_rating);
        providerNotes = (TextView) view.findViewById(R.id.provider_notes);
        providerType = (TextView) view.findViewById(R.id.provider_type);
        linearLanguages = (LinearLayout) view.findViewById(R.id.linear_languages);

        if (AwsManager.getInstance().getProvider() != null) {
            loadProviderDetails();
        } else if (AwsManager.getInstance().getProviderInfo() != null) {
            getProvider();
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.next_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_PROVIDER_DETAIL_SCREEN, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_INTAKE, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PROVIDER_DETAIL;
    }

    private void addLanguageView(final String language) {

        final TextView langView = new TextView(getContext());
        langView.setPadding(0, 12, 10, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            langView.setTextAppearance(R.style.tradeGothicLTStd_Dynamic18);
        } else {
            langView.setTextAppearance(getActivity(), R.style.tradeGothicLTStd_Dynamic18);
        }

        langView.setText(language);
        langView.setContentDescription(language);

        linearLanguages.addView(langView);
    }

    private void loadProviderDetails() {

        if (AwsManager.getInstance().getProvider() != null) {

            updateDoctorImage();

            providerName.setText(AwsManager.getInstance().getProvider().getFullName());
            providerName.setContentDescription(providerName.getText());

            if (AwsManager.getInstance().getProvider().getSpecialty() != null) {
                providerTitle.setText(AwsManager.getInstance().getProvider().getSpecialty().getName());
                providerTitle.setContentDescription(providerTitle.getText());
            }

            providerRating.setCount(AwsManager.getInstance().getProvider().getRating());
            providerRating.setContentDescription("Rated as " + AwsManager.getInstance().getProvider().getRating() + " Stars");

            providerNotes.setText(AwsManager.getInstance().getProvider().getTextGreeting());
            providerNotes.setContentDescription(providerNotes.getText());

            if (AwsManager.getInstance().getProvider().isPCP()) {
                providerType.setText(getString(R.string.yes));
            } else {
                providerType.setText(getString(R.string.no));
            }
            providerType.setContentDescription(providerType.getText());

            if (AwsManager.getInstance().getProvider().getSpokenLanguages() != null && AwsManager.getInstance().getProvider().getSpokenLanguages().size() > 0) {
                for (Language lang : AwsManager.getInstance().getProvider().getSpokenLanguages()) {
                    addLanguageView(lang.getName());
                }
            }
        }
    }

    private void updateDoctorImage() {

        // preferred method for loading image
        AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                .newImageLoader(AwsManager.getInstance().getProvider(), docImage, ProviderImageSize.SMALL)
                .placeholder(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                .error(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                .build()
                .load();
    }

    private void getProvider() {

        //29260: app is  crashing when turn off location,microphone,and camera in settings>try to login after deactivating the permission to the app
        //after that error, SDK throws following: IllegalArgumentException: sdk initialization is missing

        if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Consumer patient = AwsManager.getInstance().getPatient() != null ? AwsManager.getInstance().getPatient() : AwsManager.getInstance().getConsumer();

        AwsManager.getInstance().getAWSDK().getPracticeProvidersManager().getProvider(
                AwsManager.getInstance().getProviderInfo(),
                patient,
                new SDKCallback<Provider, SDKError>() {
                    @Override
                    public void onResponse(Provider provider, SDKError sdkError) {

                        progressBar.setVisibility(View.GONE);
                        if (sdkError == null) {

                            AwsManager.getInstance().setProvider(provider);
                            AwsManager.getInstance().setProviderInfo(null);

                            loadProviderDetails();
                        } else {
                            Timber.e("getProvider. Error = " + sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getProvider. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }
}
