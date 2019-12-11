package com.aem.utils.core.services.impl;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.aem.utils.core.models.ModelObject;
import com.aem.utils.core.services.XMLReadService;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to read xml form the provided service url.
 */
@Component(immediate = true,
           service = XMLReadService.class)
public class XMLReadServiceImpl implements XMLReadService {

  private Logger logger = LoggerFactory.getLogger(XMLReadServiceImpl.class);
  /**
   * Inject HttpClientBuilderFactory.
   */
  @Reference
  private HttpClientBuilderFactory httpClientBuilderFactory;

  /**
   * This method reads XML file from service..
   *
   * @param serviceUrl service URL.
   * @return EventsList Object
   */
  @Override public ModelObject readXMLFromURL(final String serviceUrl) {

    try {
      HttpPost post = new HttpPost(serviceUrl);
      post.addHeader("Content-Type", "application/xml");
      CloseableHttpClient httpClient = httpClientBuilderFactory.newBuilder().build();
      CloseableHttpResponse execute = httpClient.execute(post);
      String resp = EntityUtils.toString(execute.getEntity(), "UTF-8");
      JAXBContext jaxbContext = JAXBContext.newInstance(ModelObject.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      return (ModelObject) unmarshaller.unmarshal(new StringReader(resp));

    } catch (IOException | JAXBException e) {
      logger.error("Exception while reading xml from URL", e);
    }

    return null;
  }
}
