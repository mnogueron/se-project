package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class Consommateur extends Acteur implements _Consommateur {

	// constructor
	protected Consommateur(
			int type,
			Observateur observateur,
			int moyenneTempsDeTraitement,
			int deviationTempsDeTraitement)
			throws ControlException {
		super(type, observateur,
				moyenneTempsDeTraitement,
				deviationTempsDeTraitement);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int nombreDeMessages() {
		// TODO Auto-generated method stub
		return 0;
	}
}
