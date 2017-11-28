/**
 * Copyright 2015 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.util;

import com.americanwell.sdk.entity.visit.ChatItem;

import java.util.Comparator;

/**
 * Comparator for sorting {@link ChatItem} objects
 *
 * @since AWSDK 2.1
 */
public class ChatMessageComparator implements Comparator<ChatItem> {
    @Override
    public int compare(ChatItem o1, ChatItem o2) {
        final Long l = Long.valueOf(o1.getOrdinal());
        final Long r = Long.valueOf(o2.getOrdinal());
        return l.compareTo(r);
    }
}
