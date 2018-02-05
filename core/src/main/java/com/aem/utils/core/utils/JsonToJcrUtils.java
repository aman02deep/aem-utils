package com.aem.utils.core.utils;

public class JsonToJcrUtils {

    String  jsonString = "{\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"id\": \"14\",\n" +
            "      \"locale\": \"en_us\",\n" +
            "      \"slug\": \"grills/genesis-ii-e-210-gas-grill\",\n" +
            "      \"sortOrder\": 1,\n" +
            "      \"dateCreated\": {\n" +
            "        \"date\": \"2016-10-12 20:31:18.000000\",\n" +
            "        \"timezone_type\": 3,\n" +
            "        \"timezone\": \"UTC\"\n" +
            "      },\n" +
            "      \"grillCategory\": [\n" +
            "        \"8\"\n" +
            "      ],\n" +
            "      \"badges\": [\n" +
            "        \"2123\",\n" +
            "        \"2124\"\n" +
            "      ],\n" +
            "      \"featureGallery\": [\n" +
            "        {\n" +
            "          \"description\": \"High performance burners\",\n" +
            "          \"fullAsset\": \"https://ux2-production.imgix.net/grill-features/60010001_BurnerTubes_1600x950.jpg\",\n" +
            "          \"videoId\": \"\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"description\": \"Porcelain-enameled, cast-iron cooking grates\",\n" +
            "          \"fullAsset\": \"https://ux2-production.imgix.net/grill-features/60010001_PECI_1600x950.jpg\",\n" +
            "          \"videoId\": \"\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"measurementsImperial\": [\n" +
            "        {\n" +
            "          \"handle\": \"btuMainBtu\",\n" +
            "          \"label\": \"Main burners: BTU-per-hour input\",\n" +
            "          \"value\": \"26,000\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"handle\": \"dimLidOpenInches\",\n" +
            "          \"label\": \"Dimensions - Lid Open (inches)\",\n" +
            "          \"value\": \"61\\\"H x 47\\\"W x 31\\\"D\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"productMeasurements\": [\n" +
            "            {\n" +
            "              \"length\": \"31.625\",\n" +
            "              \"width\": \"25.5\",\n" +
            "              \"height\": \"25.75\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public Boolean convertJsonToJcr( String jcrNodePath ){
        return true;
    }

}
