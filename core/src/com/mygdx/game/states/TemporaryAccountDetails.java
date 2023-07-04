package com.mygdx.game.states;

import com.mygdx.game.states.TasksState.TaskContainer;

import java.util.ArrayList;
import java.util.List;

public class TemporaryAccountDetails {
    private Integer bestScore;
    private Integer coins;
    private List<TaskContainer> tasks;

    private List<PlayerStatistic> statistics;

    public TemporaryAccountDetails(){
        tasks = new ArrayList<>();
        statistics = new ArrayList<>();
        statistics.add(new PlayerStatistic("SPEED", 0, 20, 75,450));
        statistics.add(new PlayerStatistic("HEALTH", 0, 20, 100, 150));
        statistics.add(new PlayerStatistic("ARMOR", 0, 5, 100, 80));
        statistics.add(new PlayerStatistic("ARMOR EFFECTIVENESS", 0, 5, 50, 30));
        statistics.add(new PlayerStatistic("ARMOR DURATION", 0, -0.5f, 50, 6));
        statistics.add(new PlayerStatistic("ARMOR INCREASE RATE", 0, 0.02f, 50, 0.05f));
        statistics.add(new PlayerStatistic("Q", 0, 0.01f, 150, 0.05f)); //TODO: configure
        statistics.add(new PlayerStatistic("W", 0, 0.5f, 150, 7));
        statistics.add(new PlayerStatistic("E", 0, 0.25f, 150, 1));
        bestScore = 0;
        coins = 0;
    }
    public Integer getBestScore() {
        return bestScore;
    }

    public List<TaskContainer> getTasks() {
        return tasks;
    }

    public List<PlayerStatistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<PlayerStatistic> statistics) {
        this.statistics = statistics;
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
