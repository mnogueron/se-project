package jus.poc.prodcons.v2;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Consommateur;

/**
 * Created by matthieu on 06/12/15.
 */
public class Consommateur extends Acteur implements _Consommateur, Runnable {

	private int nbMessages;
	private ProdCons prodCons;
	private int nbMessagesToRead;
	
	/**
	 * Constructor for Consommateur
	 * @param type
	 * @param observateur
	 * @param moyenneTempsDeTraitement
	 * @param deviationTempsDeTraitement
	 * @param prodCons
	 * @param moyenneNbExemplaires
	 * @param deviationNbExemplaires
	 * @throws ControlException
	 */
	protected Consommateur(int type, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement,
			ProdCons prodCons, int moyenneNbExemplaires, int deviationNbExemplaires)
			throws ControlException {
		super(type, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		this.prodCons = prodCons;
		// generate random number of messages that the Consommateur can read
		nbMessagesToRead = Aleatoire.valeur(moyenneNbExemplaires, deviationNbExemplaires);
		nbMessages = nbMessagesToRead;
	}

    @Override
    public void run() {
    	Message m;
    	while(nbMessages > 0 || !prodCons.productionIsFinished()){
    		m = prodCons.get(this);
    		nbMessages--;
			try {
				sleep(Aleatoire.valeur(moyenneTempsDeTraitement, deviationTempsDeTraitement)*100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }

    // number of messages already processed by the Consommateur
	@Override
	public int nombreDeMessages() {
		return nbMessagesToRead-nbMessages;
	}
}
