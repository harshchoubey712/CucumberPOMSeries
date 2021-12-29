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
/*
we can now fail the test by changing the page locators.
*/

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

/*
 * Now create  accountspage.feature.
 */

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

/*

We are using the concept of datatables in background .
We are not using concept of examples using data driven.

Here also we are using datatable concept using user gets account section details.

11, 12th line is not flagged as we already have its definition.

run the feature file using run configuation that will generate step definitions.
*/

==========================

/*
 * Now create AccountsPageSteps.java. For that we need to use below classes with comments.
 */

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
		
=============================

/*
 * Now start  AccountsPageSteps.java. 
 */

		
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
/*
 * Now start  AccountsPage.java. 
 */


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
/* add extent dependency in pom.xml */
	<dependency>
			<groupId>tech.grasshopper</groupId>
			<artifactId>extentreports-cucumber6-adapter</artifactId>
			<version>2.8.4</version>
			<scope>test</scope>
		</dependency>
		
===============================
extent.properties
//add below properties file in src/test/resources

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

extent-config.xml

<?xml version="1.0" encoding="UTF-8"?>
<extentreports>
	<configuration>
		<!-- report theme -->
/*This file xml is basically used to show the format of extent report-->
<!-- Then we need to add extentcucumber adapter inside  testrunner.. 
plugin = {"pretty",
	      "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
*/
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
		
============================
MyTestRunner.java
<!-- 
/*
 Then we need to add extentcucumber adapter in testrunner..
 Then run this testrunner as junit which will generate extent spark html reports under SparkReport
  as per extent.properties.
  
  Under test output ,spark report gets generated.
  Screenshot created under test-output.

  Open spark report folder index.html file
  Go to the browser and paste the path to open extent html report.
 
  Bug section shows failed assertion.
  Also we get failed screenshot.
 
  Tags @accounts shows 2 tests were there and both got passed.
  All these system environments info comes from extentconfig.xml like user:Naveen, os:Mac.

  Title of the report and report name can be changed also from xml file.
  Like Extent and Grassshopper.
  Chart is coming at bottom as we used bottom.
  Theme used is dark. We can use standard also if we need in white colour.
  Now open test output folder , pdf report.
  Also the failed test details shown in report.
 */

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

=============================
POM.XML comments
/*
 * Both accountpage and loginpage features will run in parallel mode
But the scenarios for eg under login page will not run in parallel.
Means all above scenarios will run under same browser.

Similarly for accounts page scenarios.

So we can say that accountpage and login page both are running on different 
Browsers but in parallel mode.


use browser= chrome inisde config.properties.

So we will execute above in chrome means 
2 chrome browsers will run 2 features in parallel mode.

We need to add few plugins like surefire, compliler in pom.xml.

Failsafe plugin.


Here we need to provide the name of our testrunner.


Use parallel methods.


Run the project using maven by copying prroject path 
Then cd to the terminal path.

Do mvn verify.

2 features will run in parallel mode in chrome.

Refresh the project to see extent report.

----------------------------
Testrunner.java comments

Add timeline plugin for threads in Testrunner.java.

We have 2 features so 2 threads.
We can use above plugin and see that which thread executed which 
Feature.


Again run the report using junit or mvn verify.

This shows report based on the threads in the cucumber reports.

 * 
 */
-------------------------


POM.XML

<plugin> <groupId>org.apache.maven.plugins</groupId>
 <artifactId>maven-failsafe-plugin</artifactId> 
	<version>3.0.0-M3</version> 
	<executions> 
	    <execution> 
	        <goals> 
	            <goal>integration-test</goal> 
	        </goals> 
	        <configuration> 
//	        <!-- UNCOMMENT - To add any exclusions if required  -->
//	        <!-- <excludes> <exclude>**/*IT*.java</exclude> </excludes> -->
	        <includes> 
//	          <!--UNCOMMENT BELOW LINE - To execute feature files with a single runner -->
	          <include>**/MyTestRunner.java</include> 
	         
//	          <!--UNCOMMENT BELOW LINE - To execute feature files with multiple runners -->
//	         <!--  <include>**/*Runner.java</include> -->
	
	        </includes> 
	        
//	        <!--UNCOMMENT BELOW 3 LINES - To execute using parallel or combination option -->
	        <parallel>methods</parallel> 
	        <threadCount>4</threadCount> 
	        <perCoreThreadCount>true</perCoreThreadCount> 
//	        <!--UNCOMMENT BELOW 3 LINES - To execute using forking or combination option -->
	        
	        <!--<forkCount>2</forkCount> <reuseForks>true</reuseForks> <reportsDirectory>${project.build.directory}/failsafe-reports_${surefire.forkNumber}</reportsDirectory> -->
	       </configuration> 
	    </execution> 
	</executions> 
</plugin> 

-------------------------

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

}
		 
------------------------------

Create another feature acc.feature by copying accountspage.feature.

Run again using mvn verify.

3 threads started in parallel mode for running 3 features.

3 threads will then show in cucumber report.


to push the local changes to git.

Use 

git status

git add .

git commit -m "added junit parallel flow comments"

git push origin master

This will push the code to github.


=================

Cucumber Junit parallel POM changes ends here

----------------

Git full steps:
	
Create account in github.
Click on your profile.
We can go to settings, public  profile and write about you.

This shows number of repositories created by user.

Now click on new repository.

If we need to share our code to anyone make it public.

Now create some  maven project in eclipse as above.

Go to the properties of the project.

Go to the project location in the terminal using cd project path.

Then use git init.
Press git init to initialize the project.

This wll create .git file in the project path.


do ls-alt to check the files under the path.

Do ls -alt and you can see the .git file created as above . Its hidden file.


Now do git remote add origin "DemoRepo.git(github url repository path)"
	
This will create connection of remote rep to local git rep.

Do git status. This will show files in red which can be committed.



Do git add . Which will make all red files and make them ready for commit.

Then do git commit.

git commit -m "added junit parallel flow comments"

In step 4 we can also do git add homepage.java or more files .
But using git add . Will add all project files for commit.

Git push origin master will give error.

Anyone can pull the code from remote rep using url but no one can push the code to my rep.

So for pushing the code from local to remote rep we have to 1st register ssh key in remote rep.

Ssh key is not required for pulling. Its only required while pushing.


Go to account settings.

Click on ssh and gpg keys.

If I need 5 users to push their code to my rep.

Then I need to add 5 ssh keys above.

Give the title of the key.

go to link below:

https://docs.joyent.com/public-cloud/getting-started/ssh-keys/generating-an-ssh-key-manually/manually-generating-your-ssh-key-in-mac-os-x

	To generate SSH keys in macOS, follow these steps:

		Enter the following command in the Terminal window.

		ssh-keygen -t rsa

		Your private key is saved to the id_rsa file in the .ssh directory and is used to verify the public key you use belongs to the same Triton Compute Service account.
		
		Open new terminal and paste above command.
		
		Enter mac password.
		
		Never share your private key with anyone!
		Your public key is saved to the id_rsa.pub;file and is the key you upload to your Triton Compute Service account. You can save this key to the clipboard by running this:

		pbcopy < ~/.ssh/id_rsa.pub
		
		run above command and then paste in texteditor which will paste your public key like below.
		
		ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQC+TQF6yYGuayGcccXizOqk+z3AltZGtnTGT1xV1cRQ2MDzdwhYjLUGSjxyWPsT
		LuiHaI5EzkEqFjjV8g/lVAW+dVQ6ifBec4PqXAeQ3MfGcbjTn/hDumikneWCpWHTk8aJ5Eg2mDlRaZhM0YQcUfo4vW1SFDO8v/Z4
		xLI47glm7nLdRV63M+0b548JIkX9Cdp1YZ5voChv7L3J562+s7tp3F5Kq3ISZigsMC7kHCCKkFwWscIn9T3AXB+uiCA8IplGy0m4E
		rKKY4EKMCqIWh1y4nGKnGNdLGnUnQGqgFdlhTKCEqmXgA/WQYCghqV18Su/d0vDUKDENtnepQUP5zYjpkSMT0rFQswZv0YsFs3QNJ
		IL5bBjrFduLPFsRDK+l+rQudgAd7US/nT2iysQjHMbWcs4JJeyh2iPlm+LI8hhY2mpm7F36+AO47oWn4ZCCMNAl2Lv5SgwgXYnhMU
		YyDI9bWiYsN8j0TrsHZbA0UfgjIXjUKMjIdpSeNgj0HE6d/s= harshchoubey@harshs-MacBook-Pro.local

				After you copy the SSH key to the clipboard, return to your account page.
				Choose to Import Public Key and paste your SSH key into the Public Key field.

				There are 2 ssh keys above means 2 different users can push the code to above.

				Again do git push and this time code gets pushed to master branch.

				Try laptop password above.

		Sometimes it will give an error again so generate  Personal token as per below link.
		
		https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token
			
			
		
		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 :personal token og harshchoubey712
		
		
		Personal access tokens (PATs) are an alternative to using passwords for authentication to GitHub when using the GitHub API or the command line.
		
		
		Using a token on the command line
		Once you have a token, you can enter it instead of your password when performing Git operations over HTTPS.

		For example, on the command line you would enter the following:

		$ git clone https://github.com/username/repo.git
		Username: your_username
		Password: your_token
		
		
		Personal access tokens can only be used for HTTPS Git operations. If your repository uses an SSH remote URL, you will need to switch the remote from SSH to HTTPS.
		
		
		Now again try git push 
		and give token as password.
		
		This will push the changes to github.
		
		This is how we pushed code using ssh key.
		
		Now add 1 more class as above in eclipse.
		
		Git status only shows 1 class searchpage. Newly added.
		
		Now do git add .
		Git status.
		
		Now commit and push.
		
		so everytime if we change anything we can start from git status.
		
		
		-----------
		
		Git pull
		
		Mkdir javacourse
		Cd javacourse
		Now pull changes in above dir.
		
		For the 1st time we need to do git clone.
		
		git clone https://.git
			
		Now we get full project as above inside javacourse folder.
		
		To take this code to eclipse.

		Go to import, 
		General , existing projects.
		
		Browse
		
		select the project and finish.
		

       This is  for cloning java project.
       
       
       Now clone maven project.
       
       Create ebatest folder .
       
       Do git clone.
       
       To import maven project use file import maven 
       
       Ebaytest imported now.
       
       Suppose if we made any changes to the remote rep by committing directly in the repository.
       
       We can pull the changes done in remote rep to local eclipse using
       
       Now to pull the changes use git pull.
       
       git pull origin master.
       
       
       For pull we have to 1st time clone. Its for only 1st time.
       
       Need to check for PULL REQUEST NOW.
       
       Again pushing to see changes.
       
      ==========================
      Pull request
      
      1st Naveen pushes his code to the master branch.

      Then other team members joined like Sandesh, Babita…

      They will 1st clone the code ur l of master to their local.
      Say Sandesh start working on homepage.

      Babita on landingpage.

      After this they will push the code to the new branch says sandesh.
      For this they will raise pull request and mention the reviewer details as Naveen.


      Naveen will then take this sandesh branch to local eclipse, review 
      and if any changes required
      Will notify sandesh again. 

      Sandesh again raises pull request with new changes and if all ok
      Naveen will aproove the pull request.

      Then sandesh branch will merge to master branch.

      
      Likewise babita and prashant will push their changes the new b and p branches and raise pull request.

      If they are approved then it will merge to master branch.

      This master branch will be pushed to production.

      Reviewer has to review all logic high  level.

      This is pull request.
      
      If any new people joins like sarang they will take the code from 
      master branch which has the latest stable code.
      
      Say Sarang working on ust us3001 then he will create  anew branch with this name and raise pull request.

      He can raise pull request daily say for 5 days for above branch. 

      Then Naveen has to checkout this branch daily and compile the code and check for logic. If all ok then he will approve and the branch will merge to master.

      Naveen can also go to github directly and review the changes in us003 branch and merges to master but 1st approch is better.

      
      Once dev team all changes are there in master branch they 
      can push it to QA team for testing .

      Then same changes can be used for production also.
      
      If there are 10 bugs it would be assigned to different dev team members.

      Dev team will again raise pr for each bug and once its reviewed and approved will merge the bugs fixes to master branch.
      So it will be deployed to QA team for testing then.

      So QA team always works on master branch which has no bugs.

      If there are any production bugs again dev team will raise PR and work on it . 
      Merge  it back to Master branch.

      Again Master will be pushed to QA. 
      QA team will test it and then it will be pushed to production.

      
      Steps:
    	  
    	  1: Create a new branch in github.
    	  2: So now there are 2 branches master and dealers.
    	  3:  This new branch will have all the changes of master branch.
    	  4: So we will create multiple branches like above from master branch and at last will 
    	     merge all them to master .
    	     So at last master branch will have all changes of other branches.
      
          5: As soon as we create a new branch it will have all changes from master branch.


          6:Go to the project location. Marchpomseries.
          Do git branch.
          Currently its pointing to master branch.


          
          7: git pull origin dealspagebranch.

             Then do git checkout dealspagebranch.
             
             
             Now git branch poiting to dealspage branch.
             
           
           8: Now do some changes in eclipse as above.
           
           
           9:Now do git status which shows what files we have modified.


           
           10: Now do  git add as above.
           
           11:Again do git status which will show above file in green colour means it is ready to be committed.


           
           12:Do git commit which will push the changes from local to git.

           13:Then do git push which will push from git to remote rep.

              git push origin dealsbranch
              
              
           14:Go to rep and refresh which will show the change in dealspage.java as above in the dealspagebranch.

           
           In master branch there is no chnages.

           
          15:  Now raise pull request as above.
            
          Write summary and comments of the changes made.


          
          Write reviewer details.


          
          Arrow pointing to master branch means after approval changes should merge to master branch.

          
          We are creating pull request from naveen to naveen only.

          As there is only 1 person for review.


          
          Naveen will get a notification like above And details what naveen has added.


          By clicking on + button reviewer can add a review comment as well.


          
          Reviewer can add multiple comments as above.

          
          Once review completed click on finish review.

          
          If request raised for some other approver then approve will be enabled.

          Reviewer can then submit review.


          So requestor will then get the notification of the changes has to be made.
          He made changes to the eclipse and again raise PR.

          
          Requestor will do changes as per comments.
          
          Again do git add and commit
          
          git push origin dealsbranch
          
          Reviewer can review again. If all correct he can put comments as sayCorrect now.
          And submit review.
          
          Then requestor notified that all correct and then he can do merge pull request.
          
          Confirm merge.
          
          Now master branch got all merged changes.

          So we keep adding code to the dealspage branch and raise PR. After approvals  from reviewers, keep merging them to master branch.



          =============
          
          
          

           

      
       
       
       
