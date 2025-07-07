package io.whalebone.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ProgressBarTest extends UITest{

    // starts the loading bar and stops it at 75%, tests that the stopped value is within 1% from the target
    @Test
    public void testProgress() {
        page.click("text=Progress Bar");
        page.waitForURL("http://uitestingplayground.com/progressbar");
        page.click("#startButton");
        page.waitForCondition(() -> Integer.parseInt(page.locator("#progressBar").getAttribute("aria-valuenow")) >= 75);
        page.click("#stopButton");
        String resultReport = page.locator("#result").textContent();
        int result = Integer.parseInt(resultReport.split(",")[0].split(":")[1].trim());
        Assert.assertTrue(result - 75 <= 1);
    }

}
