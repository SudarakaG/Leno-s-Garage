/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenosgarage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author Eminda
 */
public class AuctionScraper {

    public static List<String> getAuctionList(String url) {
        WebDriver driver = FirefoxInitializer.getDriver();
        driver.get(url);
        Document doc = Jsoup.parse(driver.getPageSource());
        Element body = doc.body();

        JavascriptExecutor js = (JavascriptExecutor) driver;

        List<String> urls = new ArrayList<>();

//        driver.findElement(By.className("product-list"))
//        List<WebElement> productList = driver.findElements(By.className("product-wrap"));
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(AuctionScraper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            driver.findElement(By.className("remodal-wrapper")).findElement(By.className("remodal-close")).click();
//        } catch (Exception e) {
//            System.out.println("No Popup..");
//        }
//        js.executeScript("arguments[0].scrollIntoView();",productList.get(productList.size()-1) );
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(AuctionScraper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        productList = driver.findElements(By.className("product-wrap"));
        List<WebElement> productList = driver.findElements(By.className("hidden-product-link"));
        js.executeScript("arguments[0].scrollIntoView();", productList.get(productList.size() - 1));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AuctionScraper.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (driver.findElements(By.className("hidden-product-link")).size() == productList.size()) {

            productList = driver.findElements(By.className("hidden-product-link"));

            js.executeScript("arguments[0].scrollIntoView();", productList.get(productList.size() - 1));

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(AuctionScraper.class.getName()).log(Level.SEVERE, null, ex);
//            }

//            productList = driver.findElements(By.className("hidden-product-link"));
//            items = driver.findElements(By.className("product_clear")).size();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AuctionScraper.class.getName()).log(Level.SEVERE, null, ex);
        }

        productList = driver.findElements(By.className("hidden-product-link"));

        for (WebElement product : productList) {
            urls.add(product.getAttribute("href"));
            System.out.println(product.getAttribute("href"));
        }
        System.out.println(urls.size() + "urls");

        return urls;
    }

    public static List<AuctionContent> doM(String url) throws InterruptedException {
        WebDriver driver = FirefoxInitializer.getDriver();
        driver.get(url);
        Document doc = Jsoup.parse(driver.getPageSource());
        Element body = doc.body();

        JavascriptExecutor js = (JavascriptExecutor) driver;

//        AuctionContent auctionContent = new AuctionContent();
        List<AuctionContent> dataList = new ArrayList<>();

//        String rating = driver.findElement(By.cssSelector("span.spr.badge")).getAttribute("data-rating");
//        String rating = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[2]/div/div/div[1]/div[2]/span/span[1]")).getAttribute("data-rating");
//        System.out.println("Rating : "+rating);
//        auctionContent.setRating(rating);
        try {

            js.executeScript("arguments[0].scrollIntoView();", driver.findElement(By.className("spr-reviews")));
            Thread.sleep(2000);
            List<WebElement> sprPagination = driver.findElement(By.className("spr-pagination")).findElements(By.className("spr-pagination-page"));
            int pages = Integer.parseInt(sprPagination.get(sprPagination.size()-1).getAttribute("innerText"));

            for (int i = 0; i < pages; i++) {
                
                try {
                    List<WebElement> reviews = driver.findElement(By.className("spr-reviews")).findElements(By.className("spr-review"));
                    for (WebElement review : reviews) {

                        AuctionContent auctionContent = new AuctionContent();

                        auctionContent.setUrl(url);

                        String productName = driver.findElement(By.className("product_name")).getAttribute("innerText");
                        System.out.println("Product Name : " + productName);
                        auctionContent.setProductName(productName);

                        String reviewHead = review.findElement(By.className("spr-review-header-title")).getAttribute("innerText");
                        System.out.println("R Head : " + reviewHead);
                        auctionContent.setReviewHead(reviewHead);

                        String reviewBody = review.findElement(By.className("spr-review-content-body")).getAttribute("innerText");
                        System.out.println("Review Body : " + reviewBody);
                        auctionContent.setReviewBody(reviewBody);

                        String reviewCust = review.findElement(By.className("spr-review-header-byline")).getAttribute("innerText");
                        String customer = reviewCust.split(" on ")[0].trim();
                        System.out.println("Customer : " + customer);
                        auctionContent.setCustomer(customer);

                        String date = reviewCust.split(" on ")[1].trim();
                        String[] splitDate = date.replaceAll(",", "").split(" ");
                        String fDate = splitDate[1] + "-" + splitDate[0] + "-" + splitDate[2];
                        System.out.println("Date : " + fDate);
                        auctionContent.setDate(fDate);

                        List<WebElement> stars = review.findElement(By.className("spr-starratings")).findElements(By.xpath("./*"));
                        int rating = 0;
                        for (WebElement star : stars) {
                            if (star.getAttribute("class").equalsIgnoreCase("spr-icon spr-icon-star")) {
                                rating += 1;
                            }
                        }
                        String custRating = Integer.toString(rating);
                        System.out.println("Rating : " + custRating);
                        auctionContent.setRating(custRating);

                        dataList.add(auctionContent);

                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    System.out.println("No Reviews..");
                }

                driver.findElement(By.className("spr-pagination")).findElement(By.className("spr-pagination-next")).click();
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.out.println("No Pagination..");

            try {
                js.executeScript("arguments[0].scrollIntoView();", driver.findElement(By.className("spr-reviews")));
                Thread.sleep(2500);
                List<WebElement> reviews = driver.findElement(By.className("spr-reviews")).findElements(By.className("spr-review"));
                for (WebElement review : reviews) {

                    AuctionContent auctionContent = new AuctionContent();

                    auctionContent.setUrl(url);

                    String productName = driver.findElement(By.className("product_name")).getAttribute("innerText");
                    System.out.println("Product Name : " + productName);
                    auctionContent.setProductName(productName);

                    String reviewHead = review.findElement(By.className("spr-review-header-title")).getAttribute("innerText");
                    System.out.println("R Head : " + reviewHead);
                    auctionContent.setReviewHead(reviewHead);

                    String reviewBody = review.findElement(By.className("spr-review-content-body")).getAttribute("innerText");
                    System.out.println("Review Body : " + reviewBody);
                    auctionContent.setReviewBody(reviewBody);

                    String reviewCust = review.findElement(By.className("spr-review-header-byline")).getAttribute("innerText");
                    String customer = reviewCust.split(" on ")[0].trim();
                    System.out.println("Customer : " + customer);
                    auctionContent.setCustomer(customer);

                    String date = reviewCust.split(" on ")[1].trim();
                    String[] splitDate = date.replaceAll(",", "").split(" ");
                    String fDate = splitDate[1] + "-" + splitDate[0] + "-" + splitDate[2];
                    System.out.println("Date : " + fDate);
                    auctionContent.setDate(fDate);

                    List<WebElement> stars = review.findElement(By.className("spr-starratings")).findElements(By.xpath("./*"));
                    int rating = 0;
                    for (WebElement star : stars) {
                        if (star.getAttribute("class").equalsIgnoreCase("spr-icon spr-icon-star")) {
                            rating += 1;
                        }
                    }
                    String custRating = Integer.toString(rating);
                    System.out.println("Rating : " + custRating);
                    auctionContent.setRating(custRating);

                    dataList.add(auctionContent);

                    Thread.sleep(1000);
                }
            } catch (Exception x) {
                System.out.println("No Reviews..");
            }

        }

        return dataList;
    }

    private static String formatDate(String auctionDate) {
        String month = auctionDate.split(" ")[1];
        String m = "";
        if (month.equalsIgnoreCase("January")) {
            m = "01";
        } else if (month.equalsIgnoreCase("February")) {
            m = "02";
        } else if (month.equalsIgnoreCase("March")) {
            m = "03";
        } else if (month.equalsIgnoreCase("April")) {
            m = "04";
        } else if (month.equalsIgnoreCase("May")) {
            m = "05";
        } else if (month.equalsIgnoreCase("June")) {
            m = "06";
        } else if (month.equalsIgnoreCase("July")) {
            m = "07";
        } else if (month.equalsIgnoreCase("August")) {
            m = "08";
        } else if (month.equalsIgnoreCase("September")) {
            m = "09";
        } else if (month.equalsIgnoreCase("October")) {
            m = "10";
        } else if (month.equalsIgnoreCase("November")) {
            m = "11";
        } else {
            m = "12";
        }

        String date = auctionDate.split(" ")[0] + " " + m + " " + auctionDate.split(" ")[2];

        return date;
    }

}
