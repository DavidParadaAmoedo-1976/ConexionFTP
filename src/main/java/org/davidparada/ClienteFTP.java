package org.davidparada;


import org.apache.commons.net.ftp.*;

import java.io.IOException;

public class ClienteFTP
{
    public static void main( String[] args ) throws IOException {
        FTPClient cliente = new FTPClient();
        String servidorFtp = "ftp.usc.es";
        System.out.println("Nos conectamos a: " + servidorFtp);
        String usuario = "anonymous";
        String clave = "anonymous";

        try {
            cliente.connect(servidorFtp);
            cliente.enterLocalPassiveMode();

            boolean login = cliente.login(usuario,clave);
            if (login) {
                System.out.println("Login correcto ...");
            } else {
                System.out.println("Login incorrecto ...");
                cliente.disconnect();
                System.exit(1);
            }
            System.out.println();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
