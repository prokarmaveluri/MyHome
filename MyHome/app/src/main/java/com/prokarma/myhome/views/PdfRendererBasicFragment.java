package com.prokarma.myhome.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.prokarma.myhome.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

/**
 * Created by veluri on 12/02/17.
 */

public class PdfRendererBasicFragment extends Fragment implements View.OnClickListener {

    public static final String PDF_TAG = "pdf_tag";

    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";
    private static String FILENAME_WITH_PATH = "";
    private ParcelFileDescriptor mFileDescriptor;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;
    private ImageView mImageView;
    private Button mButtonPrevious;
    private Button mButtonNext;
    private int index = 0;

    public PdfRendererBasicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (getArguments() != null) {
                FILENAME_WITH_PATH = getArguments().getString("FILENAME_WITH_PATH");
            }

            // Retain view references.
            mImageView = (ImageView) view.findViewById(R.id.image);
            mButtonPrevious = (Button) view.findViewById(R.id.previous);
            mButtonNext = (Button) view.findViewById(R.id.next);
            // Bind events.
            mButtonPrevious.setOnClickListener(this);
            mButtonNext.setOnClickListener(this);

            // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
            if (null != savedInstanceState) {
                index = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
            }

        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            openRenderer(this.getActivity());

            showPage(index);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.getActivity(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            this.getActivity().finish();
        }
    }

    @Override
    public void onDetach() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    private void openRenderer(Context context) throws IOException {

        File file = new File(FILENAME_WITH_PATH);

        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, copy it into the cache directory.
            InputStream asset = context.getAssets().open("report.pdf");

            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        mPdfRenderer = new PdfRenderer(mFileDescriptor);
    }

    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        mPdfRenderer.close();
        mFileDescriptor.close();
    }

    private void showPage(int index) {
        try {
            if (mPdfRenderer.getPageCount() <= index) {
                return;
            }
            // Make sure to close the current page before opening another one.
            if (null != mCurrentPage) {
                mCurrentPage.close();
            }

            mCurrentPage = mPdfRenderer.openPage(index);

            // the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);

            // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            mImageView.setImageBitmap(bitmap);
            updateUi();

        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        mButtonPrevious.setEnabled(0 != index);
        mButtonNext.setEnabled(index + 1 < pageCount);
        getActivity().setTitle(getString(R.string.pdf_with_index, index + 1, pageCount));
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View view) {
        if (mCurrentPage == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.previous: {
                // Move to the previous page
                showPage(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.next: {
                // Move to the next page
                showPage(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }
}
