
package logica;

import java.math.BigDecimal;

public class Persona {
    private int dpi;
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String direccionDomicilio;
    private String telefonoDomicilio;
    private String telefonoMovil;
    private BigDecimal salarioBase;
    private BigDecimal bonificacion;

    // Constructor sin parámetros
    public Persona() {}

    // Constructor con parámetros
    public Persona(int dpi, String nombre1, String nombre2, String apellido1, String apellido2, 
                   String direccionDomicilio, String telefonoDomicilio, String telefonoMovil, 
                   BigDecimal salarioBase, BigDecimal bonificacion) {
        this.dpi = dpi;
        this.nombre1 = nombre1;
        this.nombre2 = nombre2;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.direccionDomicilio = direccionDomicilio;
        this.telefonoDomicilio = telefonoDomicilio;
        this.telefonoMovil = telefonoMovil;
        this.salarioBase = salarioBase;
        this.bonificacion = bonificacion;
    }

    // Getters y Setters
    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getDireccionDomicilio() {
        return direccionDomicilio;
    }

    public void setDireccionDomicilio(String direccionDomicilio) {
        this.direccionDomicilio = direccionDomicilio;
    }

    public String getTelefonoDomicilio() {
        return telefonoDomicilio;
    }

    public void setTelefonoDomicilio(String telefonoDomicilio) {
        this.telefonoDomicilio = telefonoDomicilio;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public BigDecimal getBonificacion() {
        return bonificacion;
    }

    public void setBonificacion(BigDecimal bonificacion) {
        this.bonificacion = bonificacion;
    }
    
    @Override
    public String toString() {
        return "Persona{" +
                "DPI='" + dpi + '\'' +
                ", Nombre Completo='" + nombre1 + " " + nombre2 + " " + apellido1 + " " + apellido2 + '\'' +
                ", Dirección='" + direccionDomicilio + '\'' +
                ", Teléfono Domicilio=" + telefonoDomicilio +
                ", Teléfono Móvil=" + telefonoMovil +
                ", Salario Base=" + salarioBase +
                ", Bonificación=" + bonificacion +
                '}';
    }

}
