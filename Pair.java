//Pol Martorell Herrera
/**
 * @file Pair.java
 * @brief Classe que representa una estructura de pair amb dos elements de tipus A i B.
 */

/**
 * @class Pair
 * @brief Representa una estructura de pair amb dos elements de tipus A i B.
 * @tparam A Tipus del primer element del pair.
 * @tparam B Tipus del segon element del pair.
 */
public class Pair<A, B> {

    // Descripció general: Estructura de pair amb dos elements de tipus A i B
    private final A primer; ///< Primer element del pair
    private final B segon;  ///< Segon element del pair

    /**
     * @brief Constructor de la classe Pair.
     * @param primer Primer element del pair.
     * @param segon Segon element del pair.
     * @pre ---
     * @post S'ha creat un nou pair amb el primer element primer i el segon element segon.
     */
    public Pair(A primer, B segon) {
        this.primer = primer;
        this.segon = segon;
    }

    /**
     * @brief Retorna el primer element del pair.
     * @return Primer element del pair.
     * @pre ---
     * @post Retorna el primer element del pair.
     */
    public A agafarPrimer() {
        return primer;
    }

    /**
     * @brief Retorna el segon element del pair.
     * @return Segon element del pair.
     * @pre ---
     * @post Retorna el segon element del pair.
     */
    public B agafarSegon() {
        return segon;
    }
}