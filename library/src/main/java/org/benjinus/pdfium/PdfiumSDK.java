package org.benjinus.pdfium;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.benjinus.pdfium.util.FileUtils;

import java.io.File;
import java.io.IOException;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;

@SuppressWarnings("JniMissingFunction")
public class PdfiumSDK {

    private static final String TAG = "PDFSDK";

    private final int mPageCount;
    private long mNativeDocPtr;
    private ParcelFileDescriptor mFileDescriptor;

    static {
        System.loadLibrary("pdfsdk");
        System.loadLibrary("pdfsdk_jni");
    }

    /**
     * Context needed to get screen density
     * @param pdfFile
     */
    public PdfiumSDK(File pdfFile) throws IOException {
        mFileDescriptor = ParcelFileDescriptor.open(pdfFile, MODE_READ_ONLY);
        int numFd = FileUtils.getNumFd(mFileDescriptor);
        mNativeDocPtr = nativeOpenDocument(numFd, null);
        mPageCount = nativeGetPageCount(mNativeDocPtr);
    }

    private native long nativeOpenDocument(int fd, String password);

    private native void nativeCloseDocument(long docPtr);

    private native int nativeGetPageCount(long docPtr);

    private native long nativeLoadPage(long docPtr, int pageIndex);

    private native void nativeClosePage(long pagePtr);

    private native float nativeGetPageWidth(long pagePtr);

    private native float nativeGetPageHeight(long pagePtr);

    private native int nativeGetPageRotation(long pagePtr);

    private native void nativeMyRender(long pagePtr, Bitmap bitmap,
                                               float transX, float transY, float scale);

    ///////////////////////////////////////
    // PDF SDK functions
    ///////////

    /**
     * Get total number of pages in document
     */
    public int getPageCount() {
        return mPageCount;
    }

    public void myRender(Bitmap bitmap, int pageIndex, float transX, float transY, float scale, PointF pageSize) {
        try {
            long pagePtr = nativeLoadPage(mNativeDocPtr, pageIndex);

            if (pageSize != null) {
                pageSize.x = nativeGetPageWidth(pagePtr);
                pageSize.y = nativeGetPageHeight(pagePtr);
            }

            nativeMyRender(pagePtr, bitmap, transX, transY, scale);

            nativeClosePage(pagePtr);

        } catch (NullPointerException e) {
            Log.e(TAG, "mContext may be null");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Exception throw from native");
            e.printStackTrace();
        }
    }

    /**
     * Release native resources and opened file
     */
    public void closeDocument() {

        nativeCloseDocument(mNativeDocPtr);

        if (mFileDescriptor != null) {
            try {
                mFileDescriptor.close();
            } catch (IOException ignored) {
            } finally {
                mFileDescriptor = null;
            }
        }
    }

}
