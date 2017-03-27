package webx;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Asulys on 20/03/2017.
 */
public class ClientProjet {

    private static final String API_URI = "http://api.crossref.org/works/";

    /**
     * @param DOI le numéro de doi
     * @return un tableau JSON avec les infos voulus
     */
    public Infos getInfos(String DOI) {
        Infos resultat = new Infos();
        final Client c = ClientBuilder.newClient();
        final WebTarget wt = c.target(API_URI);
        try {
            final JsonObject result = wt.path(DOI)
                    .request(MediaType.APPLICATION_JSON).get(JsonObject.class);
            resultat.titre = result.getJsonObject("message").getJsonArray("title").getString(0);
            JsonArray ja = result.getJsonObject("message").getJsonArray("author");

            for (int i = 0; i < ja.size(); i++) {
                Auteur a = new Auteur();
                JsonObject object = ja.getJsonObject(i);
                a.prenom = object.getString("given");
                a.nom = object.getString("family");

                resultat.auteurs.add(a);
            }

            ja = result.getJsonObject("message").getJsonArray("ISBN");
            for (int i = 0; i < ja.size(); i++) {
                String[] temp = ja.getString(i).split("/");
                resultat.isbn.add(temp[temp.length-1]);
            }

            return resultat;

        } catch (InternalServerErrorException e) {
            // e.printStackTrace();
            System.err.println("Réponse HTTP " + e.getResponse().getStatus());
            resultat = null;
            return resultat;
        }
    }

    /**
     * @return les ppn correspondant
     * @throws IOException
     * @throws SAXException
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     */
    public ArrayList<String> getPPN(String isbn) throws MalformedURLException, SAXException, IOException, XPathExpressionException, ParserConfigurationException {
        ArrayList<String> resultat = new ArrayList<String>();
        String url = "http://www.sudoc.fr/services/isbn2ppn/" + isbn;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try {
            Document doc = builder.parse(url);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xPath = xPathfactory.newXPath();
            String expr = "/sudoc/query/result/ppn";

            NodeList nodeList = (NodeList) xPath.compile(expr).evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                resultat.add(nNode.getTextContent());
            }

        } catch (FileNotFoundException e) {
            return resultat;
        }

        return resultat;
    }

    /**
     * @param ppn
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public ArrayList<Bibliotheque> getBU(String ppn) throws ParserConfigurationException, SAXException, IOException {
        ArrayList<Bibliotheque> resultat = new ArrayList<Bibliotheque>();
        String url = "http://www.sudoc.fr/services/multiwhere/" + ppn;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(url);
        //System.out.println(url);

        NodeList libraries = doc.getElementsByTagName("library");
        for (int i = 0; i < libraries.getLength(); i++) {
            Element library = (Element) libraries.item(i);
            Element shortname = (Element) library.getElementsByTagName("shortname").item(0);
            Element latitude = (Element) library.getElementsByTagName("latitude").item(0);
            Element longitude = (Element) library.getElementsByTagName("longitude").item(0);
            Bibliotheque temp = new Bibliotheque();
            temp.nom = shortname.getTextContent();
            temp.coord.lat = latitude.getTextContent();
            temp.coord.lon = longitude.getTextContent();

            resultat.add(temp);
        }

		/*
        XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xPath = xPathfactory.newXPath();

		String expr = "/sudoc/query/result/library";

		NodeList nodeList = (NodeList) xPath.compile(expr).evaluate(doc, XPathConstants.NODESET);

		for (int i = 1; i <= nodeList.getLength(); i++) {
			String req1 = expr + "["+ i + "]/shortname";
			String req2 = expr + "["+ i + "]/latitude";
			String req3 = expr + "["+ i + "]/longitude";
			NodeList tempnode1 = (NodeList) xPath.compile(req1).evaluate(doc, XPathConstants.NODESET);
			NodeList tempnode2 = (NodeList) xPath.compile(req2).evaluate(doc, XPathConstants.NODESET);
			NodeList tempnode3 = (NodeList) xPath.compile(req3).evaluate(doc, XPathConstants.NODESET);
			Node nNode1 = tempnode1.item(0);
			Node nNode2 = tempnode2.item(0);
			Node nNode3 = tempnode3.item(0);
			resultat += nNode1.getTextContent() + "\n";
			resultat += nNode2.getTextContent() + "\n";
			resultat += nNode3.getTextContent() + "\n";
		}
		*/
        return resultat;
    }


    /**
     * @param adress adresse dont on veut les coordonnées
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Coord getCoord(String adress) throws ParserConfigurationException, SAXException, IOException {
        Coord resultat =new Coord();
        adress.replace(' ', '+');
        String url = "http://nominatim.openstreetmap.org/search?q=" + adress + "&format=xml&polygon=1&addressdetails=1";

        //System.out.println(url);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(url);

        NodeList searchresults = doc.getElementsByTagName("searchresults");
        for (int i = 0; i < searchresults.getLength(); i++) {
            Element searchresult = (Element) searchresults.item(i);
            Element place = (Element) searchresult.getElementsByTagName("place").item(0);

            resultat.lat = place.getAttribute("lat");
            resultat.lon =  place.getAttribute("lon");

        }
        return resultat;
    }


    /**
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws JSONException
     */
    public String getDistance(Coord origine, Coord... destinations) throws ParserConfigurationException, SAXException, IOException, JSONException, JSONException {
        //String result;
        String resultat = "";
        String url = "http://sampleserver6.arcgisonline.com/ArcGIS/rest/services/Utilities/Geometry/GeometryServer/lengths?sr=4326";
        StringBuilder urlEnco = new StringBuilder();
        int i = 0;

        urlEnco.append(URLEncoder.encode("[", "UTF-8"));

        for (Coord courante : destinations) {

            urlEnco.append(URLEncoder.encode("{\"paths\":[[[" + origine.getLon() + "," + origine.getLat() + "],[" + courante.getLon() + "," + courante.getLat() + "]]]}", "UTF-8"));
            i++;
            if (i != destinations.length - 1) {
                urlEnco.append(URLEncoder.encode(",", "UTF-8"));
            }

        }

        urlEnco.append(URLEncoder.encode("]", "UTF-8"));

        url += "&polylines=";
        url += urlEnco;
        url += "&lengthUnit=9036&calculationType=preserveShape&f=json";

        //System.out.println(url);

        final Client c = ClientBuilder.newClient();

        try {
            final String result = c.target(url).request(MediaType.APPLICATION_JSON).get(String.class);

            org.json.JSONObject jo = new org.json.JSONObject(result);
            org.json.JSONArray ja = jo.getJSONArray("lengths");

            //System.out.println(result);

            for (i = 0; i < ja.length(); i++) {
                String length = ja.getString(i);

                resultat = length;
            }


            //System.out.println(resultat);

        } catch (InternalServerErrorException e) {
            // e.printStackTrace();
            System.err.println("Réponse HTTP " + e.getResponse().getStatus());
            return "erreur";
        }

        return resultat;
    }

    public String serviceWebResponse(String doi, String adresse) throws ParserConfigurationException, TransformerException, XPathExpressionException, SAXException, IOException, JSONException {
        //1a récupère les infos de base
        Infos infos = new Infos();
        infos = getInfos(doi);

        //1b récupère les ppn à partir des isbn
        for (String isbn : infos.isbn) {
            ArrayList<String> temp = new ArrayList<String>();
            temp = getPPN(isbn);
            if (!temp.isEmpty()) {

                //ajoute les ppn si pas de doublon
                for (String ppnCourant : temp) {
                    if(!infos.ppn.contains(ppnCourant))
                        infos.ppn.add(ppnCourant);
                }
            }
        }
        //System.out.println(infos.ppn);

        //1c récupère les bu ainsi que leurs coordonnées
        ArrayList<Bibliotheque> bibliotheques = new ArrayList<Bibliotheque>();
        for (String ppn : infos.ppn) {
            ArrayList<Bibliotheque> temp = new ArrayList<Bibliotheque>();
            temp = getBU(ppn);
            if (!temp.isEmpty()) {

                //ajoute les BU si pas de doublon
                //#TODO si marche pas override method equals
                for (Bibliotheque bibliothequeCoutante : temp) {
                    if(!bibliotheques.contains(bibliothequeCoutante))
                        bibliotheques.add(bibliothequeCoutante);
                }
            }
        }

        //1d récupération des coordonnées à partir de l'adresse
        Coord position = getCoord(adresse);

        //1e pour chaque BU on calcul la distance entre la BU et la position de l'adresse

        for (int i = 0; i < bibliotheques.size(); i++) {
            bibliotheques.get(i).distance = getDistance(position, bibliotheques.get(i).coord);
        }
        //System.out.println(bibliotheques);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // reponse elements
        Document doc = docBuilder.newDocument();
        Element reponse = doc.createElement("Response");
        doc.appendChild(reponse);

        reponse.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        reponse.setAttribute("xsi:schemaLocation", "https://example.com/webx/multi-livre multi-livre.xsd");
        reponse.setAttribute("xmlns", "https://example.com/webx/multi-livre");

        // result elements
        Element result = doc.createElement("result");
        reponse.appendChild(result);

        // titre elements
        Element titre = doc.createElement("titre");
        titre.appendChild(doc.createTextNode(infos.titre));
        result.appendChild(titre);

        for (Auteur a : infos.auteurs) {
            // auteurs elements
            Element auteur = doc.createElement("auteur");
            result.appendChild(auteur);

            // auteur prenom elements
            Element prenom = doc.createElement("prenom");
            prenom.appendChild(doc.createTextNode(a.prenom));
            auteur.appendChild(prenom);

            // auteur nom elements
            Element nom = doc.createElement("nom");
            nom.appendChild(doc.createTextNode(a.nom));
            auteur.appendChild(nom);
        }

        for (Bibliotheque b : bibliotheques) {
            // bibli elements
            Element bibliotheque = doc.createElement("bibliotheque");
            result.appendChild(bibliotheque);

            // bibli nom elements
            Element bnom = doc.createElement("nom");
            bnom.appendChild(doc.createTextNode(b.nom));
            bibliotheque.appendChild(bnom);

            // bibli distance elements
            Element bdist = doc.createElement("distance");
            bdist.appendChild(doc.createTextNode(b.distance));
            bibliotheque.appendChild(bdist);
        }

        /*// request elements
        Element request = doc.createElement("Request");
        rootElement.appendChild(request);

        // doi elements
        Element edoi = doc.createElement("doi");
        edoi.appendChild(doc.createTextNode(doi));
        request.appendChild(edoi);

        // adresse elements
        Element eadresse = doc.createElement("adresse");
        eadresse.appendChild(doc.createTextNode(adresse));
        request.appendChild(eadresse);*/

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult xmlResult = new StreamResult(new File("output/file.xml"));

        // Output to console for testing
        //StreamResult xmlResult = new StreamResult(System.out);

        transformer.transform(source, xmlResult);

        //tri avec xslt
        String xslt = "resources/sort.xslt";
        String output = "output/file - sorted.xml";

        Transformer transform = TransformerFactory.newInstance().newTransformer(new StreamSource( new File(xslt) ));
        StreamResult sorted = new StreamResult(new File(output));
        transform.transform(new DOMSource(doc), sorted);

        String contenu = "";
        try{
            InputStream flux=new FileInputStream(output);
            InputStreamReader lecture=new InputStreamReader(flux);
            BufferedReader buff=new BufferedReader(lecture);
            String ligne;
            while ((ligne=buff.readLine())!=null){
                contenu += ligne + "\n";
            }
            buff.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return contenu;
    }
}

