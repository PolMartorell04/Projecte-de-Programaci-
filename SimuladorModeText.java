//Pol Martorell Herrera
/**
 * @file SimuladorModeText.java
 * @brief Simula les operacions de construcció, modificació i consulta d'una xarxa de distribució d'aigua a partir d'un fitxer de text.
 */

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @class SimuladorModeText
 * @brief Classe per simular una xarxa de distribució d'aigua a partir d'un fitxer de text.
 */
public class SimuladorModeText {

    private Xarxa xarxa = new Xarxa(); ///< Xarxa de distribució d'aigua.
    private Stack<Pair<Boolean, Pair<String, Node_X>>> passos = new Stack<>(); ///< Pila per guardar els passos de l'operació.

    /**
     * @brief Simula les operacions de construcció, modificació i consulta d'una xarxa de distribució d'aigua.
     * @param fitxerEntrada Nom del fitxer d'entrada.
     * @param fitxerSortida Nom del fitxer de sortida.
     * @pre fitxerEntrada és el nom d'un fitxer de text que conté una seqüència d'operacions a realitzar sobre una xarxa de distribució d'aigua i fitxerSortida és on es guardarà el resultat de les operacions.
     * @post S'han realitzat les operacions descrites al fitxer sobre la xarxa de distribució d'aigua i s'han escrit les operacions a fitxerSortida.
     */
    public void simular(String fitxerEntrada, String fitxerSortida) {
        try {
            File fitxer = new File(fitxerEntrada + ".txt");
            FileOutputStream fitxerResultat = new FileOutputStream(fitxerSortida + ".txt");
            PrintStream printFitxer = new PrintStream(fitxerResultat);
            System.setOut(printFitxer);

            Scanner sc = new Scanner(fitxer);
            sc.useLocale(Locale.US);
            sc.useDelimiter(System.lineSeparator());
            String aux = "";
            String ent = "";
            while (sc.hasNextLine()) {
                if (!esOperacio(aux))
                    ent = sc.nextLine();
                else
                    ent = aux;
                if (!ent.trim().isEmpty()) {
                    aux = gestio_opcio(ent, sc);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arxiu no trobat: " + e.getMessage());
        }
    }

    /**
     * @brief Gestiona les operacions especificades en el fitxer d'entrada.
     * @param ent Una línia del fitxer d'entrada.
     * @param sc L'scanner des del qual es llegeixen les dades.
     * @return La següent opció a gestionar.
     * @pre ent és una línia del fitxer d'entrada i sc l'scanner del qual llegim.
     * @post S'ha realitzat l'operació descrita a ent amb les dades que ha llegit l'scanner.
     */
    private String gestio_opcio(String ent, Scanner sc) {
        String opcio = "";

        switch (ent) {
            case "terminal":
                String nomTerminal = sc.next();
                String coordenadesTerminal = sc.next();
                float demandaTerminal = sc.nextFloat();
                Coordenades cTerminal = partir_coordenades(coordenadesTerminal);
                PuntTerminal terminal = new PuntTerminal(nomTerminal, cTerminal, demandaTerminal);
                xarxa.afegir(terminal);
                break;

            case "origen":
                String nomOrigen = sc.next();
                String coordenadesOrigen = sc.next();
                Coordenades cOrigen = partir_coordenades(coordenadesOrigen);
                PuntOrigen origen = new PuntOrigen(nomOrigen, cOrigen);
                xarxa.afegir(origen);
                break;

            case "connexio":
                String nomConnexio = sc.next();
                String coordenadesConnexio = sc.next();
                Coordenades cConnexio = partir_coordenades(coordenadesConnexio);
                Connexio connexio = new Connexio(nomConnexio, cConnexio);
                xarxa.afegir(connexio);
                break;

            case "connectar":
                String nomNode1Connexio = sc.next();
                String nomNode2Connexio = sc.next();
                float capacitatConnexio = sc.nextFloat();
                Node_X node1Connexio = xarxa.node(nomNode1Connexio);
                Node_X node2Connexio = xarxa.node(nomNode2Connexio);
                xarxa.connectarAmbCanonada(node1Connexio, node2Connexio, capacitatConnexio, false);
                break;

            case "abonar":
                String clientAbonar = sc.next();
                String puntAbonar = sc.next();
                PuntTerminal terminalAbonar = (PuntTerminal) xarxa.node(puntAbonar);
                xarxa.abonar(clientAbonar, terminalAbonar);
                break;

            case "tancar":
                boolean canvis_t = false;
                String idAixetaTanca = sc.next();
                Node_X aixetaTanca = xarxa.node(idAixetaTanca);
                if (xarxa.tancarAixeta(aixetaTanca)) canvis_t = true;
                passos.push(new Pair<>(canvis_t, new Pair<>("t", aixetaTanca)));
                break;

            case "obrir":
                boolean canvis_o = false;
                String idAixetaObre = sc.next();
                Node_X aixetaObre = xarxa.node(idAixetaObre);
                if (xarxa.obrirAixeta(aixetaObre)) canvis_o = true;
                passos.push(new Pair<>(canvis_o, new Pair<>("o", aixetaObre)));
                break;

            case "backtrack":
                int nBacktrack = sc.nextInt();
                for (int i = 0; i < nBacktrack; i++) {
                    Pair<Boolean, Pair<String, Node_X>> p = passos.pop();
                    if (p.agafarPrimer()) {
                        if (p.agafarSegon().agafarPrimer().equals("t"))
                            xarxa.obrirAixeta(p.agafarSegon().agafarSegon());
                        else
                            xarxa.tancarAixeta(p.agafarSegon().agafarSegon());
                    }
                }
                break;

            case "cabal":
                String nomOrigenCabal = sc.next();
                float cabal = sc.nextFloat();
                PuntOrigen nodeCabal = (PuntOrigen) xarxa.node(nomOrigenCabal);
                xarxa.establirCabal(nodeCabal, cabal);
                break;

            case "demanda":
                String nomNodeDemanda = sc.next();
                float demanda = sc.nextFloat();
                PuntTerminal nodeDemanda = (PuntTerminal) xarxa.node(nomNodeDemanda);
                xarxa.establirDemanda(nodeDemanda, demanda);
                break;

            case "cicles":
                String nomNodeCicles = sc.next();
                PuntOrigen nodeCicles = (PuntOrigen) xarxa.node(nomNodeCicles);
                boolean teCicles = GestorXarxes.teCicles(xarxa, nodeCicles);
                if (teCicles)
                    System.out.println(nomNodeCicles + " te cicles");
                else
                    System.out.println(nomNodeCicles + " no te cicles");
                break;

            case "arbre":
                String nomNodeArbre = sc.next();
                PuntOrigen nodeArbre = (PuntOrigen) xarxa.node(nomNodeArbre);
                boolean esArbre = GestorXarxes.esArbre(xarxa, nodeArbre);
                if (esArbre)
                    System.out.println(nomNodeArbre + " es un arbre");
                else
                    System.out.println(nomNodeArbre + " no es un arbre");
                break;

            case "cabal minim":
                String nomOrigenCabalMinim = sc.next();
                String percentatgeCabalMinim = sc.next();
                percentatgeCabalMinim = percentatgeCabalMinim.substring(0, percentatgeCabalMinim.length() - 1);
                float percentatge = Float.parseFloat(percentatgeCabalMinim);
                PuntOrigen origenCabalMinim = (PuntOrigen) xarxa.node(nomOrigenCabalMinim);
                float cabalMinim = GestorXarxes.cabalMinim(xarxa, origenCabalMinim, percentatge);
                System.out.println("cabal minim");
                System.out.println(cabalMinim);
                break;

            case "exces cabal":
                String nomCanonadaExcesCabal = sc.next();
                Set<Canonada> cjtCanonades = new HashSet<>();
                Canonada canonadaExcesCabal = xarxa.canonada(nomCanonadaExcesCabal);
                while (!esOperacio(nomCanonadaExcesCabal) && sc.hasNextLine()) {
                    canonadaExcesCabal = xarxa.canonada(nomCanonadaExcesCabal);
                    cjtCanonades.add(canonadaExcesCabal);
                    nomCanonadaExcesCabal = sc.next();
                    opcio = nomCanonadaExcesCabal;
                }
                if (!sc.hasNextLine()) {
                    canonadaExcesCabal = xarxa.canonada(nomCanonadaExcesCabal);
                    cjtCanonades.add(canonadaExcesCabal);
                }
                Set<Canonada> canonadesExces = GestorXarxes.excesCabal(xarxa, cjtCanonades);
                System.out.println("exces cabal");
                for (Canonada canonada : canonadesExces) {
                    System.out.println(canonada.node1().id() + "-" + canonada.node2().id());
                }
                break;

            case "situacio":
                Map<PuntTerminal, Boolean> aiguaArriba = new HashMap<>();
                String situacio = sc.next();
                String[] partsSituacio;
                while (!esOperacio(situacio) && sc.hasNextLine()) {
                    partsSituacio = situacio.split(" ");
                    if (partsSituacio[1].equals("SI"))
                        aiguaArriba.put((PuntTerminal) xarxa.node(partsSituacio[0]), true);
                    else
                        aiguaArriba.put((PuntTerminal) xarxa.node(partsSituacio[0]), false);
                    situacio = sc.next();
                    opcio = situacio;
                }
                if (!sc.hasNextLine()) {
                    partsSituacio = situacio.split(" ");
                    if (partsSituacio[1].equals("SI"))
                        aiguaArriba.put((PuntTerminal) xarxa.node(partsSituacio[0]), true);
                    else
                        aiguaArriba.put((PuntTerminal) xarxa.node(partsSituacio[0]), false);
                }
                Set<Node_X> aixetesTancar = GestorXarxes.aixetesTancar(xarxa, aiguaArriba);
                System.out.println("tancar");
                for (Node_X aixeta : aixetesTancar) {
                    System.out.println(aixeta.id());
                }
                break;

            case "cabal abonat":
                String dni = sc.next();
                float cabalAbonat = xarxa.cabalAbonat(dni);
                System.out.println("cabal abonat");
                System.out.println(cabalAbonat);
                break;

            case "proximitat":
                String entradaCoordenadesProximitat = sc.next();
                Coordenades cProximitat = partir_coordenades(entradaCoordenadesProximitat);
                Set<Node_X> conjunt = new HashSet<>();
                String entradaProximitat = sc.next();
                while (!esOperacio(entradaProximitat) && sc.hasNextLine()) {
                    Node_X nodeProximitat = xarxa.node(entradaProximitat);
                    conjunt.add(nodeProximitat);
                    entradaProximitat = sc.next();
                    opcio = entradaProximitat;
                }
                if (!sc.hasNextLine()) {
                    Node_X nodeProximitat = xarxa.node(entradaProximitat);
                    conjunt.add(nodeProximitat);
                }
                List<Node_X> llistaProximitat = GestorXarxes.nodesOrdenats(cProximitat, conjunt);
                System.out.println("proximitat");
                for (Node_X node : llistaProximitat) {
                    System.out.println(node.id());
                }
                break;

            case "dibuix":
                String origenDibuix = sc.next();
                PuntOrigen nodeDibuix = (PuntOrigen) xarxa.node(origenDibuix);
                xarxa.dibuixar(nodeDibuix, false);
                break;

            case "max-flow":
                String origenMaxflow = sc.next();
                PuntOrigen nodeMaxflow = (PuntOrigen) xarxa.node(origenMaxflow);
                GestorXarxes.fluxMaxim(xarxa, nodeMaxflow);
                break;

            default:
                System.out.println("Entrada no valida: " + ent);
        }
        return opcio;
    }

    /**
     * @brief Converteix una cadena de coordenades en un objecte Coordenades.
     * @param coordenades Cadena de coordenades.
     * @return Objecte Coordenades corresponent a les coordenades donades.
     * @pre coordenades és una cadena de caràcters amb el format "grausLatitud:minutsLatitud:segonsLatituddireccioLatitud,grausLongitud:minutsLongitud:segonsLongituddireccioLongitud" on grausLatitud, minutsLatitud, segonsLatitud, grausLongitud, minutsLongitud i segonsLongitud són enters i direccioLatitud i direccioLongitud són caràcters 'N', 'S', 'E' o 'W'.
     * @post Retorna un objecte Coordenades amb els valors x i y.
     */
    private Coordenades partir_coordenades(String coordenades) {
        String[] parts = coordenades.split(",");
        String[] latitudParts = parts[0].split(":");
        String[] longitudParts = parts[1].split(":");

        int grausLatitud = Integer.parseInt(latitudParts[0]);
        int minutsLatitud = Integer.parseInt(latitudParts[1]);
        float segonsLatitud = Float.parseFloat(latitudParts[2].substring(0, latitudParts[2].length() - 1)); // Remove the last character 'N'
        char direccioLatitud = latitudParts[2].charAt(latitudParts[2].length() - 1); // Get the last character 'N'

        int grausLongitud = Integer.parseInt(longitudParts[0]);
        int minutsLongitud = Integer.parseInt(longitudParts[1]);
        float segonsLongitud = Float.parseFloat(longitudParts[2].substring(0, longitudParts[2].length() - 1)); // Remove the last character 'E'
        char direccioLongitud = longitudParts[2].charAt(longitudParts[2].length() - 1); // Get the last character 'E'

        return new Coordenades(grausLatitud, minutsLatitud, segonsLatitud, direccioLatitud, grausLongitud, minutsLongitud, segonsLongitud, direccioLongitud);
    }

    /**
     * @brief Comprova si una cadena és una operació vàlida.
     * @param ent Cadena a comprovar.
     * @return Cert si la cadena és una operació vàlida, fals altrament.
     * @pre Cert.
     * @post Retorna cert si ent és una operació vàlida, fals altrament.
     */
    private boolean esOperacio(String ent) {
        return ent.equals("terminal") || ent.equals("origen") || ent.equals("connexio") || ent.equals("connectar") ||
                ent.equals("abonar") || ent.equals("tancar") || ent.equals("obrir") || ent.equals("backtrack") ||
                ent.equals("cabal") || ent.equals("demanda") || ent.equals("cicles") || ent.equals("arbre") ||
                ent.equals("cabal minim") || ent.equals("exces cabal") || ent.equals("situacio") ||
                ent.equals("cabal abonat") || ent.equals("proximitat") || ent.equals("dibuix") ||
                ent.equals("max-flow");
    }
}