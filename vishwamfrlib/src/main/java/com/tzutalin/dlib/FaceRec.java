package com.tzutalin.dlib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by houzhi on 16-10-20.
 * Modified by tzutalin on 16-11-15
 * Modified by Gaurav on Feb 23, 2018
 */
public class FaceRec {
    private static final String TAG = "dlib";
    private Activity activity;
    private Context context1;
    private Bitmap tempBitmap;
    int nx1 = 0;
    int ny1 = 0;
    int nw1 = 0;
    int nh1 = 0;

    private Bitmap croppedBitmap;

    // accessed by native methods
    @SuppressWarnings("unused")
    private long mNativeFaceRecContext;
    private String dir_path = "";
    private int embedding_size = 128;
    private String log_tag = "FaceRec";

    static {
        try {
            System.loadLibrary("android_dlib");
            jniNativeClassInit();
            Log.d(TAG, "jniNativeClassInit success");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "library not found");
        }



    }

    public FaceRec (Context context){

        this.context1 = context;

    }


    public FaceRec(Activity activity ){
        this.activity=activity;

    }
    public FaceRec(String sample_dir_path, Context context) {
        dir_path = sample_dir_path;
        jniInit(dir_path);
        this.context1 = context;
    }

    @Nullable
    @WorkerThread
    public void train() {
        jniTrain();
        return;
    }



    @Nullable
    @WorkerThread
    public List<VisionDetRet> recognize(@NonNull Bitmap bitmap) {
        VisionDetRet[] detRets = jniBitmapRec(bitmap);
        return Arrays.asList(detRets);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detect(@NonNull Bitmap bitmap) {
        VisionDetRet[] detRets = jniBitmapDetect(bitmap);
        return Arrays.asList(detRets);
    }





    @Nullable
    @WorkerThread
    public ArrayList<ArrayList<Float> > get_face_encoding(@NonNull Bitmap bitmap) {

       /* tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(bitmap, 0, 0, null);
*/
     Bitmap scaledBitmap =    Bitmap.createScaledBitmap(bitmap, 300, 300, true);


       //Detect the Faces
        FaceDetector faceDetector = new FaceDetector.Builder(context1).setTrackingEnabled(false).build();

        //!!!
        //Cannot resolve method setTrackingEnabled(boolean)
        //skip for now
        //faceDetector.setTrackingEnabled(false);

        Frame frame = new Frame.Builder().setBitmap(scaledBitmap).build();
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


        croppedBitmap = Bitmap.createBitmap(scaledBitmap, nx1,ny1,nw1,nh1);

        float[] faceEncodings = jniBitmapFaceEncoding(croppedBitmap);
        if (faceEncodings.length % embedding_size != 0) {
            Log.e(log_tag, "error in calculating embeddings");
        }
        ArrayList<ArrayList<Float> > result = new ArrayList<ArrayList<Float> >();

        for (int i=0; i<faceEncodings.length/embedding_size; i++) {
            ArrayList<Float> a = new ArrayList<Float>();
            for (int j=0; j<embedding_size; j++) {
                a.add(faceEncodings[i*embedding_size + j]);
            }
            result.add(a);
        }
        return result;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    public void release() {
        jniDeInit();
    }

    @Keep
    private native static void jniNativeClassInit();

    @Keep
    private synchronized native int jniInit(String sample_dir_path);

    @Keep
    private synchronized native int jniDeInit();

    @Keep
    private synchronized native int jniTrain();

    @Keep
    private synchronized native VisionDetRet[] jniBitmapDetect(Bitmap bitmap);

    @Keep
    private synchronized native float[] jniBitmapFaceEncoding(Bitmap bitmap);

    @Keep
    private synchronized native VisionDetRet[] jniBitmapRec(Bitmap bitmap);
}
