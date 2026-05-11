package org.davidparada;

import org.apache.commons.net.ftp.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClienteFTP {

    public static void main( String[] args ) throws IOException {
        FTPClient cliente = new FTPClient();
        String servidorFtp = "ftp.usc.es";
        System.out.println("Nos conectamos a: " + servidorFtp);
        String usuario = "anonymous";
        String clave = "anonymous";

        try {
            // Nos conectamos al servidor con los datos añadidos en la variable
            cliente.connect(servidorFtp);
            // Nos conectamos en modo pasivo
            cliente.enterLocalPassiveMode();

            // comprueba que el usuario y contraseña introducidos sean validos
            boolean login = cliente.login(usuario, clave);
            if (login) {
                System.out.println("Login correcto ...");
            } else {
                System.out.println("Login incorrecto ...");
                cliente.disconnect();
                System.exit(1);
            }
            // Indica en consola en que directorio estamos
            System.out.println("Directorio actual: " + cliente.printWorkingDirectory());

            // Indica cuantos elementos hay dentro del directorio actual y los guarda en un array.
            FTPFile[] files = cliente.listFiles();
            System.out.println("Ficheros en el directorio actual: " + files.length);

            // Creamos un array para que muestre los tipos con palabras en vez de un número
            String[] tipos = {"Fichero", "Directorio", "Enlaze simb"};
            // recorre todo el directorio, y nos dice si son archivos, directorios, o enlaces, y los muestra en la consola
            for (int i = 0; i < files.length; i++) {
                System.out.printf("%15s => %s\n", files[i].getName(), tipos[files[i].getType()]);
            }
            System.out.println();

            // Vamos a recorrer todo el array para comprobar los directorios, creamos variable para guardar el nombre del directorio.
            String nombreDirectorio = "";
            for (FTPFile f : files){
                // Si el elemento es un directorio, añade el nombre a la variable y comrueva si esta vacio.
                if (f.isDirectory()) {
                    nombreDirectorio = f.getName();
                    if (!nombreDirectorio.isEmpty()) {
                        // Si no esta vacio, entra dentro de el y muestra en consola en que directorio ha entrado.
                        cliente.changeWorkingDirectory(nombreDirectorio);

                        System.out.println("Cambiado al directorio: " + cliente.printWorkingDirectory());

                        // Crea un array con los elementos dentro del nuevo directorio.
                        FTPFile[] filesDirectorio = cliente.listFiles();
                        System.out.println("Archivos de " + nombreDirectorio + ":");

                        // Ahora recorremos el directorio y comprurba que sean archivos, si encuentra archivos los muestra si lo
                        // que encuentra no son archivos, muestra mensaje y cambia de directorio.
                        boolean encontrado = false;
                        String ficheroParaBajar = "";
                        for (int i = 0; i < filesDirectorio.length; i++) {
                            if (filesDirectorio[i].isFile()) {
                                System.out.printf("Archivo encontrado: %s\n", filesDirectorio[i].getName());
                                // Guarda el nombre del archivo en una variable.
                                ficheroParaBajar = filesDirectorio[i].getName();
                                encontrado = true;
                            }
                        }

                        // Si ha encontrado archivos, en la variable esta el nombre del ultimo archivo
                        if (encontrado) {
                            System.out.println("\nListado de ficheros completado.");
                            cliente.setFileType(FTP.BINARY_FILE_TYPE);
                            System.out.println("Iniciando descarga de: " + ficheroParaBajar);

                            // Ponemos nombre a una variable para crear un directorio en nustro directorio del programa
                            String nombreCarpeta = "descargas";
                            File directorio = new File(nombreCarpeta);

                            // Si el directorio no existe lo crea.
                            if (!directorio.exists()) {
                                if (directorio.mkdirs()) {
                                    System.out.println("Directorio creado: " + nombreCarpeta);
                                }
                            }

                            // Crea un archivo con el nombre del archivo que vamos a descargar dento del directorio que hemos creado.
                            File archivoLocal = new File(directorio, ficheroParaBajar);
                            // Descarga el archivo seleccionado y guarda los datos en el archivo descargado en el archivo que hemos creado
                            try (FileOutputStream ficheroDescargado = new FileOutputStream(archivoLocal)){
                                boolean descargado = cliente.retrieveFile(ficheroParaBajar,ficheroDescargado);
                                // Muestra mensaje de si ha tenido exito o no.
                                if (descargado) {
                                    System.out.println("\nArchivo descargado con exito");
                                } else {
                                    System.out.println("No se pudo descargar el archivo");
                                }
                            }catch (IOException e){
                                System.out.println("Error de descarga: " + e.getMessage());
                            }
                            break;
                        // Muestra en consola si no ha enco0ntrado ningún archivo dentro del directorio
                        } else {
                            System.out.println("No se encontraron archivos en " + nombreDirectorio);
                            cliente.changeToParentDirectory();
                        }
                    }
                }
            }

            // Salimos del servidor
            boolean logout = cliente.logout();
            if (logout) {
                System.out.println("\nLogout del servidor FTP ...");
            } else {
                System.out.println("Error al hacer logout ...");
            }
            // Nos desconectamos
            cliente.disconnect();
            System.out.println("\n*** Desconectado ***");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
