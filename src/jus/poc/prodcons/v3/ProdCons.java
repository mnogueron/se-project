package jus.poc.prodcons.v3;

import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {
	
	private static Logger LOGGER = Logger.getLogger(ProdCons.class.getName());
	
	private Message[] buffer;
	private int in;
	private int out;
	
	private int nbProd;
	private ArrayList<Producteur> prodFinished;

	private File fp, fc;
	private Observateur observateur;
	
	/**
	 * ProdCons constructor 
	 * @param bufferSize
	 */
	public ProdCons(int bufferSize, int nbProd, Observateur observateur) {
		this.observateur = observateur;
		in = 0;
		out = 0;
		prodFinished = new ArrayList<>();
		this.nbProd = nbProd;
		buffer = new Message[bufferSize];
		for(int i=0; i<taille(); i++){
			buffer[i] = null;
		}
		fp = new File(taille());
		fc = new File(enAttente());
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
	public Message get(_Consommateur c) throws InterruptedException, ControlException {
		fc.attendre();
        if (productionIsFinished()) {
            return null;
        }

		Message m;
		synchronized (this){
			m = buffer[out];
			
			/*
			 * add retraitMessage(Consommateur C, Message M)
			 * for objective 3
			 */
			observateur.retraitMessage(c, m);
			
			buffer[out] = null;
			out = (out+1)%taille();
			LOGGER.info("[" + c.identification() + "] \tconsumes: \t\t" + m);
		}

		fp.reveiller();
			
		return m;
	}

	@Override
	public void put(_Producteur p, Message m) throws InterruptedException, ControlException {
		fp.attendre();

		synchronized (this) {
			buffer[in] = m;
			
			/*
			 * add depotMessage(Producteur P, Message M)
			 * for objective 3 
			 */
			observateur.depotMessage(p, m);
			
			in = (in + 1) % taille();
			LOGGER.info("[" + p.identification() + "] \tproduces: \t\t" + m);
		}

		fc.reveiller();
	}

	@Override
	public int taille() {
		return buffer.length;
	}

	private class File{

		private int residu;

		public File(int residu){
			this.residu = residu;
		}

		public synchronized void attendre() throws InterruptedException {
			while(residu == 0){
				if(ProdCons.this.productionIsFinished()){
                    notify();
					return;
				}
				wait();
			}
			residu--;
		}
		public synchronized void reveiller() throws InterruptedException {
			residu++;
			notifyAll();
		}
	}
}
