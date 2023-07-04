package com.mygdx.game.states;

public class PlayerStatistic {
    private String name;
    private int upgradeLevel;

    private Integer upgradeCost;
    private float upgradeValue;
    private float value;

    public PlayerStatistic(String name, int upgrade, float upgradeValue, Integer upgradeCost, float value) {
        this.name = name;
        this.upgradeLevel = upgrade;
        this.upgradeCost = upgradeCost;
        this.upgradeValue = upgradeValue;
        this.value = value;
    }
    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public Integer getUpgradeCost() {
        return upgradeCost;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public float getUpgradeValue() {
        return upgradeValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
