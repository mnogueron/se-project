package jus.poc.prodcons.v2;

import jus.poc.prodcons.Message;
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
	
	private Logger logger = Logger.getLogger(ProdCons.class.getName());
	
	private Message[] buffer;
	private int in;
	private int out;
	
	private int nbProd;
	private ArrayList<Producteur> prodFinished;

	private File fp, fc;
	
	/**
	 * ProdCons constructor 
	 * @param bufferSize
	 */
	public ProdCons(int bufferSize, int nbProd) {
		in = 0;
		out = 0;
        logger.setLevel(Level.INFO);
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
		return prodFinished.size() == nbProd;
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
	public Message get(_Consommateur c) throws InterruptedException {
		fc.attendre();
        if (productionIsFinished() && enAttente() == 0) {
            return null;
        }

		Message m;
		synchronized (this){
			m = buffer[out];
			buffer[out] = null;
			out = (out+1)%taille();
			logger.info("Consommateur \t["+c.identification()+"] \tconsumes: \t\t"+ m);
		}

		fp.reveiller();
			
		return m;
	}

	@Override
	public void put(_Producteur p, Message m) throws InterruptedException {
		fp.attendre();

		synchronized (this) {
			buffer[in] = m;
			in = (in + 1) % taille();
			logger.info("Producteur \t\t[" + p.identification() + "] \tproduces: \t\t" + m);
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
				if(ProdCons.this.productionIsFinished() && ProdCons.this.enAttente() == 0){
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
