/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.americanwell.sdk.entity.AttachmentReference;
import com.prokarma.myhome.R;

/**
 * Helper adapter for File Attachments, used in secure messaging
 */
public class AttachmentsAdapter extends ArrayAdapter<AttachmentReference> {
    private FileAttachmentProvider fileAttachmentProvider;

    public AttachmentsAdapter(final Context context, final FileAttachmentProvider fileAttachmentProvider) {
        super(context, 0);
        this.fileAttachmentProvider = fileAttachmentProvider;
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
        final AttachmentReference attachmentReference = getItem(position);

        viewHolder.textView.setText(attachmentReference.getName());
        viewHolder.textView.setTextColor(getContext().getResources().getColor(R.color.color_link));
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileAttachmentProvider.getFileAttachment(attachmentReference);
            }
        });

        return view;
    }
}
