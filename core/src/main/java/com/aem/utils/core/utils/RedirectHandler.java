package com.aem.utils.core.utils;

import java.io.IOException;

import javax.annotation.PostConstruct;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.day.cq.wcm.api.Page;
import com.google.common.net.HttpHeaders;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class)
public class RedirectHandler {

  private static final Logger LOG = LoggerFactory.getLogger(RedirectHandler.class);

  @SlingObject
  private SlingHttpServletResponse response;

  @SlingObject
  private ResourceResolver resourceResolver;

  @ScriptVariable
  private Page currentPage;

  @ScriptVariable(name = "wcmmode")
  private SightlyWCMMode wcmMode;

  @PostConstruct private void activate() throws IOException {

    String redirectUrl = getRedirectTarget();
    int statusCode = getRedirectStatus();

    if (!wcmMode.isDisabled() || StringUtils.isBlank(redirectUrl)) {
      return;
    }

    LOG.trace("Redirecting ({}) to {}", statusCode, redirectUrl);
    if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
      response.sendRedirect(redirectUrl);
    } else {
      response.setHeader(HttpHeaders.LOCATION, redirectUrl);
      response.setStatus(statusCode);
    }
  }

  public boolean isRedirectConfigured() {
    return StringUtils.isNotBlank(getRedirectTarget());
  }

  /**
   * Get the "redirectTarget" property from the page content. Check to see its
   * not pointing to same page. If its external, return it as is. If its
   * internal, remove the html extension and use the resource resolver to map
   * it.
   *
   * @return target path/url
   */
  public final String getRedirectTarget() {
    String redirectTarget = currentPage.getProperties().get("redirectTarget", String.class);

    if (StringUtils.isBlank(redirectTarget)) {
      return "";
    }

    final int protocolIndex = redirectTarget.indexOf("//");

    LOG.debug("location: {}", redirectTarget);

    if (protocolIndex > -1) {
      return redirectTarget;
    } else if (StringUtils.equals(redirectTarget, currentPage.getPath())) {
      LOG.warn("{} is trying to redirect to self", currentPage.getPath());
      return "";
    } else {
      return resourceResolver.map(redirectTarget) + ".html";
    }
  }

  /**
   * check for the redirect Status code and return accordingly
   *
   * @return 301 or 302
   */
  public final int getRedirectStatus() {
    int redirectStatusCode = HttpStatus.SC_MOVED_TEMPORARILY;

    /* Read status code from Page Properties */
    String redirectTarget =
        currentPage.getProperties().get("redirectStatusMovedPermanently", String.class);

    if (StringUtils.isNotBlank(redirectTarget) && "true".equalsIgnoreCase(redirectTarget)) {
      redirectStatusCode = HttpStatus.SC_MOVED_PERMANENTLY;
    }
    return redirectStatusCode;
  }
}
