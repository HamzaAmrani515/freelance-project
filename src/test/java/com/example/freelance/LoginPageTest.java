package com.example.freelance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginPageTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    void testLoginSuccess() {
        driver.get("http://localhost:3000/login");

        // Récupérer les champs
        WebElement usernameInput = driver.findElement(By.cssSelector("[data-testid='username-input']"));
        WebElement passwordInput = driver.findElement(By.cssSelector("[data-testid='password-input']"));
        WebElement loginButton = driver.findElement(By.cssSelector("[data-testid='login-button']"));

        // Entrer les informations
        usernameInput.sendKeys("Bpce");
        passwordInput.sendKeys("password123");

        // Soumettre le formulaire
        loginButton.click();

    }

}

