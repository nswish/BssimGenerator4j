package com.baosight.bssim.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigModelTestCase {
    @Test
    public void testSave() {
        ConfigModel config = new ConfigModel("GlobalConfig");

        config.save("{\n" +
                "    \"database\"          : 'oracle',\n" +
                "    \"database_ip\"       : \"10.25.76.190\",\n" +
                "    \"database_port\"     : 1521,\n" +
                "    \"database_service\"  : \"erpdvlp\",\n" +
                "    \"database_username\" : \"simdvlp\",\n" +
                "    \"database_password\" : \"simdvlp\",\n" +
                "    \n" +
                "    \"user_tables\"       : [{\n" +
                "        \"user\"          : \"XSSA\",\n" +
                "        \"tables\"        : [\"TSASA01\", \"TSASA02\"]\n" +
                "    }]\n" +
                "}");

        assertEquals("{\n" +
                "    \"database\"          : 'oracle',\n" +
                "    \"database_ip\"       : \"10.25.76.190\",\n" +
                "    \"database_port\"     : 1521,\n" +
                "    \"database_service\"  : \"erpdvlp\",\n" +
                "    \"database_username\" : \"simdvlp\",\n" +
                "    \"database_password\" : \"simdvlp\",\n" +
                "    \n" +
                "    \"user_tables\"       : [{\n" +
                "        \"user\"          : \"XSSA\",\n" +
                "        \"tables\"        : [\"TSASA01\", \"TSASA02\"]\n" +
                "    }]\n" +
                "}", new ConfigModel("GlobalConfig").getFileContent());
    }
}
