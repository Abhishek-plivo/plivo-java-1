package com.plivo.api;

import com.plivo.api.util.Utils;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static junit.framework.TestCase.*;

public class UtilsTest {
  private static Map<String, String> map =  new HashMap<>();

  static {
    map.put("Duration", "300");
    map.put("CallUUID", "97ceeb52-58b6-11e1-86da-77300b68f8bb");
  }
  
  @Test
  public void isValidSignatureTrue() throws Exception {
    assertEquals(true,
      Utils.isValidSignature("https://answer.url","12345","ehV3IKhLysWBxC1sy8INm0qGoQYdYsHwuoKjsX7FsXc=","my_auth_token")
      );
  }

  @Test
  public void isValidSignatureFalse() throws Exception {
    assertEquals(false,
      Utils.isValidSignature("https://answer.url","12345","ehV3IKhLysWBxC1sy8INm0qGoQYdYsHwuoKjsX7FsXc=","my_auth_tokens")
      );
  }

  @Test
  public void computeSignatureShouldWork() throws Exception {
    assertEquals("EJEt0ELanhr8hjMPIJnLNLex0dE=",
      Utils.computeSignature("MAXXXXXXXXXXXXXXXXXX", "http://foo.com/answer/", map)
      );
  }

  @Test
  public void computeSignatureShouldFail() throws Exception {
    assertEquals(false,
      Utils.validateSignature("MAXXXXXXXXXXXXXXXXXX", "http://foo.com/answer/", map, "EJEt0ELanhr8hjMPIJnLNLex0dEK")
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void computeSignatureShouldThrowIllegalArgumentException() throws Exception {
    Utils.computeSignature(null, null, null);
  }

  @Test
  public void computeSignatureEncodingShouldWork() throws Exception {
    Map<String, String> map =  new HashMap<>();
    map.put("a", "1 2");
    assertEquals("n3Xfo4u+vRFyl3gsH8B0qDUIK5g=",
      Utils.computeSignature("MAXXXXXXXXXXXXXXXXXX", "http://foo.com/answer/", map)
    );
  }
}
