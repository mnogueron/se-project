package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class Producteur extends Acteur implements _Acteur, Runnable {

	protected Producteur(int type, Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement)
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
