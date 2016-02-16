import com.thoughtworks.xstream.*;

import java.io.*;
import java.net.*;
/**
 * Program tworzy serwer i nasluchuje na porcie zadanym jako argument.
 * Serializacja grafu przy pomocy zewnetrzenj biblioteki xstream do formatu xml.
 * @author Paweł Inglot
 */
class Serwer2{	//np.4446
	public static void main(String[] args){
		if (args.length != 1) {
	            System.err.println("Usage: Serwer2 <nr portu>");
	            System.exit(1);
	        }
		int nrPortu = Integer.parseInt(args[0]);
		try(
			ServerSocket gniazdoSerwera = new ServerSocket(nrPortu);		//tworzy serwer
			Socket gniazdoDoKlienta = gniazdoSerwera.accept();				//tworzy gniazdo do klienta
			//do przeslania grafu w formacie xml odpowiedni jest strumien tekstowy polaczony z gniazdem do klienta
			PrintWriter out = new PrintWriter(gniazdoDoKlienta.getOutputStream());
		){
			Generator gen = new Generator();
			XStream xstream = new XStream();	//inicjalizacja obiektu xstream(wymagane biblioteki xmlpull,xpp3)
			String xml = xstream.toXML(gen);	//zapis grafu w formacie xml
			out.println(xml);					//przeslanie do strumienia
		}
		catch (IOException e){
			System.out.println("Exception caught when trying to listen on port "
			+ nrPortu + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}
