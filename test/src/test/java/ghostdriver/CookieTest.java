package ghostdriver;

import org.junit.Test;
import java.util.Date;
import java.util.Set;

import org.openqa.selenium.InvalidCookieDomainException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Cookie;

import static org.junit.Assert.*;

public class CookieTest extends BaseTest {
    @Test
    public void shouldBeAbleToAddCookie() throws InterruptedException {
        WebDriver d = getDriver();
        d.get("http://www.github.com");

        // Clear all cookies
        assertTrue(d.manage().getCookies().size() > 0);
        d.manage().deleteAllCookies();
        assertEquals(d.manage().getCookies().size(), 0);

        // Add a cookie
        Cookie myCookie = new Cookie("foo", "bar");
        d.manage().addCookie(myCookie);
        assertEquals(d.manage().getCookies().size(), 1);

        // Check that the new cookie it's there
        Cookie myCookieIsBack = d.manage().getCookieNamed("foo");
        assertTrue(myCookieIsBack.getDomain().contains("github"));
    }

	@Test
    public void shouldRetainCookieInfo() {
        WebDriver d = getDriver();
        d.get("https://github.com/");

        // Clear all cookies
        assertTrue(d.manage().getCookies().size() > 0);
        d.manage().deleteAllCookies();
        assertEquals(d.manage().getCookies().size(), 0);

        // Added cookie (in a sub-path - allowed)
        Cookie addedCookie =
            new Cookie.Builder("fish", "cod")
                .expiresOn(new Date(System.currentTimeMillis() + 100 * 1000)) //< now + 100sec
                .path("/404")
                .isSecure(true)
                .domain("github.com")
                .build();
        d.manage().addCookie(addedCookie);

        // Search cookie on the root-path and fail to find it
        Cookie retrieved = d.manage().getCookieNamed("fish");
        assertNull(retrieved);

        // Go to the "/404" sub-path (to find the cookie)
        d.get("https://github.com/404");
        retrieved = d.manage().getCookieNamed("fish");
        assertNotNull(retrieved);
        // Check that it all matches
        assertEquals(addedCookie.getName(), retrieved.getName());
        assertEquals(addedCookie.getValue(), retrieved.getValue());
        assertEquals(addedCookie.getExpiry(), retrieved.getExpiry());
        assertEquals(addedCookie.isSecure(), retrieved.isSecure());
        assertEquals(addedCookie.getPath(), retrieved.getPath());
        assertTrue(retrieved.getDomain().contains(addedCookie.getDomain()));

        // Clear cookies
        d.manage().deleteAllCookies();
        assertEquals(d.manage().getCookies().size(), 0);
    }

    @Test(expected = InvalidCookieDomainException.class)
    public void shouldNotAllowToCreateCookieOnDifferentDomain() {
        WebDriver d = getDriver();
        d.get("https://www.google.com/");

        // Added cookie (in a sub-path)
        Cookie addedCookie =
                new Cookie.Builder("fish", "cod")
                        .expiresOn(new Date(System.currentTimeMillis() + 100 * 1000)) //< now + 100sec
                        .path("/404")
                        .isSecure(true)
                        .domain("github.com")
                        .build();
        d.manage().addCookie(addedCookie);
    }
}