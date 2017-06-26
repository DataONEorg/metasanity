# Metasanity

No-frills schema aware metadata validator.

Attempts to validate content based on the local copies of metadata schemas 
used by Coordinating Nodes to emulate the validation process used during
the internal `create` operation.

## Build

1. Clone, fork, or download a copy of this repo
2. `cd` to the metasanity folder
3. Run `mvn package`

Result if all goes as expected should be `target/metasanity-X.Y-SNAPSHOT.jar`

## Use

First, populate the local `schema` folder with a copy of the schemas from a 
Coordinating Node (requires shell access to a CN):

```
rsync -avz -e "ssh" cn.dataone.org:/var/lib/tomcat7/webapps/metacat/schema .
```

Run metasanity from the commandline, for example:

```
java -jar target/metasanity-1.0-SNAPSHOT.jar samples/iso_01.xml
```

The metasanity expects an xml catalog file "schemas.xml" to be in the
working directory. Use `-c` to specify a different catalog.

The output from the tool is something like:

```
$ java -jar target/metasanity-1.0-SNAPSHOT.jar samples/iso_01.xml
Parsing: samples/iso_01.xml
Document is valid.
```
or:

```
$ java -jar target/metasanity-1.0-SNAPSHOT.jar samples/iso_02_cn-invalid.xml
Parsing: samples/iso_02_cn-invalid.xml
Error:
   Public ID: null
   System ID: file:///Users/vieglais/Documents/Projects/DataONE_PhaseII/Projects/NetBeans/metasanity/samples/iso_02_cn-invalid.xml
   Line number: 632
   Column number: 21
   Message: cvc-complex-type.2.4.a: Invalid content was found starting with element 'gmd:taxonomy'. One of '{"http://www.isotc211.org/2005/gmd":aggregationInfo, "http://www.isotc211.org/2005/gmd":spatialRepresentationType, "http://www.isotc211.org/2005/gmd":spatialResolution, "http://www.isotc211.org/2005/gmd":language}' is expected.

Document is not valid. Please review issues noted above.
```

## Reference

* [XMLCatalog Spec](https://www.oasis-open.org/committees/download.php/14809/xml-catalogs.html)
* [XMLCatalog Examples](http://www.sagehill.net/docbookxsl/WriteCatalog.html)
* Xerces [XMLCatalog Resolver](https://xerces.apache.org/xerces2-j/javadocs/xerces2/org/apache/xerces/util/XMLCatalogResolver.html)
* Xerces [XMLCatalog notes](http://xerces.apache.org/xerces2-j/faq-xcatalogs.html)
* Metacat [DocumentImpl.java](https://code.ecoinformatics.org/code/metacat/trunk/src/edu/ucsb/nceas/metacat/DocumentImpl.java)

