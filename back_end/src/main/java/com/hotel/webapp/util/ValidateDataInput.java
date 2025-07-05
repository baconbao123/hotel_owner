package com.hotel.webapp.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidateDataInput {
  public String capitalizeFirstLetter(String str) {
    return str != null && !str.isEmpty() ? (str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase()) : null;
  }

  public String lowercaseStr(String str) {
    return str != null && !str.isEmpty() ? str.toLowerCase() : null;
  }

  public String generateColName(String str) {
    return str.toLowerCase()
              .replaceAll("[^a-zA-Z\\s]", "")
              .trim()
              .replaceAll("\\s+", "_");
  }

  public String cutIcon(String icon) {
    if (icon == null || icon.trim().isEmpty()) {
      return null;
    }

    String trimmedIcon = icon.trim();

    if (trimmedIcon.startsWith("<i")) {
      String regex = "<i\\s+class=\"([^\"]+)\"[^>]*>";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(trimmedIcon);

      if (matcher.find()) {
        String classValue = matcher.group(1).trim();
        return classValue.isEmpty() ? null : classValue;
      }
    }

    String classValue = trimmedIcon.replaceAll("[<>]", "")
                                   .replaceAll("/+$", "")
                                   .trim();
    return classValue.isEmpty() ? null : classValue;
  }
}
