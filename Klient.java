import java.net.*;
import java.util.*;
import java.io.*;
import com.thoughtworks.xstream.*;
/**
 * program pobiera z serwerow zserializowane grafy(obj,xml),
 * i wypisuje najkrotsze sciezki metoda Dijkstry lub Bellmana-Forda.
 * argumenty wywolania: nazwa hosta, nr portu serwera 1., nr portu serwera 2.
 * @author Paweł Inglot
 */
//dopasowanie konstruktora krawedzi
class newEdge extends TmpEdge{
	private static final long serialVersionUID = 3L;
	newEdge(int x, int y, int z){	//int zamiast String
		w1=x;
		d=y;
		w2=z;
	}
}

class newGraf extends Graf{
	newGraf(List<Krawedz> EdgesList){
		vertsQueue = new PriorityQueue<Vert>(); 	//inicjalizacja kolejki
		Vert v1, v2;	//zmienne pomocnicze reprezentujące aktualne wierzchołki
		for(Krawedz k:EdgesList){							//modyfikacja w konstruktorze
			tmpListE.add(new newEdge(k.w1,k.length,k.w2));
		}
		//najpierw dodaję wszystkie wierzchołki
		for(Krawedz nowa:EdgesList){		//nowa krawędź z listy
			v1 = findVert(nowa.w1);		//1. wierzchołek bieżącej krawędzi
			v2 = findVert(nowa.w2);		//2. wierzchołek bieżącej krawędzi
			if (v1==null){
				v1 = new Vert(nowa.w1);
				vertsQueue.offer(v1);	//dodaję aktualny wierzchołek do kolejki
			}
			if (v2==null){
				v2 = new Vert(nowa.w2);
				vertsQueue.offer(v2);	//dodaję aktualny wierzchołek do kolejki
			}
			//następnie tworzę listy sąsiedztwa wierzchołków
			v1.adjList.add(new Edge(v2, nowa.length));	//dodaję aktualną krawędź do listy sąsiedztwa
			v2.adjList.add(new Edge(v1, nowa.length));	//dodaję aktualną krawędź do listy sąsiedztwa (graf nieskierowany)
		}
	}
}

class Klient{
	/**
	 * metoda znajdujaca najkrotsza sciezke w grafie
	 * @param deserialised odczytany z serwera graf w postaci obiektu zawierajacego liste trojek
	 * @param iniVert wierzcholek poczatkowy
	 * @param finVert wierzch. koncowy
	 * @throws IOException czytanie z sd input
	 */
	private static void findPath(Generator deserialised,int iniVert, int finVert) throws IOException{
		newGraf graf = new newGraf(deserialised.listaKraw);		//tworzy graf (listy sasiedztwa)
		graf.initialize(iniVert, finVert);		//inicjalizacja
		System.out.println("wybierz metodę: 1)Dijkstra   2)Bellman-Ford");
		int opcja = 0;			//wczytanie wyboru użytkownika
		while (opcja !=49 && opcja != 50)
			opcja = System.in.read();
		boolean czyMozliwe = true;
		if (opcja==49)
			graf.dijkstra();
		if (opcja==50)
			czyMozliwe = graf.ford();
		graf.wypisz(czyMozliwe, opcja);
	}
	
	public static void main(String[] args){		//np. 127.0.0.1 4445 4446
		if (args.length != 3) {
            System.err.println("Usage: Serwer1 <nazwa hosta> <nr portu serwera 1.>"
            		+ "<nr portu serwera 2.>");
            System.exit(1);
        }
		
		String nazwaHosta = args[0];
		int nrPortu1 = Integer.parseInt(args[1]),
			nrPortu2 = Integer.parseInt(args[2]);
		Generator deserialised1 = null,
				  deserialised2 = null;
		//odebranie obiektu z serwera 1. za pomoca ObjectInputStream
		try(Socket klient1 = new Socket(nazwaHosta, nrPortu1);		//tworzy gniazdo klienta
			ObjectInputStream in = new ObjectInputStream(klient1.getInputStream());)
		{
			System.out.println("Odczyt grafu jako obiektu:");
			deserialised1 = (Generator)in.readObject();		//odczyt zserializowanego obiektu
			int iniVert = 3, finVert = 7;			//wierzchołki pocz. i końc.
			findPath(deserialised1, iniVert, finVert);
		}
		catch(UnknownHostException e) {
			System.err.println("Nie znaleziono hosta" + nazwaHosta);
		}
		catch(IOException e) {
		    System.err.println("Nie udalo sie odczytac obiektu dla " + nazwaHosta+ " (serwer 1. nieaktywny)");
		}
		catch (ClassNotFoundException e) {
			System.err.println("Nie udalo sie znalezc klasy");
			e.printStackTrace();
		}
		//odczyt obiektu w postaci xml z serwera drugiego
		try(Socket klient2 = new Socket(nazwaHosta, nrPortu2);		//tworzy gniazdo klienta
			BufferedReader in = new BufferedReader(
								new InputStreamReader(klient2.getInputStream()));)
			{
				System.out.println("Odczyt grafu z xml:");
				XStream xstream = new XStream();		//inicjalizacja obiektu XStream
				deserialised2 = (Generator) xstream.fromXML(in);
				final int iniVert = 3, finVert = 7;			//wierzchołki pocz. i końc.
				findPath(deserialised2, iniVert, finVert);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
