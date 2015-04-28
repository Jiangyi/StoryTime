package com.jxz.notcontra.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.io.IOException;

/**
 * Created by Kevin Xiao on 2015-04-27.
 */

public class Shaders {

    public static ShaderProgram vignetteShader = new ShaderProgram(Gdx.files.internal("shaders/vignette.vsh"), Gdx.files.internal("shaders/vignette.fsh"));
}