package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.AudioHelper;
import com.jxz.notcontra.menu.Menu;
import com.jxz.notcontra.menu.ParseMenu;

/**
 * Created by Samuel on 2015-05-08.
 */
public class MenuState extends GameState {

    private AssetHandler assetHandler = AssetHandler.getInstance();
    private Menu currentMenu;
    private TextureAtlas menuButtonTextures;
    private Menu rootMenu;
    private Texture background;
    private Music music;

    public MenuState(Game game) {
        super(game);
        assetHandler.loadFromFile("menus/main_menu.txt");
        assetHandler.loadFromFile("menus/loading_screen.txt");
        while (!assetHandler.update()) ; // Load stuff for main menu and loading screen
        this.background = (Texture) assetHandler.getByName("menu_background");
        rootMenu = new ParseMenu("MainMenu.xml");
        rootMenu.setMenuState(this);
        setCurrentMenu(rootMenu);

        this.setMusic();
    }

    public void update() {
       /* if (!AudioHelper.getMusic().equals(music)) {
           this.setMusic();
        } */
    }

    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        //Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sb.setProjectionMatrix(hudCam.combined);
        sb.begin();
        sb.draw(this.background, 0, 0);
        currentMenu.renderMenu(sb, font);
        if (Game.getDebugMode()) {
            font.draw(sb, "MENU STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
            font.draw(sb, "DELTA TIME IN SECONDS: " + Gdx.graphics.getDeltaTime(), 100, 75);
        }
        sb.end();
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(Menu menu) {
        currentMenu = menu;
    }

    public Menu getRootMenu() {
        return rootMenu;
    }

    public void setRootMenu(Menu menu) {
        rootMenu = menu;
    }

    public TextureAtlas getMenuButtonTextures() {
        return menuButtonTextures;
    }

    public void setMenuButtonTextures(TextureAtlas menuButtonTextures) {
        this.menuButtonTextures = menuButtonTextures;
    }

    public void setMusic() {
        // Music setup
        music = Gdx.audio.newMusic(Gdx.files.internal(assetHandler.getFilePath("menu_music") + ".mp3"));
        AudioHelper.setBgMusic(this.music);
        AudioHelper.resetBackgroundMusic();
        AudioHelper.playBgMusic(true);
    }
}

