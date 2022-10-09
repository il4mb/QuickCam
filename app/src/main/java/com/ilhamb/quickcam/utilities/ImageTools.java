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
import android.view.View;

import com.ilhamb.quickcam.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ImageTools {

    public int MinCrop = 50, MaxCrop = 50;
    public Uri imageUri;
    public String city = "Pekanbaru";
    private Context ctx;
    private Bitmap bmp;


    public ImageTools(Context context, Uri uri) throws IOException {

        this.ctx = context;
        this.imageUri = uri;

        this.bmp = handleSamplingAndRotationBitmap();

    }

    public void stampDateGeo(View view) {

        float scale = ctx.getResources().getDisplayMetrics().density;

        Bitmap newBitmap = Bitmap.createBitmap(this.bmp.getWidth(), this.bmp.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(this.bmp, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(7.5f * scale);
        float textWidth = paint.measureText(city);


        Bitmap icon = drawableToBitmap(ctx.getDrawable(R.drawable.ic_baseline_location_on_24));
        icon = changeColor(icon, Color.BLACK, Color.GRAY);
        icon = resizeBitmap(icon, (int) (15 * scale), (int) (15 * scale));

        int width = (int) ((icon.getWidth() + textWidth) * scale),
                height = (int) ((icon.getHeight() + 14) * scale);

        Bitmap loc = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(loc);
        canvas1.drawBitmap(icon, 0, 0, null);
        canvas1.drawText(city, 0, height, paint);

        canvas.drawBitmap(loc, 0, 0, paint);
        this.bmp = newBitmap;
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


    private Bitmap handleSamplingAndRotationBitmap() throws IOException {
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
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        rotateImageIfRequired();

        return img;
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

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private Bitmap changeColor(Bitmap src, int colorToReplace, int colorThatWillReplace) {
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int pixel;

        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                int index = y * width + x;
                pixel = pixels[index];
                if (pixel == colorToReplace) {
                    //change A-RGB individually
                    A = Color.alpha(colorThatWillReplace);
                    R = Color.red(colorThatWillReplace);
                    G = Color.green(colorThatWillReplace);
                    B = Color.blue(colorThatWillReplace);
                    pixels[index] = Color.argb(A, R, G, B);
                    /*or change the whole color
                    pixels[index] = colorThatWillReplace;*/
                }
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
