package jus.poc.prodcons;

import java.util.Random;

/**
 * Created by matthieu on 06/12/15.
 */
public class Producteur extends Acteur implements _Producteur, Runnable {

	private int nbMessages;
    private ProdCons prodCons;

	protected Producteur(int type, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement,
                         ProdCons prodCons, int moyenneNombreDeProduction, int deviationNombreDeProduction)
			throws ControlException {
		super(type, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
        this.prodCons = prodCons;
        do {
            nbMessages = Aleatoire.valeur(moyenneNombreDeProduction, deviationNombreDeProduction);
        }while(nbMessages <= 0);
	}

	@Override
	public void run() {
		while(nombreDeMessages() > 0){
            prodCons.put(this, new MessageX("Producteur : " + this.toString() + nombreDeMessages()));
        }
	}

	@Override
	public int nombreDeMessages() {
		return nbMessages;
	}
}
