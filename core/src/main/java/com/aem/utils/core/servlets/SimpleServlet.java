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

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.aem.utils.core.utils.HttpClientUtils;
import com.day.cq.commons.jcr.JcrUtil;
import com.google.gson.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.resourceTypes=page/availableProducts",
                "sling.servlet.resourceTypes=page/availableVariants"
        })
public class SimpleServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUid = 1L;
    String selectionNode = "/etc/designs/aem-utils/products";
    String serviceUrl = "http://localhost:4502/etc/designs/aem-utils/products.json";

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {

        HttpClientUtils httpclientUtils = new HttpClientUtils();
        GetMethod getMethod = httpclientUtils.getDefaultGetMethod(serviceUrl,"");
        String serviceResponse = httpclientUtils.callExternalServiceGet(getMethod);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
        JsonElement element = gson.fromJson(serviceResponse, JsonElement.class);
        JsonObject dataObject = element.getAsJsonObject();
        JsonArray dataArray = dataObject.getAsJsonArray("data");
        /* data source */
        ValueMap vm;
        List<Resource> fakeResourceList = new ArrayList<>();


        /* try to create nodes */
        Session session = req.getResourceResolver().adaptTo(Session.class);
        try {
            /* Get or create select dropdown parent node */
            Node productsNode = JcrUtils.getOrCreateByPath(selectionNode,"nt:unstructured", session);
            if (!productsNode.hasProperty("sling:resourceType")){
                JcrUtil.setProperty(productsNode,"sling:resourceType","granite/ui/components/foundation/form/select");
                JcrUtil.setProperty(productsNode,"fieldLabel","Select Product");
                JcrUtil.setProperty(productsNode,"name","./product");
            }

            Node items = JcrUtils.getOrCreateByPath(selectionNode+"/items","nt:unstructured", session);

            for (int i = 0; i < dataArray.size(); i++) {
                JsonObject singleObject = dataArray.get(i).getAsJsonObject();
                singleObject.remove("measurementsImperial");
                singleObject.remove("measurementsMetric");

                String childNodePath = singleObject.get("id").getAsString();
                Node childNode;
                if (!items.hasNode(childNodePath)){
                    childNode = items.addNode(childNodePath,"nt:unstructured");
                }else {
                    childNode = items.getNode(childNodePath);
                }

                JcrUtil.setProperty(childNode, "text",singleObject.get("displayName").getAsString());
                JcrUtil.setProperty(childNode, "value",StringEscapeUtils.unescapeJson(singleObject.toString()));

                /* set variants node */
                JsonArray variantsArray = singleObject.getAsJsonObject("variants").getAsJsonArray("productVariants");
                int variantsArraySize = variantsArray.size();

                /* create variants node */
                Node variantsNode = JcrUtils.getOrCreateByPath(childNode.getPath()+"/variants","nt:unstructured", session);
                for (int j = 0; j < variantsArraySize; j++) {
                    JsonObject variantObj = variantsArray.get(j).getAsJsonObject();
                    String itemNumber = variantObj.get("itemNumber").getAsString();
                    Node variant = variantsNode.addNode(itemNumber,"nt:unstructured");
                    JcrUtil.setProperty(variant, "itemNumber",itemNumber);
                    JcrUtil.setProperty(variant, "color",variantObj.get("color").getAsJsonObject().get("name").getAsString());
                    JcrUtil.setProperty(variant, "price",variantObj.get("price").getAsJsonObject().get("amount").getAsString());
                }
            }
            session.save();
        } catch (RepositoryException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        /* Ends here */

        vm = new ValueMapDecorator(new HashMap<String, Object>());
        vm.put("value","#");
        vm.put("text","---- Select Product ----");
        fakeResourceList.add(new ValueMapResource(req.getResourceResolver(), new ResourceMetadata(), "nt:unstructured", vm));
        for (int i = 0; i < dataArray.size(); i++) {
            vm = new ValueMapDecorator(new HashMap<String, Object>());
            JsonObject singleObject = dataArray.get(i).getAsJsonObject();
            vm.put("value",singleObject.get("id").getAsString());
            vm.put("text",singleObject.get("displayName").getAsString());
            fakeResourceList.add(new ValueMapResource(req.getResourceResolver(), new ResourceMetadata(), "nt:unstructured", vm));
        }

        DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
        req.setAttribute(DataSource.class.getName(), ds);



            /*   JsonObject eachOption;
            JsonArray optionsArray = new JsonArray();

        for (int i = 0; i < dataArray.size(); i++) {
            eachOption = new JsonObject();
            JsonObject singleObject = dataArray.get(i).getAsJsonObject();
            eachOption.addProperty("text", singleObject.get("displayName").getAsString());
            eachOption.addProperty("value", singleObject.get("id").getAsString());
            optionsArray.add(eachOption);
        }

        JsonObject finalJsonResponse = new JsonObject();
        //Adding this finalJsonResponse object to showcase optionsRoot property functionality
        finalJsonResponse.add("root", optionsArray);

        resp.getWriter().println(finalJsonResponse.toString());
        System.out.println("optionsArray :"+optionsArray); */
    }
}
