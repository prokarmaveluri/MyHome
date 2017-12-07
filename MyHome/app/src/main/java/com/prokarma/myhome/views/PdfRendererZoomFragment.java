package com.prokarma.myhome.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;

import es.voghdev.pdfviewpager.library.PDFViewPagerZoom;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import timber.log.Timber;

/**
 * Created by veluri on 12/07/17.
 */
public class PdfRendererZoomFragment extends Fragment {

    public static final String PDF_TAG = "pdf_tag";

    private String fileNameWithEntirePath = "";
    private PDFViewPagerZoom pdfViewPager;
    private PDFPagerAdapter adapter;

    public PdfRendererZoomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_zoom, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (getArguments() != null) {
                fileNameWithEntirePath = getArguments().getString("FILENAME_WITH_PATH");
            }
            pdfViewPager = view.findViewById(R.id.pdfViewPager);
        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            adapter = new PDFPagerAdapter(getContext(), fileNameWithEntirePath);
            pdfViewPager.setAdapter(adapter);

        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        try {
            if (adapter != null) {
                adapter.close();
                adapter = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDetach();
    }
}
