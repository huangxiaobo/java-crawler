
package com.routon.jme_droid;

import com.jme3.cinematic.PlayState;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.routon.jui.JActor;
import com.routon.jui.JTimer;
import com.routon.jui.JTimer.JTimerTask;

class JSpinner extends JActor {
    private final static String TAG = "JmeWaitActor";

    private int runCnt = 0;
    private JTimer timer;
    private int nRow, nCol;
    private PlayState playState = PlayState.Stopped;

    private JSpinner(String name) {
        super(name);
    }

    public JSpinner(String name, int row, int col) {
        super(name);

        nRow = row;
        nCol = col;
    }

    public void start(long period, long delay) {
        timer = new JTimer(period, delay);
        timer.setLocalTask(new JTimerTask() {

            @Override
            public boolean task() {
                // TODO Auto-generated method stub
                Mesh mesh = getMesh();
                if (mesh == null || playState != PlayState.Playing)
                    return true;
                runCnt++;
                runCnt %= (nRow * nCol);
                // 一般从图片左上角开始,从左到右，从上到下播放图片
                float x1 = 1.0f * (runCnt % nCol) / nCol;
                float y1 = 1.0f * (nRow - runCnt / nCol) / nRow;
                float x2 = x1 + 1.0f / nCol;
                float y2 = y1 - 1.0f / nRow;
                mesh.setBuffer(Type.TexCoord, 2,
                        new float[] {
                                x1, y2,
                                x2, y2,
                                x2, y1,
                                x1, y1
                        });
                return true;
            }
        });

        timer.start();
        playState = PlayState.Playing;
    }

    public void pause() {
        timer.pause();
        playState = PlayState.Paused;
    }
}
