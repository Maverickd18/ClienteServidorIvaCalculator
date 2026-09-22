package dominio.modelos;

public class ResultadoIva {
    private double precioOriginal;
    private double porcentajeIva;
    private double valorIva;
    private double precioFinal;

    public ResultadoIva(double precioOriginal, double porcentajeIva, double valorIva, double precioFinal) {
        this.precioOriginal = precioOriginal;
        this.porcentajeIva = porcentajeIva;
        this.valorIva = valorIva;
        this.precioFinal = precioFinal;
    }

    public double getPrecioOriginal() {
        return precioOriginal;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public double getValorIva() {
        return valorIva;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }
}
