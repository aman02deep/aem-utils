package com.aem.utils.core.servlets.datasource;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import java.io.IOException;

import javax.servlet.ServletException;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.aem.utils.core.context.AppAemContextTest;
import com.google.common.collect.Iterators;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Generic Datasource Test Cases
 */
@ExtendWith(AemContextExtension.class)
public class GenericDataSourceServletTest {
  private AemContext context = AppAemContextTest.appAemContext();
  private GenericDataSourceServlet genericDataSourceServlet;

  @BeforeEach public void setup() {
    genericDataSourceServlet = new GenericDataSourceServlet();
  }

  /**
   * Test Generic datasource with all correct data
   * Expected output: returns the result size 4
   *
   * @throws ServletException
   * @throws IOException
   */
  @Test public void testGenericDatasourceWithCorrectData() throws ServletException, IOException {
    SlingHttpServletRequest request = context.request();
    SlingHttpServletResponse response = context.response();
    context.load().json("/servlets/datasource/datasource-items.json", "/path/to/items");
    context.load().json("/servlets/datasource/resource.json", "/path/to/resource");
    context.currentResource("/path/to/resource");
    genericDataSourceServlet.doGet(request, response);
    SimpleDataSource simpleDataSource = (SimpleDataSource) request
        .getAttribute("com.adobe.granite.ui.components.ds" + ".DataSource");
    assertEquals(4, Iterators.size(simpleDataSource.iterator()));
  }

  /**
   * Test Generic datasource with invalid path on resource node
   * Expected output: returns the result size 0
   *
   * @throws ServletException
   * @throws IOException
   */
  @Test public void testGenericDatasourceWithInvalidPathInsideResource()
      throws ServletException, IOException {
    SlingHttpServletRequest request = context.request();
    SlingHttpServletResponse response = context.response();
    context.load().json("/servlets/datasource/resource.json", "/path/to/resource");
    context.currentResource("/path/to/resource");
    genericDataSourceServlet.doGet(request, response);
    SimpleDataSource simpleDataSource =
        (SimpleDataSource) request.getAttribute(DataSource.class.getName());
    assertEquals(0, Iterators.size(simpleDataSource.iterator()));
  }

  /**
   * Test Generic datasource with empty path on resource node
   * Expected output: returns the result size 0
   *
   * @throws ServletException
   * @throws IOException
   */
  @Test public void testGenericDatasourceWithEmptyPathInsideResource()
      throws ServletException, IOException {
    SlingHttpServletRequest request = context.request();
    SlingHttpServletResponse response = context.response();
    context.load().json("/servlets/datasource/resource-with-empty-path.json", "/path/to/resource");
    context.currentResource("/path/to/resource");
    genericDataSourceServlet.doGet(request, response);
    SimpleDataSource simpleDataSource =
        (SimpleDataSource) request.getAttribute(DataSource.class.getName());
    assertEquals(0, Iterators.size(simpleDataSource.iterator()));
  }
}
