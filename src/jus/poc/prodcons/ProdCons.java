package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {
	
	private Message[] buffer;
	private int in;
	private int out ;
	
	/**
	 * ProdCons constructor 
	 * @param bufferSize
	 */
	public ProdCons(int bufferSize) {
		in = 0;
		out = 0;
		buffer = new Message[bufferSize];
		for(int i=0; i<taille(); i++){
			buffer[i] = null;
		}
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
	public Message get(_Consommateur c) {
		while(enAttente() <= 0){
			try {
				c.wait();
			} catch (InterruptedException e) {}
		}
		
		Message m = buffer[out];
		buffer[out] = null;
		out = (out-1)%taille();
		notify();
			
		return m;
	}

	@Override
	public void put(_Producteur p, Message m) {
		while(enAttente() >= taille()){
			try {
				p.wait();
			} catch (InterruptedException e) {}
		}
		
		buffer[in] = m;
		in = (in+1)%taille();
		notify();
	}

	@Override
	public int taille() {
		return buffer.length;
	}
}
