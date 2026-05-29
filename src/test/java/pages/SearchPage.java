package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.Waits;
public class SearchPage {
    private final By searchInput =
            By.cssSelector("[data-testid='search-input'], input[placeholder*='Search'], input[type='search']");

    private final By searchButton =
            By.cssSelector("[data-testid='search-submit'], button[type='submit'], [class*='search-btn']");
    public void enterSearchKeyword(String keyword) {
        WebElement input = Waits.waitForVisibility(searchInput);
        input.clear();                        
        input.sendKeys(keyword);
        System.out.println("Search keyword entered: " + keyword);
    }

  public void clickSearch() {
    Waits.clickUsingJS(
            Waits.waitForClickable(searchButton)
    );

    System.out.println("Search button clicked");
}
public void clearSearch() { 
    WebElement input = Waits.waitForVisibility(searchInput); 
    input.clear();  // not getting fully cleared
    System.out.println("Search box cleared"); }
}