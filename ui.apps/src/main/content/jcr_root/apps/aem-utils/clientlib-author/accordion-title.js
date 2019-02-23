/**
 * This script can be used to add Change the Title of Accordion item for blog tags mapping
 */

$(document).on("dialog-loaded", function () {
  var accordionItems = document.querySelectorAll('.accordion-multifield-item');

  [].forEach.call(accordionItems, function (accordionItem) {
    var multifieldAccordionItemTitle = $(accordionItem).find("coral-accordion-item-label");
    var multifieldAccordionItemVal = $(accordionItem).find(".accordion-multifield-item-title");
    multifieldAccordionItemTitle.text(function () {
      return multifieldAccordionItemVal.val() ? multifieldAccordionItemVal.val() : multifieldAccordionItemTitle.text();
    });
  });
});



