package webx;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Asulys on 27/03/2017.
 */
@XmlRootElement(name = "Request")
public class Requete {
    String doi;
    String adresse;

    public Requete(){

    }

    public Requete(String doi, String adresse){
        this.doi = doi;
        this.adresse = adresse;
    }

    @XmlElement
    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    @XmlElement
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }



}
