/**
 * Script can be used to limit the number of items in multifield.
 * Usage:
 * Add below property to node of resourcetype multifield (granite/ui/components/coral/foundation/form/multifield)
 * validation (type: string) : multifield-max-{allowedItems}
 *
 * Example: validation (string) : multifield-max-2	(will allow 2 items in multifield)
 * 
**/
$(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  // check elements that has attribute data-foundation-validation with value starting with "multifield-max"
  selector: "[data-foundation-validation^='multifield-max']",
  validate: function(el) {
    // parse the max number from the attribute value, the value maybe something like "multifield-max-6"
    var validationName = el.getAttribute("data-validation")
    var max = validationName.replace("multifield-max-", "");
    max = parseInt(max);
    // el here is a coral-multifield element
    if (el.items.length > max){
        // items added are more than allowed, return error
        return "Max allowed items is "+ max;
    }
  }
});