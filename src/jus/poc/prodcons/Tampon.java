package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public interface Tampon {

    int enAttente();
    Message get(_Consommateur c);
    void put(_Producteur p, Message m);
    int taille();
}
