package ru.kizup.minibox2dgame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Neuron on 24.06.2017.
 */

public class Util {

    //Получаем угол между двумя точками
    public static float getAngle(Vector2 point1, Vector2 point2){
        float A = (float) (Math.atan2(point1.y - point2.y, point1.x - point2.x) / Math.PI * 180);
        return  (A < 0) ? A + 360 : A;
    }

    //Нормализуем угол. 365 => 5, -100 => 260
    public static float normalizeAngle(float angle) {
        double pi = Math.PI;
        while (angle > pi)
            angle -= 2 * pi;
        while (angle < -pi)
            angle += 2 * pi;

        return angle;
    }

    //Получаем знак числа
    public static int sign(int i) {
        if (i == 0) return 0;
        if (i >> 31 != 0) return -1;
        return +1;
    }

    public  static float getAngleRotationToTarget(Vector2 targetVector, Body body, float rotationCoeff, float speed) {
        if (targetVector == null) return 0;

        float delta = Gdx.graphics.getDeltaTime();
        float angle = (float) Math.atan2(targetVector.y - body.getPosition().y, targetVector.x - body.getPosition().x);

        float angleBetween = angle < 0 ? (float) (angle + Math.toRadians(360)) : angle;
        float angleTurret = body.getAngle() < 0 ? (float) (body.getAngle() + Math.toRadians(360)) : body.getAngle();
        angleTurret -= Math.toRadians(90);
        float speedRotation;
        if (Math.abs(angleTurret - angleBetween) < 0.01) //  0.1 - возможнная разница углов, для устранения частого обновления
            speedRotation = 0;
        else if (angleBetween >= angleTurret) {
            // Умножение на delta для плавности вращения башни, скорость регулируется параметром rotationCoeff
            speedRotation = angleBetween - angleTurret > (angleTurret + Math.toRadians(360) - angleBetween)
                    ? -rotationCoeff * speed
                    : rotationCoeff * speed * delta;
        } else {
            // Умножение на delta для плавности вращения башни, скорость регулируется параметром rotationCoeff
            speedRotation = angleTurret - angleBetween > (angleBetween + Math.toRadians(360) - angleTurret)
                    ? rotationCoeff * speed * delta
                    : -rotationCoeff * speed * delta;
        }

        return speedRotation;
    }
}
