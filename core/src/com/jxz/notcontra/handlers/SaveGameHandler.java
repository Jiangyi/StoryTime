package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.entity.PlayerSave;

/**
 * Created by Andrew on 2015-04-19.
 */
public class SaveGameHandler {

    private static EntityManager entityManager = EntityManager.getInstance();

    public static void saveCurrentStateToFile(String filename) {
        FileHandle file = Gdx.files.local("saves/" + filename);

        Player player = GameStateManager.getInstance().getPlayState().getPlayer();
        PlayerSave playerSave = new PlayerSave();
        playerSave.setPosition(player.getPosition());
        playerSave.setHealth(player.getHealth());
        playerSave.setMana(player.getMana());
        playerSave.setName(player.getName());
        playerSave.setScore(player.getScore());
        playerSave.setWave(player.getCurrentLevel().getCurrentWave());
        playerSave.setTimeSurvived(GameStateManager.getInstance().getPlayState().getTimeSurvived());
        playerSave.setMode(GameStateManager.getInstance().getGame().getPlayMode().toString());

        Json json = new Json();
        json.setTypeName(null);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);

        file.writeString(json.prettyPrint(playerSave), false);

    }

    public static PlayerSave loadSave(String filename) {
        FileHandle file = Gdx.files.local("saves/" + filename);
        if (file.exists()) {
            Json json = new Json();
            json.setTypeName(null);
            json.setUsePrototypes(false);
            json.setIgnoreUnknownFields(true);
            json.setOutputType(JsonWriter.OutputType.json);

            return json.fromJson(PlayerSave.class, file);
        } else {
            System.out.println("Savefile does not exist!");
        }
        return null;
    }
}
