/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.utils.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;


/**
 * Generic Datasource: it can be used to include reusable jcr nodes in side
 * Touch UI dropdown.
 * To use this datasource see below jcr structure
 *  + myselect
 *    - sling:resourceType = "granite/ui/components/coral/foundation/form/select"
 *    - emptyText = "Select"
 *    - name = "./myselect"
 *    + datasource
 *      - path = "/apps/path/to/my/items"
 *      - sling:resourceType = "aem-utils/generic/datasource"
 */
@Component(service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "=Generic Datasource Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.resourceTypes=aem-utils/generic/datasource"
    })
public class GenericDataSourceServlet extends SlingSafeMethodsServlet {

  private static final Logger LOG = Logger.getLogger(GenericDataSourceServlet.class);

  /**
   * Override doGet method of SlingSafeMethodsServlet, for more details read class java doc
   * @param req SlingHttpServletRequest
   * @param resp SlingHttpServletResponse
   * @throws ServletException
   * @throws IOException
   */
    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {

      Resource resource = req.getResource();
      Resource datasource = resource.getChild("datasource");
      ResourceResolver resolver = resource.getResourceResolver();
      ValueMap dsProperties = ResourceUtil.getValueMap(datasource);
      String genericListPath = dsProperties.get("path", String.class);
      List<Resource> newResourceList = new ArrayList<>();

      if (StringUtils.isNotBlank(genericListPath)) {
        /* get list of items */
        Resource itemsRootResource = resolver.getResource(genericListPath);
        if (Objects.nonNull(itemsRootResource)) {
          Iterator<Resource> itr = itemsRootResource.getChildren().iterator();
          while (itr.hasNext()) {
            Resource item = itr.next();
            ValueMap vm = new ValueMapDecorator(item.getValueMap());
            newResourceList
                .add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));
          }
        } else {
          LOG.debug("Items root doesn't exist : requested resourcePath is :" + genericListPath);
        }
      } else {
        LOG.debug("Invalid Items root path : requested resourcePath is :" + genericListPath);
      }
      DataSource ds = new SimpleDataSource(newResourceList.iterator());
      req.setAttribute(DataSource.class.getName(), ds);
    }
}