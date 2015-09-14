package org.lombardrisk.repat.pages;

import org.lombardrisk.repat.utils.BrowserDriver;
import org.openqa.selenium.By;

/**
 * @author Kenny Wang
 *
 */
public class LoginPage extends Page {

	protected By usernameInput = By.id("loginForm:inputUsername");
	protected By passwordInput = By.id("loginForm:inputPassword");
	protected By loginButton = By.xpath("//div[@id='signinBtnBar']/input");

	public LoginPage(BrowserDriver driver) {
		super(driver);
		driver.assertPageTitle(title);
	}

	public HomePage loginAs(String username, String password) {
		logger.info("Try to login system with username=" + username
				+ ", password=" + password);
		driver.input(usernameInput, username);
		driver.input(passwordInput, password);
		driver.click(loginButton);
		return new HomePage(driver);
	}

	public Boolean isCurrentPage() {
		return driver.isElementDisplayed(usernameInput);
	}
}
