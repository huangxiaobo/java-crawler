package com.routon.jui;

import java.util.ArrayList;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;

public class JChatBox extends JScrollView {
	private static final String TAG = "JChatBox";
	
	private static final float ChatBox_DefaultInterSpace = 10.0f;
	private static final float ChatBox_DefaultScrollFPS = 30.0f;
	
	private static final int ChatBox_ID_LOCAL = 0;
	private static final int ChatBox_ID_REMOTE = 1;
	private static final int ChatBox_ID_CONTEXT = 2;
	private static final int ChatBox_ID_STAMP = 3;
	
	private ArrayList<Chat> mChatList = new ArrayList<Chat>();
	
	private float mFps = ChatBox_DefaultScrollFPS;
	
	private float mTotalHeight = 0.0f;
	
	private float dstAjustY = 0.0f;
	
	public JChatBox(String name) {
		super(name);
	}
	
	public void addChatContent(Spatial spatial, int id, float space) {
		attachChild(spatial);
		
		if (spatial instanceof JActorGene) {
			Chat chat = new Chat();
			chat.id = id;
			
			chat.content = spatial;
			chat.space = space >= 0.0f ? space : ChatBox_DefaultInterSpace;
			
			mChatList.add(chat);
			
			
			mTotalHeight += ((JActorGene) spatial).getHeight() + chat.space;
		}
	}
	
	@Override
	protected void updateWorldBound() {
		relayoutChatBox();
		
		super.updateWorldBound();
	}
	
	private void relayoutChatBox() {
		float adjustY = mAdjustY;
		int lastChatID = 0;
		
		for (int i = 0; i < mChatList.size(); i++) {
			Chat chat = mChatList.get(i);
			
			if (chat.content instanceof JActorGene) {
				float contentH = ((JActorGene) chat.content).getHeight();
				
				if (adjustY + contentH <= 0 || adjustY >= getHeight()) {
					chat.content.setVisibility(false);
				}
				else {
					float transX = 0.0f;
					float transY = 0.0f;
					float rotA = 0.0f;
					
					switch (chat.id) {
					case ChatBox_ID_LOCAL : 
						transX = getWidth() - ((JActorGene) chat.content).getWidth() / 2.0f;
						
						lastChatID = chat.id;
						break; 
						
					case ChatBox_ID_REMOTE: 
						transX = ((JActorGene) chat.content).getWidth() / 2.0f;
						
						lastChatID = chat.id;
						break; 
						
					case ChatBox_ID_CONTEXT: 
						transX = getWidth() / 2.0f;
						break; 
						
					case ChatBox_ID_STAMP: 
						if (lastChatID == ChatBox_ID_LOCAL) {
							transX = getWidth() - ((JActorGene) chat.content).getWidth() / 2.0f;
						}
						else if (lastChatID == ChatBox_ID_REMOTE) {
							transX = ((JActorGene) chat.content).getWidth() / 2.0f;
						}
						break; 
					}
					
					if (adjustY <= 0) {
						float h = adjustY + contentH;
						
						rotA = FastMath.acos(h / contentH);
						transY = h / 2.0f;
						
						
					}
					else if (adjustY + contentH <= getHeight()) {
						transY = adjustY + contentH / 2.0f;
					}
					else {
						float h = getHeight() - adjustY;
						
						rotA = -FastMath.acos(h / contentH);
						transY = adjustY + h / 2.0f;
					}
					
					chat.content.setLocalTranslation(transX, transY, 0.0f);
					chat.content.setLocalRotation(new Quaternion(rotA, 0.0f, 0.0f, 1.0f));
					
					chat.content.setVisibility(true);
				}
				
				adjustY += contentH + chat.space;
			}
		}
	}
	
	@Override
	public void setAdjustX(float adjustX) {
		// do nothing, no x adjust on chat box
	}
	
	@Override
	public void setAdjustY(float adjustY) {
		if (adjustY != mAdjustY) {
			dstAjustY = adjustY;
			
			// setup a timer to update
		}
	}
	
	private class Chat {
		int id;
		
		Spatial content;
		float space;
	}
}
