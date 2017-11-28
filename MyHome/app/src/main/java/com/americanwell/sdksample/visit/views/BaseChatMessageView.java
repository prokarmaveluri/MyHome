/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit.views;

import android.support.annotation.NonNull;

import com.americanwell.sdk.entity.visit.ChatItem;

/**
 * Base interface for views that display chat information.
 */
public interface BaseChatMessageView {
    void setChatMessage(@NonNull final ChatItem message);
}
