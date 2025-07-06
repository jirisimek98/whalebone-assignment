package io.whalebone.teams.utils;

import java.util.List;
import java.util.Map;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class RosterScraper {

    private static Playwright playwright;
    private static Browser browser;

    private static BrowserContext context;
    private static Page page;

    public static Map<String, Integer> countCanadiansVsAmericans(String uri) {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        int can = 0;
        int usa = 0;

        page.navigate(uri);
        page.waitForSelector(".rt-tr");

        Locator players = page.locator(".rt-tr");
        for (int i = 0; i < players.count(); i++) {
            Locator currentPlayer = players.nth(i);
            List<String> playerInfo = currentPlayer.locator(".rt-td").allInnerTexts();
            if (playerInfo.size() > 0) {
                String birthPlace = playerInfo.get(7);
                if (birthPlace.endsWith("CAN")) {
                    can++;
                } else if (birthPlace.endsWith("USA")) {  
                    usa++;
                }
            }
        }

        context.close();
        browser.close();
        playwright.close();

        return Map.of("CAN", can, "USA", usa);
    }

}
