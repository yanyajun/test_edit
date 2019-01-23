package com.example.administrator.test_edit;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private TimeLineEditor2 ttt;
    private DragLayout mDragLayout;
    private CutAudioView cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ttt = (TimeLineEditor2) findViewById(R.id.time_edit);
        cc = (CutAudioView) findViewById(R.id.cut_audio_view) ;


        mDragLayout = (DragLayout) findViewById(R.id.drag_layout);

        ttt.setOnSeekBarChangedListener(new TimeLineEditor2.OnSeekBarChanged() {
            @Override
            public void onLeftValueChange(float var) {
                Log.i("left", "left: " + var);
            }

            @Override
            public void onRightValueChange(float var) {
                Log.i("right", "right: " + var);
            }

            @Override
            public void onCenterTouched() {

            }
        });


        mDragLayout.setOnDragListener(new DragLayout.OnDragListener() {
            @Override
            public void topLeftTouched() {

            }

            @Override
            public void topRightTouched() {

            }

            @Override
            public void centerTouched() {

            }

            @Override
            public void bottomRightTouched() {

            }

            @Override
            public void dragMove(PointF point) {

            }
        });

        cc.setMaxDuration(1000);
        cc.setMinDuration(10);
        cc.setOnSeekBarChangedListener(new CutAudioView.OnSeekBarChanged() {
            @Override
            public void onLeftValueChange(float var) {
                Log.e("=====", "" + var);
            }

            @Override
            public void onRightValueChange(float var) {
                Log.e("=====", "" + var);
            }

            @Override
            public void onCenterTouched(float left, float right) {
                Log.e("=====", "left: " + left + " right: " + right);
            }
        });
    }
}
