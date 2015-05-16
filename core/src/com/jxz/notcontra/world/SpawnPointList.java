package com.jxz.notcontra.world;


import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Samuel on 15/05/2015.
 * Holds list of polylines for spawning purposes.
 */
public class SpawnPointList {
    // Local data structure for spawn points
    private class SpawnPointLine {
        public SpawnPointLine(Polyline line, float currentIndex) {
            this.line = line;
            index = currentIndex;
        }
        public Polyline line;
        public float index;
    }

    // Fields
    private Array<SpawnPointLine> spawnList;
    private float currentIndex;

    // Constructor
    public SpawnPointList() {
        spawnList = new Array<SpawnPointLine>();
        currentIndex = 0;
    }

    public void addLine(PolylineMapObject line) {
        SpawnPointLine spawnLine = new SpawnPointLine(line.getPolyline(), currentIndex);
        spawnList.add(spawnLine);
        currentIndex += spawnLine.line.getLength();
    }

    public Vector2 randomSpawn() {
        // Randomize target line, weighted by length
        float lineIndex = MathUtils.random(0, currentIndex);
        SpawnPointLine targetLine = spawnList.get(spawnList.size - 1);

        // Iterate through lines to find the proper line
        for (int i = 0; i < spawnList.size; i++) {
            if (i < spawnList.size - 1) {
                if (lineIndex > spawnList.get(i).index && lineIndex < spawnList.get(i + 1).index) {
                    targetLine = spawnList.get(i);
                }
            }
        }

        // Return a random position between the two vertices of the line
        return new Vector2(MathUtils.random(targetLine.line.getTransformedVertices()[0], targetLine.line.getTransformedVertices()[2]), MathUtils.random(targetLine.line.getTransformedVertices()[1], targetLine.line.getTransformedVertices()[3]));
    }
}
