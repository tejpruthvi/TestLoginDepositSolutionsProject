package testlogin.mavendependency;	

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

import com.google.gson.Gson;

public class AppTest {

	WebDriver driver;
	List<UserTest> usersList = new ArrayList<UserTest>();

	@BeforeTest
	public void invokeBrowser() {
		try {
			// Hardcoded driver path
			System.setProperty("webdriver.chrome.driver", "C:\\Users\\raja\\Desktop\\webdrivers\\chromedriver.exe");

			driver = new ChromeDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			driver.get("http://85.93.17.135:9000");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@DataProvider(name = "UserDetails")
	public Object[][] test() {

		Object[][] userData = new Object[1][4];

		userData[0][0] = "raja12";
		userData[0][1] = "rj2@gmail.com";
		userData[0][2] = "12334";
		userData[0][3] = "12334";
		
		return userData;

	}

	@Test(dataProvider = "UserDetails", priority = 3, description = "This test case check for the validations")

	public void userValidations(String name, String mail, String password, String confirmPassword) {

		Iterator<UserTest> itr = usersList.iterator();
		while (itr.hasNext()) {
			UserTest user = itr.next();
			Assert.assertFalse(user.getName().equals(name)); // Test fails if username is not unique
			Assert.assertFalse(user.getEmailId().equals(mail)); //Test fails if emailId is not unique

		}
		Assert.assertTrue(password.equals(confirmPassword)); //Test fails if password mismatch happens
	}

	@Test(dataProvider = "UserDetails", priority = 4, description = "This test case takes the credentials and try to create a new user if the name and email are unique", dependsOnMethods = {
			"userValidations" })

	public void createUser(String name, String mail, String password, String confirmPassword) {

		driver.findElement(By.id("name")).sendKeys(name);

		driver.findElement(By.id("email")).sendKeys(mail);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.id("confirmationPassword")).sendKeys(confirmPassword);

		driver.findElement(By.xpath(".//*[@id='registrationForm']/fieldset/div[5]/button")).click(); 

		String currentUrl = driver.getCurrentUrl();
		Assert.assertTrue(currentUrl.contains("users/all"));  // Test case passes if user is created

	}

	@Test(priority = 1, description = "This test case displays all the users list available")
	public void displayAllUsers() {
		driver.findElement(By.xpath(".//*[@id='registrationForm']/fieldset/div[5]/a")).click();
		WebElement table = driver.findElement((By.id("users")));
		Gson gson = new Gson();
		String tableText = table.getText();
		String[] users = tableText.split("\\r?\\n");

		for (int i = 1; i < users.length; i++) {
			String[] user = users[i].split("\\s+");
			UserTest user1 = new UserTest();
			user1.setName(user[0]);
			user1.setEmailId(user[1]);
			user1.setPassword(user[2]);
			usersList.add(user1);
		}

		String jsonString = gson.toJson(usersList);

		System.out.println(jsonString);

		String currentUrl = driver.getCurrentUrl();

		Assert.assertTrue(currentUrl.contains("users/all"));

	}

	@Test(priority = 2, description = "This test case navigates back to the new user creation page", dependsOnMethods = {
			"displayAllUsers" }, alwaysRun = true)
	public void newUser() {
		driver.findElement(By.xpath("html/body/div[1]/div/div/a")).click();
		String currentUrl = driver.getCurrentUrl();

		Assert.assertTrue(currentUrl.contains("/user/new")); 

	}

	@AfterTest
	public void closeBrowser() {

		driver.close();
	}

}