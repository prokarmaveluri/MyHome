/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.util;

import com.americanwell.sdk.entity.AttachmentReference;

/**
 * Little helper interface for AttachmentsAdapter to provide a callback
 * to fetch attachments
 */
public interface FileAttachmentProvider {
    void getFileAttachment(final AttachmentReference attachmentReference);
}
