//Natàlia Masgrau Vila
/**
 * @file Coordenades.java
 * @brief Classe que representa coordenades geogràfiques (latitud i longitud).
 */

/**
 * @class Coordenades
 * @brief Representa coordenades geogràfiques amb latitud i longitud.
 */
public class Coordenades {
    // Descripció general: Coordenades geogràfiques (latitud, longitud)

    private float latitud;  ///< Latitud de les coordenades
    private float longitud; ///< Longitud de les coordenades

    /**
     * @brief Constructor de la classe Coordenades amb graus, minuts, segons i direcció.
     * @param grausLatitud Graus de latitud (0 <= grausLatitud <= 90).
     * @param minutsLatitud Minuts de latitud (0 <= minutsLatitud <= 60).
     * @param segonsLatitud Segons de latitud (0 <= segonsLatitud <= 60).
     * @param direccioLatitud Direcció de la latitud ('N' o 'S').
     * @param grausLongitud Graus de longitud (0 <= grausLongitud <= 180).
     * @param minutsLongitud Minuts de longitud (0 <= minutsLongitud <= 60).
     * @param segonsLongitud Segons de longitud (0 <= segonsLongitud <= 60).
     * @param direccioLongitud Direcció de la longitud ('E' o 'W').
     * @pre 0 <= grausLatitud <= 90, 0 <= minutsLatitud <= 60, 0 <= segonsLatitud <= 60, direccioLatitud = 'N' o 'S', 0 <= grausLongitud <= 180, 0 <= minutsLongitud <= 60, 0 <= segonsLongitud <= 60, direccioLongitud = 'E' o 'W'.
     * @post Crea unes coordenades amb els valors indicats.
     * @throws IllegalArgumentException Si es viola la precondició.
     */
    public Coordenades(int grausLatitud, int minutsLatitud, float segonsLatitud, char direccioLatitud, int grausLongitud, int minutsLongitud, float segonsLongitud, char direccioLongitud) {
        if (grausLatitud < 0 || grausLatitud > 90 || minutsLatitud < 0 || minutsLatitud > 60 || segonsLatitud < 0 || segonsLatitud > 60 || (direccioLatitud != 'N' && direccioLatitud != 'S') || grausLongitud < 0 || grausLongitud > 180 || minutsLongitud < 0 || minutsLongitud > 60 || segonsLongitud < 0 || segonsLongitud > 60 || (direccioLongitud != 'E' && direccioLongitud != 'W')) {
            throw new IllegalArgumentException();
        }
        this.latitud = grausLatitud + minutsLatitud / 60.0f + segonsLatitud / 3600.0f;
        this.longitud = grausLongitud + minutsLongitud / 60.0f + segonsLongitud / 3600.0f;
        if (direccioLatitud == 'S') {
            this.latitud = -this.latitud;
        }
        if (direccioLongitud == 'W') {
            this.longitud = -this.longitud;
        }
    }

    /**
     * @brief Constructor de la classe Coordenades amb latitud i longitud en graus decimals.
     * @param latitud Latitud en graus decimals (-90 <= latitud <= 90).
     * @param longitud Longitud en graus decimals (-180 <= longitud <= 180).
     * @pre -90 <= latitud <= 90, -180 <= longitud <= 180.
     * @post Crea unes coordenades amb els valors indicats.
     * @throws IllegalArgumentException Si es viola la precondició.
     */
    public Coordenades(float latitud, float longitud) {
        if (latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException();
        }
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * @brief Calcula la distància entre aquestes coordenades i unes altres.
     * @param c Les coordenades amb les quals calcular la distància.
     * @return La distància entre aquestes coordenades i c, expressada en km.
     * @pre ---
     * @post Retorna la distància entre aquestes coordenades i c.
     */
    public double distancia(Coordenades c) {
        final double radiTerra = 6371.0;
        double latitud1 = Math.toRadians(this.latitud);
        double longitud1 = Math.toRadians(this.longitud);
        double latitud2 = Math.toRadians(c.latitud);
        double longitud2 = Math.toRadians(c.longitud);
        double deltaLatitud = latitud2 - latitud1;
        double deltaLongitud = longitud2 - longitud1;
        double a = Math.pow(Math.sin(deltaLatitud / 2), 2) + Math.cos(latitud1) * Math.cos(latitud2) * Math.pow(Math.sin(deltaLongitud / 2), 2);
        double c1 = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radiTerra * c1;
    }

    /**
     * @brief Retorna la longitud de les coordenades.
     * @return Longitud de les coordenades.
     * @pre ---
     * @post Retorna la longitud de les coordenades.
     */
    public float longitud() {
        return this.longitud;
    }

    /**
     * @brief Retorna la latitud de les coordenades.
     * @return Latitud de les coordenades.
     * @pre ---
     * @post Retorna la latitud de les coordenades.
     */
    public float latitud() {
        return this.latitud;
    }
}