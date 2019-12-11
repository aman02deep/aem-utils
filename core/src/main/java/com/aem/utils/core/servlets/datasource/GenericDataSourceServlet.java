package com.aem.utils.core.servlets.datasource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

/**
 * Generic Datasource: it can be used to include reusable jcr nodes in side
 * Touch UI dropdown.
 * To use this datasource see below jcr structure
 * + myselect
 * - sling:resourceType = "granite/ui/components/coral/foundation/form/select"
 * - emptyText = "Select"
 * - name = "./myselect"
 * + datasource
 * - path = "/apps/path/to/my/items"
 * - sling:resourceType = aem-utils/generic/datasource"
 */

@Component(service = Servlet.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + "=Aem Utils - Generic Datasource Servlet",
               "sling.servlet.resourceTypes=aem-utils/generic/datasource",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class GenericDataSourceServlet extends SlingSafeMethodsServlet {

  /**
   * Override doGet method of SlingSafeMethodsServlet, for more details read
   * class java doc.
   *
   * @param req  SlingHttpServletRequest
   * @param resp SlingHttpServletResponse
   * @throws ServletException
   * @throws IOException
   */
  @Override protected void doGet(final SlingHttpServletRequest req,
      final SlingHttpServletResponse resp) throws ServletException, IOException {
    ResourceResolver resolver = req.getResourceResolver();

    Stream<Resource> resourceStream =
        Optional.ofNullable(req.getResource().getChild("datasource")).map(Resource::getValueMap)
            .map(valueMap -> valueMap.get("path", String.class)).filter(StringUtils::isNotBlank)
            .map(resolver::getResource).map(Resource::getChildren)
            .map(res -> StreamSupport.stream(res.spliterator(), false)).orElse(Stream.empty());

    List<Resource> resourceList =
        resourceStream.map(Resource::getValueMap).map(ValueMapDecorator::new).map(
            vm -> new ValueMapResource(resolver, new ResourceMetadata(),
                JcrConstants.NT_UNSTRUCTURED, vm)).collect(Collectors.toList());

    DataSource ds = new SimpleDataSource(resourceList.iterator());
    req.setAttribute(DataSource.class.getName(), ds);
  }
}
