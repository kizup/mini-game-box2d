package ru.kizup.minibox2dgame.controller;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import javax.swing.Box;

import ru.kizup.minibox2dgame.model.BoxProp;
import ru.kizup.minibox2dgame.model.Bullet;
import ru.kizup.minibox2dgame.model.Tank;

/**
 * Created by Neuron on 25.06.2017.
 */

public class ContactWorldListener implements ContactListener {

    private static final String TAG = "ContactWorldListener";

    public ContactWorldListener(){
        super();
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
        if(contact.getFixtureA() != null && contact.getFixtureA().getBody().getUserData() != null)
            bodyB = contact.getFixtureA().getBody();
        if(contact.getFixtureB() != null && contact.getFixtureB().getBody().getUserData() != null)
            bodyA = contact.getFixtureB().getBody();

        if(bodyA != null && bodyB != null){
            if((bodyA.getUserData() instanceof Tank || bodyA.getUserData() instanceof BoxProp) && bodyB.getUserData() instanceof Bullet) {
                Bullet bullet = (Bullet) bodyB.getUserData();
                bullet.destroy();

                if(bodyA.getUserData() instanceof Tank)
                    ((Tank) bodyA.getUserData()).minHP(bullet.getDamage());
            }
            else if(bodyA.getUserData() instanceof Bullet && bodyB.getUserData() instanceof Tank || bodyB.getUserData() instanceof BoxProp) {
                Bullet bullet = (Bullet) bodyA.getUserData();
                bullet.destroy();

                if(bodyB.getUserData() instanceof Tank)
                    ((Tank) bodyB.getUserData()).minHP(bullet.getDamage());
            }
        }
    }
}
