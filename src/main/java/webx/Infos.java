package webx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asulys on 26/03/2017.
 */
public class Infos {
    String titre;
    ArrayList<Auteur> auteurs;
    ArrayList<String>isbn;
    ArrayList<String> ppn;

    public Infos() {
        auteurs = new ArrayList<Auteur>();
        isbn = new ArrayList<String>();
        ppn = new ArrayList<String>();
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public List<Auteur> getAuteurs() {
        return auteurs;
    }

    public void setAuteurs(ArrayList<Auteur> auteurs) {
        this.auteurs = auteurs;
    }

    public ArrayList<String> getIsbn() {
        return isbn;
    }

    public void setIsbn(ArrayList<String> isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "Infos{" +
                "titre='" + titre + '\'' +
                ", auteurs=" + auteurs +
                ", isbn=" + isbn +
                '}';
    }
}
