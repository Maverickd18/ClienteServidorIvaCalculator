package dominio.modelos;

public class CalculoIva {
    
    public double calcularValorIva(double precioSinIva, double porcentajeIva) {
        return precioSinIva * (porcentajeIva / 100.0);
    }

    public double calcularPrecioFinal(double precioSinIva, double valorIva) {
        return precioSinIva + valorIva;
    }
}
