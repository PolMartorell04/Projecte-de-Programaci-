//Pol Martorell Herrera
/**
 * @file Xarxa.java
 * @brief Implementació d'una xarxa de distribució d'aigua utilitzant un graf dirigit
 */

import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

/**
 * @class Xarxa
 * @brief Classe que representa una xarxa de distribució d'aigua, que no necessàriament és connex.
 */
public class Xarxa {

    private Graph grafXarxa; ///< Graf que representa la xarxa

    /**
     * @brief Constructor per defecte
     * @pre ---
     * @post Crea una xarxa de distribució d'aigua buida
     */
    public Xarxa() {
        grafXarxa = new SingleGraph("Xarxa");
    }

    /**
     * @brief Constructor de còpia
     * @pre ---
     * @post Crea una còpia de la xarxa x
     * @param x Xarxa a copiar
     */
    public Xarxa(Xarxa x) {
        this.grafXarxa = new SingleGraph("Copia xarxa");
        for (Node n : x.grafXarxa) {
            Node nou_node = this.grafXarxa.addNode(n.getId());
            Object n_x = n.getAttribute("node");
            if (n_x instanceof PuntOrigen) {
                PuntOrigen po = (PuntOrigen) n_x;
                nou_node.setAttribute("node", new PuntOrigen(po.id(), po.coordenades()));
            }
            else if (n_x instanceof PuntTerminal) {
                PuntTerminal pt = (PuntTerminal) n_x;
                nou_node.setAttribute("node", new PuntTerminal(pt.id(), pt.coordenades(), pt.demanda()));
            }
            else if (n_x instanceof Connexio) {
                Connexio c = (Connexio) n_x;
                nou_node.setAttribute("node", new Connexio(c.id(), c.coordenades()));
            }
            nou_node.setAttribute("ui.label", n.getAttribute("ui.label"));
            nou_node.setAttribute("ui.style", n.getAttribute("ui.style"));
        }

        for (Edge e : x.obtenirArestes()) {
            Node node1 = e.getNode0();
            Node node2 = e.getNode1();
            Canonada c = (Canonada) e.getAttribute("canonada");
            Edge aresta = grafXarxa.addEdge(node1.getId() + "-" + node2.getId(), node1.getId(), node2.getId(), true);
            aresta.setAttribute("canonada", c);
            aresta.setAttribute("ui.style", "size: 3px; text-size: 18px;");
            aresta.setAttribute("ui.label", ((Canonada) aresta.getAttribute("canonada")).cabal() + "/" + ((Canonada) aresta.getAttribute("canonada")).capacitat());
        }
    }

    /**
     * @brief Retorna el node de la xarxa amb identificador id
     * @pre ---
     * @post Retorna el node de la xarxa amb identificador id
     * @param id Identificador del node
     * @return Node_X Node amb l'identificador especificat
     * @throws NoSuchElementException Si no existeix cap node amb identificador id
     */
    public Node_X node(String id) {
        if (grafXarxa.getNode(id) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: el node no pertany a la xarxa.");
        }

        Node n = grafXarxa.getNode(id);
        Object n_x = n.getAttribute("node");
        if (n_x instanceof Node_X)
            return (Node_X) n_x;
        else return null;
    }

    /**
     * @brief Retorna la canonada de la xarxa amb identificador id
     * @pre ---
     * @post Retorna la canonada de la xarxa amb identificador id
     * @param id Identificador de la canonada
     * @return Canonada Canonada amb l'identificador especificat
     * @throws NoSuchElementException Si no existeix cap canonada amb identificador id
     */
    public Canonada canonada(String id) {
        if (grafXarxa.getEdge(id) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: la canonada no pertany a la xarxa.");
        }

        Edge e = grafXarxa.getEdge(id);
        Object can = e.getAttribute("canonada");
        if (can instanceof Canonada)
            return (Canonada) can;
        else return null;
    }

    /**
     * @brief Retorna un iterador que permet recórrer totes les canonades que surten del node
     * @pre node pertany a la xarxa
     * @post Retorna un iterador que permet recórrer totes les canonades que surten del node
     * @param node Node del qual es volen obtenir les sortides
     * @return Iterator<Canonada> Iterador de les canonades que surten del node
     */
    public Iterator<Canonada> sortides(Node_X node) {
        List<Canonada> canonades = new ArrayList<>();
        Node n = grafXarxa.getNode(node.id());
        String aux;

        if (n != null) {
            for (Edge e : n) {
                aux = e.getSourceNode().getId();
                if (aux.equals(node.id())) {
                    Object can = e.getAttribute("canonada");
                    if (can instanceof Canonada)
                        canonades.add((Canonada) can);
                }
            }
        }
        return canonades.iterator();
    }

    /**
     * @brief Retorna un iterador que permet recórrer totes les canonades que entren al node
     * @pre node pertany a la xarxa
     * @post Retorna un iterador que permet recórrer totes les canonades que entren al node
     * @param node Node del qual es volen obtenir les entrades
     * @return Iterator<Canonada> Iterador de les canonades que entren al node
     */
    public Iterator<Canonada> entrades(Node_X node) {
        List<Canonada> canonades = new ArrayList<>();
        Node n = grafXarxa.getNode(node.id());
        String aux;

        if (n != null) {
            for (Edge e : n) {
                aux = e.getTargetNode().getId();
                if (aux.equals(node.id())) {
                    Object can = e.getAttribute("canonada");
                    if (can instanceof Canonada)
                        canonades.add((Canonada) can);
                }
            }
        }
        return canonades.iterator();
    }

    /**
     * @brief Afegeix un PuntOrigen a la xarxa
     * @pre No existeix cap node amb el mateix id que nodeOrigen a la xarxa
     * @post S'ha afegit nodeOrigen a la xarxa
     * @param nodeOrigen PuntOrigen a afegir
     * @throws IllegalArgumentException Si ja existeix un node amb aquest id
     */
    public void afegir(PuntOrigen nodeOrigen) {
        if (grafXarxa.getNode(nodeOrigen.id()) != null) {
            throw new IllegalArgumentException("Error de configuració a l'opció: ja existeix a les xarxes una aixeta amb nom " + nodeOrigen.id() + ".");
        }

        Node n = grafXarxa.addNode(nodeOrigen.id());
        n.setAttribute("node", nodeOrigen);
        n.setAttribute("ui.style", "fill-color: green; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");

        String coord_dibuix = String.format("%.6f, %.6f", nodeOrigen.coordenades().longitud(), nodeOrigen.coordenades().latitud());
        n.setAttribute("ui.label", nodeOrigen.id() + " (" + coord_dibuix + ")");
    }

    /**
     * @brief Afegeix un PuntTerminal a la xarxa
     * @pre No existeix cap node amb el mateix id que nodeTerminal a la xarxa
     * @post S'ha afegit nodeTerminal a la xarxa
     * @param nodeTerminal PuntTerminal a afegir
     * @throws IllegalArgumentException Si ja existeix un node amb aquest id o si la demanda d'aigua és negativa
     */
    public void afegir(PuntTerminal nodeTerminal) {
        if (grafXarxa.getNode(nodeTerminal.id()) != null) {
            throw new IllegalArgumentException("Error de configuració a l'opció: ja existeix a les xarxes una aixeta amb nom " + nodeTerminal.id() + ".");
        }

        if(nodeTerminal.demanda()<0) {
            throw new IllegalArgumentException("Error de configuració a l'opció: la demanda d'aigua no pot ser negativa.");
        }

        Node n = grafXarxa.addNode(nodeTerminal.id());
        n.setAttribute("node", nodeTerminal);
        n.setAttribute("ui.style", "fill-color: orange; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");
    }

    /**
     * @brief Afegeix una Connexio a la xarxa
     * @pre No existeix cap node amb el mateix id que nodeConnexio a la xarxa
     * @post S'ha afegit nodeConnexio a la xarxa
     * @param nodeConnexio Connexio a afegir
     * @throws IllegalArgumentException Si ja existeix un node amb aquest id
     */
    public void afegir(Connexio nodeConnexio) {
        if (grafXarxa.getNode(nodeConnexio.id()) != null) {
            throw new IllegalArgumentException("Error de configuració a l'opció: ja existeix a les xarxes una aixeta amb nom " + nodeConnexio.id() + ".");
        }

        Node n = grafXarxa.addNode(nodeConnexio.id());
        n.setAttribute("node", nodeConnexio);
        n.setAttribute("ui.style", "fill-color: red; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");

        String coord_dibuix = String.format("%.6f, %.6f", nodeConnexio.coordenades().longitud(), nodeConnexio.coordenades().latitud());
        n.setAttribute("ui.label", nodeConnexio.id() + " (" + coord_dibuix + ")");
    }

    /**
     * @brief Connecta dos nodes amb una canonada
     * @pre node1 i node2 pertanyen a la xarxa, no estan connectats, i node1 no és un node terminal, excepcio és true si s'ha de fer flux màxim (per crear un terminal superior), fals en c.c.
     * @post S'han connectat els nodes amb una canonada de capacitat c, amb sentit de l'aigua de node1 a node2
     * @param node1 Node origen
     * @param node2 Node destí
     * @param c Capacitat de la canonada
     * @param excepcio Boolean que indica si s'ha de fer flux màxim
     * @throws NoSuchElementException Si node1 o node2 no pertanyen a la xarxa
     * @throws IllegalArgumentException Si els nodes ja estan connectats o si node1 és un node terminal o si la capacitat de la canonada és negativa
     */
    public void connectarAmbCanonada(Node_X node1, Node_X node2, float c, boolean excepcio) {
        if (grafXarxa.getNode(node1.id()) == null || grafXarxa.getNode(node2.id()) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: un dels nodes no pertany a la xarxa.");
        }

        if (node1 instanceof PuntTerminal && !excepcio) {
            throw new IllegalArgumentException("Error de configuració a l'opció: el node1 és un node terminal i no pot iniciar connexions.");
        }

        if (grafXarxa.getEdge(node1.id() + "-" + node2.id()) != null) {
            throw new IllegalArgumentException("Error de configuració a l'opció: els nodes " + node1.id() + " i " + node2.id() + " ja estan connectats.");
        }

        if(c<0) {
            throw new IllegalArgumentException("Error de configuració a l'opció: la capacitat de la canonada no pot ser negativa.");
        }

        if (node2 instanceof PuntOrigen) {
            PuntOrigen po = (PuntOrigen) node2;
            String id = po.id();
            Coordenades coordenades = po.coordenades();

            List<Edge> entrades = new ArrayList<>();
            List<Edge> sortides = new ArrayList<>();
            Iterator<Canonada> it = sortides(node2);
            while (it.hasNext())
                sortides.add(grafXarxa.getEdge(node2.id() + "-" + it.next().node2().id()));
            it = entrades(node2);
            while (it.hasNext())
                entrades.add(grafXarxa.getEdge(it.next().node1().id() + "-" + node2.id()));

            grafXarxa.removeNode(id);

            Connexio co = new Connexio(id, coordenades);
            Node n = grafXarxa.addNode(id);
            n.setAttribute("node", co);
            n.setAttribute("ui.style", "fill-color: red; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");
            n.setAttribute("ui.label", co.id());
            String coord_dibuix = String.format("%.6f, %.6f", co.coordenades().longitud(), co.coordenades().latitud());
            n.setAttribute("ui.label", co.id() + " (" + coord_dibuix + ")");
            if(!co.aixetaOberta()) n.setAttribute("ui.style", "fill-color: gray; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");

            for (Edge e : entrades) {
                Node n1 = e.getSourceNode();
                Canonada canonada = (Canonada) e.getAttribute("canonada");
                Edge newEdge = grafXarxa.addEdge(n1.getId() + "-" + id, n1.getId(), id, true);
                newEdge.setAttribute("canonada", canonada);
                newEdge.setAttribute("ui.style", e.getAttribute("ui.style"));
                newEdge.setAttribute("ui.label", e.getAttribute("ui.label"));
            }
            for (Edge e : sortides) {
                Node n2 = e.getTargetNode();
                Canonada canonada = (Canonada) e.getAttribute("canonada");
                Edge newEdge = grafXarxa.addEdge(id + "-" + n2.getId(), id, n2.getId(), true);
                newEdge.setAttribute("canonada", canonada);
                newEdge.setAttribute("ui.style", e.getAttribute("ui.style"));
                newEdge.setAttribute("ui.label", e.getAttribute("ui.label"));
            }

            node2 = co;
        }

        if(node1 instanceof PuntTerminal) {
            PuntTerminal pt = (PuntTerminal) node1;
            String id = pt.id();
            Coordenades coordenades = pt.coordenades();

            List<Edge> entrades = new ArrayList<>();
            List<Edge> sortides = new ArrayList<>();
            Iterator<Canonada> it = sortides(node1);
            while(it.hasNext())
                sortides.add(grafXarxa.getEdge(node1.id() + "-" + it.next().node2().id()));
            it = entrades(node1);
            while(it.hasNext())
                entrades.add(grafXarxa.getEdge(it.next().node1().id() + "-" + node1.id()));

            grafXarxa.removeNode(id);

            Connexio co = new Connexio(id, coordenades);
            Node n = grafXarxa.addNode(id);
            n.setAttribute("node", co);
            n.setAttribute("ui.style", "fill-color: red; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");
            n.setAttribute("ui.label", co.id());
            String coord_dibuix = String.format("%.6f, %.6f", co.coordenades().longitud(), co.coordenades().latitud());
            n.setAttribute("ui.label", co.id() + " (" + coord_dibuix + ")");
            if(!co.aixetaOberta()) n.setAttribute("ui.style", "fill-color: gray; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");

            for (Edge e : entrades) {
                Node n1 = e.getSourceNode();
                Canonada canonada = (Canonada) e.getAttribute("canonada");
                Edge newEdge = grafXarxa.addEdge(n1.getId() + "-" + id, n1.getId(), id, true);
                newEdge.setAttribute("canonada", canonada);
                newEdge.setAttribute("ui.style", e.getAttribute("ui.style"));
                newEdge.setAttribute("ui.label", e.getAttribute("ui.label"));
            }
            for (Edge e : sortides) {
                Node n2 = e.getTargetNode();
                Canonada canonada = (Canonada) e.getAttribute("canonada");
                Edge newEdge = grafXarxa.addEdge(id + "-" + n2.getId(), id, n2.getId(), true);
                newEdge.setAttribute("canonada", canonada);
                newEdge.setAttribute("ui.style", e.getAttribute("ui.style"));
                newEdge.setAttribute("ui.label", e.getAttribute("ui.label"));
            }

            node1 = co;
        }

        Canonada can = new Canonada(node1, node2, c);
        Edge e = grafXarxa.addEdge(node1.id() + "-" + node2.id(), node1.id(), node2.id(), true);
        e.setAttribute("canonada", can);
        e.setAttribute("ui.style", "size: 2px; text-size: 12px;");
    }

    /**
     * @brief Abona un client a un PuntTerminal
     * @pre nodeTerminal pertany a la xarxa
     * @post El client identificat amb idClient queda abonat al node terminal, i diu si ja ho estava
     * @param idClient Identificador del client
     * @param nodeTerminal PuntTerminal al qual abonar el client
     * @throws NoSuchElementException Si nodeTerminal no pertany a la xarxa
     */
    public void abonar(String idClient, PuntTerminal nodeTerminal) {
        if (grafXarxa.getNode(nodeTerminal.id()) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: el node terminal no pertany a la xarxa");
        }
        else {
            Node n = grafXarxa.getNode(nodeTerminal.id());
            Object n_x = n.getAttribute("node");
            if (n_x instanceof PuntTerminal) {
                PuntTerminal pt = (PuntTerminal) n_x;
                if (!pt.es_abonat(idClient)) pt.afegir_abonat(idClient);
            }
        }
    }

    /**
     * @brief Retorna el cabal actual al punt d'abastament del client identificat amb idClient
     * @pre Existeix un client identificat amb idClient a la xarxa
     * @post Retorna el cabal actual al punt d'abastament del client identificat amb idClient
     * @param idClient Identificador del client
     * @return float Cabal actual al punt d'abastament del client
     */
    public float cabalAbonat(String idClient) {
        for(Node n : grafXarxa) {
            Object n_x = n.getAttribute("node");
            if (n_x instanceof PuntTerminal) {
                PuntTerminal pt = (PuntTerminal) n_x;
                if (pt.es_abonat(idClient)) {
                    return cabal(pt);
                }
            }
        }
        return 0;
    }

    /**
     * @brief Obre l'aixeta del node
     * @pre node pertany a la xarxa
     * @post L'aixeta del node està oberta; diu si s'ha obert (l'aixeta estava tancada)
     * @param node Node del qual es vol obrir l'aixeta
     * @return boolean True si s'ha obert l'aixeta, fals en cas contrari
     * @throws NoSuchElementException Si node no pertany a la xarxa
     */
    public boolean obrirAixeta(Node_X node) {
        boolean obert = false;
        if (grafXarxa.getNode(node.id()) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: el node no pertany a la xarxa");
        }
        else {
            if (!node.aixetaOberta()) {
                node.obrirAixeta();
                obert = true;
            }
        }
        return obert;
    }

    /**
     * @brief Tanca l'aixeta del node
     * @pre node pertany a la xarxa
     * @post L'aixeta del node està tancada; diu si s'ha tancat (l'aixeta estava oberta)
     * @param node Node del qual es vol tancar l'aixeta
     * @return boolean True si s'ha tancat l'aixeta, fals en cas contrari
     * @throws NoSuchElementException Si node no pertany a la xarxa
     */
    public boolean tancarAixeta(Node_X node) {
        boolean tancat = false;
        if (grafXarxa.getNode(node.id()) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: el node no pertany a la xarxa");
        }
        else {
            if (node.aixetaOberta()) {
                node.tancarAixeta();
                tancat = true;
            }
        }
        return tancat;
    }

    /**
     * @brief Estableix el cabal d'un PuntOrigen
     * @pre nodeOrigen pertany a la xarxa i cabal >= 0
     * @post El cabal de nodeOrigen és cabal
     * @param nodeOrigen PuntOrigen del qual es vol establir el cabal
     * @param cabal Cabal a establir
     * @throws NoSuchElementException Si nodeOrigen no pertany a la xarxa
     * @throws IllegalArgumentException Si cabal és negatiu
     */
    public void establirCabal(PuntOrigen nodeOrigen, float cabal) {
        if (cabal < 0) {
            throw new IllegalArgumentException("Cabal negatiu");
        }
        else if (grafXarxa.getNode(nodeOrigen.id()) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: el node origen no pertany a la xarxa");
        }
        else {
            Node n = grafXarxa.getNode(nodeOrigen.id());
            Object n_x = n.getAttribute("node");
            if (n_x instanceof PuntOrigen) {
                PuntOrigen po = (PuntOrigen) n_x;
                po.establirCabal(cabal);
            }
        }
    }

    /**
     * @brief Estableix la demanda d'un PuntTerminal
     * @pre nodeTerminal pertany a la xarxa i demanda >= 0
     * @post La demanda de nodeTerminal és demanda
     * @param nodeTerminal PuntTerminal del qual es vol establir la demanda
     * @param demanda Demanda a establir
     * @throws NoSuchElementException Si nodeTerminal no pertany a la xarxa
     * @throws IllegalArgumentException Si demanda és negativa
     */
    public void establirDemanda(PuntTerminal nodeTerminal, float demanda) {
        if (demanda < 0) {
            throw new IllegalArgumentException("Demanda negativa");
        }
        else if (grafXarxa.getNode(nodeTerminal.id()) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: el node origen no pertany a la xarxa");
        }
        else {
            Node n = grafXarxa.getNode(nodeTerminal.id());
            Object n_x = n.getAttribute("node");
            if (n_x instanceof PuntTerminal) {
                PuntTerminal pt = (PuntTerminal) n_x;
                pt.establirDemandaActual(demanda);
            }
        }
    }

    /**
     * @brief Retorna el cabal teòric al node segons la configuració actual de la xarxa
     * @pre node pertany a la xarxa
     * @post Retorna el cabal teòric al node segons la configuració actual de la xarxa
     * @param node_x Node del qual es vol obtenir el cabal
     * @return float Cabal teòric al node
     * @throws NoSuchElementException Si node no pertany a la xarxa
     */
    public float cabal(Node_X node_x) {
        if(grafXarxa.getNode(node_x.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        float cabal = 0;
        Map<Canonada, Float> cabals_canonades = new HashMap<>();
        Map<Node_X, Float> cabals_nodes = new HashMap<>();
        Queue<Node_X> cua = new LinkedList<>();
        Set<Node_X> visitats = new HashSet<>();
        for(Node_X n : nodes()) {
            if(n instanceof PuntOrigen) {
                cua.add(n);
                visitats.add(n);
            }
        }
        while(!cua.isEmpty()) {
            Node_X n = cua.poll();
            if(n instanceof PuntOrigen) {
                cabals_nodes.put(n, ((PuntOrigen) n).cabal());
            }
            if(cabals_nodes.containsKey(n))
                repartirCabal(n, cabals_nodes, cabals_canonades, cua, visitats);
            else
                cua.add(n);
        }
        cabal = cabals_nodes.getOrDefault(node_x, 0f);
        return cabal;
    }

    /**
     * @brief Retorna la demanda teòrica al node segons la configuració actual de la xarxa
     * @pre node pertany a la xarxa
     * @post Retorna la demanda teòrica al node segons la configuració actual de la xarxa
     * @param node Node del qual es vol obtenir la demanda
     * @param demandes_canonades Map per guardar les demandes de les canonades
     * @param demandes_nodes Map per guardar les demandes dels nodes
     * @return float Demanda teòrica al node
     * @throws NoSuchElementException Si node no pertany a la xarxa
     */
    public float demanda(Node_X node, Map<Canonada, Float> demandes_canonades, Map<Node_X, Float> demandes_nodes) {
        if(grafXarxa.getNode(node.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        float demanda = 0;
        Queue<Node_X> cua = new LinkedList<>();
        Set<Node_X> visitats = new HashSet<>();
        for(Node_X n : nodes()) {
            if(n instanceof PuntTerminal) {
                cua.add(n);
                visitats.add(n);
            }
        }
        while(!cua.isEmpty()) {
            Node_X n = cua.poll();
            if(n instanceof PuntTerminal) {
                demandes_nodes.put(n, ((PuntTerminal) n).demanda_actual());
            }
            if(demandes_nodes.containsKey(n)) {
                propagarDemanda(n, demandes_nodes, demandes_canonades, cua, visitats);
            }
            else
                cua.add(n);
        }
        demanda = demandes_nodes.get(node);
        return demanda;
    }

    /**
     * @brief Dibuixa la xarxa de distribució d'aigua
     * @pre nodeOrigen pertany a la xarxa
     * @post Dibuixa la xarxa de distribució d'aigua
     * @param nodeOrigen PuntOrigen des del qual es vol iniciar el dibuix
     * @param max_flow Boolean que indica si s'ha de dibuixar el flux màxim
     * @throws NoSuchElementException Si nodeOrigen no pertany a la xarxa
     */
    public void dibuixar(PuntOrigen nodeOrigen, boolean max_flow) {
        if (grafXarxa.getNode(nodeOrigen.id()) == null) {
            throw new NoSuchElementException("Error de configuració a l'opció: el node origen no pertany a la xarxa");
        }
        else {
            String css = "edge { arrow-shape: arrow; arrow-size: 10px, 3px; }" + "node.X { fill-color: red; size: 25px; stroke-mode: plain; stroke-color: black; stroke-width: 2px; shape: cross; }";
            grafXarxa.setAttribute("ui.stylesheet", css);

            Node n = grafXarxa.getNode(nodeOrigen.id());
            Set<Node> visitats = new HashSet<>();
            calculConnex(n, visitats);
            System.setProperty("org.graphstream.ui", "swing");

            if(!max_flow) {
                for (Node node : grafXarxa) {
                    Object n_x = node.getAttribute("node");
                    if (visitats.contains(node)) {
                        if (n_x instanceof Node_X) {
                            Node_X n_dibuix = (Node_X) n_x;
                            Coordenades coord = n_dibuix.coordenades();
                            node.setAttribute("xy", coord.longitud(), coord.latitud());
                            if(n_x instanceof  PuntTerminal) {
                                PuntTerminal n_terminal = (PuntTerminal) n_dibuix;
                                String coordDibuix = String.format("%.6f, %.6f", n_terminal.coordenades().longitud(), n_terminal.coordenades().latitud());
                                String demandaDibuix = String.format("%.2f/%.2f", n_terminal.demanda_actual(), n_terminal.demanda());
                                node.setAttribute("ui.label", n_terminal.id() + " (" + coordDibuix + ") " + " (" + demandaDibuix + ")");
                            }
                            if(!n_dibuix.aixetaOberta())
                                node.setAttribute("ui.style", "fill-color: gray; size: 25px; text-size: 10px; text-alignment: center; text-offset: 0, 15px;");
                        }
                    }
                    else {
                        node.setAttribute("ui.style", "visibility: hidden;");
                        node.setAttribute("ui.hide");
                    }

                    for(Edge e : node){
                        Canonada c = (Canonada) e.getAttribute("canonada");
                        e.setAttribute("ui.label", c.cabal() + "/" + c.capacitat());
                    }
                }
            }
            else {
                for (Node node : grafXarxa) {
                    Object n_x = node.getAttribute("node");
                    if (visitats.contains(node)) {
                        if (n_x instanceof Node_X) {
                            Node_X n_dibuix = (Node_X) n_x;
                            Coordenades coord = n_dibuix.coordenades();
                            node.setAttribute("xy", coord.longitud(), coord.latitud());
                        }
                        if (n_x instanceof PuntOrigen){
                            node.setAttribute("ui.label", "");
                            node.setAttribute("ui.label", "s");
                        }
                        else if(n_x instanceof PuntTerminal){
                            node.setAttribute("ui.label", "");
                            node.setAttribute("ui.label", "t");
                        }
                        else node.setAttribute("ui.label", "");
                    }
                    else {
                        node.setAttribute("ui.style", "visibility: hidden;");
                        node.setAttribute("ui.hide");
                    }

                    for(Edge e : node){
                        Canonada c = (Canonada) e.getAttribute("canonada");
                        e.setAttribute("ui.label", c.cabal() + "/" + c.capacitat());
                    }
                }
            }

            Viewer dibuix = grafXarxa.display();
            dibuix.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);
        }
    }

    /**
     * @brief Retorna un conjunt amb tots els nodes de la xarxa
     * @pre ---
     * @post Retorna un conjunt amb tots els nodes de la xarxa
     * @return Set<Node_X> Conjunt amb tots els nodes de la xarxa
     */
    public Set<Node_X> nodes() {
        Set<Node_X> nodes = new HashSet<>();
        for(Node n : grafXarxa) {
            Object n_x = n.getAttribute("node");
            if(n_x instanceof Node_X) {
                nodes.add((Node_X) n_x);
            }
        }
        return nodes;
    }

    /**
     * @brief Propaga la demanda des d'un node als seus nodes superiors a la xarxa.
     * @pre n és un node de la xarxa.
     * @post S'ha propagat la demanda del node n als nodes superiors i s'han actualitzat les estructures per paràmetre.
     * @param n Node de la xarxa des del qual es propaga la demanda.
     * @param demandes_nodes Map que conté les demandes de cada node.
     * @param demandes_canonades Map que conté les demandes de cada canonada.
     * @param cua Cua de nodes pendents de visitar.
     * @param visitats Conjunt de nodes ja visitats.
     */
    private void propagarDemanda (Node_X n, Map<Node_X, Float> demandes_nodes, Map<Canonada, Float> demandes_canonades, Queue<Node_X> cua, Set<Node_X> visitats) {
        if (!n.aixetaOberta())
            demandes_nodes.put(n, 0.0f);

        Map<Node_X, Canonada> superiors = obtenirSuperiors(n);
        float total_capacitat = 0;
        float demanda = demandes_nodes.get(n);
        for(Canonada c : superiors.values()) {
            total_capacitat += c.capacitat();
        }
        for(Map.Entry<Node_X, Canonada> e : superiors.entrySet()) {
            float cabal = 0;
            float capacitat = e.getValue().capacitat();
            if(demanda > total_capacitat)
                cabal = capacitat;
            else
                cabal = (demanda * capacitat) / total_capacitat;
            demandes_canonades.put(e.getValue(), cabal);
            Node_X superior = e.getKey();
            if(!visitats.contains(superior)) {
                cua.add(superior);
                visitats.add(superior);
            }
            boolean sabem_sortides = true;
            float demanda_node = 0;
            Iterator<Canonada> it = sortides(superior);
            while (it.hasNext() && sabem_sortides) {
                Canonada c = it.next();
                if (!demandes_canonades.containsKey(c)) {
                    sabem_sortides = false;
                    break;
                }
                demanda_node += demandes_canonades.get(c);
            }
            if(sabem_sortides) {
                demandes_nodes.put(superior, demanda_node);
            }
        }
    }

    /**
     * @brief Obté els nodes superiors d'un node i les canonades que els connecten.
     * @pre n és un node de la xarxa.
     * @post Retorna un map amb els nodes superiors de n i les canonades que els connecten.
     * @param n Node de la xarxa del qual es volen obtenir els nodes superiors.
     * @return Un map que conté els nodes superiors de n i les canonades que els connecten.
     */
    private Map<Node_X, Canonada> obtenirSuperiors(Node_X n) {
        Map<Node_X, Canonada> superiors = new HashMap<>();
        for(Node_X node : nodes()) {
            if(node != n) {
                Iterator<Canonada> canonades = sortides(node);
                while(canonades.hasNext()) {
                    Canonada c = canonades.next();
                    if(c.node2().equals(n)) {
                        superiors.put(node, c);
                    }
                }
            }
        }
        return superiors;
    }

    /**
     * @brief Obté els nodes inferiors d'un node i les canonades que els connecten.
     * @pre n és un node de la xarxa.
     * @post Retorna un map amb els nodes inferiors de n i les canonades que els connecten.
     * @param n Node de la xarxa del qual es volen obtenir els nodes inferiors.
     * @return Un map que conté els nodes inferiors de n i les canonades que els connecten.
     */
    private Map<Node_X, Canonada> obtenirInferiors(Node_X n) {
        Map<Node_X, Canonada> inferiors = new HashMap<>();
        for(Node_X node : nodes()) {
            if(node != n) {
                Iterator<Canonada> canonades = entrades(node);
                while(canonades.hasNext()) {
                    Canonada c = canonades.next();
                    if(c.node1().equals(n)) {
                        inferiors.put(node, c);
                    }
                }
            }
        }
        return inferiors;
    }

    /**
     * @brief Obté una llista amb totes les arestes de la xarxa.
     * @pre ---
     * @post Retorna una llista amb totes les arestes de la xarxa.
     * @return Una llista amb totes les arestes de la xarxa.
     */
    private List<Edge> obtenirArestes() {
        List<Edge> arestes = new ArrayList<>();
        for(Node_X n : nodes()) {
            Iterator<Canonada> canonades = sortides(n);
            while(canonades.hasNext()) {
                Canonada c = canonades.next();
                arestes.add(grafXarxa.getEdge(n.id() + "-" + c.node2().id()));
            }
        }
        return arestes;
    }

    /**
     * @brief Reparteix el cabal des d'un node als seus nodes inferiors a la xarxa.
     * @pre n és un node de la xarxa.
     * @post S'ha repartit el cabal del node n als nodes inferiors i s'han actualitzat les estructures per paràmetre.
     * @param n Node de la xarxa des del qual es reparteix el cabal.
     * @param cabals_nodes Map que conté els cabals de cada node.
     * @param cabals_canonades Map que conté els cabals de cada canonada.
     * @param cua Cua de nodes pendents de visitar.
     * @param visitats Conjunt de nodes ja visitats.
     */
    private void repartirCabal(Node_X n, Map<Node_X, Float> cabals_nodes, Map<Canonada, Float> cabals_canonades, Queue<Node_X> cua, Set<Node_X> visitats) {
        Map<Node_X, Float> demandes_nodes = new HashMap<>();
        Map<Canonada, Float> demandes_canonades = new HashMap<>();
        float demanda_total = demanda(n, demandes_canonades, demandes_nodes);
        for(Map.Entry<Node_X, Canonada> e : obtenirInferiors(n).entrySet()) {
            float demanda = demandes_canonades.get(e.getValue());
            float cabal = 0;
            if(demanda_total > 0) {
                if(demandes_nodes.get(n) < cabals_nodes.get(n)) {
                    cabals_nodes.put(n, demandes_nodes.get(n));
                }
                cabal = (cabals_nodes.get(n) * demanda) / demanda_total;
            }
            cabals_canonades.put(e.getValue(), cabal);
            e.getValue().actualitzarCabal(cabal);
            if(!visitats.contains(e.getKey())) {
                cua.add(e.getKey());
                visitats.add(e.getKey());
            }
            boolean sabem_entrades = true;
            float cabal_node = 0;
            Iterator<Canonada> it = entrades(e.getKey());
            while(it.hasNext()) {
                Canonada c = it.next();
                if(!cabals_canonades.containsKey(c)) {
                    sabem_entrades = false;
                    break;
                }
                cabal_node += cabals_canonades.getOrDefault(c, 0f);
            }
            if(sabem_entrades)
                cabals_nodes.put(e.getKey(), cabal_node);
        }
    }

    /**
     * @brief Marca un node i tots els seus nodes connectats com a visitats.
     * @pre node és un node de la xarxa.
     * @post S'ha marcat el node com a visitat i s'han marcat com a visitats tots els nodes connectats a node.
     * @param node Node de la xarxa que es marca com a visitat.
     * @param visitats Conjunt de nodes ja visitats.
     */
    private void calculConnex(Node node, Set<Node> visitats) {
        visitats.add(node);
        for (Edge edge : node) {
            Node vei = edge.getOpposite(node);
            if (!visitats.contains(vei)) {
                calculConnex(vei, visitats);
            }
        }
    }
}
