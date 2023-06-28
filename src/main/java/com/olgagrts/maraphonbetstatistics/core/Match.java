package com.olgagrts.maraphonbetstatistics.core;

import lombok.Builder;
import lombok.Data;

/*
 (матч это когда есть 2 команды Team 1 vs Team 2), которые ожидаются.
 - время начала,
 - названия команд,
 - турнир,
 - вид спорта,
 - ссылка на событие
 */
@Builder
@Data
public class Match {
    String sport;
    String urlToEvent;
    String startTime;
    String commands;
    String tournament;
}
