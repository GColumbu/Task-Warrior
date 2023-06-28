package com.mygdx.game.states;

public class TemporaryAccountDetails {
    Integer bestScore;
    Integer coins;

    public TemporaryAccountDetails(){
        bestScore = 0;
        coins = 0;
    }
    public Integer getBestScore() {
        return bestScore;
    }

    public void setBestScore(Integer bestScore) {
        this.bestScore = bestScore;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

}
