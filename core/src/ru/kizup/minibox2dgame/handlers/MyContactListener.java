package ru.kizup.minibox2dgame.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by padmitriy on 20.06.17.
 */

public class MyContactListener implements ContactListener {

    //called when two fixtures start to collide
    @Override
    public void beginContact(Contact contact) {

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        System.out.println("contact of " + fa.getUserData() + " and " + fb.getUserData());

    }


    //called when two fixtures no longer collide
    @Override
    public void endContact(Contact contact) {
        System.out.println("end contact");

    }


    //collision detection
    //presolve here
    //collision handling
    //postsolve here
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
