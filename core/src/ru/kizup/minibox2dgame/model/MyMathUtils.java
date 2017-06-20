package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Дмитрий on 20.06.2017.
 */
public class MyMathUtils {

//        /**
//         *
//         * rotate vector
//         * @param {Array} vector [v0, v1]
//         * @param {Number} angle to rotate vector by, radians. can be negative
//         * @returns {Array} rotated vector [v0, v1]
//         */
//        exports.rotate=function(v, angle){
//            angle=math.normaliseRadians(angle);
//            return [v[0]* Math.cos(angle)-v[1]*Math.sin(angle),
//                    v[0]* Math.sin(angle)+v[1]*Math.cos(angle)];
//
//        };

    public static Vector2 rotate(Vector2 vector, float angle) {
        angle = (float) normalizeRadians(angle);
        float x = (float) (vector.x * Math.cos(angle) - vector.y * Math.sin(angle));
        float y = (float) (vector.x * Math.sin(angle) + vector.y * Math.cos(angle));
        return new Vector2(x, y);
    }


//        /**
//         *
//         * absolute angle to relative angle, in radians
//         * @param {Number} absolute angle in radians
//         * @returns {Number} relative angle in radians
//         */
//        exports.normaliseRadians=function(radians){
//            radians=radians % (2*Math.PI);
//            if(radians<0) {
//                radians+=(2*Math.PI);
//            }
//            return radians;
//        };
    public static double normalizeRadians(double radians) {
        radians = radians % (2 * Math.PI);
        if (radians < 0) {
            radians += (2 * Math.PI);
        }
        return radians;
    }

//    /**
//     *
//     * calculate vector dot product
//     * @param {Array} vector [v0, v1]
//     * @param {Array} vector [v0, v1]
//     * @returns {Number} dot product of v1 and v2
//     */
//    var dot = exports.dot=function(v1, v2){
//        return (v1[0] * v2[0]) + (v1[1] * v2[1]);
//    };

    public static double dot(Vector2 v1, Vector2 v2) {
        return (v1.x * v2.x) + (v1.y * v2.y);
    }

//    /**
//     * @param {Array} vector [v0, v1]
//     * @returns {Number} length of vector
//     */
//    var len = exports.len = function(v) {
//        return Math.sqrt(v[0]*v[0] + v[1]*v[1]);
//    };

    public static double len(Vector2 v) {
        return Math.sqrt(v.x * v.x + v.y * v.y);
    }

//    /**
//     *
//     * normalize vector to unit vector
//     * @param {Array} vector [v0, v1]
//     * @returns {Array} unit vector [v0, v1]
//     */
//    var unit = exports.unit = function(v) {
//        var l = len(v);
//        if(l) {
//            return [v[0] / l, v[1] / l];
//        }
//        return [0, 0];
//    };

    public static Vector2 unit(Vector2 v) {
        double l = len(v);

        if (l > 0) {
            float x = (float) (v.x / l);
            float y = (float) (v.y / l);
            return new Vector2(x, y);
        } else {
            return new Vector2(0, 0);
        }
    }

}
