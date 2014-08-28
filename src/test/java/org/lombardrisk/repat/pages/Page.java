package org.lombardrisk.repat.pages;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.lombardrisk.repat.utils.BrowserDriver;
import org.lombardrisk.repat.utils.I18N;
import org.lombardrisk.repat.utils.Property;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;

/**
 * @author Kenny Wang
 *
 */
public abstract class Page {

	protected Logger logger = Logger.getLogger(this.getClass());
	protected BrowserDriver driver;
	protected String title = "REPORTER Portal";
	protected I18N i18n = new I18N(Property.lang);

	protected By ajaxStatusDialog = By.id("ajaxstatusDlg");

	public Page() {
		super();
	};

	public Page(BrowserDriver driver) {
		this.driver = driver;
	};

	public abstract Boolean isCurrentPage();

	protected Boolean isStatusDialogDisplayed() {
		String hidden = driver.getElementAttribute(ajaxStatusDialog,
				"aria-hidden");
		return !Boolean.parseBoolean(hidden);
	}

	protected void waitStatusDialog() {
		logger.debug("Try to wait for status dialog appearing");
		try {
			new FluentWait<WebDriver>(driver.getDriver())
					.withTimeout(Property.statusDialogAppearIn,
							TimeUnit.SECONDS)
					.ignoring(StaleElementReferenceException.class)
					.ignoring(NoSuchElementException.class)
					.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver driver) {
							return isStatusDialogDisplayed();
						}
					});
		} catch (TimeoutException e) {
		}

		logger.debug("Try to wait for status dialog disappearing");
		new FluentWait<WebDriver>(driver.getDriver())
				.withTimeout(Property.statusDialogDisappearIn, TimeUnit.SECONDS)
				.ignoring(StaleElementReferenceException.class)
				.ignoring(NoSuchElementException.class)
				.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver driver) {
						return !isStatusDialogDisplayed();
					}
				});
	}

}
