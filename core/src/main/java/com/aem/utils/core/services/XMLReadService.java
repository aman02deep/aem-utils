package com.aem.utils.core.services;

import com.aem.utils.core.models.ModelObject;

/**
 * Service Interface to read xml form the provided service url.
 */
public interface XMLReadService {

  /**
   * This method reads XML file from service.
   *
   * @param serviceUrl service URL.
   * @return EventsList Object
   */
  ModelObject readXMLFromURL(String serviceUrl);
}
