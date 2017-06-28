package ru.kizup.minibox2dgame.controller;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import ru.kizup.minibox2dgame.model.Border;
import ru.kizup.minibox2dgame.model.Bullet;
import ru.kizup.minibox2dgame.model.tank.Tank;

/**
 * Created by Neuron on 25.06.2017.
 */

public class ContactWorldListener implements ContactListener {

    private static final String TAG = "ContactWorldListener";

    public ContactWorldListener() {
    }

    @Override
    public void beginContact(Contact contact) {
//        Gdx.app.log(TAG, "beginContact");
    }

    @Override
    public void endContact(Contact contact) {
//        Gdx.app.log(TAG, "endContact");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
//        Gdx.app.log(TAG, "preSolve");
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Body bodyA = null, bodyB = null;
        if (contact.getFixtureA() != null && contact.getFixtureA().getBody().getUserData() != null)
            bodyB = contact.getFixtureA().getBody();
        if (contact.getFixtureB() != null && contact.getFixtureB().getBody().getUserData() != null)
            bodyA = contact.getFixtureB().getBody();

        if (bodyA != null && bodyB != null) {
            if ((bodyA.getUserData() instanceof Tank || bodyA.getUserData() instanceof Border) && bodyB.getUserData() instanceof Bullet) {
                Bullet bullet = (Bullet) bodyB.getUserData();
                bullet.destroy();
                Gdx.app.log("Test", "Inertia " + bodyA.getInertia());

                if (bodyA.getUserData() instanceof Tank) {
                    Tank tank = (Tank) bodyA.getUserData();

                    if (!tank.isEnemy() && bullet.getOwner().isEnemy()) {
                        tank.takeDamage(bullet.getDamage());
                    } else if (tank.isEnemy() && !bullet.getOwner().isEnemy()) {
                        tank.takeDamage(bullet.getDamage());
                    }
                }

            } else if (bodyA.getUserData() instanceof Bullet && (bodyB.getUserData() instanceof Tank || bodyB.getUserData() instanceof Border)) {
                Bullet bullet = (Bullet) bodyA.getUserData();
                bullet.destroy();

                Gdx.app.log("Test", "Inertia " + bodyB.getInertia());

                if (bodyB.getUserData() instanceof Tank) {
                    Tank tank = (Tank) bodyB.getUserData();
                    if (!tank.isEnemy() && bullet.getOwner().isEnemy()) {
                        tank.takeDamage(bullet.getDamage());
                    } else if (tank.isEnemy() && !bullet.getOwner().isEnemy()) {
                        tank.takeDamage(bullet.getDamage());
                    }
//                    tank.takeDamage(bullet.getDamage());
                }
            }
        }
    }
}
