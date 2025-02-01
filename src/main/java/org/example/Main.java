package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("🔄 Running Selenium Automation Task...");
            checkTasks(); // Run the automation
        }, 0, 10, TimeUnit.MINUTES); // Adjust time interval as needed
    }

    public static void waitUntilFullyLoad(WebDriver driver) {
        // Wait until the page is fully loaded
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    public static void checkTasks() {
        // Automatically set up ChromeDriver
        WebDriverManager.chromedriver().driverVersion("132.0.6834.159").setup(); // Specify driver version

        // Set Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start maximized
        options.addArguments("--headless"); // Run in headless mode for GitHub Actions
        options.addArguments("--no-sandbox"); // Required for GitHub Actions
        options.addArguments("--disable-dev-shm-usage"); // Required for GitHub Actions

        // Initialize WebDriver
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open the login page
            driver.get("https://app.outlier.ai/en/expert/login"); // Replace with actual login URL
            waitUntilFullyLoad(driver); // Wait for the page to load

            // Locate the username and password fields, and set the credentials
            WebElement usernameField = driver.findElement(By.id("email"));
            WebElement passwordField = driver.findElement(By.id("password"));

            usernameField.sendKeys("amrfawzi182000@gmail.com"); // Username
            passwordField.sendKeys("outlier@182000"); // Password

            // Locate the login button and click it
            WebElement loginButton = driver.findElement(By.xpath("//button[@class='scaleui inline-flex items-center rounded border-0 font-medium shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 transition-all px-4 h-9 text-sm w-full justify-center text-neutral-0 bg-primary-400 hover:bg-primary-500 focus:ring-primary-400 mt-4']"));
            loginButton.click();

            waitUntilFullyLoad(driver); // Wait for page load after login

            // Navigate to the dashboard and marketplace
            driver.get("https://app.outlier.ai/dashboard?verificationredirect=%2Fexpert");
            waitUntilFullyLoad(driver);

            driver.get("https://app.outlier.ai/marketplace");
            waitUntilFullyLoad(driver);

            Thread.sleep(10000); // Wait for page elements to load

            // Check if the "no projects" message exists
            List<WebElement> noProjectsMessage = driver.findElements(By.xpath("//div[contains(text(), 'There are no available projects that match your qualifications at this time.')]"));
            if (!noProjectsMessage.isEmpty()) {
                System.out.println("No projects available. Closing browser...");
                System.out.println("on " + new Date());
                Thread.sleep(10000); // Allow some time before quitting
                driver.quit(); // Close browser if message exists
            } else {
                System.out.println("Congrats " + new Date());
                sendMail("merooonawar@gmail.com", "nabe iojh sdlj jkgg", "amrfawzi182000@gmail.com");
                Thread.sleep(10000); // Wait before closing
                driver.quit(); // Close browser if projects are available
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure browser closes in case of an exception
            driver.quit();
        }
    }

    public static void sendMail(String emailAddress, String emailPassword, String to) {
        String host = "smtp.gmail.com";

        // Set up properties for the session
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create an authenticator to handle the email password
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, emailPassword);
            }
        };

        // Create the session using the properties and authenticator
        Session session = Session.getInstance(properties, authenticator);

        try {
            // Create the message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAddress)); // Sender's email address
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // Recipient's email address
            message.setSubject("Send Email from Amr outlier"); // Subject
            message.setText("Congratulations, you now have outlier's tasks. Please check now!"); // Body content

            // Send the message
            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
