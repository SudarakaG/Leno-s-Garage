package lenosgarage;



import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverInitialize {

    private  static ChromeDriver driver;

    private ChromeDriverInitialize(){
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
    }

    public static synchronized ChromeDriver getDriver(){
        if(driver==null){
            new ChromeDriverInitialize();
        }
        return driver;
    }
    
    public static void refresh(){
        driver.navigate().refresh();
    }
}
