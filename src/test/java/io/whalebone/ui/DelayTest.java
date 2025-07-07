package io.whalebone.ui;

import org.testng.annotations.Test;

import com.microsoft.playwright.Page.WaitForURLOptions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class DelayTest extends UITest {

    // tests that the delayed page loads in a reasonable time
    @Test
    public void testDelay() {
        page.navigate("http://uitestingplayground.com/");
        page.click("text=Load Delay");
        page.waitForURL("http://uitestingplayground.com/loaddelay", new WaitForURLOptions().setTimeout(10000));
        PlaywrightAssertions.assertThat(page.locator("text=Button Appearing After Delay")).isVisible();
    }

}
