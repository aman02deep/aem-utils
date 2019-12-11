package com.aem.utils.core.servies;

import io.wcm.testing.mock.aem.junit5.AemContext;

import java.io.IOException;
import java.io.InputStream;

import com.aem.utils.core.context.AppAemContextTest;
import com.aem.utils.core.models.ModelObject;
import com.aem.utils.core.services.impl.XMLReadServiceImpl;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class XMLReadServiceImplTest {

  @Rule
  public AemContext context = AppAemContextTest.appAemContext();

  @Mock
  private HttpClientBuilder httpClientBuilder;

  @Mock
  private CloseableHttpClient closeableHttpClient;

  @Mock
  private CloseableHttpResponse closeableHttpResponse;

  @Mock
  private HttpEntity httpEntity;

  @Mock
  private HttpClientBuilderFactory httpClientBuilderFactory;

  @InjectMocks
  private XMLReadServiceImpl xmlReadService;

  @Before public void setUp() throws Exception {
    Mockito.when(httpClientBuilderFactory.newBuilder()).thenReturn(httpClientBuilder);
    Mockito.when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
  }

  @Test public void testXMLReadServiceResponse() throws IOException {

    Mockito.when(closeableHttpClient.execute(Mockito.any())).thenReturn(closeableHttpResponse);
    Mockito.when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);

    String xmlResp = AppAemContextTest.getFile("events/serviceResp.xml");
    InputStream initialString = IOUtils.toInputStream(xmlResp, "UTF-8");
    Mockito.when(httpEntity.getContent()).thenReturn(initialString);

    ModelObject eventsList = xmlReadService.readXMLFromURL("http://dummy-url");
    Assert.assertNotNull(eventsList);

  }

  @Test public void testXMLReadServiceException() throws IOException {

    Mockito.when(closeableHttpClient.execute(Mockito.any())).thenThrow(IOException.class);
    ModelObject eventsList = xmlReadService.readXMLFromURL("http://dummy-url");

    Assert.assertNull(eventsList);
  }
}
