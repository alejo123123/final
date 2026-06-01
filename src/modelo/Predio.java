package modelo;


public class Predio {

    private String npn;
    private String municipio;
    private String direccion;
    private String ficha;

    public Predio(String npn, String municipio, String direccion, String ficha) {
        this.npn       = npn       != null ? npn.trim()       : "";
        this.municipio = municipio != null ? municipio.trim() : "";
        this.direccion = direccion != null ? direccion.trim() : "";
        this.ficha     = ficha     != null ? ficha.trim()     : "";
    }

    

    public String getNpn()       { return npn; }
    public String getMunicipio() { return municipio; }
    public String getDireccion() { return direccion; }
    public String getFicha()     { return ficha; }

    
    public String getValorColumna(int columna) {
        return switch (columna) {
            case 0  -> npn;
            case 1  -> municipio;
            case 2  -> direccion;
            case 3  -> ficha;
            default -> "";
        };
    }

    @Override
    public String toString() {
        return npn + " | " + municipio + " | " + direccion + " | " + ficha;
    }
}
