package com.aem.utils.core.context;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.scripting.WCMBindingsConstants;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.impl.ResourceTypeBasedResourcePicker;
import org.apache.sling.models.spi.ImplementationPicker;
import org.mockito.Mockito;

import static com.day.cq.wcm.api.WCMMode.REQUEST_ATTRIBUTE_NAME;
import static io.wcm.testing.mock.wcmio.caconfig.ContextPlugins.WCMIO_CACONFIG;
import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AppAemContextTest {

  public static AemContext appAemContext() {
    return new AemContextBuilder() // register plugins
        .plugin(CACONFIG).plugin(WCMIO_CACONFIG)
        // shared context setup code for all tests
        .<AemContext>afterSetUp(context -> {
          // register sling models
          context.addModelsForPackage("com.mclaren.core");
          context.addModelsForPackage("com.adobe.cq.wcm.core.components");
          context.registerService(ImplementationPicker.class, new ResourceTypeBasedResourcePicker());
        }).build();
  }

  /**
   * Get the slingBinding object for the context.<br>
   *
   * @param context The context to use
   * @return A <code>Bindings</code> object
   */
  private static Bindings getDefaultSlingBindings(AemContext context) {
    SlingBindings slingBindings =
        (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
    if (slingBindings != null) {
      return new SimpleBindings(slingBindings);
    }
    return new SimpleBindings();
  }

  /**
   * Set a resource object in the context.<br>
   *
   * @param context The context to use
   * @param page    The page of the resource that has to be set in the context
   */
  public static void setResource(AemContext context, String page) {
    setResource(context, page, "", WCMMode.EDIT);
  }

  /**
   * Set a resource object in the context.<br>
   *
   * @param context      The context to use
   * @param resourcePath The resourcePath of the resource that has to be set in the context
   */
  public static void setResource(AemContext context, String page, String resourcePath) {
    setResource(context, page, resourcePath, WCMMode.EDIT);
  }

  /**
   * Set a resource object in the context.<br>
   *
   * @param context      The context to use
   * @param resourcePath The resourcePath of the resource that has to be set in the context
   * @param wcmmode      Set a specified wcmmode in the resource
   */
  public static void setResource(AemContext context, String page, String resourcePath,
      WCMMode wcmmode) {
    context.currentPage(page);
    context.request().setAttribute(REQUEST_ATTRIBUTE_NAME, wcmmode);

    Resource resource = context.currentResource(page + resourcePath);
    Bindings bindings = getDefaultSlingBindings(context);
    ValueMap properties = resource.adaptTo(ValueMap.class);
    PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
    HierarchyNodeInheritanceValueMap inVm =
        new HierarchyNodeInheritanceValueMap(context.currentPage().getContentResource());

    bindings.put(WCMBindings.WCM_MODE, new SightlyWCMMode(context.request()));
    bindings.put(WCMBindingsConstants.NAME_PROPERTIES, properties);
    bindings.put(WCMBindingsConstants.NAME_PAGE_MANAGER, pageManager);
    bindings.put(WCMBindingsConstants.NAME_PAGE_PROPERTIES, inVm);
    bindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, context.currentPage());

    Style style = Mockito.mock(Style.class);
    when(style.get(any(), any(Object.class))).thenAnswer(
        invocation -> invocation.getArguments()[1]
    );
    bindings.put(WCMBindingsConstants.NAME_CURRENT_STYLE, style);
  }

}
