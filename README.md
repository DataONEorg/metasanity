# Metasanity

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

To build:

1. Clone, fork, or download a copy of this repo
2. `cd` to the metasanity folder
3. Run `mvn package`

Result if all goes as expected should be `target/metasanity-X.Y-SNAPSHOT.jar`

## Reference

* [XMLCatalog Spec](https://www.oasis-open.org/committees/download.php/14809/xml-catalogs.html)
* [XMLCatalog Examples](http://www.sagehill.net/docbookxsl/WriteCatalog.html)
* Xerces [XMLCatalog Resolver](https://xerces.apache.org/xerces2-j/javadocs/xerces2/org/apache/xerces/util/XMLCatalogResolver.html)
* Xerces [XMLCatalog notes](http://xerces.apache.org/xerces2-j/faq-xcatalogs.html)
* Metacat [DocumentImpl.java](https://code.ecoinformatics.org/code/metacat/trunk/src/edu/ucsb/nceas/metacat/DocumentImpl.java)

