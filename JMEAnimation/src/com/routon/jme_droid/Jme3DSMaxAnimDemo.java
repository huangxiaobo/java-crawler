
package com.routon.jme_droid;

import com.jme3.animation.Animation;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.input.event.TouchEvent;
import com.routon.jui.JActor;
import com.routon.jui.JActorGene;
import com.routon.jui.JActorGroup;
import com.routon.jui.JActorKeyEventListener;
import com.routon.jui.JAnimLoader;
import com.routon.jui.JFocusStrategy;
import com.routon.jui.JRollerCoaster;
import com.routon.jui.JStage;

import java.io.IOException;
import java.util.List;

public class Jme3DSMaxAnimDemo extends JStage {
    private final static String TAG = "Jme3DSMaxAnimDemo";
    
    private JRollerCoaster rollerCoaster;
    
    private JFocusStrategy rollerCoasterFocusStrategy;

    private static final int PANEL_NUM = 25;

    @Override
    public void onEvent(String name, TouchEvent evt, float tpf) {
        if (evt.getType() == TouchEvent.Type.KEY_DOWN) {
            try {
                ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void simpleInitApp() {
      showRollerCoaster();
    }
    
    private void showRollerCoaster() {
        assetManager.registerLoader("com.routon.jui.JAnimLoader", "anim");
        JAnimLoader loader = new JAnimLoader();

        List<Animation> anim = null;
        try {
            AssetInfo info = assetManager.locateAsset(new AssetKey("Anims/xx.anim"));
            anim = (List<Animation>) loader.load(info);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        int N = 15;
        float[] timePos = new float[N+1];
        float t = 0, delta = 20.0f / N;
        for (int i = 0; i <= N; i++)
            timePos[i] = i * delta;
        
        rollerCoasterFocusStrategy = new JFocusStrategy(5, timePos);
        rollerCoaster = new JRollerCoaster("roller coaster", anim.get(0),
                rollerCoasterFocusStrategy);
        rollerCoaster.setLoopMode(JRollerCoaster.ROLLER_COASTER_LOOP_REVERSE);

        for (int i = 0; i < PANEL_NUM; i++) {
            JActorGroup group = new JActorGroup("");
            JActor actor = new JActor("");
            actor.setupMesh(350, 250);
            actor.setupTexture(assetManager.loadTexture("Textures/1_1.png"));
            actor.setReflection(true, 0.3f, 0.5f, 0.7f);
            group.attachChild(actor);
            rollerCoaster.attachChild(group);
        }

        rollerCoaster.requestKeyFocus();
        rollerCoaster.setLocalTranslation(0, 0, 0);
        //rollerCoaster.setUpExchange(new Vector3f(0.0f, 1.0f, 0.0f)/* normal */, new Vector3f(0.0f,
        //        0.0f, -1.0f)/* up vector */);
        rootNode.attachChild(rollerCoaster);     
        
        // 增加按键回调
        rollerCoaster.setOnKeyEventListener(new JActorKeyEventListener () {

            @Override
            public boolean onKeyUp(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onKeyDown(JActorGene actor, TouchEvent evt, float tpf) {
                // TODO Auto-generated method stub
                return false;
            }
            
        });
    }
}
