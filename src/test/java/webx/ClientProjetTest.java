package webx;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Asulys on 20/03/2017.
 */
public class ClientProjetTest {
    ClientProjet client;

    @Before
    public void setUp(){
        client = new ClientProjet();
    }

    @Test
    public void getInfos() throws Exception {
        Infos infos = client.getInfos("10.1007/978-3-662-07964-5");
        assertEquals(infos.getTitre(), "Interactive Theorem Proving and Program Development");

        List<Auteur> auteurList = infos.getAuteurs();
        assertEquals("Bertot", auteurList.get(0).getNom());
        assertEquals("Yves", auteurList.get(0).getPrenom());
        assertEquals("Castéran", auteurList.get(1).getNom());
        assertEquals("Pierre", auteurList.get(1).getPrenom());

        ArrayList<String> isbnList = infos.getIsbn();
        assertEquals("978-3-642-05880-6", isbnList.get(0));
        assertEquals("978-3-662-07964-5", isbnList.get(1));
    }

    @Test
    public void getPPN() throws Exception {
        ArrayList<String> ppnList = client.getPPN("978-3-642-05880-6");
        assertEquals("156358263", ppnList.get(0));
    }

    @Test
    public void getBU() throws Exception {
        ArrayList<Bibliotheque> bibliothequeList = client.getBU("156358263");
        Bibliotheque bibliotheque = bibliothequeList.get(0);
        assertEquals("TOULOUSE3-Bib Math. et Méca.", bibliotheque.getNom());
        assertEquals(null, bibliotheque.getDistance());
        assertEquals("43.56755709999999", bibliotheque.getCoord().getLat());
        assertEquals("1.4578939", bibliotheque.getCoord().getLon());
    }

    @Test
    public void getCoord() throws Exception {
        Coord coord = client.getCoord("90 Route de Narbonne 31400");
        assertEquals("43.5664156", coord.getLat());
        assertEquals("1.4590021", coord.getLon());

    }

    @Test
    public void getDistance() throws Exception {
        Coord coordOrig = client.getCoord("90 Route de Narbonne 31400");
        Coord coordDest = new Coord("43.56755709999999", "1.4578939");
        assertEquals("0.1552414466045036", client.getDistance(coordOrig, coordDest));
    }
}