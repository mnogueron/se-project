package jus.poc.prodcons.v1;

import jus.poc.prodcons.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static jus.poc.prodcons.v1.TestProdCons.AnsiColor;

/**
 * Created by matthieu on 06/12/15.
 */
public class Producteur extends Acteur implements _Producteur, Runnable {

	private static Logger LOGGER = Logger.getLogger(Producteur.class.getName());

    public static void initLogger(){
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new TestProdCons.LogConsoleHandler());
    }

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
        LOGGER.log(Level.INFO, "{0}[{1}] is running...{2}",
                new Object[]{AnsiColor.GREEN, identification(), AnsiColor.RESET});
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
        LOGGER.log(Level.INFO, "{0}[{1}] has finished.{2}",
                new Object[]{AnsiColor.GREEN, identification(), AnsiColor.RESET});
	}

	@Override
	public int nombreDeMessages() {
		return nbMessagesToAdd;
	}
}
