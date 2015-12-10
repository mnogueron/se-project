package jus.poc.prodcons.v2;

import java.util.Random;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

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
			try {
				sleep(Aleatoire.valeur(moyenneTempsDeTraitement, deviationTempsDeTraitement)*100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			MessageX m = new MessageX(Aleatoire.valeur(1000, 2000)+"");
            prodCons.put(this, m);
            nbMessages--;
        }
		prodCons.setProductionFinished(this);
	}

	@Override
	public int nombreDeMessages() {
		return nbMessagesToAdd;
	}
}
