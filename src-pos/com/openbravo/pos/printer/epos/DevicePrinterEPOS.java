//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.printer.epos;

import com.lowagie.text.pdf.codec.Base64;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.printer.DevicePrinter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.misc.BASE64Encoder;

/**
 *
 * @author fiLLLip
 */
public class DevicePrinterEPOS implements DevicePrinter {

    private String m_printerIPAddress;
    private String m_printerDeviceID;
    private XMLStreamWriter m_out;
    private static String m_soapXmlNamespace = "http://schemas.xmlsoap.org/soap/envelope/";
    private static String m_eposXmlNamespace = "http://www.epson-pos.com/schemas/2011/03/epos-print";
    private HttpURLConnection m_conn;
    private String m_outLine;
    private int m_lineSize;
    private String m_sName;

    public DevicePrinterEPOS(String ipAddress, String deviceID) {
        m_printerIPAddress = ipAddress;
        if (("").equals(deviceID)) {
            m_printerDeviceID = "local_printer";
        } else {
            m_printerDeviceID = deviceID;
        }
        m_sName = AppLocal.getIntString("Printer.Network");
    }

    public String getPrinterName() {
        return m_sName;
    }

    public String getPrinterDescription() {
        return null;
    }

    public JComponent getPrinterComponent() {
        return null;
    }

    public void reset() {
    }

    public void beginReceipt() {
        URL url;
        try {
            url = new URL(getPrinterURL());
            m_conn = (HttpURLConnection) url.openConnection();
            m_conn.setDoOutput(true);
            m_conn.setRequestMethod("POST");
            m_conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            XMLOutputFactory f1 = XMLOutputFactory.newInstance();
            m_out = f1.createXMLStreamWriter(m_conn.getOutputStream(), "utf-8");
            m_out.writeStartDocument("utf-8", "1.0");
            m_out.writeStartElement("Envelope");
            m_out.writeDefaultNamespace(m_soapXmlNamespace);
            m_out.writeStartElement("Body");
            m_out.writeStartElement("epos-print");
            m_out.writeDefaultNamespace(m_eposXmlNamespace);
        } catch (Exception ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printImage(BufferedImage image) {
        Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.INFO, "Starting to print image!", "");
        if (m_out == null) {
            return;
        }

        int maxWidth = 320;
        int width = image.getWidth();
        int height = image.getHeight();
        if (width > maxWidth) {
            height = (maxWidth / width) * height;
            width = maxWidth;
            image = resizeImage(image, width, height);
        }
        image = ConvertToBlackAndWhite(image);
        String output = encodeToString(image, "png");
        Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.INFO, "Print image outputted", output);
        try {
            m_out.writeStartElement("image");
            m_out.writeAttribute("align", "center");
            m_out.writeAttribute("height", Integer.toString(height));
            m_out.writeAttribute("width", Integer.toString(width));
            m_out.writeCharacters(output);
            m_out.writeEndElement();
        } catch (XMLStreamException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    private BufferedImage ConvertToBlackAndWhite(BufferedImage image) {
        BufferedImage original = image;
        BufferedImage binarized = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        int red;
        int newPixel;
        int threshold = 230;
        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {

                // Get pixels
                red = new Color(original.getRGB(i, j)).getRed();

                int alpha = new Color(original.getRGB(i, j)).getAlpha();

                if (red > threshold) {
                    newPixel = 0;
                } else {
                    newPixel = 255;
                }
                newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);
                binarized.setRGB(i, j, newPixel);

            }
        }
        return binarized;
    }

    private static int colorToRGB(int alpha, int red, int green, int blue) {
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return imageString;
    }

    public void printBarCode(String type, String position, String code) {
        if (DevicePrinter.BARCODE_EAN13.equals(type)) {
            try {
                m_out.writeStartElement("barcode");
                m_out.writeAttribute("align", "center");
                m_out.writeCharacters(code);
                m_out.writeEndElement();
            } catch (XMLStreamException ex) {
                Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void beginLine(int iTextSize) {
        m_lineSize = iTextSize + 1;
    }

    public void printText(int iStyle, String sText) {
        if (m_out == null) {
            return;
        }
        try {
            m_out.writeStartElement("text");
            m_out.writeAttribute("align", "left");
            m_out.writeAttribute("height", Integer.toString(m_lineSize));

            if ((iStyle & DevicePrinter.STYLE_BOLD) != 0) {
                m_out.writeAttribute("em", "true");
            } else {
                m_out.writeAttribute("em", "false");
            }
            if ((iStyle & DevicePrinter.STYLE_UNDERLINE) != 0) {
                m_out.writeAttribute("ul", "true");
            } else {
                m_out.writeAttribute("ul", "false");
            }

            m_out.writeCharacters(sText);
            m_out.writeEndElement();
        } catch (XMLStreamException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void endLine() {
        if (m_out == null) {
            return;
        }
        try {
            m_out.writeStartElement("text");
            m_out.writeCharacters("\n");
            m_out.writeEndElement();
        } catch (XMLStreamException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void endReceipt() {
        try {
            m_out.writeEmptyElement("cut");
            m_out.writeEndElement();
            m_out.writeEndElement();
            m_out.writeEndElement();
            m_out.writeEndDocument();
            m_out.close();

            m_conn.connect();

            // Receive response document
            StreamSource source = new StreamSource(m_conn.getInputStream());
            DOMResult result = new DOMResult();
            TransformerFactory f2 = TransformerFactory.newInstance();
            Transformer t = f2.newTransformer();
            t.transform(source, result);

            // Parse response document (DOM)
            Document doc = (Document) result.getNode();
            Element el = (Element) doc.getElementsByTagNameNS(m_eposXmlNamespace, "response").item(0);

            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.INFO, "Print receipt", el.getTextContent());
        } catch (XMLStreamException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }

        m_out = null;
    }

    public void openDrawer() {
        beginReceipt();
        if (m_out == null) {
            return;
        }
        try {
            m_out.writeEmptyElement("pulse");
            m_out.writeAttribute("drawer", "drawer_1");
            m_out.writeAttribute("time", "pulse_100");
        } catch (XMLStreamException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            m_out.writeEndElement();
            m_out.writeEndElement();
            m_out.writeEndElement();
            m_out.writeEndDocument();
            m_out.close();

            m_conn.connect();

            // Receive response document
            StreamSource source = new StreamSource(m_conn.getInputStream());
            DOMResult result = new DOMResult();
            TransformerFactory f2 = TransformerFactory.newInstance();
            Transformer t = f2.newTransformer();
            t.transform(source, result);

            // Parse response document (DOM)
            Document doc = (Document) result.getNode();
            Element el = (Element) doc.getElementsByTagNameNS(m_eposXmlNamespace, "response").item(0);

        } catch (XMLStreamException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(DevicePrinterEPOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getPrinterURL() throws Exception {
        if (m_printerDeviceID != null && m_printerIPAddress != null) {
            return "http://" + m_printerIPAddress + "/cgi-bin/epos/service.cgi?devid=" + m_printerDeviceID + "&timeout=10000";
        }
        throw new Exception("Printer parameters not set correctly.");
    }
}
