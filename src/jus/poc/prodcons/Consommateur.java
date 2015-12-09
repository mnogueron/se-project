package jus.poc.prodcons;

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
    	while(nbMessages > 0){
    		m = prodCons.get(this);
    		System.out.println("Consumed message: " + m);
    		nbMessages--;
    	}
    }

    // number of messages already processed by the Consommateur
	@Override
	public int nombreDeMessages() {
		return nbMessagesToRead-nbMessages;
	}
}
