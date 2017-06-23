# README for Metasanity

No-frills schema aware metadata validator.

First, populate the schema repository with a copy of the schemas from a 
Coordinating Node:

```
rsync -avz -e "ssh" cn.dataone.org:/var/lib/tomcat7/webapps/metacat/schema .
```

Example use:

```
java -jar target/metasanity-1.0-SNAPSHOT.jar samples/iso_01.xml

```

The metasanity expects an xml catalog file "schemas.xml" to be in the
working directory. Use `-c` to specify a different catalog.

Build using:

```
mvn package
```
