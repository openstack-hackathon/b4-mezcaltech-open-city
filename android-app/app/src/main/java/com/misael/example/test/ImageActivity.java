package com.misael.example.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    private ImageView imgTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        try {
            imgTakePicture = (ImageView) findViewById(R.id.imgTakePic);

            String imgPath = getIntent().getStringExtra("image");
            Log.d(ImageActivity.class.getSimpleName(), "IMAGE: " + imgPath);

            setPic(imgPath);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPic(String imgPath) throws Exception {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 8;

        ExifInterface ei = new ExifInterface(imgPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                break;
        }

        imgTakePicture.setImageBitmap(bitmap);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }
}
