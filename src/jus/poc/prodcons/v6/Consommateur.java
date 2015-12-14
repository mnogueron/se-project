package jus.poc.prodcons.v6;

import jus.poc.prodcons.*;
import jus.poc.prodcons.v5.ProdCons;
import jus.poc.prodcons.v5.TestProdCons;

import java.util.logging.Level;
import java.util.logging.Logger;

import static jus.poc.prodcons.v5.TestProdCons.AnsiColor;

/**
 * Created by matthieu on 06/12/15.
 */
public class Consommateur extends Acteur implements _Consommateur, Runnable {

	private static Logger LOGGER = Logger.getLogger(jus.poc.prodcons.v5.Consommateur.class.getName());

	public static void initLogger(){
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(new TestProdCons.LogConsoleHandler());
	}

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
        LOGGER.log(Level.INFO, "{0}[{1}] is running...{2}",
                new Object[]{AnsiColor.GREEN, identification(), AnsiColor.RESET});
    	Message m;
    	while(nbMessages > 0){
			try {
				m = prodCons.get(this);
				if(m == null && prodCons.productionIsFinished()){
					break;
				}
				
				/*
				 * added consommationMessage(Consommateur C, Message M, int T)
				 * for objective 3
				 */
				observateur.consommationMessage(this, m, moyenneTempsDeTraitement);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ControlException e) {
				e.printStackTrace();
			}
			nbMessages--;
			try {
				sleep(Aleatoire.valeur(moyenneTempsDeTraitement, deviationTempsDeTraitement)*100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
        LOGGER.log(Level.INFO, "{0}[{1}] has finished.{2}",
                new Object[]{AnsiColor.GREEN, identification(), AnsiColor.RESET});
    }

    // number of messages already processed by the Consommateur
	@Override
	public int nombreDeMessages() {
		return nbMessagesToRead-nbMessages;
	}
}
