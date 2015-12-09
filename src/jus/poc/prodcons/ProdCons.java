package jus.poc.prodcons;

import java.util.ArrayList;
import java.util.HashMap;

import sun.org.mozilla.javascript.Synchronizer;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {
	
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
	public synchronized Message get(_Consommateur c) {
		while(enAttente() <= 0){
			if(productionIsFinished()){
				return null;
			}
			
			try {
				//c.wait();
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();}
		}
		
		Message m = buffer[out];
		buffer[out] = null;
		out = (out+1)%taille();
		notifyAll();
			
		return m;
	}

	@Override
	public synchronized void put(_Producteur p, Message m) {
		while(enAttente() >= taille()){
			try {
				//p.wait();
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();}
		}
		
		buffer[in] = m;
		in = (in+1)%taille();
		notifyAll();
	}

	@Override
	public int taille() {
		return buffer.length;
	}
}
