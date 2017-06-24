package ru.kizup.minibox2dgame.model;


import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Neuron on 25.06.2017.
 */

public class ContactWorldListener implements ContactListener {

    private static final String TAG = "ContactWorldListener";

    private World world;

    public ContactWorldListener(World world){
        super();
        this.world = world;
    }

    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
//        Body body = null;
//        if(contact.getFixtureA() != null && contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() instanceof Bullet)
//            body = contact.getFixtureA().getBody();
//
//        if(contact.getFixtureB() != null && contact.getFixtureB().getUserData() != null  && contact.getFixtureB().getUserData() instanceof Bullet)
//            body = contact.getFixtureB().getBody();
//        if(body != null){
//            body.setActive(false);
//            world.destroyBody(body);
//        }
    }
}
