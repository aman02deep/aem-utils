(function ($, window, document) {

  /* Adapting window object to foundation-registry */
  let registry = $(window).adaptTo("foundation-registry");

  /*Validator for TextField - Any Custom logic can go inside validate function - starts */
  registry.register("foundation.validation.validator", {

    selector: "[data-validation=txt-validate]",
    validate: function (el) {
      let element = $(el);
      let pattern = element.data('pattern');
      let value = element.val();
      let error;
      if (value.length === 0) {
        return "Please enter text";
      } else {
        let patterns = {
          latitude: /^-{0,1}((90|90.[0]{1,20}|[0-9]|[1-8][0-9])|(89|[0-9]|[1-8][0-9])[.]{1}[0-9]{1,20}){1}$/,
          longitude: /^-{0,1}((180|180.[0]{1,20}|[0-9]|([0-9][0-9])|([1][0-7][0-9]))|(179|[0-9]|([0-9][0-9])|([1][0-7][0-9]))[.]{1}[0-9]{1,20}){1}$/,
          alpha: /[a-zA-Z]+/,
          alphaNumeric: /\w+/,
          integer: /-?\d+/
        }

        /*
         * Test pattern if set. Pattern can be a preset regex pattern name or
         * a regular expression such as "^\\d+$".
         */
        if (pattern) {
          if (patterns[pattern]) {
            error = !patterns[pattern].test(value);
          } else {
            error = !(new RegExp(pattern)).test(value);
          }

          if (error) {
            return "The field must match the pattern: " + pattern;
          }
        }
      }
    }
  });
})
($, window, document);
