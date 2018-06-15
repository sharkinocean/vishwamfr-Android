package com.tzutalin.vishhwamfr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceRec;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private FaceRec mFaceRec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap bitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ree);
        mFaceRec = new FaceRec(Constants.getDLibDirectoryPath(), getApplicationContext());
        long time1 = System.currentTimeMillis();
        ArrayList<String> result = new ArrayList<>();
        ArrayList<ArrayList<Float>> face_encodings = mFaceRec.get_face_encoding(bitmap);
        for (ArrayList<Float> fe : face_encodings) {
            result.add(fe.toString());
        }
        String text = "";
        for (String s : result) {
            text += s;
        }
        long time2 = System.currentTimeMillis();
        String diff = String.valueOf(time1- time2);
        Log.e("diff", diff);

        Log.e("enc", text);



    }
}
