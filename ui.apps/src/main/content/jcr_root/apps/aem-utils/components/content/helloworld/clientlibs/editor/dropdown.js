( function (document, $, Coral) {
    "use strict";

    var TYPE = "./type", VARIANT = "./variants";

    $(document).on("foundation-contentloaded", function (e) {

        var variant;
        var typeSelect = $("[name='" + TYPE +"']").closest(".coral-Select");
        var variantSelect = $("select[name='" + VARIANT +"']").closest(".coral-Select");

        /* populate variants dropdown if there is already a selected item */
        getVariants($("[name='./type']").val());

        /* it updates the hidden value on variant select, this variant will be used later on whenever user willopen dialog again */
        variantSelect.on('selected.select', function(event){
            $("input[name='./variants']").val(event.selected);
        });

        /* it populates the variant dropdown whenever user will select a different product */
        typeSelect.on('selected.select', function(event){
            getVariants(event.selected);
        });

        /* Get variants call get the varaiants stored in etc/design folder */
        function getVariants(productId){
            var url = "/etc/designs/aem-utils/products/items/"+productId+"/variants.infinity.json";
            $.getJSON(url).done(function(data){
                var currentVariant = $("input[name='./variants']").val();
                console.log(currentVariant);

                var selectionNode = $("select[name='" + VARIANT +"']")[0];
                $(selectionNode).empty();

                // clear UL
                $("[name='" + VARIANT +"']").siblings('.coral-SelectList').children().remove();

                $("<option>").appendTo(selectionNode).val("#").html("---- Select Variant ----");
                $.each(data, function( k, v ) {
                    if(k != 'jcr:primaryType'){
                        $("<option>").appendTo(selectionNode).val(v.itemNumber).html(v.color+", price: $ "+v.price+" ( Item Number: "+v.itemNumber+" )");
                    }
                });

                /* select the correct variant in the dropdown, if there is a already selected value available */
                $("select[name='" + VARIANT +"'] option[value='" +currentVariant+ "']").prop('selected', 'selected').change();

                variant = new CUI.Select({
                    element: $("select[name='" + VARIANT +"']").closest(".coral-Select")
                });
            });
        }

    });

})(document, Granite.$, Coral);
