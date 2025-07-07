package io.whalebone.ui;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

// base class for UI tests with playwright, initiates playwright resources before each test
public class UITest {

    protected static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;

    @BeforeClass
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @AfterClass
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeMethod
    void setupApp() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate("http://uitestingplayground.com/");
    }

    @AfterMethod
    void closeContext() {
        context.close();
    }

}
