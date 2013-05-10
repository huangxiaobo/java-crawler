package com.routon.jui;

import static com.jme3.util.xml.SAXUtil.parseFloat;

import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.util.xml.SAXUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class JAnimLoader extends DefaultHandler implements AssetLoader {  
    private static final String TAG = "JAnimLoader";
    private Stack<String> elementStack = new Stack<String>();
    private com.jme3.animation.AnimationFactory animFactory;
    private float time;
    List<Animation> animationList = null;

    public JAnimLoader() {
        super();
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {
    }

    private void reset() {
        elementStack.clear();

        // NOTE: Setting some of those to null is only needed
        // if the parsed file had an error e.g. startElement was called
        // but not endElement
        animFactory = null;
    }

    private void checkTopNode(String topNode) throws SAXException {
        if (!elementStack.peek().equals(topNode)) {
            throw new SAXException("dotScene parse error: Expected parent node to be " + topNode);
        }
    }

    private Quaternion parseQuat(Attributes attribs) throws SAXException {
        if (attribs.getValue("x") != null) {
            // defined as quaternion
            float x = parseFloat(attribs.getValue("x"));
            float y = parseFloat(attribs.getValue("y"));
            float z = parseFloat(attribs.getValue("z"));
            float w = parseFloat(attribs.getValue("w"));
            return new Quaternion(x, y, z, w);
        } else if (attribs.getValue("qx") != null) {
            // defined as quaternion with prefix "q"
            float x = parseFloat(attribs.getValue("qx"));
            float y = parseFloat(attribs.getValue("qy"));
            float z = parseFloat(attribs.getValue("qz"));
            float w = parseFloat(attribs.getValue("qw"));
            return new Quaternion(x, y, z, w);
        } else if (attribs.getValue("angle") != null) {
            // defined as angle + axis
            float angle = parseFloat(attribs.getValue("angle"));
            float axisX = parseFloat(attribs.getValue("axisX"));
            float axisY = parseFloat(attribs.getValue("axisY"));
            float axisZ = parseFloat(attribs.getValue("axisZ"));
            Quaternion q = new Quaternion();
            q.fromAngleAxis(angle, new Vector3f(axisX, axisY, axisZ));
            return q;
        } else {
            // defines as 3 angles along XYZ axes
            float angleX = parseFloat(attribs.getValue("angleX"));
            float angleY = parseFloat(attribs.getValue("angleY"));
            float angleZ = parseFloat(attribs.getValue("angleZ"));
            Quaternion q = new Quaternion();
            q.fromAngles(angleX, angleY, angleZ);
            return q;
        }
    }
    
    private void parseAnimations(Attributes attribs) throws SAXException {
        animationList = new ArrayList<Animation>();
     }

    private void parseAnimation(Attributes attribs) throws SAXException {
        String name = attribs.getValue("name");
        float length = parseFloat(attribs.getValue("length"));
        
        animFactory = new AnimationFactory(length, name);
    }

    private void parseKeyframe(Attributes attribs) throws SAXException {
        time = parseFloat(attribs.getValue("time"));
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
//        System.out.println(TAG + " startElement.");
        if (qName.equals("animations")) {
            if (elementStack.size() != 0) {
                throw new SAXException("dotScene parse error: 'animations' element must be the root XML element");
            }
            parseAnimations(attribs);
        } else if (qName.equals("animation")) {
            String curElement = elementStack.peek();
            if (!curElement.equals("animations")) {
                throw new SAXException("AnimLoader parse error: "
                        + "animation element can only appear under 'animations'");
            }
            parseAnimation(attribs);
        } else if (qName.equals("animation")) {
            
        } else if (qName.equals("keyframe")) {
            parseKeyframe(attribs);
        } else if (qName.equals("translation")) {
            if (animFactory == null) {
                throw new SAXException("SceneLoader::Can't recognize the translation, there is no keyframe.");
            }
            else {
                animFactory.addTimeTranslation(time, SAXUtil.parseVector3(attribs));
            }
        } else if (qName.equals("quaternion") || qName.equals("rotation")) {
            if (animFactory == null) {
                throw new SAXException("SceneLoader::Can't recognize the rotation, there is no keyframe.");
            }
            else {
                animFactory.addTimeRotation(time, parseQuat(attribs));
            }
        } else if (qName.equals("scale")) {
            if (animFactory == null) {
                throw new SAXException("SceneLoader::Can't recognize the scale, there is no keyframe.");
            }
            else {
                animFactory.addTimeScale(time, SAXUtil.parseVector3(attribs));
            }
        } 

        elementStack.push(qName);
    }

    @Override
    public void endElement(String uri, String name, String qName) throws SAXException {
        if (qName.equals("animations")) {
            ;
        } else if (qName.equals("animation")) {
            if (animFactory != null) {
                animationList.add(animFactory.buildAnimation());
            }
            animFactory = null;
        } else if (qName.equals("keyframe")) {
            //
        } 
        checkTopNode(qName);
        elementStack.pop();
    }

    @Override
    public void characters(char ch[], int start, int length) {
    }

    public Object load(AssetInfo info) throws IOException {
        try {
            reset();

            // == Run 2nd pass to load entities and other objects ==

            // Added by larynx 25.06.2011
            // Android needs the namespace aware flag set to true 
            // Kirill 30.06.2011
            // Now, hack is applied for both desktop and android to avoid
            // checking with JmeSystem.
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XMLReader xr = factory.newSAXParser().getXMLReader();

            xr.setContentHandler(this);
            xr.setErrorHandler(this);

            InputStreamReader r = null;

            try {
                r = new InputStreamReader(info.openStream());
                xr.parse(new InputSource(r));
            } finally {
                if (r != null) {
                    r.close();
                }
            }
            
            return animationList;
        } catch (SAXException ex) {
            IOException ioEx = new IOException("Error while parsing animation file");
            ioEx.initCause(ex);
            throw ioEx;
        } catch (ParserConfigurationException ex) {
            IOException ioEx = new IOException("Error while parsing animation file");
            ioEx.initCause(ex);
            throw ioEx;
        }
    }
}