package dominio.modelos;

public class DatosIva {
    private double precioSinIva;
    private double porcentajeIva;

    public DatosIva(double precioSinIva, double porcentajeIva) {
        this.precioSinIva = precioSinIva;
        this.porcentajeIva = porcentajeIva;
    }

    public double getPrecioSinIva() {
        return precioSinIva;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }
}
