import java.util.*;
import java.io.*;

/**
 * Program generujący losowy graf spójny i nieskierowany,
 * o zadanej liczbie krawędzi i wierzchołków.
 * zapisuje graf do pliku txt
 * @author Paweł Inglot
 */


/** klasa reprezentująca krawędź w grafie */
class Krawedz implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	int w1, w2, length;		//wierzchołki i waga krawędzi
	
	/** Konstruktor tworzący nową losową krawędź
	 *  połączoną z którymś z istniejących wierzchołków
	 * @param ile liczba wszystkich wierzchołków (określa górny zakres losowania etykiet)
	 */
	Krawedz(int ile){
		Random los = new Random();
		w1 = los.nextInt(ile);	
		w2 = los.nextInt(ile);
		while (w1==w2) w2 = los.nextInt(ile);	//usuwam krawędzie łączące wierzchołek z nim samym
		length = los.nextInt(300)+1;		//długość krawędzi >0
	}
	/** Konstruktor używany do utworzenia początkowych krawędzi tak,
	 * aby graf zachował swoją spójność.
	 * @param ile liczba wszystkich wierzchołków
	 * @param nr liczba już utworzonych wierzchołków  (określa górny zakres losowania etykiet)
	 */
	Krawedz(int ile, int nr){
		w1 = nr;	//jeden z wierzchołków otrzymuje kolejny numer (aż do osiągnięcia limitu wierzchołków)
		Random los = new Random();
		if (nr==0) w2 = 1;
		else w2 = los.nextInt(nr);		//losuje liczbę od 0 do etykiety wierzch. o najwyższym numerze
		while (w1==w2) w2 = los.nextInt(nr);
		length = new Random().nextInt(300)+1;
	}
	/**
	 * @return zwraca opis pojedynczej krawędzi
	 */
	String wypisz(){
		return this.w1 + "," + this.length + "," + this.w2;
	}
}
//----------------------------------------------------------------------------------------------
/** Klasa umożliwia stworzenie grafu
 * i zapisanie do pliku.
 */
public class Generator implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Krawedz> listaKraw = new ArrayList<Krawedz>();	//lista przechowująca krawędzie
	int ileWierzch = 1000,		//zadana liczba wierzchołków
		ileKraw = 20000,		//zadana liczba krawędzi
		i=0,	//pomocnicze liczniki pętli
		j=0;
	/** konstruktor wypełniający listę krawędziami
	 */
	Generator(){
		while (j<ileWierzch){
			listaKraw.add(new Krawedz(ileWierzch, i));
			i++; j++;
		}
		for(; j<ileKraw; j++)
			listaKraw.add(new Krawedz(ileWierzch));
	}
//---------------------------------------------------------------------------------------------	
	/** metoda zapisująca graf do pliku txt wg formatu
	 * wierzchołek,waga krawędzi,wierzchołek
	 * @param file nazwa pliku do zapisu
	 * @throws FileNotFoundException wyjątek gdy nie odnajdzie pliku
	 */
	private void zapisz(String file) throws FileNotFoundException{

			PrintWriter wyjscie;	
			wyjscie = new PrintWriter(file);	//utworzenie strumienia zapisu
			for (int i=0; i<ileKraw; i++)		//zapis do strumienia
				wyjscie.println(listaKraw.get(i).wypisz());
			wyjscie.close();		//zamknięcie str.

	}
//---------------------------------------------------------------------------------------------
	/** metoda pomocnicza przy zapisie
	 * @throws FileNotFoundException wyjątek gdy nie odnajdzie pliku
	 */
	void doPliku() throws FileNotFoundException{
		final String file = "graf.txt";
		File f = new File(file);	//f - zmienna reprezentująca plik
		if (f.exists()){
			System.out.println("Plik już istnieje. Czy utworzyć nowy? y/n");
			try {
				char opcja = (char) System.in.read();	//pytanie do użytkownika czy korzystać
				if (opcja == 'y')						//z istniejącego pliku
					zapisz(file);
				else System.out.println("pracuję na poprzednim pliku.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else
			zapisz(file);		//wywołanie metody zapisującej do strumienia
	}

//----------------------------------------------------------------------------------------------
	/** pkt wejścia dla aplikacji 
	 * @param args tablica ciągów arg. wywołania
	 * @throws FileNotFoundException wyjątek gdy nie odnajdzie pliku
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Generator gen = new Generator();	//utworzenie generatora grafu
		gen.doPliku();		//wywołanie zapisu do pliku
	}
}
