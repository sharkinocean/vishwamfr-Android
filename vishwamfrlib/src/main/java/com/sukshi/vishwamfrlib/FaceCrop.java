package com.sukshi.vishwamfrlib;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by reenath on 22/05/18.
 */

public class FaceCrop extends Activity{

    public void detectFace( Bitmap myBitmap){

        //Create a Paint object for drawing with
/*        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.GREEN);
        myRectPaint.setStyle(Paint.Style.STROKE);*/

        //Create a Canvas object for drawing on
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Detect the Faces
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).build();

        //!!!
        //Cannot resolve method setTrackingEnabled(boolean)
        //skip for now
        //faceDetector.setTrackingEnabled(false);

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        int nx1 = 0;
        int ny1 = 0;
        int nw1 = 0;
        int nh1 = 0;
        //Draw Rectangles on the Faces
        for(int i=0; i<faces.size(); i++) {
            //   Log.e("William" , "faces.size()" + faces.size());
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

        Bitmap te = Bitmap.createBitmap(myBitmap, nx1, ny1, nw1, nh1);



    }

}
