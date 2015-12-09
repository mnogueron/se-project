package jus.poc.prodcons;

import java.util.Random;

/**
 * Created by matthieu on 06/12/15.
 */
public class Producteur extends Acteur implements _Producteur, Runnable {

	private int nbMessages;
    private int nbMessagesToAdd;
    private ProdCons prodCons;

	protected Producteur(int type, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement,
                         ProdCons prodCons, int moyenneNombreDeProduction, int deviationNombreDeProduction)
			throws ControlException {
		super(type, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
        this.prodCons = prodCons;
        nbMessages = Aleatoire.valeur(moyenneNombreDeProduction, deviationNombreDeProduction);
        nbMessagesToAdd = nbMessages;
	}

	@Override
	public void run() {
		while(nbMessages > 0){
            prodCons.put(this, new MessageX("Producteur : " + this.toString() + nombreDeMessages()));
            nbMessages--;
        }
	}

	@Override
	public int nombreDeMessages() {
		return nbMessagesToAdd;
	}
}
