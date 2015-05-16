package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.menu.Menu;

/**
 * Created by Samuel on 2015-05-08.
 */
public class MenuState extends GameState {

    private AssetHandler assetHandler = AssetHandler.getInstance();
    private Menu currentMenu;
    private TextureAtlas menuButtonTextures;

    public MenuState(Game game) {
        super(game);
        assetHandler.loadFromFile("textures/menu/main_menu.txt");
        assetHandler.loadFromFile("textures/menu/loading_screen.txt");
        while (!assetHandler.update()) ; // Load stuff for main menu and loading screen
        menuButtonTextures = (TextureAtlas) assetHandler.getByName("menu_buttons");
        currentMenu = new Menu(this, "MainMenu.xml");

    }

    public void update() {
        // Do nothing
    }

    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        //Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sb.setProjectionMatrix(hudCam.combined);
        sb.begin();
        currentMenu.renderMenu(sb);
        font.draw(sb, "MENU STATE... FPS: " + Gdx.graphics.getFramesPerSecond(), 100, 100);
        font.draw(sb, "DELTA TIME IN SECONDS: " + Gdx.graphics.getDeltaTime(), 100, 75);

        sb.end();
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(Menu menu) {
        currentMenu = menu;
    }

    public TextureAtlas getMenuButtonTextures() {
        return menuButtonTextures;
    }

    public void setMenuButtonTextures(TextureAtlas menuButtonTextures) {
        this.menuButtonTextures = menuButtonTextures;
    }
}

