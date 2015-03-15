# Prerequisites #
  * Google code account and admin of this project
  * Admin of project on maven central
  * all changes committed
  * [GPG key generated and published](https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven) for signing the artifacts ([troubleshooting](http://elgg.wlug.org.nz/pg/pages/view/8060/gnupg))

# Making a release #

The following commands generate a new release:

```
mvn release:clean
mvn release:prepare  -Dusername=<googlecode-user> -Dpassword=<googlecode-password>
mvn release:perform  -Dusername=<googlecode-user> -Dpassword=<googlecode-password>
```

Or in a single command:

```
mvn release:clean release:prepare release:perform -Dusername=<googlecode-user> -Dpassword=<googlecode-password>
```