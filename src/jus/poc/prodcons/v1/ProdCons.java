package jus.poc.prodcons.v1;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

import static jus.poc.prodcons.v1.TestProdCons.AnsiColor;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {

	private static Logger LOGGER = Logger.getLogger(ProdCons.class.getName());

    public static void initLogger(){
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new TestProdCons.LogConsoleHandler());
    }
	
	private Message[] buffer;
	private int in;
	private int out;
	
	private int nbProd;
	private ArrayList<Producteur> prodFinished;
	
	/**
	 * ProdCons constructor 
	 * @param bufferSize
	 */
	public ProdCons(int bufferSize, int nbProd) {
		in = 0;
		out = 0;
		prodFinished = new ArrayList<>();
		this.nbProd = nbProd;
		buffer = new Message[bufferSize];
		for(int i=0; i<taille(); i++){
			buffer[i] = null;
		}
	}
	
	public synchronized void setProductionFinished(Producteur p){
		prodFinished.add(p);
	}
	
	public synchronized boolean productionIsFinished(){
        return prodFinished.size() == nbProd && enAttente() == 0;
	}

	@Override
	public synchronized int enAttente() {
		int nbBusy = 0;
		for(int i=0; i<taille(); i++){
			if (buffer[i] != null){
				nbBusy++;
			}
		}
		return nbBusy;
	}

	@Override
	public synchronized Message get(_Consommateur c) {
		while(enAttente() <= 0){
            if (productionIsFinished()) {
                return null;
            }

			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();}
		}
		
		Message m = buffer[out];
		buffer[out] = null;
		out = (out+1)%taille();
        LOGGER.log(Level.INFO, "{0}[{1}] \tconsumes: \t\t{2}{3}",
                new Object[]{AnsiColor.CYAN, c.identification(), m, AnsiColor.RESET});

		notifyAll();
        return m;
	}

	@Override
	public synchronized void put(_Producteur p, Message m) {
		while(enAttente() >= taille()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();}
		}
		
		buffer[in] = m;
		in = (in+1)%taille();
        LOGGER.log(Level.INFO, "{0}[{1}] \tproduces: \t\t{2}{3}",
                new Object[]{AnsiColor.PURPLE, p.identification(), m, AnsiColor.RESET});
		notifyAll();
	}

	@Override
	public int taille() {
		return buffer.length;
	}
}
