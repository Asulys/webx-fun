package webx;

/**
 * Created by Asulys on 26/03/2017.
 */
public class Bibliotheque {
    String nom;
    String distance;
    Coord coord;

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Bibliotheque{" +
                "nom='" + nom + '\'' +
                ", distance='" + distance + '\'' +
                ", coord=" + coord +
                '}';
    }

    public Bibliotheque() {
        coord = new Coord();
    }
}
