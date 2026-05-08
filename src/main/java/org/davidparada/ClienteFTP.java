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
            cliente.connect(servidorFtp);
            cliente.enterLocalPassiveMode();

            boolean login = cliente.login(usuario, clave);
            if (login) {
                System.out.println("Login correcto ...");
            } else {
                System.out.println("Login incorrecto ...");
                cliente.disconnect();
                System.exit(1);
            }
            System.out.println("Directorio actual: " + cliente.printWorkingDirectory());

            FTPFile[] files = cliente.listFiles();
            System.out.println("Ficheros en el directorio actual: " + files.length);

            String[] tipos = {"Fichero", "Directorio", "Enlaze simb"};
            for (int i = 0; i < files.length; i++) {
                System.out.printf("%15s => %s\n", files[i].getName(), tipos[files[i].getType()]);
            }
            System.out.println();
            String nombreDirectorio = "";
            for (FTPFile f : files){
                if (f.isDirectory()) {
                    nombreDirectorio = f.getName();
                    if (!nombreDirectorio.isEmpty()) {
                        cliente.changeWorkingDirectory(nombreDirectorio);

                        System.out.println("Cambiado al directorio: " + cliente.printWorkingDirectory());

                        FTPFile[] filesDirectorio = cliente.listFiles();
                        System.out.println("Contenido de " + nombreDirectorio + ":");

                        boolean encontrado = false;
                        String ficheroParaBajar = "";
                        for (int i = 0; i < filesDirectorio.length; i++) {
                            if (filesDirectorio[i].isFile()) {
                                System.out.printf("Archivo encontrado: %s\n", filesDirectorio[i].getName());
                                ficheroParaBajar = filesDirectorio[i].getName();
                                encontrado = true;
                            }
                        }

                        if (encontrado) {
                            System.out.println("\nListado de ficheros completado.");
                            cliente.setFileType(FTP.BINARY_FILE_TYPE);
                            System.out.println("Iniciando descarga de: " + ficheroParaBajar);

                            String nombreCarpeta = "descargas";
                            File directorio = new File(nombreCarpeta);

                            if (!directorio.exists()) {
                                if (directorio.mkdirs()) {
                                    System.out.println("Directorio creado: " + nombreCarpeta);
                                }
                            }

                            File archivoLocal = new File(directorio, ficheroParaBajar);
                            try (FileOutputStream ficheroDescargado = new FileOutputStream(archivoLocal)){
                                boolean descargado = cliente.retrieveFile(ficheroParaBajar,ficheroDescargado);
                                if (descargado) {
                                    System.out.println("\nArchivo descargado con exito");
                                } else {
                                    System.out.println("No se pudo descargar el archivo");
                                }
                            }catch (IOException e){
                                System.out.println("Error de descarga: " + e.getMessage());
                            }
                            break;
                        } else {
                            System.out.println("No se encontraron archivos en " + nombreDirectorio);
                            cliente.changeToParentDirectory();
                        }
                    }
                }
            }

            boolean logout = cliente.logout();
            if (logout) {
                System.out.println("\nLogout del servidor FTP ...");
            } else {
                System.out.println("Error al hacer logout ...");
            }
            cliente.disconnect();
            System.out.println("\n*** Desconectado ***");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
