package io.whalebone.ui;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class SampleAppTests extends UITest {

    @Override
    @BeforeMethod
    void setupApp() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate("http://uitestingplayground.com/");
        page.click("text=Sample App");
        page.waitForURL("http://uitestingplayground.com/sampleapp");
    }

    // helper method to fill the login form
    private void inputCredentials(String username, String password) {
        page.fill("input[name='UserName']", username);
        page.fill("input[name='Password']", password);
        page.click("#login");
    }

    // verifies login with valid credentials and the logout functionality
    @Test
    public void testValidLogin() {
        inputCredentials("Jirik", "pwd");
        PlaywrightAssertions.assertThat(page.locator("#loginstatus")).containsText("Welcome, Jirik!");

        page.click("#login");
        PlaywrightAssertions.assertThat(page.locator("#loginstatus")).containsText("User logged out.");
    }

    // verifies that the login fails with incorrect password
    @Test
    public void testValidUsernameInvalidPassword() {
        inputCredentials("User", "incorrect_password");
        PlaywrightAssertions.assertThat(page.locator("#loginstatus")).containsText("Invalid username/password");
    }

    // verifies that the login fails with an empty username
    @Test
    public void testInvalidUsernameValidPassword() {
        inputCredentials("", "pwd");
        PlaywrightAssertions.assertThat(page.locator("#loginstatus")).containsText("Invalid username/password");
    }

    // verifies that the login fails with both empty username and invalid password
    @Test
    public void testInvalidUsernameInalidPassword() {
        inputCredentials("", "incorrect_password");
        PlaywrightAssertions.assertThat(page.locator("#loginstatus")).containsText("Invalid username/password");
    }

}
