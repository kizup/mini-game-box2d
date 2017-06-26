package ru.kizup.minibox2dgame.model;

/**
 * Created by Neuron on 25.06.2017.
 */

public class OptionsBullet {

    private int damage;
    private int armorDamage; //Не используется
    private float scale;
    private String nameTexture;
    private String pathTexture;

    public OptionsBullet(int damage, int armorDamage, float scale, String nameTexture, String pathTexture) {
        this.damage = damage;
        this.armorDamage = armorDamage;
        this.scale = scale;
        this.nameTexture = nameTexture;
        this.pathTexture = pathTexture;
    }

    public static OptionsBullet getBullet(Type type){
        switch (type){
            case YBR_365P: return new OptionsBullet(500, 0, 3f, "YBR-365P", "bullet.json");
            case YBR_365: return new OptionsBullet(500, 0, 3f, "YBR-365", "bullet.json");
            case YBR_365K: return new OptionsBullet(500, 0, 3f, "YBR-365K", "bullet.json");
        }
        throw new NullPointerException("Неправильный тип bullet");
    }

    public enum Type{
        YBR_365P,
        YBR_365,
        YBR_365K
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getArmorDamage() {
        return armorDamage;
    }

    public void setArmorDamage(int armorDamage) {
        this.armorDamage = armorDamage;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getNameTexture() {
        return nameTexture;
    }

    public void setNameTexture(String nameTexture) {
        this.nameTexture = nameTexture;
    }

    public String getPathTexture() {
        return pathTexture;
    }

    public void setPathTexture(String pathTexture) {
        this.pathTexture = pathTexture;
    }
}
