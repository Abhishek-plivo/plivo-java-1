package com.plivo.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import static java.nio.charset.StandardCharsets.*;
import java.net.*;


public class Utils {
	public static boolean isValidSignature (String url,String nonce,String signature,String authToken )
    throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] nonceBytes = nonce.getBytes(ISO_8859_1);
		nonce = new String(nonceBytes, UTF_8);
		byte[] signatureBytes = signature.getBytes(ISO_8859_1);
		signature = new String(signatureBytes, UTF_8);
		byte[] authTokenBytes = authToken.getBytes(ISO_8859_1);
		authToken = new String(authTokenBytes, UTF_8);
		byte[] urlBytes = url.getBytes(ISO_8859_1);
		url = new String(urlBytes, UTF_8);

		String base_url = "";
		String signature_mac = "";

		try {
			URL parsedURL = new URL(url);
			base_url = parsedURL.getProtocol() + "://" + parsedURL.getHost() + parsedURL.getPath();
		} catch (MalformedURLException e) {
			System.out.println(e);
			System.out.println("\nException in Plivo.utils.isValidSignature\n Valid 'url' needs to be passed\n\n");	  
		}

		try {
			String payload = base_url+nonce;
			String SIGNATURE_ALGORITHM = "HmacSHA256";
			SecretKeySpec secret = new SecretKeySpec(authToken.getBytes(), SIGNATURE_ALGORITHM);
			Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
			mac.init(secret);
			signature_mac = new String(Base64.getEncoder().encode(mac.doFinal(payload.getBytes())));
			signature_mac = signature_mac.trim();
		} catch (Exception e) {
			System.out.println("Error while creating HmacSHA256");
		}

		return signature_mac.equals(signature);
  }


  public static boolean allNotNull(Object... objects) {
    return Stream.of(objects)
      .noneMatch(Objects::isNull);
  }

  public static boolean isSubaccountIdValid(String id) {
    return id != null && id.startsWith("SA") && id.length() == 20;
  }

  public static boolean isAccountIdValid(String id) {
    return id != null && id.startsWith("MA") && id.length() == 20;
  }

  public static boolean anyNotNull(Object... objects) {
    return Stream.of(objects)
      .anyMatch(Objects::nonNull);
  }

  public static Map<String, Object> objectToMap(ObjectMapper objectMapper, Object object) {
    Map<String, Object> origMap = objectMapper.convertValue(object, Map.class);
    Map<String, Object> map = new LinkedHashMap<>();
    for (Entry<String, Object> entry : origMap.entrySet()) {
      if (entry.getValue() != null) {
        if (entry.getValue() instanceof Map) {
          Map<String, Object> innerEntries = objectMapper.convertValue(entry.getValue(), Map.class);
          for (Entry<String, Object> innerEntry : innerEntries.entrySet()) {
            map.put(entry.getKey() + innerEntry.getKey(), innerEntry.getValue());
          }
        } else {
          map.put(entry.getKey(), entry.getValue());
        }
      }
    }
    return map;
  }

  private final static String SIGNATURE_ALGORITHM = "HmacSHA1";

  public static String computeSignature(String authId, String url, Map<String, String> params)
    throws NoSuchAlgorithmException, InvalidKeyException {
    if (!allNotNull(authId, url, params)) {
      throw new IllegalArgumentException("authId, url and params must be non-null");
    }

    List<Entry<String, String>> entryList =  new ArrayList<Entry<String, String>>(params.entrySet());
    entryList.sort((Comparator<Entry<String, String>>) (o1, o2) -> {
      return o1.getKey().compareTo(o2.getKey());
    });

    String joinedParams = url.concat(entryList.stream().map(it -> it.getKey().concat(it.getValue())).collect(Collectors.joining("")));

    SecretKeySpec signingKey = new SecretKeySpec(authId.getBytes(), SIGNATURE_ALGORITHM);
    Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
    mac.init(signingKey);
    return new String(Base64.getEncoder().encode(mac.doFinal(joinedParams.getBytes())));
  }

  public static boolean validateSignature(String authId, String url, Map<String, String> params, String signature)
    throws InvalidKeyException, NoSuchAlgorithmException {
    return computeSignature(authId, url, params).equals(signature);
  }
}
