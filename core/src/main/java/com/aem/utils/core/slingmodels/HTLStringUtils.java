package com.aem.utils.core.slingmodels;

import javax.annotation.PostConstruct;

import com.day.cq.wcm.api.Page;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

/**
 * this class is use to replace following pattern with provided tag within text
 * [[ text ]]
 * the above will return <strong>text</strong>, if the provided tag is strong
 * the above will return text, if the provided tag is empty or null
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HTLStringUtils {
  /**
   * Logger instance.
   */
  private static final Logger LOG = Logger.getLogger(HTLStringUtils.class);
  @RequestAttribute
  private String text;
  @RequestAttribute
  private String method;
  @RequestAttribute
  private String checkPattern;
  @ScriptVariable
  private Page currentPage;
  private Boolean value;

  @PostConstruct protected void init() {
    if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(method)) {
      switch (method) {
        case "contains":
          value = stringContainsCheck();
          break;
        case "startsWith":
          value = stringStartsWithCheck();
          break;
        default:
          value = false;
      }
    } else {
      value = false;
      LOG.debug("Inject text is null or Empty for page:" + currentPage.getPath());
    }
  }

  private Boolean stringStartsWithCheck() {
    return text.startsWith(checkPattern);
  }

  private Boolean stringContainsCheck() {
    return text.contains(checkPattern);
  }

  /**
   * Return the value after tag replacement in text
   *
   * @return the value
   */
  public Boolean getValue() {
    return value;
  }
}
