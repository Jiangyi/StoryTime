package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Andrew on 2015-06-03.
 */
public class HighScoreHandler {

    private static final int SIZE = 5;
    private static ArrayList<HighScore> highScores = new ArrayList<HighScore>(
            Collections.nCopies(5, new HighScore() {{
                setName("");
                setScore(0); }}
            ));
    private static final FileHandle FILE = Gdx.files.local("highScore.dat");

    // Initializing Json as an expression. Double brace syntax FTW.
    private static Json json = new Json() {{
        setTypeName(null);
        setUsePrototypes(false);
        setIgnoreUnknownFields(true);
        setOutputType(JsonWriter.OutputType.json);
    }};

    public static void loadFromDisk() {
        if (FILE.exists()) {
            ArrayList<JsonValue> jsonList = json.fromJson(ArrayList.class, FILE);

            for (int i = 0; i < SIZE; i++) {
                highScores.set(i, json.readValue(HighScore.class, jsonList.get(i)));
            }
        } else {
            System.out.println("High score file does not exist!");
        }
    }

    public static void flushToDisk() {
        FILE.writeString(json.prettyPrint(highScores), false);
    }

    public static boolean addHighScore(String name, int score) {
        for (int i = 0; i < SIZE; i++) {
            if (highScores.get(i).getScore() < score) {
                HighScore tempScore = new HighScore();
                tempScore.setName(name);
                tempScore.setScore(score);
                highScores.add(i, tempScore);
                highScores.subList(5, highScores.size()).clear();
                flushToDisk();
                return true;
            }
        }
        return false;
    }

    public static ArrayList<HighScore> getHighScores() {
        if (highScores.get(0).getScore() == 0 && FILE.exists()) {
            loadFromDisk();
        }
        return highScores;
    }

    public static int getLowestScore() {
        return highScores.get(SIZE - 1).getScore();
    }
    public static class HighScore {
        private String name;
        private int score;

        public HighScore() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }
}
