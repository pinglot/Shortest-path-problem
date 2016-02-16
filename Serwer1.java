import java.io.*;
import java.net.*;
/**
 * Program tworzy serwer i nasluchuje na porcie zadanym jako argument.
 * wywolany generuje losowy graf, serializuje go, a nastepnie przesyla w postaci obiektu 
 * @author Paweł Inglot
 *
 */
class Serwer1{	//np. 4445
	public static void main(String[] args){
		if (args.length != 1) {
	            System.err.println("Usage: Serwer1 <nr portu>");
	            System.exit(1);
	        }
		int nrPortu = Integer.parseInt(args[0]);
		try(
			ServerSocket gniazdoSerwera = new ServerSocket(nrPortu);	//tworzy serwer na danym porcie
			Socket gniazdoDoKlienta = gniazdoSerwera.accept();			//tworzy gniazdo komunikacji z klientem
			//otwieram strumien (z domyslnej biblioteki javy) sluzacy do serializacji obiektu
			//i ustawiam jego ujscie na gniazdo do klienta
			ObjectOutputStream out = new ObjectOutputStream(gniazdoDoKlienta.getOutputStream());
		){
			Generator gen = new Generator();		//generuj graf
			out.writeObject(gen);					//serializacja
		}
		catch (IOException e){
			System.out.println("Exception caught when trying to listen on port "
			+ nrPortu + " or listening for a connection");
			System.out.println(e.getMessage());
		}
   
	}
}
