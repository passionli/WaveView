package com.liguang.waveview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WaveView view = (WaveView) findViewById(R.id.waveView);
        view.setInterval(1000);
        view.setDuration(3000);
        view.setTimeInterpolator(new AccelerateInterpolator());
        view.setCircleColor(Color.YELLOW);
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
