package ru.kizup.minibox2dgame.model.factory;

import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.controller.CollisionCategory;
import ru.kizup.minibox2dgame.model.Border;

/**
 * Created by dmitry on 27.06.2017.
 */

public class BordersFactory extends BaseFactory {

    private static BordersFactory sInstance = null;

    private BordersFactory() {}

    public static BordersFactory getInstance() {
        if (sInstance == null) sInstance = new BordersFactory();
        return sInstance;
    }

    public void initFactory(float w, float h) {
        width = w;
        height = h;
    }

    public Border createBottomBorder(World world) {
        return new Border(width, 1, width / 2, 0.5f, world, CollisionCategory.MASK_BORDER);
    }

    public Border createTopBorder(World world) {
        return new Border(width, 1, width/ 2, height - 0.5f, world, CollisionCategory.MASK_BORDER);
    }

    public Border createLeftBorder(World world) {
        return new Border(1, height, 0.5f, height / 2, world, CollisionCategory.MASK_BORDER);
    }

    public Border createRightBorder(World world) {
        return new Border(1, height, width - 0.5f, height / 2, world, CollisionCategory.MASK_BORDER);
    }

}
