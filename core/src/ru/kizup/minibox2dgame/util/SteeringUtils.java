package ru.kizup.minibox2dgame.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Neuron on 29.06.2017.
 */

public class SteeringUtils {

    public static float vectoreToAngle(Vector2 vector2){
        return (float)Math.atan2(-vector2.x, vector2.y);
    }

    public static Vector2 angleToVectore(Vector2 outVector, float angle){
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }
}
