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

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
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
		prodFinished = new ArrayList<>();
		this.nbProd = nbProd;
		buffer = new Message[bufferSize];
		for(int i=0; i<taille(); i++){
			buffer[i] = null;
		}
		fp = new File(taille());
		fc = new File(enAttente());
	}
	
	public void setProductionFinished(Producteur p){
		prodFinished.add(p);
	}
	
	public boolean productionIsFinished(){
		return prodFinished.size() == nbProd;
	}

	@Override
	public int enAttente() {
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
		if(productionIsFinished()){
			return null;
		}
		fc.attendre();

		Message m;
		synchronized (this){
			m = buffer[out];
			buffer[out] = null;
			out = (out+1)%taille();
			logger.info(ANSI_YELLOW + "Consommateur \t["+c.identification()+"] \tconsumes: \t\t"
					+ ((m==null)?ANSI_RED:"")+ m + ANSI_RESET);
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
			logger.info(ANSI_BLUE + "Producteur \t\t[" + p.identification() + "] \tproduces: \t\t" + m + ANSI_RESET);
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
					return;
				}
				wait();
			}
			residu--;
		}
		public synchronized void reveiller() throws InterruptedException {
			residu++;
			notify();
		}
	}
}
