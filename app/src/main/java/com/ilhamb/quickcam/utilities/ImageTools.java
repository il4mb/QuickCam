package com.ilhamb.quickcam.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.ilhamb.quickcam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ImageTools {

    public int MinCrop = 0, MaxCrop = 15;
    public Uri imageUri;
    public String city = "Pekanbaru";
    private Context ctx;
    private Bitmap bmp;

    public ImageTools(Context context, Bitmap bitmap, Uri uri) throws Exception {

        this.ctx = context;
        this.imageUri = uri;
        this.bmp = bitmap;


        try {
            File file = new File(RealPathUtil.getRealPath(context, this.imageUri));
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            this.bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {

        }
    }

    public ImageTools(Context context, Uri uri) throws Exception {

        this.ctx = context;
        this.imageUri = uri;

        this.bmp = handleSamplingAndRotationBitmap();

    }

    public void stampDateGeo() {

        Date currentTime = Calendar.getInstance().getTime();
        String localDate = currentTime.toLocaleString();

        Stamp stamp = new Stamp(this.bmp);
        stamp.addText(localDate, 8f);

        this.bmp = stamp.bitmap;
    }

    public void CropPresisi() {

        Random rand = new Random();
        float min = this.MinCrop;
        float max = this.MaxCrop;
        float random = min + rand.nextFloat() * (max - min);

        int width = this.bmp.getWidth();
        int height = this.bmp.getHeight();

        int X_per = (int) (width * (random / 100));
        int Y_per = (int) (height * (random / 100));

        int cropX = (width - X_per);
        int cropY = (height - Y_per);

        int randX = rand.nextInt((X_per - 0) + 1) + 0;
        int randY = rand.nextInt((Y_per - 0) + 1) + 0;

        this.bmp = Bitmap.createBitmap(this.bmp, randX, randY, cropX, cropY);

    }

    public Bitmap getBitmap() {
        return this.bmp;
    }


    private Bitmap handleSamplingAndRotationBitmap() throws Exception {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = this.ctx.getContentResolver().openInputStream(this.imageUri);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = this.ctx.getContentResolver().openInputStream(this.imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, options);

        if(bitmap == null) {

            InputStream is = this.ctx.getContentResolver().openInputStream(this.imageUri);
            bitmap = BitmapFactory.decodeStream(is);
        }

        rotateImageIfRequired();

        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private void rotateImageIfRequired() throws IOException {

        InputStream input = this.ctx.getContentResolver().openInputStream(this.imageUri);
        ExifInterface ei;

        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(this.imageUri.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotateImage(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotateImage(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotateImage(270);
        }
    }

    private void rotateImage(int degree) {

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        this.bmp = Bitmap.createBitmap(this.bmp, 0, 0, this.bmp.getWidth(), this.bmp.getHeight(), matrix, true);
        this.bmp.recycle();
    }


}

class Stamp {

    public Bitmap bitmap;
    public Canvas canvas;
    private double relasi;

    int padding = 15;

    public Stamp(Bitmap bmp) {

        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmp, 0, 0, null);

        this.relasi = Math.sqrt(canvas.getWidth() * canvas.getHeight()) / 250;

        this.padding = (int) (relasi * 15);

        this.bitmap = bitmap;
    }




    public void addText(@NonNull String txt,@NonNull float size) {


        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((float) (relasi*size));

        Rect bounds = new Rect();
        paint.getTextBounds(txt, 0, txt.length(), bounds);

        int y = bounds.height(),
                x = bounds.width();

        int height = y + padding;
        Bitmap layer = Bitmap.createBitmap(this.bitmap.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(layer);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(layer, 0, y, paint);

        canvas.drawText(txt, padding, y, paint);
        this.canvas.drawBitmap(layer, 0, this.bitmap.getHeight() - height, null);

    }
}