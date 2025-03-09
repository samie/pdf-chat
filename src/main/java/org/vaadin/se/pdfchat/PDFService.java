package org.vaadin.se.pdfchat;

import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;

import java.io.InputStream;

@Service
public class PDFService {


    public static final String CONTENT_TYPE = "application/pdf";

    /** Parse PDF into text using Apache Tika.
     * We ignore everything else than 
     */
    public String parseToText(InputStream inputStream) {
        Tika tika = new Tika();
        try (TikaInputStream tis = TikaInputStream.get(inputStream)) {
            String mimeType = tika.detect(tis);
            if (mimeType.equals(CONTENT_TYPE)) {
                AutoDetectParser parser = new AutoDetectParser();
                ContentHandler handler = new BodyContentHandler();
                parser.parse(tis, handler, new Metadata());
                return handler.toString();
            } else {
                throw new RuntimeException("Unsupported file type: " + mimeType);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error parsing PDF", ex);
        }
    }

}
