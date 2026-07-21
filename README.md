## geojson

```bash
mvn -q -DskipTests package
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:$(cat cp.txt)" geojson
```

## jsonpatch

```bash
mvn -q -DskipTests package
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:$(cat cp.txt)" jsonpatch
```

## fd

```bash
mvn -q -DskipTests package
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:$(cat cp.txt)" fd
```

## springboot-jdk17-fd-geojson

生成 Spring Boot 3.2 fatjar 环境，验证 JDK9+ 的 `jar:http` 下载缓存 + `jar:file:/proc/self/fd` 重开链。

```bash
./scripts/springboot_jdk17_fd_geojson.sh
```

默认在 Linux 使用 `/proc/self/fd`，macOS 使用 `/dev/fd`。可通过 `FD_ROOT`、`MIN_FD`、`MAX_FD` 调整枚举范围。

默认 `PAYLOAD_MODE=fd_array` 使用 FD 枚举数组形态，可在 JDK17+ 命中 FD 重开链。`PAYLOAD_MODE=geojson_properties` 和 `PAYLOAD_MODE=geojson_geometry` 保留为 GeoJSON 包装负对照：它们可以触发第一阶段远程 JAR 请求，但第一阶段 `Exception` 软失败后会中断当前对象解析，无法继续枚举 FD。
