# Practica-2-SINT

/**
 * Created by Likytho on 29/11/2015.
 *
 * Problemas con las eñes y con los acentos de los XMLs, tener en cuenta al probar en el laboratorio.
 */

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Sint153P2 extends HttpServlet {

    String fase1 = "";

    String fase2 = "";
    Map<Integer, String> mapFase2 = new TreeMap<Integer, String>();
    String fase3 = "";
    Map<Integer, String> mapFase3 = new TreeMap<Integer, String>();
    String fase4 = "";
    Map<Integer, String> mapFase4 = new TreeMap<Integer, String>();

    ArrayList<Document> listaDocuments = new ArrayList<Document>();
    ArrayList<String> listadoXMLs = new ArrayList<String>();


    DocumentBuilderFactory dbf = null;
    DocumentBuilder db = null;
    Document doc = null;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //Salida será el printer de nuestras webs para las consultas
        ServletOutputStream salida = res.getOutputStream();
        res.setContentType("text/html");


        //Todo este tocho está dedicado a las fases
        if ((req.getParameter("fase") == null) || (req.getParameter("fase").equals("0"))) {

            leerXML("file:///D:/Users/Likytho/Google%20Drive/Teleco%20%28UVigo%29/3%C2%BA%20Grado%20%282015-2016%29/1%C2%BA%20Cuatrimestre/Servicios%20de%20Internet/Pr%C3%A1ctica/Pr%C3%A1ctica%202/P2/sabina.xml");
            fase1(req, res, salida);
            fase1 = "";
            fase2 = "";
            fase3 = "";
            fase4 = "";

        } else {

            //Fase 1 -> Escoger tipo de consulta
            if (req.getParameter("fase").equals("1")) {

                if (req.getParameter("consulta").equals("1")) {
                    fase1 = "Lista de canciones de un álbum";
                    fase2Album(req, res, salida);
                }

                if (req.getParameter("consulta").equals("2")) {
                    fase1 = "Número de canciones de un estilo";
                    fase2Estilo(req, res, salida);
                }
            }

            //Fase 211 -> Consulta sobre la lista de canciones de un álbum + intérprete.
            if (req.getParameter("fase").equals("211")) {

                if (req.getParameter("interprete") == null) {
                    fase2 = "";
                    fase2Album(req, res, salida);
                } else {
                    fase2 = req.getParameter("interprete");
                    fase3Album(req, res, salida);
                }
            }

            //Fase 212 -> + álbum
            if (req.getParameter("fase").equals("212")) {

                if (req.getParameter("album") == null) {
                    fase3 = "";
                    fase3Album(req, res, salida);
                } else {
                    fase3 = req.getParameter("album");
                    try {
                        faseFinalAlbum(req, res, salida);
                    } catch (XPathException e) {
                    }

                }
            }

            //Fase 221 -> Número de canciones de un estilo + año
            if (req.getParameter("fase").equals("221")) {

                if (req.getParameter("anho") == null) {
                    System.out.println("Me ejecuto.");
                    fase2 = "";
                    fase2Estilo(req, res, salida);
                } else {

                    if (req.getParameter("anho").equals("1")) {
                        fase2 = "Año 1";
                        fase3Estilo(req, res, salida);
                    }

                    if (req.getParameter("anho").equals("2")) {
                        fase2 = "Año 2";
                        fase3Estilo(req, res, salida);
                    }

                    if (req.getParameter("anho").equals("3")) {
                        fase2 = "Año 3";
                        fase3Estilo(req, res, salida);
                    }

                    if (req.getParameter("anho").equals("4")) {
                        fase2 = "Año 4";
                        fase3Estilo(req, res, salida);
                    }

                    if (req.getParameter("anho").equals("5")) {
                        fase2 = "Año 5";
                        fase3Estilo(req, res, salida);
                    }

                    if (req.getParameter("anho").equals("6")) {
                        fase2 = "Todos";
                        fase3Estilo(req, res, salida);
                    }
                }
            }

            //Fase 222 -> + album
            if (req.getParameter("fase").equals("222")) {

                if (req.getParameter("album2") == null) {
                    fase3 = "";
                    fase3Estilo(req, res, salida);
                } else {

                    if (req.getParameter("album2").equals("1")) {
                        fase3 = "Álbum 1";
                        fase4Estilo(req, res, salida);
                    }

                    if (req.getParameter("album2").equals("2")) {
                        fase3 = "Álbum 2";
                        fase4Estilo(req, res, salida);
                    }

                    if (req.getParameter("album2").equals("3")) {
                        fase3 = "Álbum 3";
                        fase4Estilo(req, res, salida);
                    }

                    if (req.getParameter("album2").equals("4")) {
                        fase3 = "Álbum 4";
                        fase4Estilo(req, res, salida);
                    }

                    if (req.getParameter("album2").equals("5")) {
                        fase3 = "Todos";
                        fase4Estilo(req, res, salida);
                    }
                }
            }

            //Fase 223 -> + estilo
            if (req.getParameter("fase").equals("223")) {

                if (req.getParameter("estilo") == null) {
                    fase4 = "";
                    fase4Estilo(req, res, salida);
                } else {

                    if (req.getParameter("estilo").equals("1")) {
                        fase4 = "Estilo 1";
                        faseFinalEstilo(req, res, salida);
                    }

                    if (req.getParameter("estilo").equals("2")) {
                        fase4 = "Estilo 2";
                        faseFinalEstilo(req, res, salida);
                    }

                    if (req.getParameter("estilo").equals("3")) {
                        fase4 = "Estilo 3";
                        faseFinalEstilo(req, res, salida);
                    }

                    if (req.getParameter("estilo").equals("4")) {
                        fase4 = "Estilo 4";
                        faseFinalEstilo(req, res, salida);
                    }

                    if (req.getParameter("estilo").equals("5")) {
                        fase4 = "Todos";
                        faseFinalEstilo(req, res, salida);
                    }
                }
            }
        }
    }

    public void fase1(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Por favor, realice una selección:</h2>");
        salida.println("<form method=GET action='?fase=1'>");
        salida.println("<input type='radio' name='consulta' value='1' checked> Lista de canciones de un álbum.<br>");
        salida.println("<input type='radio' name='consulta' value='2'> Número de canciones de un estilo.<br><br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='1'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase2Album(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + "</h2>");
        salida.println("<h2>Por favor, seleccione un intérprete:</h2>");
        salida.println("<form method=GET action='?fase=211'>");

        int j = 1;

        for (int i = 0; i < listaDocuments.size(); i++) {

            Document docAux = getDoc(i);

            if (docAux.getElementsByTagName("NombreC") != null) {

                String NombreC = docAux.getElementsByTagName("NombreC").item(0).getTextContent();
                salida.println("<input type='radio' name='interprete' value='" + docAux.getElementsByTagName("Id").item(0).getTextContent() + "' checked> " + NombreC + ".<br>");
                j++;

            } else {

                String NombreG = docAux.getElementsByTagName("NombreG").item(0).getTextContent();
                salida.println("<input type='radio' name='interprete' value='" + docAux.getElementsByTagName("Id").item(0).getTextContent() + "' checked> " + NombreG + ".<br>");
                j++;

            }
        }

        salida.println("<input type='radio' name='interprete' value='Todos' checked> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='211'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=0'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase2Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {


        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + "</h2>");
        salida.println("<h2>Por favor, seleccione un año:</h2>");
        salida.println("<form method=GET action='?fase=221'>");


        NodeList nodoAnhos = doc.getElementsByTagName("Año");
        int i = nodoAnhos.getLength();
        int j;

        for (j = 0; j < i; j++) {

            if (!mapFase2.containsValue(doc.getElementsByTagName("Año").item(j).getTextContent())) {
                mapFase2.put(Integer.parseInt(doc.getElementsByTagName("Año").item(j).getTextContent()), doc.getElementsByTagName("Año").item(j).getTextContent());
            }
        }

        j = 0;
        Iterator iterator = mapFase2.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = (Integer) iterator.next();
            salida.println("<input type='radio' name='anho' id='" + mapFase2.get(key) + "' value='" + (j + 1) + "' checked> " + mapFase2.get(key) + ".<br>");
            j++;
        }

        salida.println("<input type='radio' name='anho' value='" + (j + 1) + "' checked> Todos.<br>");


        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='221'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=0'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase3Album(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        Iterator iterator = mapFase3.keySet().iterator();

        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + "</h2>");
        salida.println("<h2>Por favor, seleccione un álbum:</h2>");
        salida.println("<form method=GET action='?fase=212'>");

        if (fase2.equalsIgnoreCase("Todos")) {

            for (int j = 0; j < listaDocuments.size(); j++) {

                Document docAux = getDoc(j);
                NodeList nodoAlbums = docAux.getElementsByTagName("NombreA");
                for (int z = 0; z < nodoAlbums.getLength(); z++) {
                    salida.println("<input type='radio' name='album' value='" + nodoAlbums.item(z).getTextContent() + "' checked> " + nodoAlbums.item(z).getTextContent() + ".<br>");
                }
            }
        } else {

            for (int j = 0; j < listaDocuments.size(); j++) {

                Document docAux = getDoc(j);

                if (docAux.getElementsByTagName("Id").item(0).getTextContent().equals(fase2)) {
                    NodeList nodoAlbums = docAux.getElementsByTagName("NombreA");
                    for (int z = 0; z < nodoAlbums.getLength(); z++) {
                        salida.println("<input type='radio' name='album' value='" + nodoAlbums.item(z).getTextContent() + "' checked> " + nodoAlbums.item(z).getTextContent() + ".<br>");
                    }
                }
            }
        }

        salida.println("<input type='radio' name='album' value='Todos' checked> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='212'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=211'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase3Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + "</h2>");
        salida.println("<h2>Por favor, seleccione un álbum:</h2>");
        salida.println("<form method=GET action='?fase=222'>");


        NodeList nodoCanciones = doc.getElementsByTagName("Album");
        int i = nodoCanciones.getLength();
        int j;

        salida.println("Tiene un total de " + i + " albumes.<br>");

        for (j = 0; j < i; j++) {
            salida.println("<input type='radio' name='album2' value='" + (j + 1) + "' checked> " + doc.getElementsByTagName("NombreA").item(j).getTextContent() + ".<br>");
        }

        //salida.println ("<input type='radio' name='album2' value='1' checked> Álbum 1.<br>");
        //salida.println ("<input type='radio' name='album2' value='2'> Álbum 2.<br>");
        //salida.println ("<input type='radio' name='album2' value='3'> Álbum 3.<br>");
        //salida.println ("<input type='radio' name='album2' value='4'> Álbum 4.<br>");
        salida.println("<input type='radio' name='album2' value='" + (j + 1) + "'> Todos.<br>");

        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='222'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=221'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase4Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        Iterator iterador = mapFase4.keySet().iterator();

        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3 + "</h2>");
        salida.println("<h2>Por favor, seleccione un estilo:</h2>");
        salida.println("<form method=GET action='?fase=223'>");

        NodeList nodoCanciones = doc.getElementsByTagName("Cancion");
        int i = nodoCanciones.getLength();
        int j;

        for (j = 0; j < i; j++) {
            if (!mapFase4.containsValue(nodoCanciones.item(j).getAttributes().item(0).getTextContent())) {
                mapFase4.put(j, nodoCanciones.item(j).getAttributes().item(0).getTextContent());
                salida.println("<input type='radio' name='estilo' value='" + (j + 1) + "' checked> " + nodoCanciones.item(j).getAttributes().item(0).getTextContent() + ".<br>");
            }
        }

        salida.println("<input type='radio' name='estilo' value='" + (j + 1) + "'> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='223'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=222'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void faseFinalAlbum(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathException {

        XPath xpath = XPathFactory.newInstance().newXPath();

        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira) (FINAL ÁLBUM).</h1>");
        salida.println("<form method=GET action='?fase=41'>");
        salida.println("<h3>Su selección ha sido:</h3>");
        salida.println("<h4>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3);
        salida.println("<h3>El resultado de su consulta es el siguiente:</h3>");
        salida.println("<h4>");
        salida.println("<ul>");

        if (fase2.equalsIgnoreCase("Todos")) {

            Document docAux = null;

            for (int m = 0; m < listaDocuments.size(); m++) {
                docAux = listaDocuments.get(m);

                if (fase3.equalsIgnoreCase("Todos")) {

                    NodeList listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion", docAux, XPathConstants.NODESET);
                    imprimirCanciones(listaCanciones, salida);

                    } else {

                    for (int x = 0; x < docAux.getElementsByTagName("Album").getLength(); x++) {

                        if (docAux.getElementsByTagName("NombreA").item(x).getTextContent().equalsIgnoreCase(fase3)) {

                            NodeList listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='" + fase3 + "']/Cancion", docAux, XPathConstants.NODESET);
                            imprimirCanciones(listaCanciones, salida);

                        }
                    }
                }

            }


        } else {

            Document docAux = null;

            for (int i = 0; i < listaDocuments.size(); i++) {
                docAux = listaDocuments.get(i);
                if (docAux.getElementsByTagName("Id").item(0).getTextContent().equals(fase2)) break;
            }

            if (fase3.equalsIgnoreCase("Todos")) {

                NodeList listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion", docAux, XPathConstants.NODESET);
                imprimirCanciones(listaCanciones, salida);

            } else {

                for (int x = 0; x < docAux.getElementsByTagName("Album").getLength(); x++) {

                    if (docAux.getElementsByTagName("NombreA").item(x).getTextContent().equalsIgnoreCase(fase3)) {

                        NodeList listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='" + fase3 + "']/Cancion", docAux, XPathConstants.NODESET);
                        imprimirCanciones(listaCanciones, salida);

                    }
                }
            }
        }



        salida.println("</ul>");
        salida.println("</h4>");
        salida.println("<input type='hidden' name='fase' value='41'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=212'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void faseFinalEstilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<html>");
        salida.println("<body>");
        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira) (FINAL ESTILO).</h1>");
        salida.println("<form method=GET action='?fase=42'>");
        salida.println("<h3>Su selección ha sido:</h3>");
        salida.println("<h4>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3 + " || Fase 4: " + fase4 + "</h4>");
        salida.println("<h3>El resultado de su consulta es el siguiente:</h3>");
        salida.println("<h4>");
        salida.println("El número de canciones es: ");
        salida.println("</h4>");
        salida.println("<input type='hidden' name='fase' value='42'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=223'>");
        salida.println("</form>");
        salida.println("</body>");
        salida.println("</html>");
    }





    public Document getDoc(int d) {

        Document document = null;
        document = listaDocuments.get(d);
        return document;
    }

    public void leerXML(String XML) throws IOException {

        boolean error = false;

        //Empezamos obteniendo documentos
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        putXMLandDoc(XML, null);

        try {
            db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException exception) throws SAXException {
                    System.out.println("Warning en archivo XML: " + exception.toString());
                }

                public void error(SAXParseException exception) throws SAXException {
                    System.out.println("Error en archivo XML: " + exception.toString());
                }

                public void fatalError(SAXParseException exception) throws SAXException {
                    System.out.println("Error grave en archivo XML: " + exception.toString());
                }
            });

            for (int z = 0; z < listadoXMLs.size(); z++) {

                XML = listadoXMLs.get(z);
                doc = db.parse(XML);

                if (doc != null) {

                    putXMLandDoc(XML, doc);

                    NodeList nodosIML = doc.getElementsByTagName("IML");

                    for (int j = 0; j < nodosIML.getLength(); j++) {
                        putXMLandDoc("file:///D:/Users/Likytho/Google%20Drive/Teleco%20%28UVigo%29/3%C2%BA%20Grado%20%282015-2016%29/1%C2%BA%20Cuatrimestre/Servicios%20de%20Internet/Pr%C3%A1ctica/Pr%C3%A1ctica%202/P2/" + doc.getElementsByTagName("IML").item(j).getTextContent(), null);
                    }

                }
            }

        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }

    }

    public void putXMLandDoc(String XML, Document doc) {

        boolean yaExiste = false;

        if (doc != null) {

            if (!listadoXMLs.contains(doc.getDocumentURI())) {

                listadoXMLs.add(doc.getDocumentURI());

            }

            for (int x = 0; x < listaDocuments.size(); x++) {

                Document docAux = listaDocuments.get(x);
                if (doc.getDocumentURI().equals(docAux.getDocumentURI())) yaExiste = true;

            }

            if (!yaExiste) listaDocuments.add(doc);

        } else {

            if (!listadoXMLs.contains(XML)) listadoXMLs.add(XML);

        }

        if (XML != null) {
            if (!listadoXMLs.contains(XML)) {
                //System.out.println("Me ejecuto! "+ XML +  " " + doc);
                listadoXMLs.add(XML);
                if (doc != null) listaDocuments.add(doc);
            }
        }
    }

    public void imprimirCanciones (NodeList listaCanciones, ServletOutputStream salida) throws ServletException, IOException{

        if (listaCanciones != null) {

            for (int i = 0; i < listaCanciones.getLength(); i++) {

                NodeList listaNodosCanciones = listaCanciones.item(i).getChildNodes();
                String nombreC = "";
                String duracion = "";
                String descripcion = "";

                for (int j = 0; j < listaNodosCanciones.getLength(); j++) {

                    String cancion = listaNodosCanciones.item(j).getNodeName();

                    if (cancion.equals("NombreT")) {
                        nombreC = listaNodosCanciones.item(j).getTextContent().trim().replaceAll("\n", "");
                    }

                    if (cancion.equals("Duracion")) {
                        duracion = listaNodosCanciones.item(j).getTextContent().trim().replaceAll("\n", "");
                    }

                    if (cancion.equals("#text")) {
                        String descripcionAux = listaNodosCanciones.item(j).getTextContent().trim().replaceAll("\n", "");
                        if (!descripcionAux.equals("")) {
                            descripcion = descripcionAux;
                        }
                    }
                }

                if (descripcion.equals("")) descripcion = "--";

                salida.println("<li> " + nombreC + " (" + duracion + ", " + descripcion + ")<BR>");
            }
        }

    }

}
