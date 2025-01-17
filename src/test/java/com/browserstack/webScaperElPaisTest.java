package com.browserstack;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class webScaperElPaisTest extends SeleniumTest {

    @Test
    public void webScraping() throws InterruptedException, GeneralSecurityException, IOException {
//        System.setProperty("webdriver.chrome.driver", "/Users/racheljosese/Downloads/chromedriver");
        String API_KEY="AIzaSyBkm_4-DCQ47vsWoaRpf8-vOt2ZHEXru7Q";
//        DesiredCapabilities dcap = new DesiredCapabilities();
//        dcap.setCapability("pageLoadStrategy", "eager");
//        ChromeOptions opt = new ChromeOptions();
//        opt.merge(dcap);
//        WebDriver driver = new ChromeDriver(dcap);
        List<String> headerList=new ArrayList<>();
        driver.get("https://elpais.com");
        driver.manage().window().maximize();
        Thread.sleep(4000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement cookiesaccept = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("didomi-notice-agree-button")));
        cookiesaccept.click();
        for(int i=1;i<=5;i++){
            String articlepath="/html/body/main/div[2]/section[1]/div/div/article["+i+"]/header/h2/a";
            WebElement homepage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("main")));
//            Thread.sleep(5000);
            WebElement articleLink = driver.findElement(By.xpath(articlepath));
            articleLink.click();
//          Thread.sleep(6000);
            WebElement title = driver.findElement(By.xpath("/html/body/article/header/div[1]/h1"));
            headerList.add(title.getText());
//                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/article/header/div[1]/h1")));
            System.out.println(title.getText());
            WebElement content = driver.findElement(By.xpath("/html/body/article/header/div[1]/h2"));
            System.out.println(content.getText());
            WebElement image=driver.findElement(By.xpath("/html/body/article/header/div[2]/figure/span/img"));
            String imageUrltemp = image.getAttribute("src");
            try {
                //generate url
                URL imageURL = new URL(imageUrltemp);
                int countF = 0;
                //read url and retrieve image
                BufferedImage saveImage = ImageIO.read(imageURL);

                //download image to the workspace where the project is, save picture as picture.png (can be changed)
                ImageIO.write(saveImage, "jpg", new File("/Users/racheljose/IdeaProjects/WebScrapingAssignment/src/main/java/org/example/images/articleimage"+i + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            driver.get("https://elpais.com/");
            Thread.sleep(2000);

        }
        Translate t = new Translate.Builder(
                GoogleNetHttpTransport.newTrustedTransport()
                , GsonFactory.getDefaultInstance(), null)
                // Set your application name
                .setApplicationName("Stackoverflow-Example")
                .build();
        Translate.Translations.List list = t.new Translations().list(
                headerList,
                // Target language
                "EN");

        list.setKey(API_KEY);
        TranslationsListResponse response = list.execute();
        HashMap<String,Integer> map=new HashMap<>();
        System.out.println("Translated text-----------");
        for (TranslationsResource translationsResource : response.getTranslations())
        {
            String translatedHeader=translationsResource.getTranslatedText();
            String[] splited = translatedHeader.split("\\s+");
            for(String word:splited){
                map.put(word,map.getOrDefault(word,0)+1);
            }
            System.out.println(translationsResource.getTranslatedText());

        }
        for(String word:map.keySet()){
            int occurence=map.get(word);
            if(occurence>1){
                System.out.println("word:"+word+" occurs:"+occurence);
            }
        }
        driver.quit();
    }
}
