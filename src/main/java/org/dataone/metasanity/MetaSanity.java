/*
 * Bare bones xml metadata validator.
 */
package org.dataone.metasanity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.xerces.util.XMLCatalogResolver;

import org.dataone.metasanity.AltXMLCatalogResolver;

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

        Logger logger = LoggerFactory.getLogger(MetaSanity.class);
        ArgumentParser argparser = ArgumentParsers.newArgumentParser("MetaSanity")
                .defaultHelp(true)
                .description("Validate some metadata");
        argparser.addArgument("input")
                .help("input metadata file")
                .required(true);
        argparser.addArgument("-c", "--catalog")
                .required(false)
                .setDefault("schemas.xml")
                .help("XMLCatalog document to use.");
        argparser.addArgument("-a", "--altresolver")
                .required(false)
                .type(Boolean.class)
                .action(Arguments.storeTrue())
                .setDefault(false)
                .help("Use verbose alternate catalog resolver.");
        Namespace ns = null;
        try {
            ns = argparser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argparser.handleError(e);
            logger.error(e.toString());
            System.exit(1);
        }

        String xml_document = ns.getString("input");
        String catalog = ns.getString("catalog");
        if (catalog == null) {
            catalog = "schemas.xml";
        }
        logger.info("Using catalog: " + catalog);

        String[] catalogs = {catalog};
        XMLCatalogResolver resolver = null;
        if (ns.getBoolean("altresolver")) {
            logger.warn("Using verbose alternative Catalog Resolver");
            resolver = new AltXMLCatalogResolver();
        } else {
            resolver = new XMLCatalogResolver();
        }
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
            ValidationErrorHandler error_handler = new ValidationErrorHandler();
            r.setErrorHandler(error_handler);
            logger.info("Parsing: " + xml_document);
            r.parse(xml_document);
            if (error_handler.issue_count == 0) {
                logger.info("Document is valid.");
            } else {
                logger.warn("\nDocument is not valid with " 
                        + error_handler.issue_count 
                        + " issues. Please review issues noted above.");
            }

        } catch (SAXException e) {
            logger.error(e.toString());
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    private static class ValidationErrorHandler extends DefaultHandler {

        public Integer issue_count = 0;
        private Logger logger = LoggerFactory.getLogger(XMLReader.class);
               
        public void warning(SAXParseException e) throws SAXException {
            logger.warn(eToString(e));
        }

        public void error(SAXParseException e) throws SAXException {
            logger.error(eToString(e));
        }

        public void fatalError(SAXParseException e) throws SAXException {
            logger.error("FATAL exception raised by parser");
            logger.error(eToString(e));
        }

        private String eToString(SAXParseException e) {
            String result = new String();
            issue_count++;
            result = e.getMessage();
            result += "\n  Public ID: " + e.getPublicId();
            result += "\n  System ID: " + e.getSystemId();
            result += "\n  Line number: " + e.getLineNumber();
            result += "\n  Column number: " + e.getColumnNumber();
            return result;
        }
    }
}
