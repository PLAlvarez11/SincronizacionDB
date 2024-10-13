package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import logica.Persona;

public class PersonaDB {
    // Método que retorna una lista de personas según el tipo de base de datos
     public List<Persona> obtenerListaPersonas(String tipoDB) {
        // Crear la lista de personas
        List<Persona> listaPersonas = new ArrayList<>();

        // Datos de Persona 1
        String query = "select * from empleado ORDER BY dpi";
        
        try (Connection connection = validarConexion(tipoDB);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            //Recorremos el result set para crear los objetos persona
            while (resultSet.next()) {
                
                Persona persona = new Persona(
                    resultSet.getInt("dpi"),                // dpi
                    resultSet.getString("primer_nombre"),   // nombre1
                    resultSet.getString("segundo_nombre"),  // nombre2
                    resultSet.getString("primer_apellido"), // apellido1
                    resultSet.getString("segundo_apellido"),// apellido2
                    resultSet.getString("direccion"),       // direccionDomicilio
                    resultSet.getString("telefono_casa"),   // telefonoDomicilio
                    resultSet.getString("telefono_movil"),  // telefonoMovil
                    resultSet.getBigDecimal("salario_base"),// salarioBase
                    resultSet.getBigDecimal("bonificacion") // bonificacion
                );
                //Agregamos el objeto a la lista para la tabla
                listaPersonas.add(persona);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Retornar la lista de personas
        return listaPersonas;
    }
    
    // Método que guarda en DB una persona y guarda en documento
    public boolean crearPersona (Persona persona, String tipoDB){
        try{
            //Validamos la conexion
            Connection connection = validarConexion(tipoDB);
            
            //Creamos el script SQL para validar si el registro a insertar ya existe
            String sql = "SELECT * FROM empleado WHERE dpi = ?";
            
            //Metemos los parametros del script
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, persona.getDpi());
            
            //Ejecutamos la consulta
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                //Si, si exite devuelve false para que no inserte el dato
                return false;
            }
            
            //Creamos el script SQL para insertar
             sql = "INSERT INTO empleado (dpi, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, direccion, telefono_casa, telefono_movil, salario_base, bonificacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
             ps = connection.prepareStatement(sql);
            
             //Metemos los parametros del script
            ps.setInt(1, persona.getDpi());
            ps.setString(2, persona.getNombre1());
            ps.setString(3, persona.getNombre2());
            ps.setString(4, persona.getApellido1());
            ps.setString(5, persona.getApellido2());
            ps.setString(6, persona.getDireccionDomicilio());
            ps.setString(7, persona.getTelefonoDomicilio());
            ps.setString(8, persona.getTelefonoMovil());
            ps.setBigDecimal(9, persona.getSalarioBase());
            ps.setBigDecimal(10, persona.getBonificacion());
            
            //Creamos el string para ser insertado en la bitacora
            String texto = "1|"+ tipoDB +"|INSERT|VALUES("+ persona.getDpi() +", '"+ persona.getNombre1() +"', '"+ persona.getNombre2() +"', '"+ persona.getApellido1() +"', '"+ persona.getApellido2() +"', '"+ persona.getDireccionDomicilio() +"', '"+ persona.getTelefonoDomicilio() +"', '"+ persona.getTelefonoMovil() +"', "+ persona.getSalarioBase() +", "+ persona.getBonificacion() +")";
            
            //Ejecutamos la consulta y validamos si inserto datos
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("¡Inserción exitosa!");
                
                //Al momento de ser exitoso guardemos en bitacora
                agregarTransaccion(texto);
            }
            
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Código SQLState para error de clave duplicada
                System.out.println("Error: Llave duplicada. El empleado ya existe.");
            // Puedes optar por hacer algo más aquí, como omitir el error, intentar otro ID, etc.
            } else {
                // Manejar otras excepciones SQL
                e.printStackTrace();
            }
        }
        return true;
    }
    
    // Método que actualiza en DB una persona y guarda en documento
    public boolean atualizarPersona (Persona persona, String tipoDB){
       try {
           //Validamos la conexion
            Connection connection = validarConexion(tipoDB);
            
            //Creamos el script SQL para obtner el regisro original
            String sql = "SELECT * FROM empleado WHERE dpi = ?";
            
            //Ejecutamos la consulta
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, persona.getDpi());

            ResultSet resultSet = ps.executeQuery();
            
            //Metemos a un objeto el result set con los datos del registro original
            Persona personaOriginal = new Persona();
            while (resultSet.next()) {
                personaOriginal = new Persona(
                    resultSet.getInt("dpi"),                // dpi
                    resultSet.getString("primer_nombre"),   // nombre1
                    resultSet.getString("segundo_nombre"),  // nombre2
                    resultSet.getString("primer_apellido"), // apellido1
                    resultSet.getString("segundo_apellido"),// apellido2
                    resultSet.getString("direccion"),       // direccionDomicilio
                    resultSet.getString("telefono_casa"),   // telefonoDomicilio
                    resultSet.getString("telefono_movil"),  // telefonoMovil
                    resultSet.getBigDecimal("salario_base"),// salarioBase
                    resultSet.getBigDecimal("bonificacion") // bonificacion
                );
            }
            
            String texto;
           //Validamos si el nombre original es diferente al nombre insertado
           if (!personaOriginal.getNombre1().equals(persona.getNombre1())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET primer_nombre = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setString(1, persona.getNombre1());
                    ps.setInt(2, persona.getDpi());
                    
                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|primer_nombre|"+ persona.getNombre1() +"|"+ persona.getDpi();
                    
                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
           
           //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getNombre2().equals(persona.getNombre2())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET segundo_nombre = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setString(1, persona.getNombre2());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|segundo_nombre|"+ persona.getNombre2() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
            //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getApellido1().equals(persona.getApellido1())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET primer_apellido = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setString(1, persona.getApellido1());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|primer_apellido|"+ persona.getApellido1() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
            //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getApellido2().equals(persona.getApellido2())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET segundo_apellido = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setString(1, persona.getApellido2());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|segundo_apellido|"+ persona.getApellido2() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
            //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getDireccionDomicilio().equals(persona.getDireccionDomicilio())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET direccion = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setString(1, persona.getDireccionDomicilio());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|direccion|"+ persona.getDireccionDomicilio() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
            //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getTelefonoDomicilio().equals(persona.getTelefonoDomicilio())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET telefono_casa = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setString(1, persona.getTelefonoDomicilio());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|telefono_casa|"+ persona.getTelefonoDomicilio() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
            //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getTelefonoMovil().equals(persona.getTelefonoMovil())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET telefono_movil = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setString(1, persona.getTelefonoMovil());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|telefono_movil|"+ persona.getTelefonoMovil() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
            //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getSalarioBase().equals(persona.getSalarioBase())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET salario_base = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setBigDecimal(1, persona.getSalarioBase());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|salario_base|"+ persona.getSalarioBase() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
            //Validamos si el dato original es diferente al dato insertado
            if (!personaOriginal.getBonificacion().equals(persona.getBonificacion())) {
              try{
                  //Se crear el script SQL y sus parametos 
                    sql = "UPDATE empleado SET bonificacion = ? WHERE dpi = ?";
                    ps = connection.prepareStatement(sql);
                    ps.setBigDecimal(1, persona.getBonificacion());
                    ps.setInt(2, persona.getDpi());

                    //Se crea la cadena para ls bitacora
                    texto = "1|"+ tipoDB +"|UPDATE|bonificacion|"+ persona.getBonificacion() +"|"+ persona.getDpi();

                    //Ejecutamos la consulta y validamos si inserto datos
                    int filasActualizadas = ps.executeUpdate();
                    if (filasActualizadas > 0) {
                        //Al momento de ser exitoso guardemos en bitacora
                        agregarTransaccion(texto);
                        System.out.println("¡Actualización exitosa!");
                    } else {
                        System.out.println("No se encontró ningún usuario con ese email.");
                    }
                } catch (SQLException e) {
                  e.printStackTrace();
              }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    // Método que elimina en DB una persona y guarda en documento
    public boolean eliminarPersona (Persona persona, String tipoDB){
        try {
            //Validamos y creamos la conexion
            Connection connection = validarConexion(tipoDB);
            
            //Se crea el script y sus parametos
            String sql = "DELETE FROM empleado WHERE dpi = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, persona.getDpi());

            //Se ejecuta la eliminación
            int filasEliminadas = ps.executeUpdate();
            if (filasEliminadas > 0) {
                
                //Se crea la cadena que se guardara en la bitacora 
                String texto = "1|"+ tipoDB +"|DELETE|" + persona.getDpi();
                agregarTransaccion(texto);
                
                System.out.println("¡Usuario eliminado exitosamente!");
            } else {
                System.out.println("No se encontró ningún usuario con ese email.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    // Método que sincroniza las 2 DB y guarda en documento los cambios
    public boolean sincronizar2DB (List<Persona> personas){
        int linea = 0;
        
        //Se leen todas las trasnacciones en la bitacora
        List<String> transacciones = leerTransacciones();
        // Recorre las transacciones
        for (String transaccion : transacciones) {
            
            //Solo vamos a obetener que inicien con 1, es decir las transacciones que no han sido sincronizadas
            if (transaccion.startsWith("1")) {
                //Metemos dento de un arreglo separdo por el simbolo "|" 
                String[] arreglo = transaccion.split("\\|");
                String tipo = "", valor1 = "", valor2 = "", fecha = "", query = "", id="";
                
                //Recorremos el arreglo
                for (int i = 0; i < arreglo.length; i++) {
                    if ( i == 2){
                        //Si vamos por la vuelta 2, este será el tipo de transaccion
                        tipo = arreglo[i];
                    }else if (tipo.equals("DELETE") && i == 3){
                        //Si vamos por la vuelta 3 y el tipo de transacciones es DELETE, obtenderemos el id a eliminar
                        id =  arreglo[i];
                    }else if (i == 3){
                        //Si vamos por la vuelta e, este será el valor de insercion o el valor a editar
                        valor1 = arreglo[i];
                    }else if (tipo.equals("UPDATE") && i == 4){
                        //Si vamos por la vuelta 4 y es un update el tipo de transaccion. este sera el valor a insetar 
                        valor2 =  arreglo[i];
                    }else if (tipo.equals("UPDATE") && i == 5){
                        //Si vamos por la vuelta 5, este sera el id de la transaccion
                        id =  arreglo[i];
                    }else if (i == 4){
                        //esta sera la fecha de ingreso
                        fecha =  arreglo[i];
                    }
                }
                
                //Dependiendo el tipo de transaccion asi vamos a construir el query para su sincronizacion
                switch (tipo) {
                    case "INSERT":
                        query = "INSERT INTO empleado ";
                        query += valor1;
                        break;
                    case "UPDATE":
                        query = "UPDATE empleado ";
                        query += "SET " + valor1 + " = '" + valor2+"'";
                        query += " WHERE dpi = " + id;

                        break;
                    case "DELETE":
                        query = "DELETE FROM empleado ";
                        query += "WHERE dpi = " + id;
                        break;
                    default:
                        break;
                }
                
                //Realizamos la conexion y ejecutamos el query construido en Postgre
                try {
                    Connection connectionPostgre = validarConexion("PostgreSQL");
                    PreparedStatement psPostgre = connectionPostgre.prepareStatement(query);
                    psPostgre.executeUpdate();
                } catch (SQLException e) {
                    if (e.getSQLState().equals("23505")) { // Código SQLState para error de clave duplicada
                        System.out.println("Error: Llave duplicada. El empleado ya existe.");
                        // Puedes optar por hacer algo más aquí, como omitir el error, intentar otro ID, etc.
                    } else {
                        // Manejar otras excepciones SQL
                        e.printStackTrace();
                    }
                }
                
                //Realizamos la conexion y ejecutamos el query construido en MySQL
                try {
                    Connection connectioMySQL = validarConexion("MySQL");
                    PreparedStatement psMySQL = connectioMySQL.prepareStatement(query);
                    psMySQL.executeUpdate();
                } catch (SQLException e) {
                    if (e.getSQLState().equals("23505")) { // Código SQLState para error de clave duplicada
                        System.out.println("Error: Llave duplicada. El empleado ya existe.");
                        // Puedes optar por hacer algo más aquí, como omitir el error, intentar otro ID, etc.
                    } else {
                        // Manejar otras excepciones SQL
                        e.printStackTrace();
                    }
                }
                
                //Una vez la sincronizacion realizada, le cambiamos el estado a 0 (que ya fue sincronizaso)
                editarTransaccion(linea, validarTraslado(transaccion, '0'));
            }
            linea++;
        }
        return true;
    }    
    
    public Connection validarConexion(String DB) throws SQLException{
        //Valida a que conexion nos debemos conectar
        if(DB.equals("PostgreSQL")){
            return ConectorPostgre.getConnection();
        }else{
            return ConectorMySQL.getConnection();
        }
    }
    
    public static void agregarTransaccion(String transaccion) {
        //Agrega a la bitacora que transacccion se realizo, junto a su hora
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("bitacora.txt", true))) {
            // Obtener la fecha y hora actuales para el registro
            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // Escribir la transacción en el archivo
            escritor.write(transaccion + " | " + fechaHora);
            escritor.newLine(); // Escribir una nueva línea para la próxima transacción
            
        } catch (IOException e) {
            System.err.println("Error al escribir en la bitácora: " + e.getMessage());
        }
    }
    
    public static List<String> leerTransacciones() {
        //Obtene en una lista todas las transacciones guardadas en la bitacora
        List<String> transacciones = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader("bitacora.txt"))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                transacciones.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de bitácora: " + e.getMessage());
        }

        return transacciones;
    }
    
    public static void editarTransaccion(int indice, String nuevaTransaccion) {
        //Es nuestro metodo para editar las transacciones que ya fueron sincrizadas
        List<String> transacciones = leerTransacciones();

        if (indice >= 0 && indice < transacciones.size()) {
            // Modificar la transacción en el índice dado
            transacciones.set(indice, nuevaTransaccion);

            // Sobrescribir el archivo con la lista actualizada
            escribirTransacciones(transacciones);
        } else {
            System.out.println("El índice proporcionado está fuera de rango.");
        }
    }
    
    public static void escribirTransacciones(List<String> transacciones) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("bitacora.txt"))) {
            for (String transaccion : transacciones) {
                escritor.write(transaccion);
                escritor.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en la bitácora: " + e.getMessage());
        }
    }
    
    public static String validarTraslado(String cadena, char nuevoCaracter) {
        // Verifica que la cadena no esté vacía
        if (cadena == null || cadena.isEmpty()) {
            return cadena;
        }

        // Crea la nueva cadena con el primer carácter reemplazado
        return nuevoCaracter + cadena.substring(1);
    }
}