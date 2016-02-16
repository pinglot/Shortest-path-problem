import java.io.*;
import java.util.*;

/** Program wczytujący graf spójny, nieskierowany
 * z pliku txt w postaci wierzchołek,etykieta,wierzchołek.
 * Umożliwia znalezienie najkrótszej ścieżki m. 2 wierzchołkami
 * metodami Dijkstry i Bellmana-Forda.
 * @author Paweł Inglot
 */

/** klasa reprezentująca krawędź w liście sąsiedztwa,
 * tj. zawierająca drugi wierzchołek i wagę krawędzi
 */
class Edge{
	Vert w2;		//wierzchołek 2.
	int waga;
	
	/** Konstruktor
	 * @param y nr 2. wierzchołka
	 * @param z waga krawędzi
	 */
	
	Edge(int y, int z){
		w2 = new Vert(y);
		waga = z;
	}
	
	/** konstruktor kopiujący wierzchołek drugi
	 * @param ref referencja do wierzchołka wcześniej utworzonego
	 * @param len waga krawędzi
	 */
	Edge(Vert ref, int len){
		w2 = ref;
		waga = len;
	}
}
//----------------------------------------------------------------------------------------------
/** klasa reprezentująca wierzchołek w grafie.
 * implementuje porównanie 2 wierzchołków, w celu obsługi kolejki priorytetowej
 */
class Vert implements Comparable<Vert>{
	int numer,			//nr wierzchołka
		d;				//odległość od wierzchołka początkowego
	Vert parent;		//poprzednik danego wierzchołka
	List<Edge> adjList;	//lista sąsiedztwa wierzchołka (zawiera krawędzie)
	/**
	 * konstruktor inicjujący wierzchołek
	 * @param x numer wierzchołka do utworzenia
	 */
	
	Vert(int x){
		numer = x;
		d=Integer.MAX_VALUE;	//początkowo maksymalna wartość
		parent = null;			//pocz. brak
		adjList  = new ArrayList<Edge>();//pocz. pusta lista sąsiedztwa
	}
	
	/** metoda porónująca wierzchołki pod kątem odległości
	 * od wierzchołka początkowego.
	 */
	public int compareTo(Vert x){
		if (this.d < x.d) return -1;	//bieżący w. bliżej początku
		if (this.d > x.d) return +1;	//bieżący jest dalszy
		return 0;	//równe
	}
	
/*	pomocnicza metoda wypisująca
 * String wypisz(){
		return this.numer + "," + this.d;
	}
*/
}
//----------------------------------------------------------------------------------------------
/** pomocnicza klasa reprezentująca krawędzie przy wczytaniu z pliku,
 * aby łatwiej można było przekształcić je do postaci kolejki wierzchołków i listy sąsiedztwa.
 */
class TmpEdge implements java.io.Serializable{
	private static final long serialVersionUID = 5L;
	int w1,w2,d;
	TmpEdge(String x, String y, String z){
		w1=Integer.parseInt(x);
		d=Integer.parseInt(y);
		w2=Integer.parseInt(z);
	}
	TmpEdge(){}
	/*	pomocnicza metoda wypisująca
	String wypisz(){
		return this.w1 + "," + this.w2 + "," + this.d;
	}
	*/
}

class EdgeWW{
	Vert w1,w2;
	int d;
	
	EdgeWW(int x, int y, int z){
		w1=Graf.findVert(x);
		w2=Graf.findVert(y);
		d = z;
	}
}

//----------------------------------------------------------------------------------------------
/** Klasa reprezentująca graf
 */
public class Graf{
	/**kolejka priorytetowa wierzchołków uporządkowanych w rosnącej
	 * kolejności względem odległości od wierzchołka źródłowego
	 */
	static PriorityQueue<Vert> vertsQueue;
	List<TmpEdge> tmpListE = new ArrayList<TmpEdge>();
	List<EdgeWW> edgesList = new ArrayList<EdgeWW>();
	Vert pocz, konc;	//wierzchołki początkowy i końcowy
//---------------------
	/**
	 * metoda wyszukująca wierzchołek w kolejce prioryt.
	 * @param nr numer szukanego wierzchołka
	 * @return referencja do znalezionego w. lub null, gdy nie znaleziono
	 */
	static Vert findVert(int nr){
		for(Vert szukany:vertsQueue){
			if (szukany.numer == nr)
				return szukany;
		}
		return null;
	}
//---------------------	
	Graf(){}
	/** rozbudowany konstruktor, wczytujący graf z pliku, wypełniający kolejkę
	 * oraz tworzący listy sąsiedztwa wierzchołków
	 * @param filename nazwa pliku z grafem
	 * @throws IOException gdy nie znajdzie pliku
	 */
	Graf(String filename) throws IOException {
		vertsQueue = new PriorityQueue<Vert>(); 	//inicjalizacja kolejki
		FileReader fr = new FileReader(filename);	//utworzenie strumienia do odczytu z pliku
		BufferedReader in = new BufferedReader(fr);
		String s;
		StringBuilder sb = new StringBuilder();
		while ((s = in.readLine() ) != null)
			sb.append(s + ",");
		sb.delete(sb.length()-1, sb.length());		//usuwam ostatni znak nowej linii
		in.close();									//zamykam strumień
		String [] items = sb.toString().split(",");	//usuwam przecinki ze Stringów

		//tworzę listę krawędzi przekształcając Stringi z tablicy wczytanej z pliku
		
		for(int i=0; i<items.length; i=i+3){
			tmpListE.add(new TmpEdge(items[i],items[i+1],items[i+2]));
		}
		Vert v1, v2;	//zmienne pomocnicze reprezentujące aktualne wierzchołki
		//najpierw dodaję wszystkie wierzchołki
		for(TmpEdge nowa:tmpListE){		//nowa krawędź z listy
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
			v1.adjList.add(new Edge(v2, nowa.d));	//dodaję aktualną krawędź do listy sąsiedztwa
			v2.adjList.add(new Edge(v1, nowa.d));	//dodaję aktualną krawędź do listy sąsiedztwa (graf nieskierowany)
		}
	}
//--------------------
	/**
	 * @return zwraca liczbę wierzchołków grafu
	 */
	int ileWierzch(){
		return vertsQueue.size();
	}
//---------------------	
	/** Wstępna inicjalizacja wierzchołka początkowego.
	 * Weryfikuje poprawność wierzchołków pocz. i końcowego.
	 * @param iniVert wierzchołek pocz.
	 * @param finVert wierzchołek końc.
	 */
	void initialize(int iniVert, int finVert){
		if(iniVert >=ileWierzch() || finVert >=ileWierzch()){		//gdy błędny numer wierzchołka pocz./końc.
			System.out.println("Któryś z podanych wierzchołków nie istnieje.");
			return;
		}
		for(Vert biezacy:vertsQueue)		//wyszukanie wierzchołka początkowego w kolejce
			if (biezacy.numer == iniVert){
				biezacy.d = 0;				//jego odległość = 0
				biezacy.parent = null;		//brak przodka
				vertsQueue.remove(biezacy);
				vertsQueue.offer(biezacy);	//aktualizacja jego stanu w kolejce
				pocz = biezacy;
				break;						//zakończenie iteracji
			}
		for(Vert v2:vertsQueue) if(v2.numer == finVert) konc = v2;	//odnalezienie w. końc.
	}
//---------------------
	/** Metoda wypisująca najkrótszą ścieżkę i jej długość
	 * @param czyMozliwe w przypadku metody Bellmana-Forda sprawdza,
	 * czy możliwe jest wyznaczenie ścieżki (czy nie występują cykle ujemne)
	 * @param opcja który algorytm wybrano wybrano
	 */
	void wypisz (boolean czyMozliwe, int opcja){
		if (czyMozliwe == false){
			System.out.println("Występują ujemne cykle w grafie.");
			return;
		}
		Deque<Integer> stos = new ArrayDeque<Integer>();	//pomocniczy stos do wypisywania
		Vert tmpV = konc;
		while (tmpV != null){		//od w.końc. podążaj za przodkami i wrzucaj na stos,
			stos.push(tmpV.numer);	//aż osiągniesz nulla
			tmpV=tmpV.parent;
		}

		//wypisanie wyników na ekran
		if (opcja == 49) System.out.println("Metoda Dijkstry.");
		else System.out.println("Metoda Bellmana-Forda. \nNie występują ujemne cykle w grafie.");
		System.out.println("Długość najkrótszej ścieżki z w. "
						+pocz.numer +" do "+konc.numer+" wynosi: "+ konc.d);
		System.out.print(stos.poll());
		while(stos.isEmpty() == false)				//dopóki stos niepusty
			System.out.print( "->" + stos.poll());	//wypisuj
		System.out.println();
	}
	
//---------------------	
	/** obliczenie najkrótszej ścieżki metodą Dijkstry
	 */
	void dijkstra(){
		while (vertsQueue.isEmpty() == false){			//dopóki kolejka zawiera wierzchołki
			Vert NajblizszyWierz=vertsQueue.peek();		//pobierz ten najbliżej źródłowego
			for(Edge biezKraw:NajblizszyWierz.adjList){	//przejdź przez listę sąsiedztwa
				if(biezKraw.w2 == null) continue;		
				if(biezKraw.w2.d > NajblizszyWierz.d+biezKraw.waga){	//relaksacja krawędzi
					biezKraw.w2.d=NajblizszyWierz.d+biezKraw.waga;
					biezKraw.w2.parent = NajblizszyWierz;
					vertsQueue.remove(biezKraw.w2);
					vertsQueue.offer(biezKraw.w2);						//aktualizacja stanu wierzchołka w kolejce
				}
			}
			vertsQueue.remove(NajblizszyWierz);			//usunięcie przerobionego wierzchołka z kolejki
		}
	}
//--------------------
	/** obliczenie najkrótszej ścieżki metodą Bellmana-Forda
	 * sprawdza dodatkowo, czy nie występują ujemne cykle w grafie.
	 * Jeśli tak, to informuje o tym, w przeciwnym wypadku oblicza najkrótszą ścieżkę.
	 * @return false jeśli występują cykle ujemne
	 * @return true jeśli nie występują cykle ujemne i da się obliczyć ścieżkę
	 */	
	boolean ford(){
		for(TmpEdge e:tmpListE){
			edgesList.add(new EdgeWW(e.w1, e.w2, e.d));
			edgesList.add(new EdgeWW(e.w2, e.w1, e.d));
		}
		for(int i=1; i<ileWierzch()-1; i++)
			for(EdgeWW e:edgesList)
				if((e.w1.d!=Integer.MAX_VALUE) && (e.w1.d + e.d < e.w2.d)){
					e.w2.d = e.w1.d + e.d;
					e.w2.parent = e.w1;
				}
		for(EdgeWW e:edgesList)
			if(e.w1.d + e.d < e.w2.d)
				return false;
		return true;
		
	}
/*
 * boolean ford(){
		for(TmpEdge e:tmpListE){
			edgesList.add(new EdgeWW(e.w1, e.w2, e.d));
			edgesList.add(new EdgeWW(e.w2, e.w1, e.d));
		}
		for(int i=1; i<ileWierzch()-1; i++)
			for(EdgeWW e:edgesList)
				if((e.w1.d!=Integer.MAX_VALUE) && (e.w1.d + e.d < e.w2.d)){
					e.w2.d = e.w1.d + e.d;
					e.w2.parent = e.w1;
				}
		for(EdgeWW e:edgesList)
			if(e.w1.d + e.d < e.w2.d)
				return false;
		return true;
		
	}
 */
//----------------------------------------------------------------------------------------------
	/** funkcja główna - main
	 * @param args tablica ciągów arg. wywołania
	 * @throws IOException w przypadku braku pliku do odczytu
	 */
	public static void main(String[] args) throws IOException{
		String plik = "graf.txt";				//plik z grafem
		int iniVert = 3, finVert = 7;			//wierzchołki pocz. i końc.
		Graf graf = new Graf(plik);
		graf.initialize(iniVert, finVert);		//inicjalizacja
		System.out.println("wybierz metodę: 1)Dijkstra   2)Bellman-Ford");
		int opcja = System.in.read();			//wczytanie wyboru użytkownika
		boolean czyMozliwe = true;
		if (opcja==49){
			long startTime = System.nanoTime();
			graf.dijkstra();
			long endTime = System.nanoTime();
			System.out.println("\nTook "+(endTime - startTime)/10e-9 + " s"); 	
		}
		else  if (opcja==50){
			long startTime = System.nanoTime();
			czyMozliwe = graf.ford();
			long endTime = System.nanoTime();
			System.out.println("\nTook "+(endTime - startTime)/10e-9 + " s"); 	
		}
			  else System.out.println("wybrano nieprawidłową opcję");
		graf.wypisz(czyMozliwe, opcja);

	}
}
