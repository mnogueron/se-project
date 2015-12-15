package jus.poc.prodcons.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

/**
 * Created by matthieu on 06/12/15.
 */
public class TestProdCons extends Simulateur {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[\u001b[34m%4$s\u001B[0m]: {%2$s} %5$s%6$s%n");
    }

    private static Logger LOGGER = Logger.getLogger(TestProdCons.class.getName());

    public static void initLogger(){
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new LogConsoleHandler());
    }

    public static enum AnsiColor{
        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        private String value = "";

        AnsiColor(String value) {
            this.value = value;
        }

        public String toString(){
            return value;
        }
    }

    private int nbProd;
    private int nbCons;
    private int nbBuffer;
    private int tempsMoyenProduction;
    private int deviationTempsMoyenProduction;
    private int tempsMoyenConsommation;
    private int deviationTempsMoyenConsommation;
    private int nombreMoyenDeProduction;
    private int deviationNombreMoyenDeProduction;
    private int nombreMoyenNbExemplaire;
    private int deviationNombreMoyenNbExemplaire;
    
    private ArrayList<Consommateur> consommateurs; 
    private ArrayList<Producteur> producteurs; 
    
    private ProdCons prodCons;
    private int typeConsommateur = 2;
    private int typeProducteur = 1;

	public TestProdCons(Observateur observateur) {
		super(observateur);
        LOGGER.log(Level.INFO, "{0}Initialize local variables{1}",
                new Object[]{AnsiColor.GREEN, AnsiColor.RESET});
        init("options.xml");
        prodCons = new ProdCons(nbBuffer, nbProd, nbCons, this.observateur);
        consommateurs = new ArrayList<>();
        producteurs = new ArrayList<>();

        try {
			observateur.init(nbProd, nbCons, nbBuffer);
		} catch (ControlException e1) {
			e1.printStackTrace();
		}

        LOGGER.log(Level.INFO, "{0}Create all Consommateur{1}",
                new Object[]{AnsiColor.GREEN, AnsiColor.RESET});
        for(int i = 0; i<nbCons; i++){
        	try {
        		Consommateur c = new Consommateur(typeConsommateur, this.observateur,
						tempsMoyenConsommation, deviationTempsMoyenConsommation, prodCons,
						nombreMoyenNbExemplaire, deviationNombreMoyenNbExemplaire);
				consommateurs.add(c);
				observateur.newConsommateur(c);
			} catch (ControlException e) {
				e.printStackTrace();
			}
        }

        LOGGER.log(Level.INFO, "{0}Create all Producteur{1}",
                new Object[]{AnsiColor.GREEN, AnsiColor.RESET});
        for(int i = 0; i<nbProd; i++){
        	try {
        		Producteur p = new Producteur(typeProducteur, this.observateur,
						tempsMoyenProduction, deviationTempsMoyenProduction, prodCons,
						nombreMoyenDeProduction, deviationNombreMoyenDeProduction);
				producteurs.add(p);
				observateur.newProducteur(p);
			} catch (ControlException e) {
				e.printStackTrace();
			}
        }
	}

	@Override
	protected void run() throws Exception {
		for(Producteur p : producteurs){
			p.start();
		}
		for(Consommateur c : consommateurs){
			c.start();
		}
	}

    public void init(String file){
        class Properties extends java.util.Properties{
            private static final long serialVersionUID = 1L;
            public int get(String key){
                return Integer.parseInt(getProperty(key));
            }

            public Properties(String file){
                try {
                    loadFromXML(ClassLoader.getSystemResourceAsStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Properties options = new Properties("jus/poc/prodcons/options/"+file);
        nbProd = options.get("nbProd");
        nbCons = options.get("nbCons");
        nbBuffer = options.get("nbBuffer");
        tempsMoyenProduction = options.get("tempsMoyenProduction");
        deviationTempsMoyenProduction = options.get("deviationTempsMoyenProduction");
        tempsMoyenConsommation = options.get("tempsMoyenConsommation");
        deviationTempsMoyenConsommation = options.get("deviationTempsMoyenConsommation");
        nombreMoyenDeProduction = options.get("nombreMoyenDeProduction");
        deviationNombreMoyenDeProduction = options.get("deviationNombreMoyenDeProduction");
        nombreMoyenNbExemplaire = options.get("nombreMoyenNbExemplaire");
        deviationNombreMoyenNbExemplaire = options.get("deviationNombreMoyenNbExemplaire");
    }

	public static void main(String[] args){
        // Initialize all logger and set their output to System.out and not System.err
        {
            TestProdCons.initLogger();
            Consommateur.initLogger();
            ProdCons.initLogger();
            Producteur.initLogger();
        }

        if(args.length > 0){
            if(!args[0].equals("-Ddebug=1")){
                LogManager.getLogManager().reset();
            }
        }
        new TestProdCons(new Observateur()).start();
    }

    public static class LogConsoleHandler extends Handler {
        @Override
        public void publish(LogRecord record) {
            if(getFormatter() == null){
                setFormatter(new SimpleFormatter());
            }

            try {
                String message = getFormatter().format(record);
                if(record.getLevel().intValue() >= Level.WARNING.intValue()){
                    System.err.write(message.getBytes());
                }
                else{
                    System.out.write(message.getBytes());
                }
            } catch (IOException e) {
                reportError(null, e, ErrorManager.FORMAT_FAILURE);
            }
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    }
}
