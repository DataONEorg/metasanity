/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dataone.metasanity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;

/**
 *
 * @author vieglais
 */
public class AltXMLCatalogResolver extends XMLCatalogResolver {

    private Logger logger = LoggerFactory.getLogger(AltXMLCatalogResolver.class);

    public org.xml.sax.InputSource resolveEntity(java.lang.String publicId,
            java.lang.String systemId)
            throws org.xml.sax.SAXException,
            java.io.IOException {
        System.out.println("resolveEntity 1: " + publicId + "\n    " + systemId);
        return super.resolveEntity(publicId, systemId);
    }

    public org.xml.sax.InputSource resolveEntity(java.lang.String name,
            java.lang.String publicId,
            java.lang.String baseURI,
            java.lang.String systemId)
            throws org.xml.sax.SAXException,
            java.io.IOException {
        System.out.println("resolveEntity 2: " + publicId + "\n    " + baseURI + "\n    " + systemId);
        return super.resolveEntity(name, publicId, baseURI, systemId);
    }

    public org.xml.sax.InputSource getExternalSubset(java.lang.String name,
            java.lang.String baseURI)
            throws org.xml.sax.SAXException,
            java.io.IOException {
        System.out.println("getExternalSubset: " + name + "\n    " + baseURI);
        return super.getExternalSubset(name, baseURI);
    }

    public org.w3c.dom.ls.LSInput resolveResource(java.lang.String type,
            java.lang.String namespaceURI,
            java.lang.String publicId,
            java.lang.String systemId,
            java.lang.String baseURI) {
        System.out.println("resolveResource: " + type + "\n    " + namespaceURI + "\n    " + publicId + "\n    " + systemId + "\n    "+ baseURI);
        return super.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
    }

    public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
            throws IOException {
        System.out.println("resolveEntity 3: " + resourceIdentifier.getExpandedSystemId());
        XMLInputSource source = super.resolveEntity(resourceIdentifier);
        if (source == null) {
            System.out.println("Resolved to null.");
        } else {
            System.out.println("Resovled to: " + source.getBaseSystemId());
        }
        return source;
    }

    public String resolveIdentifier(XMLResourceIdentifier resourceIdentifier)
            throws IOException,
            XNIException {
        System.out.println("resolveIdentifier: " + resourceIdentifier.toString());
        String result = super.resolveIdentifier(resourceIdentifier);
        //System.out.println(resourceIdentifier.toString() + " --> " + result);
        return result;
    }

    private File getFile(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        System.out.println("getFile URL = " + url.toString());
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            logger.error(e.toString());
        }
        return null;
    }
}
