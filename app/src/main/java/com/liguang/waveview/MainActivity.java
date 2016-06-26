package com.liguang.waveview;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    int[] mWaveViews = {R.id.waveView1,R.id.waveView2,R.id.waveView3,R.id.waveView4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WaveView view = (WaveView) findViewById(R.id.waveView1);
        view.setDelay(250);
        view.setDuration(3000);
        view.setCircleColor(Color.BLUE);
        view.setStyle(Paint.Style.STROKE);
        view.start();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.isRunning()){
                    view.cancel();;
                }else {
                    view.start();
                }
            }
        });

        for (int id : mWaveViews){
            ((WaveView) findViewById(id)).start();
        }


//        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long frameTimeNanos) {
//                float radius = view.getRadius();
//                radius += 2;
//                if (radius > 1000){
//                    radius = 20;
//                }
//                view.setRadius(radius);
//                view.invalidate();
//
//                Choreographer.getInstance().postFrameCallback(this);
//            }
//        });
    }
}
