import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageFetcher {

    public static String email = "your_email";
    public static String password = "your_password";

    public static void main(String[] args) throws InterruptedException{
        System.setProperty("webdriver.gecko.driver", "C:\\Gecko\\geckodriver.exe");
        // Create a new instance of the FireFox driver
        WebDriver driver = new FirefoxDriver();

        String url = "https://accounts.google.com/signin/v2/identifier?continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&service=mail&sacu=1&rip=1&flowName=GlifWebSignIn&flowEntry=ServiceLogin";
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        System.out.println("URL is opened in window.");

        driver.findElement(By.id("identifierId")).click();
        System.out.println("Button is clicked.");

        driver.findElement(By.id("identifierId")).sendKeys(email);
        System.out.println("Email entered");
        driver.findElement(By.className("CwaK9")).click();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@name='password']"))));
            System.out.println("Waiting finished");
            driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
            System.out.println("Password entered");
            driver.findElement(By.className("CwaK9")).click();

            WebDriverWait wait_2 = new WebDriverWait(driver, 30);
            wait_2.until(ExpectedConditions.visibilityOf(driver.findElement(By.className("bsU"))));
            String unread_emails = driver.findElement(By.className("bsU")).getText();
            System.out.println("You have " + unread_emails + " unread emails");
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }

        boolean disabled = false;

        while(!disabled) {
            Thread.sleep(3000);
            List<WebElement> emails = driver.findElements(By.xpath("//tr[@class='zA zE']"));
            for(WebElement e : emails) {
                String xPath = getAbsoluteXPath(driver, e);
                String sender = e.findElement(By.xpath(xPath + "/td[5]/div[2]/span[1]/span[@class='zF']")).getAttribute("email");
                String subject = "";
                if(!sender.toLowerCase().contains("google"))
                    subject = e.findElement(By.xpath(xPath + "/td[6]/div/div/div/span/span[@class='bqe']")).getText();
                System.out.println("Sender: " + sender + " , Subject: " + subject);
            }
            driver.findElement(By.id(":k1")).click();

            try {
                String val = driver.findElement(By.id(":k1")).getAttribute("aria-disabled");
                if(val.equals(true))
                    disabled = true;
            } catch(NullPointerException e) {
                //System.out.println(e.toString());
            }
        }
        driver.quit();
    }

    public static String getAbsoluteXPath(WebDriver driver, WebElement element)
    {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "function absoluteXPath(element) {"+
                        "var comp, comps = [];"+
                        "var parent = null;"+
                        "var xpath = '';"+
                        "var getPos = function(element) {"+
                        "var position = 1, curNode;"+
                        "if (element.nodeType == Node.ATTRIBUTE_NODE) {"+ "return null;"+
                        "}"+
                        "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling) {"+
                        "if (curNode.nodeName == element.nodeName) {"+
                        "++position;"+
                        "}"+
                        "}"+
                        "return position;"+
                        "};"+

                        "if (element instanceof Document) {"+
                        "return '/';"+
                        "}"+

                        "for (; element && !(element instanceof Document); element = element.nodeType == Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {"+
                        "comp = comps[comps.length] = {};"+
                        "switch (element.nodeType) {"+
                        "case Node.TEXT_NODE:"+
                        "comp.name = 'text()';"+
                        "break;"+
                        "case Node.ATTRIBUTE_NODE:"+
                        "comp.name = '@' + element.nodeName;"+
                        "break;"+
                        "case Node.PROCESSING_INSTRUCTION_NODE:"+
                        "comp.name = 'processing-instruction()';"+
                        "break;"+
                        "case Node.COMMENT_NODE:"+
                        "comp.name = 'comment()';"+
                        "break;"+
                        "case Node.ELEMENT_NODE:"+
                        "comp.name = element.nodeName;"+
                        "break;"+
                        "}"+
                        "comp.position = getPos(element);"+
                        "}"+

                        "for (var i = comps.length - 1; i >= 0; i--) {"+
                        "comp = comps[i];"+
                        "xpath += '/' + comp.name.toLowerCase();"+
                        "if (comp.position !== null) {"+
                        "xpath += '[' + comp.position + ']';"+
                        "}"+
                        "}"+

                        "return xpath;"+

                        "} return absoluteXPath(arguments[0]);", element);
    }
}
