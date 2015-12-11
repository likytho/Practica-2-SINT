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
import java.net.*;

public class Sint153P2 extends HttpServlet {

    public final String URLInicial = "http://clave.det.uvigo.es:8080/~sint153/p2/";
    ArrayList<String> listaErrores = new ArrayList<>();

    String fase1 = "";

    String fase2 = "";
    String fase2Autor = "";
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

    //MÉTODO DE INICIALIZACIÓN
    public void init(ServletOutputStream salida) throws ServletException, IOException {
        listadoXMLs.add("http://clave.det.uvigo.es:8080/~sint153/p2/springsteen.xml");

        for (int w = 0; w < listadoXMLs.size(); w++){
            leerXML(listadoXMLs.get(w));
        }
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    //MÉTODO PRINCIPAL
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //Salida será el printer de nuestras webs para las consultas
        ServletOutputStream salida = res.getOutputStream();
        res.setContentType("text/html");

        salida.println("<!DOCTYPE html>");
        salida.println("<head>");
        salida.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-15\">");
        salida.println("<title>Servicio IML</title>");
        salida.println("<link href=\"p2/iml.css\" rel=\"stylesheet\" type=\"text/css\" media=\"all\">");
        salida.println("</head>");
        salida.println("<body background=\"p2/notas.jpg\">");
        salida.println("<div id=\"wrapper\">");
        salida.println("<div id=\"container\">");


        //Todo este tocho está dedicado a las fases
        if ((req.getParameter("fase") == null) || (req.getParameter("fase").equals("0"))) {

            init(salida);

            fase1 = "";            fase2 = "";  fase2Autor = "";           fase3 = "";            fase4 = "";        autorFase3 = "";
            mapFase2.clear();            mapFase2Consulta1.clear();            mapFase2Consulta1ID.clear();
            mapFase3.clear();            mapFase3Consulta1Aux.clear();
            mapFase4.clear();

            salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
            salida.println("<h2>Por favor, realice una selección:</h2>");
            salida.println("<form method=GET action='?fase=1'>");
            salida.println("<input type='radio' name='consulta' value='1' checked> Lista de canciones de un álbum.<br>");
            salida.println("<input type='radio' name='consulta' value='2'> Número de canciones de un estilo.<br><br>");
            salida.println("<input type='submit' value='Enviar'>");
            salida.println("<input type='hidden' name='fase' value='1'>");
            salida.println("</form>");
            salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
            salida.println("</div></div>");

            if(!listaErrores.isEmpty()){
                salida.println("<div id=\"wrapper\">");
                salida.println("<div id=\"container\">");
                salida.println("<h3>Notificaciones:</h3>");

                for(int u=0; u<listaErrores.size(); u++){
                    salida.println("<font color=\"red\"> " + listaErrores.get(u) + "</font>");
                }

                salida.println("</div></div>");
            }

            salida.println("</body>");
            salida.println("</html>");

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
                    String fase2Aux = req.getParameter("interprete");
                    String [] fase2AuxAux = fase2Aux.split("#");
                    fase2 = fase2AuxAux[0];
                    fase2Autor = fase2AuxAux[1];
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
                    faseFinalAlbum(req, res, salida);
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


    //PRIMERA CONSULTA
    public void fase2Album(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
        salida.println("<h2>Fase 1: " + fase1 + "</h2>");
        salida.println("<h2>Por favor, seleccione un intérprete:</h2>");
        salida.println("<form method=GET action='?fase=211'>");

        consultaFase2Album();

        if(!mapFase2Consulta1.isEmpty()){

            Iterator iterador = mapFase2Consulta1.keySet().iterator();
            while(iterador.hasNext()){

                String key = (String) iterador.next();
                String Nombre = mapFase2Consulta1.get(key);
                String ID = mapFase2Consulta1ID.get(Nombre);

                salida.println("<input type='radio' name='interprete' value='" + ID + "#" + Nombre + "' checked> " + Nombre + ".<br>");
            }

            salida.println("<input type='radio' name='interprete' value='Todos#Todos' checked> Todos.<br>");
            salida.println("<input type='submit' value='Enviar'><br>");
            salida.println("<input type='hidden' name='fase' value='211'>");

        } else {
            salida.println("<h2>No hay opciones disponibles, la consulta no puede continuar.</h2>");
        }

        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=0'>");
        salida.println("</form>");
        salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase3Album(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + "</h2>");
        salida.println("<h2>Por favor, seleccione un álbum:</h2>");
        salida.println("<form method=GET action='?fase=212'>");

        try{
            consultaFase3Album();
        } catch (XPathException e){}

        if(!mapFase3Consulta1Aux.isEmpty()){

            Iterator iterador = mapFase3Consulta1Aux.keySet().iterator();
            while(iterador.hasNext()){

                Integer key = (Integer) iterador.next();
                ArrayList <String> arrayAux = mapFase3Consulta1Aux.get(key);


                for (int h=0; h<arrayAux.size(); h++){
                    salida.println("<input type='radio' name='album' value='" + arrayAux.get(h) + "' checked> " + arrayAux.get(h) + " ( " + key + " ).<br>");
                }
            }

            salida.println("<input type='radio' name='album' value='Todos' checked> Todos.<br>");
            salida.println("<input type='submit' value='Enviar'> <br>");

        } else {
            salida.println("<h2>No hay opciones disponibles, la consulta no puede continuar.</h2>");
        }

        salida.println("<input type='hidden' name='fase' value='212'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=211'>");
        salida.println("</form>");
        salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void faseFinalAlbum(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        ArrayList<String> listaCanciones = null;

        salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
        salida.println("<form method=GET action='?fase=41'>");
        salida.println("<h3>Su selección ha sido:</h3>");
        salida.println("<h4>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3);
        salida.println("<h3>El resultado de su consulta es el siguiente:</h3>");
        salida.println("<h4>");
        salida.println("<ul>");

        try{
            listaCanciones = consultaFaseFinalAlbum();
        } catch (XPathException e) {}

        if(!listaCanciones.isEmpty()){

            for (int a=0; a<listaCanciones.size(); a++){
                salida.println("<li> " + listaCanciones.get(a) + "<br>");
            }

        } else {
            salida.println("<h2>No hay canciones que mostrar.</h2>");
        }

        salida.println("</ul>");
        salida.println("</h4>");
        salida.println("<input type='hidden' name='fase' value='41'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=212'>");
        salida.println("</form>");
        salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }


    //SEGUNDA CONSULTA
    public void fase2Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException {

        salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
        salida.println("<h2>Fase 1: " + fase1 + "</h2>");
        salida.println("<h2>Por favor, seleccione un año:</h2>");
        salida.println("<form method=GET action='?fase=221'>");

        consultaFase2Estilo();

        if(!mapFase2.isEmpty()){
            Iterator iterator = mapFase2.keySet().iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                salida.println("<input type='radio' name='anho'  value='" + mapFase2.get(key) + "' checked> " + mapFase2.get(key) + ".<br>");
            }

            salida.println("<input type='radio' name='anho' value='Todos' checked> Todos.<br>");
            salida.println("<input type='submit' value='Enviar'> <br>");

        } else {
            salida.println("<h2>No hay opciones disponibles, la consulta no puede continuar.</h2>");
        }

        salida.println("<input type='hidden' name='fase' value='221'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=0'>");
        salida.println("</form>");
        salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase3Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException {

        TreeMap<String, ArrayList<String>> mapAux = null;

        salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + "</h2>");
        salida.println("<h2>Por favor, seleccione un álbum:</h2>");
        salida.println("<form method=GET action='?fase=222'>");

        try {
            mapAux = consultaFase3Estilo();
        } catch (XPathException e) {}

        if(!mapAux.isEmpty()){

            Iterator iterador = mapAux.keySet().iterator();
            while(iterador.hasNext()){

                String key = (String) iterador.next();
                ArrayList<String> arrayAux = mapAux.get(key);

                salida.println("<b>Autor: </b>" + key + "<br>");

                for (int a=0; a<arrayAux.size(); a++){
                    String albumAux = arrayAux.get(a);

                    String [] splitted = albumAux.split("#");

                    salida.println("<input type='radio' name='album2' value='"+ albumAux +"' checked> " + splitted[0]  + ".<br>");
                }
                salida.println("<br>");
            }
            salida.println("<input type='radio' name='album2' value='Todos#Todos' checked> Todos.<br>");
            salida.println("<input type='submit' value='Enviar'> <br>");
        } else {
            salida.println("<h2>No hay opciones disponibles, la consulta no puede continuar.</h2>");
        }

        salida.println("<input type='hidden' name='fase' value='222'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=221'>");
        salida.println("</form>");
        salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void fase4Estilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException {

        salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
        salida.println("<h2>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3 + "</h2>");
        salida.println("<h2>Por favor, seleccione un estilo:</h2>");
        salida.println("<form method=GET action='?fase=223'>");

        try {
            consultaFase4Estilo();
        } catch (XPathException e) {}

        if(!mapFase4.isEmpty()){
            Iterator iterador = mapFase4.keySet().iterator();
            while (iterador.hasNext()){
                Integer key = (Integer) iterador.next();
                String estilo = mapFase4.get(key);

                salida.println("<input type='radio' name='estilo' value='"+estilo+"' checked> "+estilo+".<br>");
            }

            salida.println("<input type='radio' name='estilo' value='Todos' checked> Todos.<br>");
            salida.println("<input type='submit' value='Enviar'> <br>");
        } else {
            salida.println("<h2>No hay opciones disponibles, la consulta no puede continuar.</h2>");
        }

        salida.println("<input type='hidden' name='fase' value='223'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=222'>");
        salida.println("</form>");
        salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }

    public void faseFinalEstilo(HttpServletRequest req, HttpServletResponse res, ServletOutputStream salida) throws ServletException, IOException, XPathExpressionException {

        int totalCanciones = 0;

        salida.println("<h1>SERVICIO DE CONSULTA DE INFORMACIÓN MUSICAL</h1>");
        salida.println("<form method=GET action='?fase=42'>");
        salida.println("<h3>Su selección ha sido:</h3>");
        salida.println("<h4>Fase 1: " + fase1 + " || Fase 2: " + fase2 + " || Fase 3: " + fase3 + " || Fase 4: " + fase4 + "</h4>");
        salida.println("<h3>El resultado de su consulta es el siguiente:</h3>");
        salida.println("<h4>");

        try{
            totalCanciones = consultaFaseFinalEstilo();
        } catch (XPathException e){}

        salida.println("El número de canciones es: " + totalCanciones + ".<br>");
        salida.println("</h4>");
        salida.println("<input type='hidden' name='fase' value='42'>");
        salida.println("<input type='submit' value='Inicio' onClick='form.fase.value=0'>");
        salida.println("<input type='submit' value='Atrás' onClick='form.fase.value=223'>");
        salida.println("</form>");
        salida.println("<h5>Servicio de consulta de información musical (sint153 - Pedro Tubío Figueira).</h5>");
        salida.println("</div></div>");
        salida.println("</body>");
        salida.println("</html>");
    }


    //MÉTODOS BÚSQUEDA FASES
    //CONSULTA 1
    public void consultaFase2Album(){
        mapFase2Consulta1.clear();
        mapFase2Consulta1ID.clear();

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

    }

    public void consultaFase3Album() throws XPathException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList listaAlbumes = null;
        mapFase3Consulta1Aux.clear();

        String anho = "";
        String album = "";


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
                //break;
            }
        }
    }

    public ArrayList<String> consultaFaseFinalAlbum() throws XPathExpressionException, ServletException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        ArrayList<String> listadoFinalCanciones = new ArrayList<String>();

        if (fase2.equalsIgnoreCase("Todos")) {

            Document docAux = null;

            for (int m = 0; m < listaDocuments.size(); m++) {
                docAux = listaDocuments.get(m);

                if (fase3.equalsIgnoreCase("Todos")) {
                    NodeList listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album/Cancion", docAux, XPathConstants.NODESET);
                    listadoFinalCanciones.addAll(obtenerCancionesFaseFinalConsulta1(listaCanciones));
                } else {
                    for (int x = 0; x < docAux.getElementsByTagName("Album").getLength(); x++) {
                        if (docAux.getElementsByTagName("NombreA").item(x).getTextContent().equalsIgnoreCase(fase3)) {
                            NodeList listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='" + fase3 + "']/Cancion", docAux, XPathConstants.NODESET);
                            listadoFinalCanciones.addAll(obtenerCancionesFaseFinalConsulta1(listaCanciones));
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
                listadoFinalCanciones = obtenerCancionesFaseFinalConsulta1(listaCanciones);
            } else {

                for (int x = 0; x < docAux.getElementsByTagName("Album").getLength(); x++) {
                    if (docAux.getElementsByTagName("NombreA").item(x).getTextContent().equalsIgnoreCase(fase3)) {
                        NodeList listaCanciones = (NodeList) xpath.evaluate("/Interprete/Album[NombreA='" + fase3 + "']/Cancion", docAux, XPathConstants.NODESET);
                        listadoFinalCanciones = obtenerCancionesFaseFinalConsulta1(listaCanciones);
                    }
                }
            }
        }
        return listadoFinalCanciones;
    }

    //CONSULTA 2
    public void consultaFase2Estilo(){

        mapFase2.clear();
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

    }

    public TreeMap<String, ArrayList<String>> consultaFase3Estilo() throws XPathException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        TreeMap<String, ArrayList<String>> listaImprimir = new TreeMap<>();
        String autor = "";

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

                for (int j=0; j<nodoAlbums.getLength(); j++){

                    if(!listaImprimir.containsKey(autor)){
                        ArrayList<String> arrayAux = new ArrayList<String>();
                        arrayAux.add(nodoAlbums.item(j).getTextContent() + "#" + docAux.getElementsByTagName("Id").item(0).getTextContent());

                        listaImprimir.put(autor, arrayAux);
                    } else {
                        ArrayList<String> arrayAux = listaImprimir.get(autor);
                        arrayAux.add(nodoAlbums.item(j).getTextContent() + "#" + docAux.getElementsByTagName("Id").item(0).getTextContent());
                        listaImprimir.put(autor, arrayAux);
                    }
                }
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

                for (int j = 0; j < listaAlbums.getLength(); j++) {

                    NodeList nodoAlbumHijos = listaAlbums.item(j).getChildNodes();
                    String nombreAlbum = "";

                    for(int k=0; k<nodoAlbumHijos.getLength(); k++){
                        String Album = nodoAlbumHijos.item(k).getNodeName();

                        if (Album.equals("NombreA")){
                            nombreAlbum = nodoAlbumHijos.item(k).getTextContent().trim().replaceAll("\n","");
                            if(!listaImprimir.containsKey(autor)){
                                ArrayList<String> arrayAux = new ArrayList<String>();
                                arrayAux.add((nombreAlbum) + "#" + docAux.getElementsByTagName("Id").item(0).getTextContent());

                                listaImprimir.put(autor, arrayAux);
                            } else {
                                ArrayList<String> arrayAux = listaImprimir.get(autor);
                                arrayAux.add((nombreAlbum) + "#" + docAux.getElementsByTagName("Id").item(0).getTextContent());
                                listaImprimir.put(autor, arrayAux);
                            }

                        }
                    }
                }
            }
        }

        return listaImprimir;
    }

    public void consultaFase4Estilo() throws XPathException {

        mapFase4.clear();
        XPath xpath = XPathFactory.newInstance().newXPath();

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

    }

    public int consultaFaseFinalEstilo() throws XPathException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        int totalCanciones = 0;

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
        return totalCanciones;
    }


    //MÉTODOS AUXILIARES
    public Document getDoc(int d) {

        Document document = null;
        document = listaDocuments.get(d);
        return document;
    }

    public void leerXML(String XML) throws ServletException, IOException {

        listaErrores.clear();
        boolean yaExiste = false;

        //Empezamos obteniendo documentos
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);

        try {
            db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException exception) throws SAXException {
                    listaErrores.add("Warning: " + exception.toString());
                }

                public void error(SAXParseException exception) throws SAXException {
                    listaErrores.add("Error: " + exception.toString());
                }

                public void fatalError(SAXParseException exception) throws SAXException {
                    listaErrores.add("Fatal error: " + exception.toString());
                }
            });

            if(XML.startsWith("http://")){
                doc=db.parse(new URL (XML).openStream(), "http://clave.det.uvigo.es:8080/~sint153/p2/");
            }else{
                doc=db.parse(new URL (URLInicial+XML).openStream(), "http://clave.det.uvigo.es:8080/~sint153/p2/");
            }

            for (int i = 0; i<listaDocuments.size(); i++){

                Document docAux = listaDocuments.get(i);

                String IDdoc = doc.getElementsByTagName("Id").item(0).getTextContent();;
                String IDdocAux = docAux.getElementsByTagName("Id").item(0).getTextContent();

                if (IDdoc.equals(IDdocAux)) yaExiste = true;
            }

            if (!yaExiste) listaDocuments.add(doc);

            NodeList nodosIML = doc.getElementsByTagName("IML");

            for (int i = 0; i < nodosIML.getLength(); i++) {
                String siguienteXML = nodosIML.item(i).getTextContent();
                if(!siguienteXML.startsWith("http://")) siguienteXML = URLInicial+siguienteXML;

                if ((!siguienteXML.equals("")) && (!listadoXMLs.contains(siguienteXML))) listadoXMLs.add(siguienteXML);
            }

        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }

    }

    public ArrayList<String> obtenerCancionesFaseFinalConsulta1 (NodeList listaCanciones) throws ServletException {

        ArrayList<String> listaFinalConsulta1 = new ArrayList<String>();

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

                listaFinalConsulta1.add(nombreC + " (" + duracion + ", " + descripcion + ")");
            }
        }
        return listaFinalConsulta1;
    }

}
