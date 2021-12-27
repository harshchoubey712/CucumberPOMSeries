package stepdefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.pages.AccountsPage;
import com.pages.LoginPage;
import com.qa.factory.DriverFactory;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class AccountsPageSteps {

	private LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
//Create loginpage class object.
	private AccountsPage accountsPage;

	@Given("user has already logged in to application")
	public void user_has_already_logged_in_to_application(DataTable credTable) {
		
//import io.cucumber.datatable.DataTable; for datatable.

		List<Map<String, String>> credList = credTable.asMaps();
//Use .asMaps which will return list<Map>.
		String userName = credList.get(0).get("username");
/*
 * Index 0 will give us 1st map.
 * We need to get the value of key username,password from feature file.

 * Map will be created like username as key, password as key and their corresponding values below.
 * So credlist.get(0) will give 1st map and from there we are taking key as username.

 * 
In login page we are creating 1 dologin() method to do login.

public AccountsPage doLogin(String un, String pwd) {
		System.out.println("login with: " + un + " and " + pwd);
		driver.findElement(emailId).sendKeys(un);
		driver.findElement(password).sendKeys(pwd);
		driver.findElement(signInButton).click();
		return new AccountsPage(driver);

After login in the loginpage with useranme, password we reaches to the accountpage so above method dologin should
Return accountpage class object.
		
 */
		String password = credList.get(0).get("password");

		DriverFactory.getDriver()
				.get("http://automationpractice.com/index.php?controller=authentication&back=my-account");
		accountsPage = loginPage.doLogin(userName, password);
/*
 * This username, password is coming from feature file in the form of datatable credtable.
 * So now we have used accountpage referenece and saved details of loginpage as loginpage succesful login will return accountpage.
 */

	}

	@Given("user is on Accounts page")
	public void user_is_on_accounts_page() {
		String title = accountsPage.getAccountsPageTitle();
		//Calling accountpagetitle method.
		System.out.println("Accounts Page title is: " + title);
	}

	@Then("user gets accounts section")
	public void user_gets_accounts_section(DataTable sectionsTable) {

		List<String> expAccountSectionsList = sectionsTable.asList();
	
		/*
		 We will get above data table in the form of list from feature file.
		 |ORDER HISTORY AND DETAILS|
         |MY CREDIT SLIPS|
         |MY ADDRESSES|
         |MY PERSONAL INFORMATION|
         |MY WISHLISTS|
         |Home|
		 */
		
		System.out.println("Expected accounts section list: " + expAccountSectionsList);

		List<String> actualAccountSectionsList = accountsPage.getAccountsSectionsList();
		/*
		 * above will return the actual account list.
		 * 
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
		System.out.println("Actual accounts section list: " + actualAccountSectionsList);

		Assert.assertTrue(expAccountSectionsList.containsAll(actualAccountSectionsList));

	}

	@Then("accounts section count should be {int}")
	public void accounts_section_count_should_be(Integer expectedSectionCount) {
		Assert.assertTrue(accountsPage.getAccountsSectionCount() == expectedSectionCount);
	}

}
