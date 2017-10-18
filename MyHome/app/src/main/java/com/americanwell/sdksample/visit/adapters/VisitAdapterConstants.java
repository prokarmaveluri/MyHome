/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.adapters;

/**
 * Constant view types used by the VisitReport sub-adapters
 */
public class VisitAdapterConstants {

    /**
     * Constant integer representing a Header view
     */
    static final int TYPE_HEADER = 200;

    /**
     * Constant integer representing a Transfer view
     */
    static final int TYPE_TRANSFER = 201;

    /**
     * Constant integer representing a Topics view
     */
    static final int TYPE_TOPICS = 202;

    /**
     * Constant integer representing a Triage Q&A view
     */
    static final int TYPE_TRIAGE_QA = 203;

    /**
     * Constant integer representing a Pharmacy view
     */
    static final int TYPE_PHARMACY = 205;

    /**
     * Constant integer representing an Accepted Disclaimers view
     */
    static final int TYPE_ACCEPTED_DISCLAIMERS = 206;

    /**
     * Constant integer representing a Providers Notes view
     */
    static final int TYPE_PROVIDER_NOTES = 207;

    /**
     * Constant integer representing a Diagnoses view
     */
    static final int TYPE_DIAGNOSES = 208;

    /**
     * Constant integer representing a Prescription view
     */
    static final int TYPE_PRESCRIPTIONS = 209;

    /**
     * Constant integer representing a Procedures view
     */
    static final int TYPE_PROCEDURES = 210;

    /**
     * Constant integer representing an Agenda Item Followup view
     */
    static final int TYPE_AGENDA_ITEM_FOLLOW_UP = 211;

    /**
     * Constant integer representing a Title Header view
     */
    static final int TYPE_TITLE_HEADER = 213;


    /**
     * Constant integer representing a Sub Header view
     */
    static final int TYPE_SUB_HEADER = 249;

    /**
     * Constant integer representing a Chat view from the system
     */
    static final int TYPE_CHAT_SYSTEM = 250;

    /**
     * Constant integer representing a Chat view from a user
     */
    static final int TYPE_CHAT_USER = 251;

    /**
     * Constant integer representing a HIPAA notice
     * @since AWSDK 3.1
     */
    static final int TYPE_HIPAA_NOTICE = 252;

    /**
     * Constant integer representing additional HIPAA notice
     * @since AWSDK 3.1
     */
    static final int TYPE_HIPAA_NOTICE_ADDITIONAL = 253;

}
