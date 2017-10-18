/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdksample.LocationListActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for finding a pharmacy
 */
@RequiresPresenter(FindPharmacyPresenter.class)
public class FindPharmacyActivity extends LocationListActivity<FindPharmacyPresenter> {

    public static String EXTRA_PHARMACY = "pharmacy";

    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.list_header)
    TextView listHeader;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private PharmaciesAdapter pharmaciesAdapter;

    public static Intent makeIntent(@NonNull final Context context) {
        return new Intent(context, FindPharmacyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_find_pharmacy);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getPresenter().findPharmacies(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        pharmaciesAdapter = new PharmaciesAdapter(this, getPresenter().isMultiCountry());
        listView.setAdapter(pharmaciesAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_find_pharmacy, menu);

        final MenuItem nearMeItem = menu.findItem(R.id.action_near_me);
        boolean locationFailed = getPresenter().getLocationFailed();
        ProgressBar spinner = null;
        boolean showSpinner = getPresenter().getLocation() == null && !locationFailed;
        if (showSpinner) {
            spinner = new ProgressBar(this);
            spinner.setIndeterminate(true);
            spinner.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            getPresenter().setLocationFailed(true);
                            invalidateOptionsMenu();
                            Toast.makeText(FindPharmacyActivity.this,
                                    R.string.pharmacy_search_no_location, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    },
                    1000 * 60);
        }
        nearMeItem.setActionView(spinner);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean consume = true;
        switch (item.getItemId()) {
            case R.id.action_near_me:
                if (getPresenter().getLocation() != null) {
                    searchView.clearFocus();
                    searchView.setQuery("", false); // clear the zip code if using the location-based search
                    getPresenter().requestPharmaciesByLocation();
                }
                else {
                    Toast.makeText(FindPharmacyActivity.this,
                            R.string.pharmacy_search_no_location, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                consume = super.onOptionsItemSelected(item);
                break;

        }
        return consume;
    }

    @OnItemClick(R.id.list_view)
    public void onPharmacyClick(int position) {
        Intent result = new Intent();
        result.putExtra(EXTRA_PHARMACY, pharmaciesAdapter.getItem(position));
        setResult(RESULT_OK, result);
        finish();
    }

    public void setPharmacies(final List<Pharmacy> pharmacies) {
        pharmaciesAdapter.clear();
        pharmaciesAdapter.addAll(pharmacies);
    }

    public void setEmptyPharmacies(final boolean empty, final String query) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (empty) {
            emptyView.setText(getString(R.string.pharmacy_list_empty, query));
        }
    }

    public void setQuery(final String query) {
        searchView.setQuery(query, false);
    }

    public void setListHeader(final String headerText) {
        String finalText = String.format(getString(R.string.pharmacy_search_results_near), headerText);
        listHeader.setText(finalText);
    }

    public class PharmaciesAdapter extends ArrayAdapter<Pharmacy> {

        private boolean isMultiCountry;

        public PharmaciesAdapter(final Context context, boolean isMultiCountry) {
            super(context, 0);

            this.isMultiCountry = isMultiCountry;
        }

        private class ViewHolder {
            TextView textView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view;
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final Pharmacy pharmacy = getItem(position);

            final String pharmacyTextBuilder = sampleUtils.buildPharmacyDisplayText(
                    getContext(), pharmacy, false, isMultiCountry);
            viewHolder.textView.setText(pharmacyTextBuilder);
            return view;
        }
    }
}
