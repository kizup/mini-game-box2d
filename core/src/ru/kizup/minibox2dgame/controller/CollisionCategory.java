package ru.kizup.minibox2dgame.controller;

/**
 * Created by Neuron on 25.06.2017.
 */

public class CollisionCategory {

    //0×001, 0×002, 0×004 и 0×008 и т.д.
    public static final short CATEGORY_PLAYER = 0x0001;
    public static final short CATEGORY_SCENERY = 0x0002;
    public static final short CATEGORY_BULLET = 0x0004;

    public static final short MASK_TANK = CATEGORY_BULLET | CATEGORY_PLAYER | CATEGORY_SCENERY;
    public static final short MASK_BULLET = CATEGORY_PLAYER | CATEGORY_SCENERY;
    public static final short MASK_BORDER =  CATEGORY_PLAYER;
    public static final short MASK_SCENERY = CATEGORY_PLAYER | CATEGORY_BULLET;
}
