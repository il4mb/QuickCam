package com.ilhamb.quickcam.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;

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

    public void stampDateGeo () {

        int extraHeight = (int) (this.bmp.getHeight() * 0.15);

        Bitmap newBitmap = Bitmap.createBitmap(this.bmp.getWidth(),
                this.bmp.getHeight() + extraHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.BLUE);
        canvas.drawBitmap(this.bmp, 0, 0, null);

        Resources resources = this.ctx.getResources();
        float scale = resources.getDisplayMetrics().density;

        Paint pText = new Paint();
        pText.setColor(Color.WHITE);

        setTextSizeForWidth(pText,(int) (this.bmp.getHeight() * 0.10), city);

        Rect bounds = new Rect();
        pText.getTextBounds(city, 0, city.length(), bounds);

        int x = ((newBitmap.getWidth()-(int)pText.measureText(city))/2);
        int h = (extraHeight+bounds.height())/2;
        int y = (this.bmp.getHeight()+h);

        canvas.drawText(city, x, y, pText);

        this.bmp = newBitmap;
    }

    public void CropPresisi () {

        Random rand = new Random();
        float min = this.MinCrop;
        float max = this.MaxCrop;
        float random = min + rand.nextFloat() * (max - min);

        int width = this.bmp.getWidth();
        int height = this.bmp.getHeight();

        int X_per = (int) (width * (random/100));
        int Y_per = (int) (height * (random/100));

        int cropX = (width - X_per);
        int cropY = (height - Y_per);

        int randX = rand.nextInt((X_per - 0) + 1) + 0;
        int randY = rand.nextInt((Y_per - 0) + 1) + 0;

        this.bmp = Bitmap.createBitmap(this.bmp, randX, randY, cropX, cropY);

    }

    public Bitmap getBitmap () { return this.bmp; }


    private void setTextSizeForWidth(Paint paint, float desiredHeight,
                                     String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 15f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = (testTextSize * desiredHeight / bounds.height()) / text.length()*2;

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
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
}
