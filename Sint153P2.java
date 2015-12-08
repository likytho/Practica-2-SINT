/**
 * Created by Likytho on 29/11/2015.
 *
 * Problemas con las eñes y con los acentos de los XMLs, tener en cuenta al probar en el laboratorio. Parece que esto funciona en el Tomcat de Ubuntu.
 *
 * CSS y fondo -> relativo + cambiar nombre del archivo CSS.
 *
 * Comprobador de ruta de XML (Relativa o absoluta)
 * 
 * Gestión de errores:
 * - Por supuesto, cualquier cosa que implique el fichero no esté bien formado.
 * - La falta de algún elemento obligatorio, o un orden incorrecto de los mismos.
 * - La falta de algún atributo obligatorio.
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
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.*;

public class Sint153P2 extends HttpServlet {

    String fase1 = "";

    String fase2 = "";
    Map<Integer, String> mapFase2 = new TreeMap<Integer, String>();
    Map<String, String> mapFase2Consulta1 = new TreeMap<String, String>();
    Map<String, String> mapFase2Consulta1ID = new TreeMap<String, String>();

    String fase3 = "";
    String autorFase3 = "";
    Map<Integer, String> mapFase3 = new TreeMap<Integer, String>();
    Map<Integer, ArrayList<String>> mapFase3Consulta1Aux = new TreeMap<Integer, ArrayList<String>>();

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

        salida.println("<!DOCTYPE html>");
        salida.println("<head>");
        salida.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
        salida.println("<title>Servicio IML</title>");
        salida.println("<link href=\"http://clave.det.uvigo.es:8080/~sint153/p2/p2.css\" rel=\"stylesheet\" type=\"text/css\" media=\"all\">");
        salida.println("</head>");
        salida.println("<body background=\"http://clave.det.uvigo.es:8080/~sint153/p1/images/notas.jpg\">");
        salida.println("<div id=\"wrapper\">");
        salida.println("<div id=\"container\">");


        //Todo este tocho está dedicado a las fases
        if ((req.getParameter("fase") == null) || (req.getParameter("fase").equals("0"))) {
            leerXML("file:///D:/Users/Likytho/Google%20Drive/Teleco%20%28UVigo%29/3%C2%BA%20Grado%20%282015-2016%29/1%C2%BA%20Cuatrimestre/Servicios%20de%20Internet/Pr%C3%A1ctica/Pr%C3%A1ctica%202/P2/sabina.xml");
            fase1(req, res, salida);
            fase1 = "";            fase2 = "";            fase3 = "";            fase4 = "";        autorFase3 = "";
            mapFase2.clear();            mapFase2Consulta1.clear();            mapFase2Consulta1ID.clear();
            mapFase3.clear();            mapFase3Consulta1Aux.clear();
            mapFase4.clear();
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
                    try {
                        fase3Album(req, res, salida);
                    } catch (XPathException e) {}
                }
            }

            //Fase 212 -> + álbum
            if (req.getParameter("fase").equals("212")) {
                if (req.getParameter("album") == null) {
                    fase3 = "";
                    try {
                        fase3Album(req, res, salida);
                    } catch (XPathException e) {}
                } else {
                    fase3 = req.getParameter("album");
                    try {
                        faseFinalAlbum(req, res, salida);
                    } catch (XPathException e) {}
                }
            }

            //Fase 221 -> Número de canciones de un estilo + anho
            if (req.getParameter("fase").equals("221")) {
                if (req.getParameter("anho") == null) {
                    fase2 = "";
                    fase2Estilo(req, res, salida);
                } else {
                    fase2 = req.getParameter("anho");
                    try {
                        fase3Estilo(req, res, salida);
                    } catch (XPathException e){}
                }
            }

            //Fase 222 -> + album
            if (req.getParameter("fase").equals("222")) {
                if (req.getParameter("album2") == null) {
                    fase3 = "";
                    autorFase3 = "";
                    try {
                        fase3Estilo(req, res, salida);
                    } catch (XPathException e){}
                } else {
                    String[] fase3Aux = req.getParameter("album2").split("#");
                    fase3 = fase3Aux[0];
                    autorFase3 = fase3Aux[1];
                    try{
                        fase4Estilo(req, res, salida);
                    } catch (XPathException e) {}
                }
            }

            //Fase 223 -> + estilo
            if (req.getParameter("fase").equals("223")) {

                if (req.getParameter("estilo") == null) {
                    fase4 = "";
                    try{
                        fase4Estilo(req, res, salida);
                    } catch (XPathException e) {}
                } else {
                    fase4 = req.getParameter("estilo");
                    try{
                        faseFinalEstilo(req, res, salida);
                    } catch (XPathException e) {}
                }
            }
        }
    }

    public void fase1(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Por favor, realice una selección:</h2>");
        salida.println("<form method=GET action='?fase=1'>");
        salida.println("<input type='radio' name='consulta' value='1' checked> Lista de canciones de un álbum.<br>");
        salida.println("<input type='radio' name='consulta' value='2'> Número de canciones de un estilo.<br><br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='1'>");
        salida.println("</form>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase2Album(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        mapFase2Consulta1.clear();
        mapFase2Consulta1ID.clear();

        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + "</h2>");
        salida.println("<h2>Por favor, seleccione un intérprete:</h2>");
        salida.println("<form method=GET action='?fase=211'>");


        for (int i = 0; i < listaDocuments.size(); i++) {
            Document docAux = getDoc(i);

            if (docAux.getElementsByTagName("NombreC") != null) {
                String NombreC = docAux.getElementsByTagName("NombreC").item(0).getTextContent();
                mapFase2Consulta1.put(NombreC, NombreC);
                mapFase2Consulta1ID.put(NombreC, docAux.getElementsByTagName("Id").item(0).getTextContent());
            } else {
                String NombreG = docAux.getElementsByTagName("NombreG").item(0).getTextContent();
                mapFase2Consulta1.put(NombreG, NombreG);
                mapFase2Consulta1ID.put(NombreG, docAux.getElementsByTagName("Id").item(0).getTextContent());
            }
        }

        Iterator iterador = mapFase2Consulta1.keySet().iterator();
        while(iterador.hasNext()){

            String key = (String) iterador.next();
            String Nombre = mapFase2Consulta1.get(key);
            String ID = mapFase2Consulta1ID.get(Nombre);

            salida.println("<input type='radio' name='interprete' value='" + ID + "' checked> " + Nombre + ".<br>");
        }

        salida.println("<input type='radio' name='interprete' value='Todos' checked> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='211'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=0'>");
        salida.println("</form>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase2Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        mapFase2.clear();

        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + "</h2>");
        salida.println("<h2>Por favor, seleccione un año:</h2>");
        salida.println("<form method=GET action='?fase=221'>");

        NodeList nodoAnhos = null;

        for (int j=0; j<listaDocuments.size(); j++){

            Document docAux = listaDocuments.get(j);
            nodoAnhos = docAux.getElementsByTagName("Año");

            for (int k=0; k<nodoAnhos.getLength(); k++){
                if (!mapFase2.containsValue(docAux.getElementsByTagName("Año").item(k).getTextContent())) {
                    mapFase2.put(Integer.parseInt(docAux.getElementsByTagName("Año").item(k).getTextContent()), docAux.getElementsByTagName("Año").item(k).getTextContent());
                }
            }
        }

        Iterator iterator = mapFase2.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = (Integer) iterator.next();
            salida.println("<input type='radio' name='anho'  value='" + mapFase2.get(key) + "' checked> " + mapFase2.get(key) + ".<br>");
        }

        salida.println("<input type='radio' name='anho' value='Todos' checked> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='221'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=0'>");
        salida.println("</form>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase3Album(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException  {

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList listaAlbumes = null;
        mapFase3Consulta1Aux.clear();

        String anho = "";
        String album = "";

        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + "</h2>");
        salida.println("<h2>Por favor, seleccione un álbum:</h2>");
        salida.println("<form method=GET action='?fase=212'>");

        if (fase2.equalsIgnoreCase("Todos")) {
            for (int j = 0; j < listaDocuments.size(); j++) {

                Document docAux = getDoc(j);

                NodeList nodoAlbums = docAux.getElementsByTagName("NombreA");
                listaAlbumes = (NodeList) xpath.evaluate("/Interprete/Album", docAux, XPathConstants.NODESET);
                for (int z = 0; z < nodoAlbums.getLength(); z++) {

                    NodeList listaAnhos = listaAlbumes.item(z).getChildNodes();

                    for (int w=0; w<listaAnhos.getLength(); w++){

                        String aux = listaAnhos.item(w).getNodeName();

                        if (aux.equalsIgnoreCase("NombreA")){
                            album = listaAnhos.item(w).getTextContent();
                        }

                        if (aux.equalsIgnoreCase("Año")){
                            anho = listaAnhos.item(w).getTextContent();
                        }

                        if((!anho.equalsIgnoreCase("")) && (!album.equalsIgnoreCase(""))){

                            if(!mapFase3Consulta1Aux.containsKey(Integer.parseInt(anho))){

                                ArrayList <String> arrayTemp = new ArrayList<String>();
                                arrayTemp.add(album);

                                mapFase3Consulta1Aux.put((Integer.parseInt(anho)), arrayTemp);
                            } else {

                                ArrayList <String> arrayTemp = mapFase3Consulta1Aux.get(Integer.parseInt(anho));
                                arrayTemp.add(album);

                                mapFase3Consulta1Aux.put(Integer.parseInt(anho), arrayTemp);
                            }
                            album = "";
                            anho = "";
                        }
                    }
                }
            }
        } else {
            for (int j = 0; j < listaDocuments.size(); j++) {

                Document docAux = getDoc(j);

                if (docAux.getElementsByTagName("Id").item(0).getTextContent().equals(fase2)) {
                    NodeList nodoAlbums = docAux.getElementsByTagName("NombreA");
                    listaAlbumes = (NodeList) xpath.evaluate("/Interprete/Album", docAux, XPathConstants.NODESET);
                    for (int z = 0; z < nodoAlbums.getLength(); z++) {

                        NodeList listaAnhos = listaAlbumes.item(z).getChildNodes();

                        for (int w=0; w<listaAnhos.getLength(); w++){

                            String aux = listaAnhos.item(w).getNodeName();

                            if (aux.equalsIgnoreCase("NombreA")){
                                album = listaAnhos.item(w).getTextContent();
                            }

                            if (aux.equalsIgnoreCase("Año")){
                                anho = listaAnhos.item(w).getTextContent();
                            }

                            if((!anho.equalsIgnoreCase("")) && (!album.equalsIgnoreCase(""))){

                                if(!mapFase3Consulta1Aux.containsKey(Integer.parseInt(anho))){

                                    ArrayList <String> arrayTemp = new ArrayList<String>();
                                    arrayTemp.add(album);

                                    mapFase3Consulta1Aux.put((Integer.parseInt(anho)), arrayTemp);
                                } else {

                                    ArrayList <String> arrayTemp = mapFase3Consulta1Aux.get(Integer.parseInt(anho));
                                    arrayTemp.add(album);

                                    mapFase3Consulta1Aux.put(Integer.parseInt(anho), arrayTemp);
                                }

                                album = "";
                                anho = "";
                            }
                        }
                    }
                }
                break;
            }
        }

        Iterator iterador = mapFase3Consulta1Aux.keySet().iterator();
        while(iterador.hasNext()){

            Integer key = (Integer) iterador.next();
            ArrayList <String> arrayAux = mapFase3Consulta1Aux.get(key);


            for (int h=0; h<arrayAux.size(); h++){
                salida.println("<input type='radio' name='album' value='" + arrayAux.get(h) + "' checked> " + arrayAux.get(h) + " ( " + key + " ).<br>");
            }
        }

        salida.println("<input type='radio' name='album' value='Todos' checked> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='212'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=211'>");
        salida.println("</form>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase3Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        String autor = "";

        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + "</h2>");
        salida.println("<h2>Por favor, seleccione un álbum:</h2>");
        salida.println("<form method=GET action='?fase=222'>");

        if(fase2.equalsIgnoreCase("Todos")){

            for (int i=0; i<listaDocuments.size(); i++){

                Document docAux = listaDocuments.get(i);
                NodeList nodoAlbums = docAux.getElementsByTagName("NombreA");
                autor = "";

                if (docAux.getElementsByTagName("NombreC").item(0) != null){
                    autor = docAux.getElementsByTagName("NombreC").item(0).getTextContent();
                } else {
                    autor = docAux.getElementsByTagName("NombreG").item(0).getTextContent();
                }

                salida.println("<b>Autor: "+ autor + "</b><br>");

                for (int j=0; j<nodoAlbums.getLength(); j++){
                    mapFase3.put(mapFase3.size()+1, nodoAlbums.item(j).getTextContent());
                    salida.println("<input type='radio' name='album2' value='" + nodoAlbums.item(j).getTextContent() + "#" + docAux.getElementsByTagName("Id").item(0).getTextContent() +"' checked> " + nodoAlbums.item(j).getTextContent() + ".<br>");
                }

                salida.println("<br>");
            }
        } else {

            for (int i=0; i<listaDocuments.size(); i++) {

                Document docAux = listaDocuments.get(i);

                NodeList listaAlbums = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + fase2 + "']", docAux, XPathConstants.NODESET);

                autor = "";

                if (docAux.getElementsByTagName("NombreC").item(0) != null){
                    autor = docAux.getElementsByTagName("NombreC").item(0).getTextContent();
                } else {
                    autor = docAux.getElementsByTagName("NombreG").item(0).getTextContent();
                }

                salida.println("<b>Autor: "+ autor + "</b><br>");

                for (int j = 0; j < listaAlbums.getLength(); j++) {

                    NodeList nodoAlbumHijos = listaAlbums.item(j).getChildNodes();
                    String nombreAlbum = "";

                    for(int k=0; k<nodoAlbumHijos.getLength(); k++){
                        String Album = nodoAlbumHijos.item(k).getNodeName();

                        if (Album.equals("NombreA")){
                            nombreAlbum = nodoAlbumHijos.item(k).getTextContent().trim().replaceAll("\n","");
                            mapFase3.put(mapFase3.size()+1, nombreAlbum);
                            salida.println("<input type='radio' name='album2' value='" + nombreAlbum + "#" + docAux.getElementsByTagName("Id").item(0).getTextContent() + "' checked> " + nombreAlbum + ".<br>");
                        }
                    }
                }
                salida.println("<br>");
            }
        }

        salida.println("<input type='radio' name='album2' value='Todos#Todos' checked> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='222'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=221'>");
        salida.println("</form>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase4Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException {

        mapFase4.clear();
        XPath xpath = XPathFactory.newInstance().newXPath();

        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3 + "</h2>");
        salida.println("<h2>Por favor, seleccione un estilo:</h2>");
        salida.println("<form method=GET action='?fase=223'>");

        for (int i = 0; i < listaDocuments.size(); i++){
            NodeList listaCancionesEstilo = null;
            Document docAux = listaDocuments.get(i);

            if (fase2.equalsIgnoreCase("Todos")){
                if(fase3.equalsIgnoreCase("Todos")){
                    listaCancionesEstilo = (NodeList) xpath.evaluate("/Interprete/Album/Cancion/@estilo", docAux, XPathConstants.NODESET); //
                } else {

                    String IDAux = docAux.getElementsByTagName("Id").item(0).getTextContent();

                    if (IDAux.equalsIgnoreCase(autorFase3)){
                        listaCancionesEstilo = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='"+fase3+"']/Cancion/@estilo", docAux, XPathConstants.NODESET); //
                    }
                }
            } else {

                if(fase3.equalsIgnoreCase("Todos")){
                    listaCancionesEstilo = (NodeList) xpath.evaluate("/Interprete/Album[Año='"+fase2+"']/Cancion/@estilo", docAux, XPathConstants.NODESET);
                } else {

                    String IDAux = docAux.getElementsByTagName("Id").item(0).getTextContent();

                    if (IDAux.equalsIgnoreCase(autorFase3)){
                        listaCancionesEstilo = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='"+fase3+"' and Año='"+fase2+"']/Cancion/@estilo", docAux, XPathConstants.NODESET);
                    }
                }
            }

            if (listaCancionesEstilo != null){
                for (int j=0; j<listaCancionesEstilo.getLength(); j++){
                    if (!mapFase4.containsValue(listaCancionesEstilo.item(j).getTextContent())) mapFase4.put(mapFase4.size()+1, listaCancionesEstilo.item(j).getTextContent());
                }
            }
        }

        Iterator iterador = mapFase4.keySet().iterator();
        while (iterador.hasNext()){
            Integer key = (Integer) iterador.next();
            String estilo = mapFase4.get(key);

            salida.println("<input type='radio' name='estilo' value='"+estilo+"' checked> "+estilo+".<br>");
        }

        salida.println("<input type='radio' name='estilo' value='Todos' checked> Todos.<br>");
        salida.println("<input type='submit' value='Enviar'>");
        salida.println("<input type='hidden' name='fase' value='223'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=222'>");
        salida.println("</form>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void faseFinalAlbum(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException {

        XPath xpath = XPathFactory.newInstance().newXPath();

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
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void faseFinalEstilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        int totalCanciones = 0;

        salida.println("<h1>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira) (FINAL ESTILO).</h1>");
        salida.println("<form method=GET action='?fase=42'>");
        salida.println("<h3>Su selección ha sido:</h3>");
        salida.println("<h4>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3 + " || Fase 4: " + fase4 + "</h4>");
        salida.println("<h3>El resultado de su consulta es el siguiente:</h3>");
        salida.println("<h4>");

        for (int i = 0; i < listaDocuments.size(); i++){

            NodeList listaCanciones = null;
            Document docAux = listaDocuments.get(i);

            if (fase2.equalsIgnoreCase("Todos")){
                if(fase3.equalsIgnoreCase("Todos")){
                    if(fase4.equalsIgnoreCase("Todos")){
                        listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion", docAux, XPathConstants.NODESET);
                    } else {
                        listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion[@estilo='"+fase4+"']", docAux, XPathConstants.NODESET);
                    }
                } else {

                    String IDAux = docAux.getElementsByTagName("Id").item(0).getTextContent();

                    if (IDAux.equalsIgnoreCase(autorFase3)){
                        if(fase4.equalsIgnoreCase("Todos")){
                            listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='"+fase3+"']/Cancion", docAux, XPathConstants.NODESET);
                        } else {
                            listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='"+fase3+"']/Cancion[@estilo='"+fase4+"']", docAux, XPathConstants.NODESET);
                        }
                    }
                }
            } else {
                if(fase3.equalsIgnoreCase("Todos")){
                    if(fase4.equalsIgnoreCase("Todos")){
                        listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='"+fase2+"']/Cancion", docAux, XPathConstants.NODESET);
                    } else {
                        listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + fase2 + "']/Cancion[@estilo='" + fase4 + "']", docAux, XPathConstants.NODESET);
                    }
                } else {

                    String IDAux = docAux.getElementsByTagName("Id").item(0).getTextContent();

                    if (IDAux.equalsIgnoreCase(autorFase3)){
                        if(fase4.equalsIgnoreCase("Todos")){
                            listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + fase2 + "' and NombreA='"+fase3+"']/Cancion", docAux, XPathConstants.NODESET);
                        } else {
                            listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[Año='" + fase2 + "' and NombreA='"+fase3+"']/Cancion[@estilo='"+fase4+"']", docAux, XPathConstants.NODESET);
                        }
                    }
                }
            }

            if (listaCanciones != null) {
                totalCanciones = totalCanciones + listaCanciones.getLength();
            }
        }

        salida.println("El número de canciones es: " + totalCanciones + ".<br>");
        salida.println("</h4>");
        salida.println("<input type='hidden' name='fase' value='42'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=223'>");
        salida.println("</form>");
        salida.println("</div></div>");
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
