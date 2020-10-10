package org.benjinus.pdfium.samples;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.benjinus.pdfium.PdfiumSDK;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imageView);

        decodePDFPage();
    }

    private void decodePDFPage() {
        PdfiumSDK document;
        try {
            File pdfFile = ((SamplesApplication) getApplication()).createNewSampleFile("Sample.pdf");
            document = new PdfiumSDK(pdfFile);

            Bitmap bitmap = Bitmap.createBitmap(getScreenWidth(), getScreenHeight(), Bitmap.Config.ARGB_8888);

            PointF pageSize = new PointF();
            document.myRender(bitmap, 3, 0, 0, 1, pageSize);

            Toast.makeText(this, "Pages : " + document.getPageCount() + "\nSize: " + pageSize.x + " x " + pageSize.y, Toast.LENGTH_LONG).show();

            mImageView.setImageBitmap(bitmap);

            document.closeDocument();
            document = null;

        } catch (Exception e) {
            Log.e("MyApp3", e.toString());
            document = null;
        }
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

}
