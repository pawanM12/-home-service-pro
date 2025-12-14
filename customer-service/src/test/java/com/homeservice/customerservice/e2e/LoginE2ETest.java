package com.homeservice.customerservice.e2e;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        // Assume Chrome driver is installed or configured in system path
        // For standard local dev, user might need to setup driver path or use
        // WebDriverManager
        // Here we use basic setup
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI/CD
        options.addArguments("--disable-gpu");
        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            baseUrl = "http://localhost:" + port;
        } catch (Exception e) {
            // Driver might not be available
            System.out.println("ChromeDriver not available, skipping UI test initialization");
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Disabled("Requires locally installed ChromeDriver")
    public void testLoginFlow() {
        if (driver == null)
            return;

        driver.get(baseUrl + "/login");
        assertEquals("Login - HomeService Pro", driver.getTitle());

        // Fill login form (assuming user exists or we register first)
        // Since DB is H2 mem, we might need to register first in this flow
        driver.get(baseUrl + "/register");
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Redirects to login
        assertTrue(driver.getCurrentUrl().contains("/login"));

        // Login
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verify Dashboard
        assertTrue(driver.getCurrentUrl().contains("/dashboard"));
        assertTrue(driver.getPageSource().contains("Welcome, testuser"));
    }
}
