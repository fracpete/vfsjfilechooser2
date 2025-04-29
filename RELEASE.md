# Release

Switch to Java 11 (Java 9 has outdated certificates)

Use the following command to make a new release:
```
mvn release:prepare release:perform
```

Push all changes

Go to the following URL and publish the artifact:

```
https://central.sonatype.com/publishing/deployments
```
