/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.documents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.consumer.DocumentRecord;
import com.americanwell.sdksample.BaseSwipeToRefreshActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.SampleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for showing the list of Health Document records and retrieving attachments
 */
@RequiresPresenter(HealthDocumentsPresenter.class)
public class HealthDocumentsActivity extends BaseSwipeToRefreshActivity<HealthDocumentsPresenter, DocumentRecord> {

    private int ATTACH_REQUEST_CODE = 10000;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, HealthDocumentsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_health_documents);
    }

    @Override
    protected ArrayAdapter createAdapter() {
        return new HealthDocumentsAdapter(this);
    }

    @Override
    protected int getTitleRes() {
        return 0;
    }

    @Override
    public void onRefresh() {
        getPresenter().getHealthDocuments();
    }

    @OnClick(R.id.fab)
    public void addHealthDocument() {
        final Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ATTACH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ATTACH_REQUEST_CODE && resultCode == RESULT_OK) {
            getPresenter().uploadDocument(data.getData());
        }
    }

    // presenter will call this after a doc is removed
    public void setHealthDocumentRemoved() {
        Toast.makeText(this, R.string.health_document_removed, Toast.LENGTH_SHORT).show();
    }

    // presenter will call this after a doc is uploaded
    public void setHealthDocumentUploaded() {
        Toast.makeText(this, R.string.health_document_uploaded, Toast.LENGTH_SHORT).show();
    }

    public class HealthDocumentsAdapter extends ArraySwipeAdapter<DocumentRecord> {
        public HealthDocumentsAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.item_health_document_swiper;
        }

        public class ViewHolder {
            @BindView(R.id.health_document_name)
            TextView name;
            @BindView(R.id.health_document_desc)
            TextView desc;
            @BindView(R.id.item_health_document_swiper)
            SwipeLayout swipeLayout;
            SwipeLayout.SwipeListener swipeListener;
            @BindView(R.id.item_health_document_delete)
            View delete;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_health_document, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final DocumentRecord documentRecord = (DocumentRecord) getItem(position);

            viewHolder.name.setText(documentRecord.getName());
            viewHolder.desc.setText(documentRecord.getDescription());

            // swipe handling
            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            // request the attachment from the presenter
            viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPresenter().getHealthDocumentAttachment(documentRecord);
                }
            });

            // swipe listener
            if (viewHolder.swipeListener != null) {
                viewHolder.swipeLayout.removeSwipeListener(viewHolder.swipeListener);
            }
            viewHolder.swipeListener = new SampleSwipeListener(swipeRefreshLayout);
            viewHolder.swipeLayout.addSwipeListener(viewHolder.swipeListener);

            // request the deletion of the document from the presenter
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPresenter().removeHealthDocument(documentRecord);
                }
            });

            return view;
        }
    }

}
