import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class GoogleSearchPage extends BrowserAbstractPage {

    public GoogleSearchPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }


    public Collection<String> GoogleSearchQueries() {
        List<WebElement> linkElements = new ArrayList<WebElement>();
        ListIterator<WebElement> itr = null;
        driver.get("http://google.com");
        WebElement gelement = driver.findElement(By.name("q"));
        WebElement toClick = null;
        gelement.sendKeys("mantis");
        gelement.submit();
        int pageNumber = 1;
        WebDriverWait wait = new WebDriverWait(driver, 15);
        boolean b = false;
        Collection<String> finalOutput = null;

        while (!b) {
            linkElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h3[@class='r']/a")));
            itr = linkElements.listIterator(); // re-initializing iterator
            while (itr.hasNext()) {
                toClick = itr.next();
                if (toClick.getText().equals("MantisBT download | SourceForge.net")) {
                    toClick.click();
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(text(), 'Files')]")));
                    driver.findElement(By.xpath("//*[contains(text(), 'Files')]")).click();
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("name")));
                    driver.findElement(By.linkText("mantis-stable")).click();
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("label")));
                    b = true;


                    MantisPageObject table = new MantisPageObject(driver);
                    int maximumDownload = table.maximumDownloads();
                    List<String> version = table.versionInfo();
                    List<Integer> download = table.downloads();

                    Multimap<Integer, String> map = ArrayListMultimap.create();

                    for (int i = 0; i < download.size(); i++) {
                        map.put(download.get(i), version.get(i));
                    }
                    finalOutput = map.get(table.maximumDownloads());
                    System.out.println("versions and download statistics log:\n"+ map);
                    System.out.println("version number that has the most numbers of downloading:\n"+ finalOutput);
                    break;
                }
            }
            if (!b) {
                driver.findElement(By.xpath("//a[@id='pnnext']/span[1]")).click();
                pageNumber++;
                linkElements.clear(); // clean list
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//td[@class='cur']"), pageNumber + ""));
            }
        }
        return finalOutput;
    }




}