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

## XML Catalog

Note that `metasanity` uses an XMLCatalog and so differs from the implementation on the DataONE CNs. 

Three examples of XML Catalog files are provided:

* `schemas.xml` Generic catalog for Dublin Core and ISOTC211
* `isotc211-catalog.xml` Catalog specifically for the formatID `http://www.isotc211.org/2005/gmd`
* `isotc211-noaa-catalog.xml` Catalog specifically for the formatID `http://www.isotc211.org/2005/gmd-noaa`

Example of ISOTC211 from NOAA valid for the gmd-noaa schema variant:

```
java -jar target/metasanity-1.0-SNAPSHOT.jar -c isotc211-noaa-catalog.xml samples/iso_01.xml
Aug 23, 2017 3:03:54 PM org.dataone.metasanity.MetaSanity main
INFO: Using catalog: isotc211-noaa-catalog.xml
Aug 23, 2017 3:03:54 PM org.dataone.metasanity.MetaSanity main
INFO: Parsing: samples/iso_01.xml
Aug 23, 2017 3:03:55 PM org.dataone.metasanity.MetaSanity main
INFO: Document is valid.
```

And invalid for the plain ISOTC211 variant:

```
java -jar target/metasanity-1.0-SNAPSHOT.jar -c isotc211-catalog.xml samples/iso_01.xml
Aug 23, 2017 3:10:15 PM org.dataone.metasanity.MetaSanity main
INFO: Using catalog: isotc211-catalog.xml
Aug 23, 2017 3:10:15 PM org.dataone.metasanity.MetaSanity main
INFO: Parsing: samples/iso_01.xml
Aug 23, 2017 3:10:22 PM org.dataone.metasanity.MetaSanity$ValidationErrorHandler error
SEVERE: cvc-complex-type.2.4.a: Invalid content was found starting with element 'gmx:Anchor'. One of '{"http://www.isotc211.org/2005/gco":CharacterString}' is expected.
  Public ID: null
  System ID: file:///Users/vieglais/Documents/Projects/DataONE_PhaseII/Projects/NetBeans/metasanity/samples/iso_01.xml
  Line number: 136
  Column number: 167

...

Aug 23, 2017 3:10:22 PM org.dataone.metasanity.MetaSanity$ValidationErrorHandler error
SEVERE: cvc-complex-type.2.4.a: Invalid content was found starting with element 'gmx:Anchor'. One of '{"http://www.isotc211.org/2005/gco":CharacterString}' is expected.
  Public ID: null
  System ID: file:///Users/vieglais/Documents/Projects/DataONE_PhaseII/Projects/NetBeans/metasanity/samples/iso_01.xml
  Line number: 884
  Column number: 125
Aug 23, 2017 3:10:22 PM org.dataone.metasanity.MetaSanity main
WARNING:
Document is not valid with 70 issues. Please review issues noted above.
```


## Reference

* [XMLCatalog Spec](https://www.oasis-open.org/committees/download.php/14809/xml-catalogs.html)
* [XMLCatalog Examples](http://www.sagehill.net/docbookxsl/WriteCatalog.html)
* Xerces [XMLCatalog Resolver](https://xerces.apache.org/xerces2-j/javadocs/xerces2/org/apache/xerces/util/XMLCatalogResolver.html)
* Xerces [XMLCatalog notes](http://xerces.apache.org/xerces2-j/faq-xcatalogs.html)
* Metacat [DocumentImpl.java](https://code.ecoinformatics.org/code/metacat/trunk/src/edu/ucsb/nceas/metacat/DocumentImpl.java)

