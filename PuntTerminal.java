//Pol Martorell Herrera
/**
 * @file PuntTerminal.java
 * @brief Classe que representa un node terminal d'una xarxa de distribució d'aigua.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @class PuntTerminal
 * @brief Representa un node terminal d'una xarxa de distribució d'aigua.
 * @extends Node_X
 */
public class PuntTerminal extends Node_X {

    // Descripció general: Node terminal d'una xarxa de distribució d'aigua

    private final float demandaPunta; ///< Demanda punta d'aigua del terminal en l/s
    private float demandaActual;      ///< Demanda actual d'aigua del terminal en l/s
    private List<String> abonats = new ArrayList<>(); ///< Llista d'abonats al punt terminal

    /**
     * @brief Constructor de la classe PuntTerminal.
     * @param id Identificador del node terminal.
     * @param c Coordenades del node terminal.
     * @param demandaPunta Demanda punta d'aigua del terminal en l/s.
     * @pre ---
     * @post S'ha creat un nou terminal amb identificador id, coordenades c i demanda punta demanda en l/s.
     */
    public PuntTerminal(String id, Coordenades c, float demandaPunta) {
        super(id, c);
        this.demandaPunta = demandaPunta;
    }

    /**
     * @brief Retorna la demanda punta d'aigua del terminal.
     * @return Demanda punta d'aigua del terminal en l/s.
     * @pre ---
     * @post Retorna la demanda punta d'aigua del terminal.
     */
    public float demanda() {
        return this.demandaPunta;
    }

    /**
     * @brief Retorna la demanda actual d'aigua del terminal.
     * @return Demanda actual d'aigua del terminal en l/s.
     * @pre ---
     * @post Retorna la demanda actual d'aigua del terminal.
     */
    public float demanda_actual() {
        return this.demandaActual;
    }

    /**
     * @brief Estableix la demanda actual d'aigua del terminal.
     * @param demanda Nova demanda actual d'aigua en l/s.
     * @pre Demanda >= 0
     * @post La demanda d'aigua actual del terminal és demanda.
     * @throws IllegalArgumentException Si demanda és menor que 0.
     */
    public void establirDemandaActual(float demanda) {
        if (demanda < 0) {
            throw new IllegalArgumentException("Demanda negativa");
        } else {
            this.demandaActual = demanda;
        }
    }

    /**
     * @brief Afegeix un abonat a la llista d'abonats del terminal.
     * @param id Identificador de l'abonat.
     * @pre ---
     * @post S'ha afegit l'abonat amb identificador id a la llista d'abonats.
     */
    public void afegir_abonat(String id) {
        if (!abonats.contains(id) || abonats.size() < 3) {
            abonats.add(id);
        }
    }

    /**
     * @brief Comprova si un abonat està a la llista d'abonats del terminal.
     * @param id Identificador de l'abonat.
     * @return True si l'abonat està a la llista, false en cas contrari.
     * @pre ---
     * @post Retorna si l'abonat amb identificador id és abonat al punt terminal.
     */
    public boolean es_abonat(String id) {
        return abonats.contains(id);
    }
}