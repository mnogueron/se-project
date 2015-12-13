package jus.poc.prodcons.v4;

import jus.poc.prodcons.Message;

/**
 * Created by matthieu on 06/12/15.
 */
public class MessageX implements Message {

    private String message;
    private int nbExemplaire;

    public MessageX(String message, int nbExemplaire){
        this.message = message;
        this.nbExemplaire = nbExemplaire;
    }

    public int getNbExemplaire(){
        return this.nbExemplaire;
    }

    public void removeExemplaire(int toRemove) {
        this.nbExemplaire -= toRemove;
    }

    public boolean isEmpty(){
        return nbExemplaire <= 0;
    }

    public String toString(){
        return message;
    }
	
}
