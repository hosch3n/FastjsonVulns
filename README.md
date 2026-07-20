## geojson

```bash
mvn -q -DskipTests package
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:$(cat cp.txt)" geojson
```
