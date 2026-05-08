package org.davidparada;

import org.apache.commons.net.ftp.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;

public class ClienteFTP2 {
    public static void main(String[] args) {
        FTPClient cliente = new FTPClient();
        String servidorFtp = "ftp.usc.es";
        String usuario = "anonymous";
        String clave = "anonymous";

        try {
            System.out.println("Conectando a: " + servidorFtp);
            cliente.connect(servidorFtp);
            cliente.enterLocalPassiveMode(); // Importante para evitar bloqueos de firewall

            if (cliente.login(usuario, clave)) {
                System.out.println("Login correcto...");

                // 1. Listar elementos del directorio raíz
                System.out.println("\n--- Contenido del Directorio Raíz ---");
                FTPFile[] files = cliente.listFiles();
                for (FTPFile file : files) {
                    String tipo = file.isDirectory() ? "Directorio" : "Fichero";
                    System.out.printf("[%s] %s\n", tipo, file.getName());
                }

                // 2. Entrar en un directorio (por ejemplo, 'pub')
                // Buscamos el primero que sea un directorio para que el código sea dinámico
                String nombreDirectorio = "";
                for (FTPFile f : files) {
                    if (f.isDirectory()) {
                        nombreDirectorio = f.getName();
                        break;
                    }
                }

                if (!nombreDirectorio.isEmpty()) {
                    System.out.println("\nCambiando al directorio: " + nombreDirectorio);
                    cliente.changeWorkingDirectory(nombreDirectorio);

                    // Listar contenido del nuevo directorio
                    FTPFile[] subFiles = cliente.listFiles();
                    System.out.println("Contenido de " + nombreDirectorio + ":");

                    String ficheroADescargar = "";
                    for (FTPFile sf : subFiles) {
                        System.out.println(sf.getType() + " -> " + sf.getName());
                        // Guardamos el nombre de un fichero (no directorio) para descargar
                        if (sf.isFile()) {
                            ficheroADescargar = sf.getName();
                        }
                    }

                    // 3. Descargar un fichero (si existe alguno)
                    if (!ficheroADescargar.isEmpty()) {
                        System.out.println("\nDescargando fichero: " + ficheroADescargar);
                        cliente.setFileType(FTP.BINARY_FILE_TYPE); // Binario para evitar corrupción

                        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(ficheroADescargar))) {
                            boolean descargado = cliente.retrieveFile(ficheroADescargar, out);
                            if (descargado) {
                                System.out.println("Fichero descargado con éxito.");
                            }
                        }
                    }
                }

                cliente.logout();
            } else {
                System.out.println("Login incorrecto.");
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (cliente.isConnected()) {
                    cliente.disconnect();
                    System.out.println("\n*** Desconectado ***");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}