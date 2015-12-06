package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public abstract class Simulateur {
	
	protected Observateur observateur;
	
	//constructor 
	public Simulateur(Observateur observateur){
		
	}
	
	public void start(){
		
	}
	
	protected abstract void run() throws java.lang.Exception;
}
