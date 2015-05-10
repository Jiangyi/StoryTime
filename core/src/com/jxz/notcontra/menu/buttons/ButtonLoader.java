package com.jxz.notcontra.menu.buttons;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by jiangyi on 09/05/15.
 */
public class ButtonLoader extends AsynchronousAssetLoader<Button, ButtonLoader.ButtonParameter> {

    private Button button;

    public ButtonLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ButtonParameter parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, ButtonParameter parameter) {

    }

    @Override
    public Button loadSync(AssetManager manager, String fileName, FileHandle file, ButtonParameter parameter) {
        return null;
    }

    public static class ButtonParameter extends AssetLoaderParameters<Button> {
        // Do nothing
    }
}
