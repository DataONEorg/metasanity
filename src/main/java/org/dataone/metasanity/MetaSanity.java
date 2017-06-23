/*
 * Bare bones xml metadata validator.
 */
package org.dataone.metasanity;

import java.io.IOException;
import org.apache.xerces.util.XMLCatalogResolver;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 *
 * @author vieglais
 */
public class MetaSanity {

    public final static String SAXPARSER = "org.apache.xerces.parsers.SAXParser";

    // features are described at: https://xerces.apache.org/xerces-j/features.html
    public final static String DECLARATIONHANDLERPROPERTY = "http://xml.org/sax/properties/declaration-handler";
    public final static String LEXICALPROPERTY = "http://xml.org/sax/properties/lexical-handler";
    public final static String VALIDATIONFEATURE = "http://xml.org/sax/features/validation";
    public final static String SCHEMAVALIDATIONFEATURE = "http://apache.org/xml/features/validation/schema";
    public final static String NAMESPACEFEATURE = "http://xml.org/sax/features/namespaces";
    public final static String NAMESPACEPREFIXESFEATURE = "http://xml.org/sax/features/namespace-prefixes";
    public final static String EXTERNALSCHEMALOCATIONPROPERTY = "http://apache.org/xml/properties/schema/external-schemaLocation";
    public final static String FULLSCHEMAVALIDATIONFEATURE = "http://apache.org/xml/features/validation/schema-full-checking";
    public final static String SCHEMALANG = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public final static String SCHEMATYPE = "http://www.w3.org/2001/XMLSchema";

    public final static String CATALOG_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArgumentParser argparser = ArgumentParsers.newArgumentParser("MetaSanity")
                .defaultHelp(true)
                .description("Validate some metadata");
        argparser.addArgument("input")
                .help("input metadata file")
                .required(true);
        argparser.addArgument("-c", "--catalog").required(false)
                .help("XMLCatalog document to use.");
        Namespace ns = null;
        try {
            ns = argparser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argparser.handleError(e);
            System.exit(1);
        }

        String xml_document = ns.getString("input");
        String catalog = ns.getString("catalog");
        if (catalog == null) {
            catalog = "schemas.xml";
        }

        String[] catalogs = {catalog};
        XMLCatalogResolver resolver = new XMLCatalogResolver();
        resolver.setPreferPublic(true);
        resolver.setCatalogList(catalogs);

        try {
            XMLReader r = XMLReaderFactory.createXMLReader(SAXPARSER);
            r.setProperty(CATALOG_RESOLVER, resolver);

            r.setFeature(VALIDATIONFEATURE, true);
            r.setFeature(NAMESPACEFEATURE, true);
            r.setProperty(SCHEMALANG, SCHEMATYPE);
            r.setFeature(SCHEMAVALIDATIONFEATURE, true);
            r.setFeature(FULLSCHEMAVALIDATIONFEATURE, true);
            MyErrorHandler error_handler = new MyErrorHandler();
            r.setErrorHandler(error_handler);
            System.out.println("Parsing: " + xml_document);
            r.parse(xml_document);
            if (error_handler.issue_count == 0) {
                System.out.println("Document is valid.");
            } else {
                System.out.println("\nDocument is not valid. Please review issues noted above.");
            }

        } catch (SAXException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private static class MyErrorHandler extends DefaultHandler {

        public Integer issue_count = 0;

        public void warning(SAXParseException e) throws SAXException {
            System.out.println("Warning: ");
            printInfo(e);
        }

        public void error(SAXParseException e) throws SAXException {
            System.out.println("Error: ");
            printInfo(e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            System.out.println("Fatal error: ");
            printInfo(e);
        }

        private void printInfo(SAXParseException e) {
            issue_count++;
            System.out.println("   Public ID: " + e.getPublicId());
            System.out.println("   System ID: " + e.getSystemId());
            System.out.println("   Line number: " + e.getLineNumber());
            System.out.println("   Column number: " + e.getColumnNumber());
            System.out.println("   Message: " + e.getMessage());
        }
    }
}
