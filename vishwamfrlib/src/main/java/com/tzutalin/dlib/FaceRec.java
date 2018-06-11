package com.tzutalin.dlib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

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


    public FaceRec(Activity activity){
        this.activity=activity;
    }
    public FaceRec(String sample_dir_path) {
        dir_path = sample_dir_path;
        jniInit(dir_path);
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



    private class initRecAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "initRecAsync onPreExecute called");

            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            // create dlib_rec_example directory in sd card and copy model files
            File folder = new File(Constants.getDLibDirectoryPath());
            boolean success = false;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                File image_folder = new File(Constants.getDLibImageDirectoryPath());
                image_folder.mkdirs();
                if (!new File(Constants.getFaceShapeModelPath()).exists()) {
                    FileUtils.copyFileFromRawToOthers(activity,  R.raw.shape_predictor_5_face_landmarks, Constants.getFaceShapeModelPath());
                }
                if (!new File(Constants.getFaceDescriptorModelPath()).exists()) {
                    FileUtils.copyFileFromRawToOthers(activity, R.raw.dlib_face_recognition_resnet_model_v1, Constants.getFaceDescriptorModelPath());
                }
            } else {
                //Log.d(TAG, "error in setting dlib_rec_example directory");
            }
           FaceRec mFaceRec = new FaceRec(Constants.getDLibDirectoryPath());
            mFaceRec.train();
            return null;
        }

        protected void onPostExecute(Void result) {

        }
    }


    @Nullable
    @WorkerThread
    public ArrayList<ArrayList<Float> > get_face_encoding(@NonNull Bitmap bitmap) {
        float[] faceEncodings = jniBitmapFaceEncoding(bitmap);
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
