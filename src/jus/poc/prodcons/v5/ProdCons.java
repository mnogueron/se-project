package jus.poc.prodcons.v5;

import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {
	
	private Logger LOGGER = Logger.getLogger(ProdCons.class.getName());
	
	private Message[] buffer;
	private int in;
	private int out;
	
	private int nbProd;
	// changed ArrayList to CopyOnWriteArrayList which is thread-safe
	private CopyOnWriteArrayList<Producteur> prodFinished;

	private Observateur observateur;
	
	private int count;
	// Create a new lock for objective 5
	private Lock lock = new ReentrantLock();
	// Create 2 conditions for objective 5
	private final Condition isEmpty = lock.newCondition();
	private final Condition isFull = lock.newCondition();
	
	/**
	 * ProdCons constructor 
	 * @param bufferSize
	 * @param nbProd
	 * @param observateur
	 */
	public ProdCons(int bufferSize, int nbProd, Observateur observateur) {
		this.observateur = observateur;
		in = 0;
		out = 0;
        LOGGER.setLevel(Level.INFO);
		prodFinished = new CopyOnWriteArrayList<>();
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
		return prodFinished.size() == nbProd && enAttente() == 0;
	}

	@Override
	// added a lock as the method shouldn't be synchronized anymore
	// for objective 5
	public int enAttente() {
		lock.lock();
		int nbBusy = 0;
		try{
			for(int i=0; i<taille(); i++){
				if (buffer[i] != null){
					nbBusy++;
				}
			}
		}
		finally{
			lock.unlock();
		}
		return nbBusy;
	}

	@Override
	public Message get(_Consommateur c) throws InterruptedException, ControlException {
		lock.lock();
		Message m;
		count = enAttente();
        
        try{
	        while(count == 0){
	        	// signal all isEmpty if productionIsFinished
	        	if(productionIsFinished()){
	        		isEmpty.signalAll();
	        		return null;
	        	}
	        	isEmpty.await();
	        }
	        
			m = buffer[out];
			
			/*
			 * add retraitMessage(Consommateur C, Message M)
			 * for objective 3
			 */
			observateur.retraitMessage(c, m);
			
			buffer[out] = null;
			out = (out+1)%taille();
			count--;
			
			LOGGER.info("[" + c.identification() + "] \tconsumes: \t\t" + m);
			
			// notifies the producer that there is space available in the buffer
			isFull.signal();
        }
        finally{
			lock.unlock();
        }
			
		return m;
	}

	@Override
	public void put(_Producteur p, Message m) throws InterruptedException, ControlException {
		lock.lock();
		count = enAttente();
		
		try{
			while(count == taille()){
				isFull.await();
			}
			
			buffer[in] = m;
				
			/*
			 * add depotMessage(Producteur P, Message M)
			 * for objective 3 
			 */
			observateur.depotMessage(p, m);
			
			in = (in + 1) % taille();
			count++;
			
			LOGGER.info("[" + p.identification() + "] \tproduces: \t\t" + m);
			
			// notifies the consumer that data is available to read
			isEmpty.signal();
		}
		finally{
			lock.unlock();
		}
	}

	@Override
	public int taille() {
		return buffer.length;
	}

}
