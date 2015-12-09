package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class TestProdCons extends Simulateur {

	public TestProdCons(Observateur observateur) {
		super(observateur);
	}

	@Override
	protected void run() throws Exception {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args){
        new TestProdCons(new Observateur()).start();
    }
}
