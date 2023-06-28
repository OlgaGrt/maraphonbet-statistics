package com.olgagrts.maraphonbetstatistics.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;


@Service
public class ReportGenerator {

    private static final String MARATHON_BET_URL = "https://www.marathonbet.by/su/betting/?cpcids=all&cppcids=all&interval=ALL_TIME";

    Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    public List<Match> makeReport() throws IOException {

        List<Match> allMatches = new LinkedList<>();

        Document document = null;
        document = Jsoup.connect(MARATHON_BET_URL).get();
        Element content = document.selectFirst("#events_content > div:nth-child(1) > div");

        Elements elements = content.getElementsByClass("sport-category-container");

        for (Element element : elements) {
            String sportCategoryName = element.getElementsByClass("sport-category-header").first().getElementsByTag("a").first().select("a").text();
            logger.info("sportCategoryName: " + sportCategoryName);
            Elements tournaments = element.getElementsByClass("sport-category-content");
            for (Element tournamentContainer : tournaments) {
                Elements tournament = tournamentContainer.getElementsByClass("category-container collapsed");

                for (Element value : tournament) {
                    try {
                        extractMatchesForEachTournament(allMatches, sportCategoryName, value);
                    } catch (InterruptedException | IOException e) {
                        // go to next child url
                    }
                }
            }
        }
        return allMatches;
    }

    private void extractMatchesForEachTournament(List<Match> allMatches, String sportCategoryName, Element game) throws InterruptedException, IOException {
        Element table = game.getElementsByClass("category-header").first();
        String tourniquetLink = table.getElementsByClass("show-category-events").first().getElementsByTag("a").attr("href");

        String fullTourniquetLinkUrl = "https://www.marathonbet.by" + tourniquetLink;
        sleep(1);
        Document tourniquetDocument = Jsoup.connect(fullTourniquetLinkUrl).get();
        Elements tournamentNameElements = tourniquetDocument.getElementsByClass("category-label").first().getElementsByTag("h2").first().getElementsByTag("span");
        StringBuilder tournamentName = new StringBuilder("");
        for (Element sss1 : tournamentNameElements) {
            tournamentName.append(sss1.text());
        }
        logger.info("tournamentName: " + tournamentName);

        Elements pairGames = tourniquetDocument.getElementsByClass("bg coupon-row");
        for (Element pairGame : pairGames) {
            extractAllTournamentGames(allMatches, sportCategoryName, fullTourniquetLinkUrl, tournamentName, pairGame);
        }
    }

    private void extractAllTournamentGames(List<Match> allMatches, String sportCategoryName, String fullTourniquetLinkUrl, StringBuilder tournamentName, Element pairGame) {
        String pairGameName = pairGame.getElementsByClass("bg coupon-row").first().attr("data-event-name");
        logger.info("pairGames: " + pairGameName);


        String url2 = pairGame.getElementsByClass("bg coupon-row").first().attr("data-event-path");

        Match match = Match.builder()
                .sport(sportCategoryName)
                .urlToEvent("https://www.marathonbet.by/su/betting/" + url2)
                .tournament(String.valueOf(tournamentName))
                .commands(pairGameName)
                .build();

        parseMatchDate(pairGame, match);
        allMatches.add(match);
    }

    private void parseMatchDate(Element pairGame, Match match) {
        if (pairGame.getElementsByClass("date date-short").first() != null) {
            String time = pairGame.getElementsByClass("date date-short").first().text();
            if (time.length() > 10) {
                String date = time.substring(0, 2);
                String month = time.substring(3, 6);
                String parsedTime = time.substring(7, 12);
                month = getMonth(month);
                match.setStartTime(parsedTime + "/" + date + "/" + month);
                logger.info(match.getStartTime());
            } else {
                LocalDate a = LocalDate.now();
                String dateWithMonth = time + "/" + a.getDayOfMonth() + "/" + a.getMonthValue();
                match.setStartTime(dateWithMonth);
                logger.info(match.getStartTime());
            }

        } else if (pairGame.getElementsByClass("date date-with-month").first() != null) {
            String russianDate = pairGame.getElementsByClass("date date-with-month").first().text();
            String date = russianDate.substring(0, 2);
            String month = russianDate.substring(3, 6);
            String time = russianDate.substring(7, 12);

            month = getMonth(month);

            logger.info(" date: " + date);
            match.setStartTime(time + "/" + date + "/" + month);
        }
    }

    private String getMonth(String month) {
        switch (month) {
            case "июн" -> month = "6";
            case "июл" -> month = "7";
            case "авг" -> month = "8";
        }
        return month;
    }
}
