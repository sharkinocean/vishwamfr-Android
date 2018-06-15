package com.tzutalin.dlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by reenath on 22/05/18.
 */

public class FaceCrop   {


    private Context context;
    private Bitmap bitmap;
    int nx1 = 0;
    int ny1 = 0;
    int nw1 = 0;
    int nh1 = 0;

    private FaceCrop(Context context, Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.context = context;
        cropFace();
    }

    public void cropFace(){

        if (bitmap == null) {
            throw new RuntimeException("Image should not be null");
        }

        //context should not be null
        if (context == null) {
            throw new RuntimeException("Context should not be null");
        }

        //Create a Paint object for drawing with
/*        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.GREEN);
        myRectPaint.setStyle(Paint.Style.STROKE);*/

        //Create a Canvas object for drawing on
        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(bitmap, 0, 0, null);

        //Detect the Faces
        FaceDetector faceDetector = new FaceDetector.Builder(context).setTrackingEnabled(false).build();

        //!!!
        //Cannot resolve method setTrackingEnabled(boolean)
        //skip for now
        //faceDetector.setTrackingEnabled(false);

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        //Draw Rectangles on the Faces
        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            float w1 = thisFace.getWidth();
            float h1 = thisFace.getHeight();
            nx1 = (int)x1;
            ny1 = (int)y1;
            nw1 = (int)w1;
            nh1 = (int)h1;
            //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 10, 10, myRectPaint);
        }


        getFaceCroppedBitmap();


    }
    public Bitmap getFaceCroppedBitmap() {
        //we can't get cropped bitmap is bitmap is null or cropArea is null
        if (bitmap == null ) {
            throw new RuntimeException("Initialize FaceDetectionCrop using FaceDetectionCrop.initialize() before using it.");
        }
        //crop bitmap with calculated cropArea
        return Bitmap.createBitmap(bitmap, nx1, ny1, nw1, nh1);
    }



    public static synchronized FaceCrop faceCrop(Context context, Bitmap bitmap) {
        return new FaceCrop(context, bitmap);
    }

}
