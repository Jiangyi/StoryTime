package com.jxz.notcontra.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.jxz.notcontra.handlers.AssetHandler;

/**
 * Created by Kevin Xiao on 2015-06-02.
 */
public class AnimationEx extends Animation {

    private Vector2[] offset;
    private String animType;

    /**
     * IMPORTANT: Using this constructor will not attempt to load any xml files that contain offset values
     *
     * This constructor is no different than the libgdx Animation class implementation that this class extends
     * The animation type will be null, to which the animation offset will return (0, 0) unless manually set.
     */
    public AnimationEx(float frameDuration, Array<? extends TextureRegion> keyFrames) {
        super(frameDuration, keyFrames);
        animType = null;
    }

    /**
     * Calls super constructor to set up animations (stores frame duration and key frames), and then sets up
     * offset values for each different frame by parsing an XML file
     *
     * @param frameDuration the time between frames in seconds.
     * @param animType the String value for the set of animation frames
     *                 must be identical in the .xml file as well as in the TextureAtlas .pack file
     * @param texAtlasName the key name of the TextureAtlas to be loaded by the AssetManager
     *                     must be the exact same name as the TextureAtlas .pack and offset .xml files
     */
    public AnimationEx(float frameDuration, String animType, String texAtlasName) {
        super(frameDuration, ((TextureAtlas) AssetHandler.getInstance().getByName(texAtlasName)).findRegions(animType));
        this.animType = animType;
        this.offset = new Vector2[super.getKeyFrames().length];
        parseOffsetFile(AssetHandler.getInstance().getFilePath(texAtlasName) + ".xml");
    }

    public void parseOffsetFile(String fileDir) {
        try {
            XmlReader.Element root = new XmlReader().parse(Gdx.files.internal(fileDir));
            Array<XmlReader.Element> animTypeElements = root.getChildrenByName(animType);
            for (int i = 0; i < animTypeElements.size; i++) {
                offset[i] = new Vector2(animTypeElements.get(i).getFloat("x"), animTypeElements.get(i).getFloat("y"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! XML info not found for offsets");
            animType = null;
        }
    }

    public Vector2 getAnimOffset(float animStateTime) {
        if (animType == null) {
            return new Vector2(0, 0);
        }
        return offset[getKeyFrameIndex(animStateTime)];
    }

}
