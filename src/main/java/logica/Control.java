
package logica;

import igu.Inicio;
import java.util.List;
import persistencia.PersonaDB;

public class Control {
    
    private PersonaDB personaDB;
    
    public Control (){
        this.personaDB = new PersonaDB ();
    }
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Inicio inicio = new Inicio();
        inicio.setVisible(true);
        
    }

    // Método para obtener la lista de personas dependiendo del tipo de base de datos
    public List<Persona> obtenerPersonas(String tipoDB) {
        System.out.println("Enviando personas");
        // Delegar la obtención de datos a la capa de modelo (PersonaDAO)
        return personaDB.obtenerListaPersonas(tipoDB);
    }
    
    // Método para llamar función crearPersona de PersonaDB y validar datos
    public boolean crearPersona(Persona persona, String tipoDB) {
        //Aquí se puede validar los datos de persona antes de guardar en DB
        personaDB.crearPersona(persona, tipoDB);
        return true;
    }
    
    // Método para llamar función actualizarPersona de PersonaDB y validar datos
    public boolean actualizarPersona(Persona persona, String tipoDB) {
        //Aquí se puede validar los datos de persona antes de guardar en DB
        personaDB.atualizarPersona(persona, tipoDB);
        return true;
    }
    
    // Método para llamar función eliminarPersona de PersonaDB y validar datos
    public boolean eliminarPersona(Persona persona, String tipoDB) {
        //Aquí se puede validar los datos de persona antes de guardar en DB
        personaDB.eliminarPersona(persona, tipoDB);
        return true;
    }
    
    // Método para llamar función sincronizar y validar datos
    public boolean sincronizar2DB(List<Persona> personas) {
        //Aquí se puede validar los datos de persona antes de guardar en DB
        personaDB.sincronizar2DB(personas);
        return true;
    }
}
