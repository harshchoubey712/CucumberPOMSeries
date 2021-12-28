package com.qa.Util;



public class Project_flow {

	
	/*
	 * first we create login feature file then execute it which will generate the stepdefinition.
	 * we will copy them in step definitinition file Loginpagesteps.java.
	 */
======================
Loginpage.feature
 * create this feature file under src/test/resources folder,  AppFeatures folder, Loginpage.feature.


Feature: Login page feature

Scenario: Login page title
Given user is on login page
When user gets the title of the page
Then page title should be "Login - My Store"

Scenario: Forgot Password link
Given user is on login page
Then forgot your password link should be displayed

Scenario: Login with correct credentials
Given user is on login page
When user enters username "dec2020secondbatch@gmail.com"
And user enters password "Selenium@12345"
And user clicks on Login button
Then user gets the title of the page
And page title should be "My account - My Store"


	
============================
driverfactory.java 
/*
create this under src/main/java folder, com.qa.factory package, driverfactory.java .

This method is used to initialize the thradlocal driver on the basis of given browser.
Threadlocal is for running in parallel mode.
As threadlocal is initialized with webdriver it will return the webdriver .(line 47 to 49).
It has 2 methods set and get.
Get will return the webdriver.
Return getdriver() will give the current instance of webdriver like chrome or firefox.

If 3 threads are running parallel they will be in sync using synchronized(line 62).
*/
	
	public WebDriver driver;

	public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
	public WebDriver init_driver(String browser) {

		System.out.println("browser value is: " + browser);

		if (browser.equals("chrome")) {
			WebDriverManager.chromedriver().setup();
			tlDriver.set(new ChromeDriver());
		} else if (browser.equals("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			tlDriver.set(new FirefoxDriver());
		} else if (browser.equals("safari")) {
			tlDriver.set(new SafariDriver());
		} else {
			System.out.println("Please pass the correct browser value: " + browser);
		}

		getDriver().manage().deleteAllCookies();
		getDriver().manage().window().maximize();
		return getDriver();
		
		
	public static synchronized WebDriver getDriver() {
	    return tlDriver.get();
		
			
=============================
* create this under src/test/java folder, Apphooks package, ApplicationHooks.java
	
This hooks will use below files: from different folders.
	
1: ConfigReader.java for @Before(order = 0) hook.
	
	ConfigReader.java
	ConfigReader.java under com.qa.Util package inside src/main/java folder.
		
		
	public class ConfigReader 
	{
       
	private Properties prop;

	/*
	 * This method is used to load the properties from config.properties file
	 * @return it returns Properties prop object.
	 prop.load(ip) wher ip pointing to cucumber.properties file which holds browser=chrome.
	 so prop is pointing to chrome browser.
	 */
	public Properties init_prop() 
	 {

		prop = new Properties();
		try {
			FileInputStream ip = new FileInputStream("./src/test/resources/config/cucumber.properties");
			prop.load(ip);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prop;

	}

      }
		
2: driverfactory.java for @Before(order = 1) hook.
create this under src/main/java folder, com.qa.factory package, driverfactory.java .
		
	public WebDriver driver;

	public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
	public WebDriver init_driver(String browser) {

		System.out.println("browser value is: " + browser);

		if (browser.equals("chrome")) {
			WebDriverManager.chromedriver().setup();
			tlDriver.set(new ChromeDriver());
		} else if (browser.equals("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			tlDriver.set(new FirefoxDriver());
			
			
			
package AppHooks;

import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.qa.factory.DriverFactory;
import com.qa.Util.ConfigReader;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class ApplicationHooks {

	private DriverFactory driverFactory;
	private WebDriver driver;
	private ConfigReader configReader;
	Properties prop;
	
	

	
	/*
	 Hooks will run before each scenario. @Before(order = 0) will run 1st in before hook. This uses ConfigReader.java defined above.
	 
	 @After(order = 1) will run 1st in after hook.
	 
	 So as per hooks before running each scenario, @Before(order = 0) hook will initilalize prop file . so prop will point to browser=chrome.
	 
	 and @Before(order = 1) hook will 1st get the value of browser using prop.getProperty("browser") and then launch the new driver using 
	 driverFactory.init_driver(browserName);
	 
	 
	 After running each sceanrion , @After(order = 1) hook  will run 1st which checks if any sceanrio is failed and if yes it will
	 attach failed screenshot the same scenario.
	 
	 After that @After(order = 0)hook will run which will quit  the browser.
	 
	 In @After(order = 1)  hook:
	 teardown method will run if any sceanrio gets failed.
         Sourcepath will take a screenshot.

         Scenario.attach will then attach the screenshot of the failed step to the scenario inside the cucumber report.
	
	 
	 */
	
	@Before(order = 0)
	
	public void getProperty() {
		configReader = new ConfigReader();
		prop = configReader.init_prop();
	}

	

	@Before(order = 1)
	public void launchBrowser() {
		String browserName = prop.getProperty("browser");
		driverFactory = new DriverFactory();
		driver = driverFactory.init_driver(browserName);

		
	}

	@After(order = 0)
	public void quitBrowser() {
		driver.quit();
	}

	
	@After(order = 1)
	public void tearDown(Scenario scenario) {
		if (scenario.isFailed()) {
			// take screenshot:
			String screenshotName = scenario.getName().replaceAll(" ", "_");
			
   
		byte[] sourcePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		scenario.attach(sourcePath, "image/png", screenshotName);

		}
	}

}

===============================
LoginPage.java page class inside src/main/java, com.pages package.

public class LoginPage {

	private WebDriver driver;

	// 1. By Locators: OR
	private By emailId = By.id("email");
	private By password = By.id("passwd");
	private By signInButton = By.id("SubmitLogin");
	private By forgotPwdLink = By.linkText("Forgot your password?111");

	// 2. Constructor of the page class:
	public LoginPage(WebDriver driver) {
		this.driver = driver;
	}

	// 3. page actions: features(behavior) of the page the form of methods:

	public String getLoginPageTitle() {
		return driver.getTitle();
	}

	public boolean isForgotPwdLinkExist() {
		return driver.findElement(forgotPwdLink).isDisplayed();
	}

	public void enterUserName(String username) {
		driver.findElement(emailId).sendKeys(username);
	}

	public void enterPassword(String pwd) {
		driver.findElement(password).sendKeys(pwd);
	}

	public void clickOnLogin() {
		driver.findElement(signInButton).click();
	}

	public AccountsPage doLogin(String un, String pwd) {
		System.out.println("login with: " + un + " and " + pwd);
		driver.findElement(emailId).sendKeys(un);
		driver.findElement(password).sendKeys(pwd);
		driver.findElement(signInButton).click();
		return new AccountsPage(driver);
	}
	

========================
	
LoginPageSteps.java under src/test/java , stepdefinitions package.
	
/*
	 * Created loginpage class object and then call its methods like getLoginPageTitle, isForgotPwdLinkExist.
	 * All assertions has to be done in test class inside stepdefinitoons not in the main class.
*/

public class LoginPageSteps {

	private static String title;
	private LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
	

	

	@Given("user is on login page")
	public void user_is_on_login_page() {

		DriverFactory.getDriver()
				.get("http://automationpractice.com/index.php?controller=authentication&back=my-account");
	}

	@When("user gets the title of the page")
	public void user_gets_the_title_of_the_page() {
		title = loginPage.getLoginPageTitle();
		System.out.println("Page title is: " + title);
	}

	@Then("page title should be {string}")
	public void page_title_should_be(String expectedTitleName) {
		Assert.assertTrue(title.contains(expectedTitleName));
	}

	@Then("forgot your password link should be displayed")
	public void forgot_your_password_link_should_be_displayed() {
		Assert.assertTrue(loginPage.isForgotPwdLinkExist());
	}

	@When("user enters username {string}")
	public void user_enters_username(String username) {
		loginPage.enterUserName(username);
	}

	@When("user enters password {string}")
	public void user_enters_password(String password) {
		loginPage.enterPassword(password);
	}

	@When("user clicks on Login button")
	public void user_clicks_on_login_button() {
		loginPage.clickOnLogin();
	}

=======================
package testrunners;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/AppFeatures"},
		glue = {"stepdefinitions", "AppHooks"},
		plugin = {"pretty",
				"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
				"timeline:test-output-thread/"
							
		         }
		
		)

public class MyTestRunner {

}

Now run the testrunner file as above.

=====================

we can now fail the test by changing the page locators.

LoginPage.java page class

public class LoginPage {

	private WebDriver driver;

	// 1. By Locators: OR
	private By emailId = By.id("email");
	private By password = By.id("passwd");

	private By forgotPwdLink = By.linkText("Forgot your password?111");

@After(order = 1) hook will execute after running each scenario.

@After(order = 1) hook  will check if any sceanrio is failed and if yes it will
attach failed screenshot the same scenario.
	 

this will fail the Forgot password scenario and screenshot will be taken.

Go to the cucumber report url.

This shows the screenshot of the failed test along with error message.

===========================

Now create  accountspage.feature.

Feature: Account Page Feature

Background:
Given user has already logged in to application
|username|password|
|dec2020secondbatch@gmail.com|Selenium@12345|

#We are using the concept of datatables in background .
#We are not using concept of examples using data driven.

@accounts
Scenario: Accounts page title
Given user is on Accounts page
When user gets the title of the page
Then page title should be "My account - My Store"

@accounts
Scenario: Accounts section count
Given user is on Accounts page
Then user gets accounts section
#Here also we are using datatable concept using user gets account section details.
|ORDER HISTORY AND DETAILS|
|MY CREDIT SLIPS|
|MY ADDRESSES|
|MY PERSONAL INFORMATION|
|MY WISHLISTS|
|Home|
And accounts section count should be 6



We are using the concept of datatables in background .
We are not using concept of examples using data driven.

Here also we are using datatable concept using user gets account section details.

11, 12th line is not flagged as we already have its definition.

run the feature file using run configuation that will generate step definitions.

==========================
Create loginpage class object.
import io.cucumber.datatable.DataTable; for datatable.
Use .asMaps which will return list<Map>. ((List<Map<String, String>> credList = credTable.asMaps();))

Index 0 will give us 1st map.  (( String userName = credList.get(0).get("username");))
We need to get the value of key username,password from feature file.

Map will be created like username as key, password as key and their corresponding values below.
So credlist.get(0) will give 1st map and from there we are taking key as username.
	
In login page class we are creating 1 dologin() method to do login as per below.

public AccountsPage doLogin(String un, String pwd) {
		System.out.println("login with: " + un + " and " + pwd);
		driver.findElement(emailId).sendKeys(un);
		driver.findElement(password).sendKeys(pwd);
		driver.findElement(signInButton).click();
		return new AccountsPage(driver);
	
After login in the loginpage with useranme, password we reaches to the accountpage so above method dologin should
Return accountpage class object.
	
This username, password is coming from feature file in the form of datatable credtable.  ((accountsPage = loginPage.doLogin(userName, password);)).
	
So now we have used accountpage referenece and saved details of loginpage as loginpage succesful login will return accountpage.	
	
(( @Then("user gets accounts section")
  public void user_gets_accounts_section(DataTable sectionsTable) {
  List<String> expAccountSectionsList = sectionsTable.asList();
	
))
We will get above data table , sectionsTable in the form of list from feature file shown below. This will be our expected list.
	 |ORDER HISTORY AND DETAILS|
         |MY CREDIT SLIPS|
         |MY ADDRESSES|
         |MY PERSONAL INFORMATION|
         |MY WISHLISTS|
         |Home|
((List<String> actualAccountSectionsList = accountsPage.getAccountsSectionsList();))
 above will return the actual account list.
 Below extract taken from accountpage.java
	
	
		 * private By accountSections = By.cssSelector("div#center_column span");
		 * public List<String> getAccountsSectionsList() {

		List<String> accountsList = new ArrayList<>();
		List<WebElement> accountsHeaderList = driver.findElements(accountSections);

		for (WebElement e : accountsHeaderList) {
			String text = e.getText();
			System.out.println(text);
			accountsList.add(text);
		}

		return accountsList;
		 */
		/*
		
		
public class AccountsPageSteps {

	private LoginPage loginPage = new LoginPage(DriverFactory.getDriver());

	private AccountsPage accountsPage;

	@Given("user has already logged in to application")
	public void user_has_already_logged_in_to_application(DataTable credTable) {
		
	 List<Map<String, String>> credList = credTable.asMaps();
	 

	 String userName = credList.get(0).get("username");
	 
	 String password = credList.get(0).get("password");

         DriverFactory.getDriver()
				.get("http://automationpractice.com/index.php?controller=authentication&back=my-account");
	 accountsPage = loginPage.doLogin(userName, password);
	}

	@Given("user is on Accounts page")
	public void user_is_on_accounts_page() {
		String title = accountsPage.getAccountsPageTitle();

	}

	@Then("user gets accounts section")
	public void user_gets_accounts_section(DataTable sectionsTable) {

		List<String> expAccountSectionsList = sectionsTable.asList();
	
		System.out.println("Expected accounts section list: " + expAccountSectionsList);

		List<String> actualAccountSectionsList = accountsPage.getAccountsSectionsList();
		
	
		 
		System.out.println("Actual accounts section list: " + actualAccountSectionsList);

		Assert.assertTrue(expAccountSectionsList.containsAll(actualAccountSectionsList));

	}

	@Then("accounts section count should be {int}")
	public void accounts_section_count_should_be(Integer expectedSectionCount) {
		Assert.assertTrue(accountsPage.getAccountsSectionCount() == expectedSectionCount);
	}

}

===============================
AccountsPage.java


package com.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AccountsPage {

	private WebDriver driver;

	private By accountSections = By.cssSelector("div#center_column span");

	public AccountsPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public String getAccountsPageTitle() {
		return driver.getTitle();
	}

	public int getAccountsSectionCount() {
		return driver.findElements(accountSections).size();
	}

	public List<String> getAccountsSectionsList() {

		List<String> accountsList = new ArrayList<>();
		List<WebElement> accountsHeaderList = driver.findElements(accountSections);

		for (WebElement e : accountsHeaderList) {
			String text = e.getText();
			System.out.println(text);
			accountsList.add(text);
		}

		return accountsList;

	}

}
=========================

AccountsPage.java

package com.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AccountsPage {

	private WebDriver driver;

	private By accountSections = By.cssSelector("div#center_column span");

	public AccountsPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public String getAccountsPageTitle() {
		return driver.getTitle();
	}

	public int getAccountsSectionCount() {
		return driver.findElements(accountSections).size();
	}

	public List<String> getAccountsSectionsList() {

		List<String> accountsList = new ArrayList<>();
		List<WebElement> accountsHeaderList = driver.findElements(accountSections);

		for (WebElement e : accountsHeaderList) {
			String text = e.getText();
			System.out.println(text);
			accountsList.add(text);
		}

		return accountsList;

	}

}
==============================

pom.xml

* add extent dependency in pom.xml.

	<dependency>
			<groupId>tech.grasshopper</groupId>
			<artifactId>extentreports-cucumber6-adapter</artifactId>
			<version>2.8.4</version>
			<scope>test</scope>
		</dependency>
		
===============================

extent.properties

* add below properties file in src/test/resources

extent.reporter.spark.start=true
extent.reporter.spark.out=test-output/SparkReport/Spark.html
extent.reporter.spark.config=src/test/resources/extent-config.xml

extent.reporter.spark.out=test-output/SparkReport/

screenshot.dir=test-output/
screenshot.rel.path=../
extent.reporter.pdf.start=true
extent.reporter.pdf.out=test output/PdfReport/ExtentPdf.pdf
#basefolder.name=reports
#basefolder.datetimepattern=d-MMM-YY HH-mm-ss
extent.reporter.spark.vieworder=dashboard,test,category,exception,author,device,log
systeminfo.os=Mac
systeminfo.user=Naveen
systeminfo.build=1.1
systeminfo.AppName=AutomationPractice


===================
extent_config.xml

<!--add below xml file in src/test/resources.-->
<!--This file xml is basically used to show the format of extent report-->



<?xml version="1.0" encoding="UTF-8"?>
<extentreports>
	<configuration>
		<!-- report theme -->

plugin = {"pretty",
	      "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
-->
		<!-- standard, dark -->

		<theme>dark</theme>
		<!-- document encoding -->

		<!-- defaults to UTF-8 -->

		<encoding>UTF-8</encoding>
		<!-- protocol for script and stylesheets -->

		<!-- defaults to https -->

		<protocol>http</protocol>
		<!-- title of the document -->
		<documentTitle>Extent</documentTitle>
		<!-- report name - displayed at top-nav -->

		<reportName>Grasshopper Report</reportName>
		<!-- location of charts in the test view -->

		<!-- top, bottom -->

		<testViewChartLocation>bottom</testViewChartLocation>
		<!-- custom javascript -->

		<scripts>

<![CDATA[
$(document).ready(function() {
});
]]>
		</scripts>
		<!-- custom styles -->
		<styles>
<![CDATA[
]]>
		</styles>
	</configuration>
</extentreports>

============================
MyTestRunner.java

<!-- Then we need to add extentcucumber adapter in testrunner..
* Then run this testrunner as junit which will generate extent spark html reports under SparkReport
  as per extent.properties.

* Under test output spark report gets generated.
* Screenshot created under test-output.
* 
* Open spark report folder index.html file
* Go to the browser and paste the path to open extent html report.
* 
* Bug section shows failed assertion.
* Also we get failed screenshot.
* 
* Tags @accounts shows 2 tests were there and both got passed.

* All these system environments info comes from extentconfig.xml like user:Naveen, os:Mac.
* 
* Title of the report and report name can be changed also from xml file.
  Like Extent and Grassshopper.

* Chart is coming at bottom as we used bottom.

* Theme used is dark. We can use standard also if we need in white colour.

* Now open test output folder , pdf report.
* Also the failed test details shown in report.

package testrunners;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/AppFeatures"},
		glue = {"stepdefinitions", "AppHooks"},
		plugin = {"pretty",
				"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
				"timeline:test-output-thread/"
							
		         }
		
		)

public class MyTestRunner {

}

=========================

Project flow ends:

	 */
	
	 
	
}
