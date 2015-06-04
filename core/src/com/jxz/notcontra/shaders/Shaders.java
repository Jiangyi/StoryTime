package com.jxz.notcontra.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;


/**
 * Created by Kevin Xiao on 2015-04-27.
 */

public class Shaders {

    public enum ShaderType {
        PASSTHROUGH, VIGNETTE
    }

    private ShaderProgram currentShader;
    private ShaderProgram[] shaderArray = new ShaderProgram[2];

    public Shaders(int width, int height) {
        shaderArray[0] = new ShaderProgram(Gdx.files.internal("shaders/passthrough.vsh"), Gdx.files.internal("shaders/passthrough.fsh"));
        shaderArray[1] = new ShaderProgram(Gdx.files.internal("shaders/vignette.vsh"), Gdx.files.internal("shaders/vignette.fsh"));
        currentShader = shaderArray[0];
        bindShaders(width, height);
    }

    public ShaderProgram getShaderType(ShaderType type) {
        if (type == ShaderType.PASSTHROUGH) {
            currentShader = shaderArray[0];
        } else if (type == ShaderType.VIGNETTE) {
            currentShader = shaderArray[1];
        } else {
            currentShader = null;
        }
        return currentShader;
    }

    public void bindShaders(int width, int height) {
        for (ShaderProgram i : shaderArray) {
            i.begin();
            i.setUniformf("u_resolution", width, height);
        }
    }

    public void unbindShaders() {
        for (ShaderProgram i : shaderArray) {
            i.end();
        }
    }
}