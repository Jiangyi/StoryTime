package com.jxz.notcontra.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.AssetHandler;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.LoadingBar;

/**
 * Created by Kevin Xiao on 2015-04-09.
 * Load State
 */
public class LoadState extends GameState {

    TextureAtlas loadAtlas;
    private Sprite logo_libgdx;
    private BitmapFont font = new BitmapFont();
    private AssetHandler assetHandler = AssetHandler.getInstance();
    private LoadingBar loadingBar;
    private float progress = 0f;
    private GlyphLayout layout;
    private boolean isEnteringGame;
    private boolean isDoneLoading;

    private final String LOADING = "Loading Game: ";
    private final String DONE_LOADING = "Done Loading! Press ESC to enter game.";

    public LoadState(Game game) {
        super(game);
        // Load and parse assets
        loadingBar = new LoadingBar();
        loadAtlas = (TextureAtlas) assetHandler.getByName("menu_loadingbar");
        logo_libgdx = loadAtlas.createSprite("libgdx");
        layout = new GlyphLayout();
    }

    public void load(String... filePaths) {
        isEnteringGame = true;
        // Load general level file
        assetHandler.loadFromFile("levels/general.txt");
        for (String i : filePaths) {
            assetHandler.loadFromFile(i);
        }
    }

    public void update() {
        loadingBar.update();
        isDoneLoading = assetHandler.update();
        if (isDoneLoading && !isEnteringGame) {
            GameStateManager.getInstance().setState(GameStateManager.State.MENU);
        }
    }

    public void render() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        assetHandler.update();
        progress = Interpolation.swingOut.apply(progress, assetHandler.getProgress(), 0.06f);

        sb.begin();
        //Draw libgdx logo
        sb.draw(logo_libgdx, Game.VID_WIDTH - logo_libgdx.getWidth() / 2 - 5, 5, 150, 25);
        // Drawing loading frame and bar
        sb.draw(loadingBar.getFrameSprite(), loadingBar.getPositionX(), loadingBar.getPositionY(), loadingBar.getBarSprite().getWidth(), loadingBar.getBarSprite().getHeight());
        sb.draw(loadingBar.getBarSprite().getTexture(),
                loadingBar.getPositionX(),
                loadingBar.getPositionY(),
                (progress > 0.995f ? loadingBar.getBarSprite().getWidth() : loadingBar.getBarSprite().getWidth() * progress),
                loadingBar.getBarSprite().getHeight(),
                loadingBar.getBarSprite().getRegionX(),
                loadingBar.getBarSprite().getRegionY(),
                (progress > 0.995f ? (loadingBar.getBarSprite().getRegionWidth()) : Math.round(loadingBar.getBarSprite().getRegionWidth() * progress)),
                loadingBar.getBarSprite().getRegionHeight(), false, false);
        // Draw fonts
        layout.setText(font, LOADING + Math.round(progress * 100) + "%");
        font.draw(sb, layout, Game.VID_WIDTH / 2 - layout.width / 2, Game.VID_HEIGHT / 2 + 45);
        layout.setText(font, DONE_LOADING);
        if (Math.round(progress * 100) == 100) {
            font.draw(sb, layout, Game.VID_WIDTH / 2 - layout.width / 2, Game.VID_HEIGHT / 2 - (45 - layout.height));
        }

        sb.end();
    }

    public boolean getIsDoneLoading() {
        return isDoneLoading;
    }

    public boolean getIsEnteringGame() {
        return isEnteringGame;
    }
}