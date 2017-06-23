package ru.kizup.minibox2dgame.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Neuron on 24.06.2017.
 */

public class Util {

    public static float getAngle(Vector2 point1, Vector2 point2){
        float A = (float) (Math.atan2(point1.y - point2.y, point1.x - point2.x) / Math.PI * 180);
        return  (A < 0) ? A + 360 : A;
    }

    public static float normalizeAngle(float angle) {
        double pi = Math.PI;
        while (angle > pi)
            angle -= 2 * pi;
        while (angle < -pi)
            angle += 2 * pi;

        return angle;
    }

    public static int sign(int i) {
        if (i == 0) return 0;
        if (i >> 31 != 0) return -1;
        return +1;
    }
}
