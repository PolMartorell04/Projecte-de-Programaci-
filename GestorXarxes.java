//Natalia Masgrau Vila
/**
 * @file GestorXarxes.java
 * @brief Mòdul funcional amb funcions per a la gestió de xarxes de distribució d'aigua
 */

import java.util.*;

/**
 * @class GestorXarxes
 * @brief Classe abstracta per a la gestió de xarxes de distribució d'aigua.
 */
public abstract class GestorXarxes {
    /**
     * @brief Diu si la component connexa de la xarxa x que conté nodeOrigen té cicles.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen de la xarxa.
     * @return True si té cicles, false altrament.
     * Pre: nodeOrigen pertany a la xarxa x.
     * Post: Diu si la component connexa de la xarxa x que conté nodeOrigen té cicles.
     */
    public static boolean teCicles(Xarxa x, Node_X nodeOrigen) {
        Set<Node_X> visitats = new HashSet<>();
        Set<Node_X> pilaRecursio = new HashSet<>();
        List<PuntOrigen> origens = obtenirOrigens(x, nodeOrigen);
        boolean teCicles = false;
        while (!teCicles && !origens.isEmpty()) {
            teCicles = dfs(x, origens.remove(0), visitats, pilaRecursio);
        }
        return teCicles;
    }

    /**
     * @brief Diu si la component connexa de la xarxa x que conté nodeOrigen és un arbre.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen de la xarxa.
     * @return True si és un arbre, false altrament.
     * Pre: nodeOrigen pertany a la xarxa x.
     * Post: Diu si la component connexa de la xarxa x que conté nodeOrigen és un arbre.
     */
    public static boolean esArbre(Xarxa x, PuntOrigen nodeOrigen) {
        if (teCicles(x, nodeOrigen)) return false;
        else {
            Set<Node_X> visitats = new HashSet<>();
            Queue<Node_X> cua = new LinkedList<>();
            cua.add(nodeOrigen);
            visitats.add(nodeOrigen);
            while (!cua.isEmpty()) {
                Node_X actual = cua.poll();
                for (Node_X vei : obtenirVeins(x, actual)) {
                    if (obtenirEntrades(x, vei).size() > 1) return false;
                    if (!visitats.contains(vei)) {
                        visitats.add(vei);
                        cua.add(vei);
                    }
                }
            }
            return true;
        }
    }

    /**
     * @brief Retorna el cabal mínim que hi hauria d’haver entre tots els nodes d’origen de la component connexa de la xarxa x que conté nodeOrigen.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen de la xarxa.
     * @param percentatgeDemandaSatisfet Percentatge de demanda satisfet.
     * @return Cabal mínim.
     * Pre: nodeOrigen pertany a la xarxa x, la component connexa de la xarxa x que conté nodeOrigen no té cicles, i percentatgeDemandaSatisfet > 0.
     * Post: Retorna el cabal mínim que hi hauria d’haver entre tots els nodes d’origen de la component connexa de la xarxa x que conté nodeOrigen, per tal que cap node terminal de la mateixa component, d'entre aquells on arribi aigua, no rebi menys d'un percentatgeDemandaSatisfet% de la seva demanda.
     */
    public static float cabalMinim(Xarxa x, PuntOrigen nodeOrigen, float percentatgeDemandaSatisfet) {
        float cabalMinim = 0;
        Set<Node_X> visitats = new HashSet<>();
        Queue<Node_X> cua = new LinkedList<>();
        cua.add(nodeOrigen);
        while (!cua.isEmpty()) {
            Node_X actual = cua.poll();
            visitats.add(actual);
            if (actual instanceof PuntTerminal) {
                PuntTerminal terminal = (PuntTerminal) actual;
                float demanda = terminal.demanda_actual();
                float demandaMinima = demanda * percentatgeDemandaSatisfet / 100;
                cabalMinim += demandaMinima;
            }
            for (Node_X vei : obtenirVeins(x, actual)) {
                if (!visitats.contains(vei) && vei.aixetaOberta()) {
                    cua.add(vei);
                }
            }
        }
        return cabalMinim;
    }

    /**
     * @brief Retorna el subconjunt de canonades de cjtCanonades tals que, si es satisfés la demanda de tots els nodes terminals de la mateixa component, es sobrepassaria la seva capacitat.
     * @param x Xarxa de distribució d'aigua.
     * @param cjtCanonades Conjunt de canonades.
     * @return Subconjunt de canonades excedides.
     * Pre: Les canonades de cjtCanonades pertanyen a una mateixa component connexa, sense cicles, de la xarxa x.
     * Post: Retorna el subconjunt de canonades de cjtCanonades tals que, si es satisfés la demanda de tots els nodes terminals de la mateixa component, es sobrepassaria la seva capacitat.
     */
    public static Set<Canonada> excesCabal(Xarxa x, Set<Canonada> cjtCanonades) {
        Set<Canonada> canonadesExcedides = new HashSet<>();
        List<PuntTerminal> terminals = new ArrayList<>();
        obtenirTerminals(x, terminals);
        List<PuntOrigen> origens = new ArrayList<>();

        Map<Canonada, Float> demandesCanonades = new HashMap<>();
        Map<Node_X, Float> demandesNodes = new HashMap<>();
        Queue<Node_X> cuaDemandes = new LinkedList<>(terminals);
        Set<Node_X> encuatsDemanda = new HashSet<>();
        guardarDemandes(terminals, demandesNodes);
        while (!cuaDemandes.isEmpty()) {
            Node_X actual = cuaDemandes.poll();
            if(actual instanceof PuntOrigen) {
                origens.add((PuntOrigen) actual);
            }
            if (demandesNodes.containsKey(actual)) {
                propagarDemanda(x, actual, demandesNodes, demandesCanonades, cuaDemandes, encuatsDemanda);
            } else {
                cuaDemandes.add(actual);
            }
        }

        Map<Canonada, Float> cabalsCanonades = new HashMap<>();
        Map<Node_X, Float> cabalsNodes = new HashMap<>();
        Queue<Node_X> cuaCabals = new LinkedList<>(origens);
        Set<Node_X> encuatsCabal = new HashSet<>();
        guardarCabals(origens, cabalsNodes);
        while (!cuaCabals.isEmpty()) {
            Node_X actual = cuaCabals.poll();
            if (cabalsNodes.containsKey(actual)) {
                repartirCabal(actual, x, inferiors(x, actual), cabalsNodes, cabalsCanonades, cuaCabals, encuatsCabal, demandesCanonades);
            } else {
                cuaCabals.add(actual);
            }
        }

        for (Canonada canonada : cjtCanonades) {
            if (cabalsCanonades.get(canonada) > canonada.capacitat()) {
                canonadesExcedides.add(canonada);
            }
        }
        return canonadesExcedides;
    }

    /**
     * @brief Retorna el conjunt de nodes n de la xarxa x més propers als terminals t de aiguaArriba, tals que per sota de n la situació actual de la xarxa és incoherent amb aiguaArriba.
     * @param x Xarxa de distribució d'aigua.
     * @param aiguaArriba Map que indica si arriba aigua als terminals.
     * @return Conjunt de nodes a tancar.
     * Pre: Tots els terminals d'aiguaArriba pertanyen a la xarxa x, aiguaArriba.get(t) indica si arriba aigua a t, i la xarxa x té forma d'arbre.
     * Post: Retorna el conjunt de nodes n de la xarxa x més propers (seguint la topologia) als terminals t de aiguaArriba, tals que per sota de n la situació actual de la xarxa és incoherent amb aiguaArriba.
     */
    public static Set<Node_X> aixetesTancar(Xarxa x, Map<PuntTerminal, Boolean> aiguaArriba) {
        Set<Node_X> nodesTancar = new HashSet<>();
        Map<Node_X, Boolean> incoherents = new HashMap<>();
        mirarIncoherents(x, aiguaArriba, incoherents);
        for(Node_X node : incoherents.keySet()) {
            if(incoherents.get(node)) {
                tancarNode(x, node, incoherents, nodesTancar);
            }
        }
        return nodesTancar;
    }

    /**
     * @brief Retorna una llista amb els nodes de cjtNodes ordenats segons la seva distància a c i, en cas d'empat, en ordre alfabètic dels seus identificadors.
     * @param c Coordenades de referència.
     * @param cjtNodes Conjunt de nodes.
     * @return Llista de nodes ordenats.
     * Pre: ---
     * Post: Retorna una llista amb els nodes de cjtNodes ordenats segons la seva distància a c i, en cas d'empat, en ordre alfabètic dels seus identificadors.
     */
    public static List<Node_X> nodesOrdenats(Coordenades c, Set<Node_X> cjtNodes) {
        List<Node_X> nodesOrdenats = new ArrayList<>(cjtNodes);
        nodesOrdenats.sort((node1, node2) -> {
            double distancia1 = node1.coordenades().distancia(c);
            double distancia2 = node2.coordenades().distancia(c);
            if (distancia1 < distancia2) {
                return -1;
            } else if (distancia1 > distancia2) {
                return 1;
            } else {
                return node1.id().compareTo(node2.id());
            }
        });
        return nodesOrdenats;
    }

    /**
     * @brief Dibuixa el flux màxim que pot circular per la xarxa x, tenint en compte la capacitat de les canonades.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen de la xarxa.
     * Pre: nodeOrigen pertany a la xarxa x.
     * Post: Dibuixa el flux màxim que pot circular per la xarxa x, tenint en compte la capacitat de les canonades.
     */
    public static void fluxMaxim(Xarxa x, PuntOrigen nodeOrigen) {
        Xarxa xCopia = new Xarxa(x);
        List<PuntTerminal> terminals = terminals(x, nodeOrigen);
        List<PuntOrigen> origens = origens(x, nodeOrigen);
        PuntTerminal superTerminal = new PuntTerminal("superTerminal", new Coordenades(0, 0), 0);
        PuntOrigen superOrigen = new PuntOrigen("superOrigen", new Coordenades(0, 0));
        xCopia.afegir(superTerminal);
        xCopia.afegir(superOrigen);
        for (PuntTerminal terminal : terminals) {
            xCopia.connectarAmbCanonada(terminal, superTerminal, Float.POSITIVE_INFINITY, true);
        }
        for (PuntOrigen origen : origens) {
            xCopia.connectarAmbCanonada(superOrigen, origen, Float.POSITIVE_INFINITY, false);
        }
        fordFulkerson(xCopia, superOrigen, superTerminal);
        xCopia.dibuixar(nodeOrigen, true);
    }

    /**
     * @brief Diu si hi ha un cicle a la component connexa de la xarxa x que conté actual.
     * @param x Xarxa de distribució d'aigua.
     * @param actual Node actual.
     * @param visitats Conjunt de nodes visitats.
     * @param pilaRecursio Conjunt de nodes en la recursió.
     * @return True si hi ha un cicle, false altrament.
     * Pre: actual és un node de la xarxa x, visitats conté els nodes visitats fins ara, pilaRecursio conté els nodes en el camí de la recursió actual.
     * Post: Diu si hi ha un cicle a la component connexa de la xarxa x que conté actual.
     */
    private static boolean dfs(Xarxa x, Node_X actual, Set<Node_X> visitats, Set<Node_X> pilaRecursio) {
        if (pilaRecursio.contains(actual)) {
            return true;
        }
        if (visitats.contains(actual)) {
            return false;
        }

        visitats.add(actual);
        pilaRecursio.add(actual);

        for (Node_X vei : obtenirInferiors(x, actual)) {
            if (dfs(x, vei, visitats, pilaRecursio)) {
                return true;
            }
        }

        pilaRecursio.remove(actual);
        return false;
    }

    /**
     * @brief Retorna un conjunt amb els veïns de node.
     * @param x Xarxa de distribució d'aigua.
     * @param node Node de la xarxa.
     * @return Conjunt de veïns de node.
     * Pre: node és un node de la xarxa x.
     * Post: Retorna un conjunt amb els veïns de node (només sortides perquè la xarxa és dirigida).
     */
    private static Set<Node_X> obtenirVeins(Xarxa x, Node_X node) {
        Set<Node_X> veins = new HashSet<>();
        Iterator<Canonada> canonades = x.sortides(node);
        while (canonades.hasNext()) {
            veins.add(canonades.next().node2());
        }
        canonades = x.entrades(node);
        while (canonades.hasNext()) {
            veins.add(canonades.next().node1());
        }
        return veins;
    }

    /**
     * @brief Propaga la demanda del node actual cap amunt, actualitzant les demandes i els cabals de les canonades.
     * @param x Xarxa de distribució d'aigua.
     * @param actual Node actual.
     * @param demandesNodes Map de demandes de nodes.
     * @param demandesCanonades Map de demandes de canonades.
     * @param cua Cua de nodes.
     * @param encuats Conjunt de nodes encuats.
     * Pre: actual és un node de la xarxa x, demandesNodes conté la demanda de tots els nodes de la xarxa x fins al punt on esteem, cabalsCanonades conté els cabals de totes les canonades de la xarxa x fins al punt on esteem, i cua és una cua de nodes.
     * Post: Propaga la demanda del node actual cap amunt, actualitzant les demandes i els cabals de les canonades.
     */
    private static void propagarDemanda(Xarxa x, Node_X actual, Map<Node_X, Float> demandesNodes, Map<Canonada, Float> demandesCanonades, Queue<Node_X> cua, Set<Node_X> encuats) {
        Map<Node_X, Canonada> superiors = obtenirSuperiors(x, actual);
        eliminarTancats(superiors, demandesNodes, demandesCanonades, cua, encuats);
        float demanda = demandesNodes.get(actual);
        repartirDemanda(demanda, x, superiors, demandesNodes, demandesCanonades, cua, encuats);
    }

    /**
     * @brief Retorna un mapa amb els nodes superiors d'actual com a claus i la capacitat de la canonada que els connecta com a valor.
     * @param x Xarxa de distribució d'aigua.
     * @param actual Node actual.
     * @return Map de nodes superiors i les seves canonades.
     * Pre: actual és un node de la xarxa x.
     * Post: Retorna un mapa amb els nodes superiors d'actual com a claus i la capacitat de la canonada que els connecta com a valor.
     */
    private static Map<Node_X, Canonada> obtenirSuperiors(Xarxa x, Node_X actual) {
        Map<Node_X, Canonada> superiors = new HashMap<>();
        Iterator<Canonada> entrades = x.entrades(actual);
        while (entrades.hasNext()) {
            Canonada canonada = entrades.next();
            superiors.put(canonada.node1(), canonada);
        }
        return superiors;
    }

    /**
     * @brief Retorna un mapa amb els nodes inferiors d'actual com a claus i la capacitat de la canonada que els connecta com a valor.
     * @param x Xarxa de distribució d'aigua.
     * @param actual Node actual.
     * @return Map de nodes inferiors i les seves canonades.
     * Pre: actual és un node de la xarxa x.
     * Post: Retorna un mapa amb els nodes inferiors d'actual com a claus i la capacitat de la canonada que els connecta com a valor.
     */
    private static Map<Node_X, Canonada> inferiors(Xarxa x, Node_X actual) {
        Map<Node_X, Canonada> inferiors = new HashMap<>();
        Iterator<Canonada> sortides = x.sortides(actual);
        while (sortides.hasNext()) {
            Canonada canonada = sortides.next();
            inferiors.put(canonada.node2(), canonada);
        }
        return inferiors;
    }

    /**
     * @brief Elimina els nodes tancats de superiors, actualitzant les demandes i els cabals de les canonades.
     * @param superiors Map de nodes superiors i les seves canonades.
     * @param demandesNodes Map de demandes de nodes.
     * @param cabalsCanonades Map de cabals de canonades.
     * @param cua Cua de nodes.
     * @param encuats Conjunt de nodes encuats.
     * Pre: superiors conté els nodes superiors d'un node actual, demandesNodes conté la demanda de tots els nodes de la xarxa x fins al punt on esteem, cabalsCanonades conté els cabals de totes les canonades de la xarxa x fins al punt on esteem, i cua és una cua de nodes.
     * Post: Elimina els nodes tancats de superiors, actualitzant les demandes i els cabals de les canonades.
     */
    private static void eliminarTancats(Map<Node_X, Canonada> superiors, Map<Node_X, Float> demandesNodes, Map<Canonada, Float> cabalsCanonades, Queue<Node_X> cua, Set<Node_X> encuats) {
        Iterator<Map.Entry<Node_X, Canonada>> it = superiors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Node_X, Canonada> entry = it.next();
            Node_X node = entry.getKey();
            Canonada canonada = entry.getValue();
            if (!node.aixetaOberta()) {
                demandesNodes.put(node, 0f);
                cabalsCanonades.put(canonada, 0f);
                if (!encuats.contains(node)) {
                    cua.add(node);
                    encuats.add(node);
                }
                it.remove();
            }
        }
    }

    /**
     * @brief Reparteix la demanda entre els nodes superiors no tancats, actualitzant les demandes i els cabals de les canonades.
     * @param demanda Demanda actual.
     * @param x Xarxa de distribució d'aigua.
     * @param superiors Map de nodes superiors i les seves canonades.
     * @param demandesNodes Map de demandes de nodes.
     * @param cabalsCanonades Map de cabals de canonades.
     * @param cua Cua de nodes.
     * @param encuats Conjunt de nodes encuats.
     * Pre: superiors conté els nodes superiors d'un node actual, demandesNodes conté la demanda de tots els nodes de la xarxa x fins al punt on esteem, cabalsCanonades conté els cabals de totes les canonades de la xarxa x fins al punt on esteem, i cua és una cua de nodes.
     * Post: Reparteix la demanda entre els nodes superiors no tancats, actualitzant les demandes i els cabals de les canonades.
     */
    private static void repartirDemanda(float demanda, Xarxa x, Map<Node_X, Canonada> superiors, Map<Node_X, Float> demandesNodes, Map<Canonada, Float> cabalsCanonades, Queue<Node_X> cua, Set<Node_X> encuats) {
        float totalCapacitat = 0;
        for (Canonada canonada : superiors.values()) {
            totalCapacitat += canonada.capacitat();
        }
        for (Map.Entry<Node_X, Canonada> entry : superiors.entrySet()) {
            float capacitat = entry.getValue().capacitat();
            float cabal = (demanda * capacitat) / totalCapacitat;
            cabalsCanonades.put(entry.getValue(), cabal);
            Node_X superior = entry.getKey();
            if (!encuats.contains(superior)) {
                cua.add(superior);
                encuats.add(superior);
            }
            List<Canonada> sortides = obtenirSortides(x, entry.getKey());
            boolean sabemSortides = true;
            float demandaNode = 0;
            for (Canonada sortida : sortides) {
                if (!cabalsCanonades.containsKey(sortida)) {
                    sabemSortides = false;
                    break;
                }
                demandaNode += cabalsCanonades.getOrDefault(sortida, 0.0f);
            }
            if (sabemSortides) {
                demandesNodes.put(entry.getKey(), demandaNode);
            }
        }
    }

    /**
     * @brief Reparteix el cabal entre els nodes inferiors, actualitzant els cabals de les canonades.
     * @param actual Node actual.
     * @param x Xarxa de distribució d'aigua.
     * @param inferiors Map de nodes inferiors i les seves canonades.
     * @param cabalsNodes Map de cabals de nodes.
     * @param cabalsCanonades Map de cabals de canonades.
     * @param cuaCabals Cua de nodes.
     * @param encuatsCabal Conjunt de nodes encuats.
     * @param demandesCanonades Map de demandes de canonades.
     * Pre: actual és un node de la xarxa x, inferiors conté els nodes inferiors d'un node actual, cabalsNodes conté els cabals de tots els nodes de la xarxa x fins al punt on esteem, cabalsCanonades conté els cabals de totes les canonades de la xarxa x fins al punt on esteem, cuaCabals és una cua de nodes, encuatsCabal és un conjunt de nodes i demandesCanonades conté les demandes de tots els nodes de la xarxa x fins al punt on esteem.
     * Post: Reparteix el cabal entre els nodes inferiors, actualitzant els cabals de les canonades.
     */
    private static void repartirCabal(Node_X actual, Xarxa x, Map<Node_X, Canonada> inferiors, Map<Node_X, Float> cabalsNodes, Map<Canonada, Float> cabalsCanonades, Queue<Node_X> cuaCabals, Set<Node_X> encuatsCabal, Map<Canonada, Float> demandesCanonades) {
        float totalDemanda = 0;
        for (Canonada canonada : obtenirSortides(x, actual)) {
            totalDemanda += demandesCanonades.get(canonada);
        }
        for(Map.Entry<Node_X, Canonada> entry : inferiors.entrySet()) {
            float demanda = demandesCanonades.get(entry.getValue());
            float cabal = (cabalsNodes.get(actual) * demanda) / totalDemanda;
            cabalsCanonades.put(entry.getValue(), cabal);
            Node_X inferior = entry.getKey();
            if(!encuatsCabal.contains(inferior)) {
                cuaCabals.add(inferior);
                encuatsCabal.add(inferior);
            }
            List<Canonada> entrades = obtenirEntrades(x, entry.getKey());
            boolean sabemEntrades = true;
            float cabalNode = 0;
            for(Canonada entrada : entrades) {
                if(!cabalsCanonades.containsKey(entrada)) {
                    sabemEntrades = false;
                    break;
                }
                cabalNode += cabalsCanonades.getOrDefault(entrada, 0.0f);
            }
            if(sabemEntrades) {
                cabalsNodes.put(entry.getKey(), cabalNode);
            }
        }
    }

    /**
     * @brief Retorna una llista amb les canonades de sortida de node.
     * @param x Xarxa de distribució d'aigua.
     * @param node Node de la xarxa.
     * @return Llista de canonades de sortida.
     * Pre: node és un node de la xarxa x.
     * Post: Retorna una llista amb les canonades de sortida de node.
     */
    private static List<Canonada> obtenirSortides(Xarxa x, Node_X node) {
        List<Canonada> sortides = new ArrayList<>();
        Iterator<Canonada> sortidesIt = x.sortides(node);
        while (sortidesIt.hasNext()) {
            sortides.add(sortidesIt.next());
        }
        return sortides;
    }

    /**
     * @brief Retorna una llista amb les canonades d'entrada de node.
     * @param x Xarxa de distribució d'aigua.
     * @param node Node de la xarxa.
     * @return Llista de canonades d'entrada.
     * Pre: node és un node de la xarxa x.
     * Post: Retorna una llista amb les canonades d'entrada de node.
     */
    private static List<Canonada> obtenirEntrades(Xarxa x, Node_X node) {
        List<Canonada> entrades = new ArrayList<>();
        Iterator<Canonada> entradesIt = x.entrades(node);
        while (entradesIt.hasNext()) {
            entrades.add(entradesIt.next());
        }
        return entrades;
    }

    /**
     * @brief Aplica l'algorisme de Ford-Fulkerson per trobar el flux màxim.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen de la xarxa.
     * @param superTerminal Node superTerminal.
     * Pre: nodeOrigen i superTerminal pertanyen a la xarxa x.
     * Post: Aplica l'algorisme de Ford-Fulkerson per trobar el flux màxim.
     */
    private static void fordFulkerson(Xarxa x, PuntOrigen nodeOrigen, PuntTerminal superTerminal) {
        float fluxTotal = 0;

        List<Node_X> cami = trobarCami(x, nodeOrigen, superTerminal);
        while (!cami.isEmpty()) {
            float fluxMaxim = fluxMaximCami(x, cami);
            System.out.println("Flux màxim del camí: " + fluxMaxim);

            fluxTotal += fluxMaxim;

            actualitzarCapacitats(x, cami, fluxMaxim);

            cami = trobarCami(x, nodeOrigen, superTerminal);
        }

        System.out.println("Flux màxim total: " + fluxTotal);
    }

    /**
     * @brief Retorna una llista amb els nodes del camí de nodeOrigen a superTerminal, o una llista buida si no hi ha cap camí.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen de la xarxa.
     * @param superTerminal Node superTerminal.
     * @return Llista de nodes del camí.
     * Pre: nodeOrigen i superTerminal pertanyen a la xarxa x.
     * Post: Retorna una llista amb els nodes del camí de nodeOrigen a superTerminal, o una llista buida si no hi ha cap camí.
     */
    private static List<Node_X> trobarCami(Xarxa x, PuntOrigen nodeOrigen, PuntTerminal superTerminal) {
        Map<Node_X, Node_X> predecessors = new HashMap<>();
        Queue<Node_X> cua = new LinkedList<>();
        cua.add(nodeOrigen);
        predecessors.put(nodeOrigen, null);

        while (!cua.isEmpty()) {
            Node_X actual = cua.poll();

            if (actual.equals(superTerminal)) {
                List<Node_X> cami = new LinkedList<>();
                for (Node_X node = superTerminal; node != null; node = predecessors.get(node)) {
                    cami.add(0, node);
                }
                return cami;
            }

            for (Node_X vei : obtenirVeins(x, actual)) {
                if (!predecessors.containsKey(vei) && vei.aixetaOberta() && capacitatCanonada(x, actual, vei) > 0) {
                    predecessors.put(vei, actual);
                    cua.add(vei);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * @brief Retorna el flux màxim del camí.
     * @param x Xarxa de distribució d'aigua.
     * @param cami Llista de nodes del camí.
     * @return Flux màxim del camí.
     * Pre: cami conté els nodes d'un camí de la xarxa x.
     * Post: Retorna el flux màxim del camí.
     */
    private static float fluxMaximCami(Xarxa x, List<Node_X> cami) {
        float fluxMaxim = Float.POSITIVE_INFINITY;
        for (int i = 0; i < cami.size() - 1; i++) {
            float capacitat = capacitatCanonada(x, cami.get(i), cami.get(i + 1));
            if (capacitat <= 0) {
                throw new IllegalArgumentException("Capacitat " + capacitat + " no valida per al camí");
            }
            if (capacitat < fluxMaxim) {
                fluxMaxim = capacitat;
            }
        }
        return fluxMaxim;
    }

    /**
     * @brief Actualitza les capacitats de les canonades del camí.
     * @param x Xarxa de distribució d'aigua.
     * @param cami Llista de nodes del camí.
     * @param fluxMaxim Flux màxim del camí.
     * Pre: cami conté els nodes d'un camí de la xarxa x i fluxMaxim és el flux màxim del camí.
     * Post: Actualitza les capacitats de les canonades del camí.
     */
    private static void actualitzarCapacitats(Xarxa x, List<Node_X> cami, float fluxMaxim) {
        for (int i = 0; i < cami.size() - 1; i++) {
            Canonada canonada = trobarCanonada(x, cami.get(i), cami.get(i + 1));
            if (canonada != null) {
                float novaCapacitat = canonada.capacitat() - fluxMaxim;
                canonada.actualitzarCapacitat(novaCapacitat);
            } else {
                System.out.println("Error: No s'ha trobat la canonada que connecta " + cami.get(i).id() + " amb " + cami.get(i + 1).id());
            }
        }
    }

    /**
     * @brief Retorna la canonada que connecta node1 i node2.
     * @param x Xarxa de distribució d'aigua.
     * @param node1 Node origen.
     * @param node2 Node destí.
     * @return Canonada que connecta node1 i node2.
     * Pre: node1 i node2 són dos nodes de la xarxa x connectats per una canonada.
     * Post: Retorna la canonada que connecta node1 i node2.
     */
    private static Canonada trobarCanonada(Xarxa x, Node_X node1, Node_X node2) {
        Iterator<Canonada> sortides = x.sortides(node1);
        while (sortides.hasNext()) {
            Canonada canonada = sortides.next();
            if (canonada.node2() == node2) {
                return canonada;
            }
        }
        return null;
    }

    /**
     * @brief Retorna la capacitat de la canonada que connecta node1 i node2.
     * @param x Xarxa de distribució d'aigua.
     * @param node1 Node origen.
     * @param node2 Node destí.
     * @return Capacitat de la canonada.
     * Pre: node1 i node2 són dos nodes de la xarxa x connectats per una canonada.
     * Post: Retorna la capacitat de la canonada que connecta node1 i node2.
     */
    private static float capacitatCanonada(Xarxa x, Node_X node1, Node_X node2) {
        Iterator<Canonada> sortides = x.sortides(node1);
        while (sortides.hasNext()) {
            Canonada canonada = sortides.next();
            if (canonada.node2() == node2) {
                return canonada.capacitat();
            }
        }
        return 0;
    }

    /**
     * @brief Omple el mapa incoherents amb els nodes incoherents de la xarxa x.
     * @param x Xarxa de distribució d'aigua.
     * @param aiguaArriba Map que indica si arriba aigua als terminals.
     * @param incoherents Map de nodes incoherents.
     * Pre: Tots els terminals d'aiguaArriba pertanyen a la xarxa x, aiguaArriba.get(t) indica si arriba aigua a t, i la xarxa x té forma d'arbre.
     * Post: Omple el mapa incoherents amb els nodes incoherents de la xarxa x.
     */
    private static void mirarIncoherents(Xarxa x, Map<PuntTerminal, Boolean> aiguaArriba, Map<Node_X, Boolean> incoherents) {
        for (Map.Entry<PuntTerminal, Boolean> entry : aiguaArriba.entrySet()) {
            boolean aixetaTancada = false;
            Node_X node = entry.getKey();
            while (node != null && !aixetaTancada) {
                if (!node.aixetaOberta()) {
                    aixetaTancada = true;
                }
                node = superior(x, node);
            }
            if (aixetaTancada && entry.getValue()) {
                incoherents.put(entry.getKey(), true);
            } else if (!aixetaTancada && !entry.getValue()) {
                incoherents.put(entry.getKey(), true);
            } else {
                incoherents.put(entry.getKey(), false);
            }
        }
    }

    /**
     * @brief Retorna el node superior d'actual.
     * @param x Xarxa de distribució d'aigua.
     * @param actual Node actual.
     * @return Node superior.
     * Pre: la xarxa x te forma d'arbre i actual és un node de la xarxa x.
     * Post: Retorna el node superior d'actual.
     */
    private static Node_X superior(Xarxa x, Node_X actual) {
        Node_X superior = null;
        Iterator<Canonada> entrades = x.entrades(actual);
        while (entrades.hasNext()) {
            superior = entrades.next().node1();
        }
        return superior;
    }

    /**
     * @brief Retorna una llista amb els nodes inferiors d'actual.
     * @param x Xarxa de distribució d'aigua.
     * @param actual Node actual.
     * @return Llista de nodes inferiors.
     * Pre: actual és un node de la xarxa x.
     * Post: Retorna una llista amb els nodes inferiors d'actual.
     */
    private static List<Node_X> obtenirInferiors(Xarxa x, Node_X actual) {
        List<Node_X> inferiors = new ArrayList<>();
        Iterator<Canonada> sortides = x.sortides(actual);
        while (sortides.hasNext()) {
            inferiors.add(sortides.next().node2());
        }
        return inferiors;
    }

    /**
     * @brief Afegeix a nodesTancar els nodes que cal tancar perquè la xarxa sigui coherent.
     * @param x Xarxa de distribució d'aigua.
     * @param node Node actual.
     * @param incoherents Map de nodes incoherents.
     * @param nodesTancar Conjunt de nodes a tancar.
     * Pre: node és un node de la xarxa x, incoherents conté els nodes incoherents de la xarxa x, nodesTancar és un conjunt buit.
     * Post: Afegeix a nodesTancar els nodes que cal tancar perquè la xarxa sigui coherent.
     */
    private static void tancarNode(Xarxa x, Node_X node, Map<Node_X, Boolean> incoherents, Set<Node_X> nodesTancar){
        if(node == null)
            return;

        boolean[] coherencia = new boolean[2];

        if(obtenirInferiors(x, node).isEmpty()) {
            tancarNode(x, superior(x, node), incoherents, nodesTancar);
        }
        else {
            teCoherentsiIncoherents(x, node, incoherents, coherencia);
            if(coherencia[0] && coherencia[1])
                nodesTancar.add(node);
            else {
                tancarNode(x, superior(x, node), incoherents, nodesTancar);
            }
        }
    }

    /**
     * @brief Diu si el node té tan inferiors coherents com incoherents.
     * @param x Xarxa de distribució d'aigua.
     * @param node Node actual.
     * @param incoherents Map de nodes incoherents.
     * @param coherencia Array de dos booleans per guardar si el node té inferiors coherents i incoherents.
     * Pre: ---
     * Post: true si el node té tan inferiors coherents com incoherents, false en cas contrari.
     */
    private static void teCoherentsiIncoherents(Xarxa x, Node_X node, Map<Node_X, Boolean> incoherents, boolean[] coherencia) {
        List<Node_X> inferiors = obtenirInferiors(x, node);
        if(inferiors.isEmpty())
            return;
        for(Node_X inferior : inferiors) {
            if(incoherents.containsKey(inferior)) {
                if (incoherents.get(inferior)) {
                    coherencia[1] = true;
                }
                else {
                    coherencia[0] = true;
                }
                if(coherencia[0] && coherencia[1]) {
                    break;
                }
            }
            else {
                teCoherentsiIncoherents(x, inferior, incoherents, coherencia);
            }
        }
    }

    /**
     * @brief Omple la llista terminals amb tots els terminals de la xarxa x.
     * @param x Xarxa de distribució d'aigua.
     * @param terminals Llista de terminals.
     * Pre: ---
     * Post: Omple la llista terminals amb tots els terminals de la xarxa x.
     */
    private static void obtenirTerminals(Xarxa x, List<PuntTerminal> terminals) {
        for (Node_X node : x.nodes()) {
            if (node instanceof PuntTerminal) {
                terminals.add((PuntTerminal) node);
            }
        }
    }

    /**
     * @brief Retorna una llista amb els nodes d'origen de la component connexa d'origen.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen.
     * @return Llista de nodes d'origen.
     * Pre: nodeOrigen pertany a la xarxa x.
     * Post: Retorna una llista amb els nodes d'origen de la component connexa d'origen.
     */
    private static List<PuntOrigen> obtenirOrigens(Xarxa x, Node_X nodeOrigen) {
        List<PuntOrigen> origens = new ArrayList<>();
        Set<Node_X> visitats = new HashSet<>();
        Queue<Node_X> cua = new LinkedList<>();
        cua.add(nodeOrigen);
        while (!cua.isEmpty()) {
            Node_X actual = cua.poll();
            visitats.add(actual);
            if (actual instanceof PuntOrigen) {
                origens.add((PuntOrigen) actual);
            }
            for (Node_X vei : obtenirVeins(x, actual)) {
                if (!visitats.contains(vei)) {
                    cua.add(vei);
                }
            }
        }
        return origens;
    }

    /**
     * @brief Omple el mapa demandesNodes amb les demandes dels terminals.
     * @param terminals Llista de terminals.
     * @param demandesNodes Map de demandes de nodes.
     * Pre: ---
     * Post: Omple el mapa demandesNodes amb les demandes dels terminals.
     */
    private static void guardarDemandes(List<PuntTerminal> terminals, Map<Node_X, Float> demandesNodes) {
        for (PuntTerminal terminal : terminals) {
            demandesNodes.put(terminal, terminal.demanda_actual());
        }
    }

    /**
     * @brief Omple el mapa cabalsNodes amb els cabals dels nodes d'origen.
     * @param origens Llista de nodes d'origen.
     * @param cabalsNodes Map de cabals de nodes.
     * Pre: ---
     * Post: Omple el mapa cabalsNodes amb els cabals dels nodes d'origen.
     */
    private static void guardarCabals(List<PuntOrigen> origens, Map<Node_X, Float> cabalsNodes) {
        for (PuntOrigen origen : origens) {
            cabalsNodes.put(origen, origen.cabal());
        }
    }

    /**
     * @brief Retorna una llista amb els terminals de la component connexa d'origen.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen.
     * @return Llista de terminals.
     * Pre: nodeOrigen pertany a la xarxa x.
     * Post: Retorna una llista amb els terminals de la component connexa d'origen.
     */
    private static List<PuntTerminal> terminals(Xarxa x, PuntOrigen nodeOrigen) {
        List<PuntTerminal> terminals = new ArrayList<>();
        Set<Node_X> visitats = new HashSet<>();
        Queue<Node_X> cua = new LinkedList<>();
        cua.add(nodeOrigen);
        while (!cua.isEmpty()) {
            Node_X actual = cua.poll();
            visitats.add(actual);
            if (actual instanceof PuntTerminal) {
                if(!terminals.contains((PuntTerminal) actual))
                    terminals.add((PuntTerminal) actual);
            }
            for (Node_X vei : obtenirVeins(x, actual)) {
                if (!visitats.contains(vei)) {
                    cua.add(vei);
                }
            }
        }
        return terminals;
    }

    /**
     * @brief Retorna una llista amb els nodes d'origen de la component connexa d'origen.
     * @param x Xarxa de distribució d'aigua.
     * @param nodeOrigen Node origen.
     * @return Llista de nodes d'origen.
     * Pre: nodeOrigen pertany a la xarxa x.
     * Post: Retorna una llista amb els nodes d'origen de la component connexa d'origen.
     */
    private static List<PuntOrigen> origens(Xarxa x, PuntOrigen nodeOrigen) {
        List<PuntOrigen> origens = new ArrayList<>();
        Set<Node_X> visitats = new HashSet<>();
        Queue<Node_X> cua = new LinkedList<>();
        cua.add(nodeOrigen);
        while (!cua.isEmpty()) {
            Node_X actual = cua.poll();
            visitats.add(actual);
            if (actual instanceof PuntOrigen) {
                if(!origens.contains((PuntOrigen) actual))
                    origens.add((PuntOrigen) actual);
            }
            for (Node_X vei : obtenirVeins(x, actual)) {
                if (!visitats.contains(vei)) {
                    cua.add(vei);
                }
            }
        }
        return origens;
    }
}
