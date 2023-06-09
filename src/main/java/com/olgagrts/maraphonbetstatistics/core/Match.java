package com.olgagrts.maraphonbetstatistics.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 (матч это когда есть 2 команды Team 1 vs Team 2), которые ожидаются.
 - время начала,
 - названия команд,
 - турнир,
 - вид спорта,
 - ссылка на событие
 */

@ToString
@Setter
@Getter
public class Match {
    String sport;
    String urlToEvent;
    String startTime;
    String commands;
    String tournament;
}
