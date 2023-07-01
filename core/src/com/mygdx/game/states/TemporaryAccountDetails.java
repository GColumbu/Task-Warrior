package com.mygdx.game.states;

import com.mygdx.game.states.TasksState.Task;
import com.mygdx.game.states.TasksState.TaskContainer;

import java.util.ArrayList;
import java.util.List;

public class TemporaryAccountDetails {
    private Integer bestScore;
    private Integer coins;
    private List<TaskContainer> tasks;

    public TemporaryAccountDetails(){
        tasks = new ArrayList<>();
        bestScore = 0;
        coins = 0;
    }
    public Integer getBestScore() {
        return bestScore;
    }

    public List<TaskContainer> getTasks() {
        return tasks;
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
