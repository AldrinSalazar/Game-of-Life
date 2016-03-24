#include "GoL.h"
#include "SFML\Graphics.hpp"
#include "Utils.h"
#include <stdlib.h>

/* Simulacion de GoL, toma un numero de celdas y si el tablero sera infinito
   (al llegar al borde aparece del otro lado).
*/
Gol::Gol(int numero_celdas, bool infinito) : Estado("Prueba"){
	/*Semilla aleatoria para crear estados al azar*/
	srand(time(NULL));

	generacion_ = 0;
	infinito_ = infinito;
	tamaño_de_celda_ = 15;
	celula_ = nullptr;
	color_celula_ = sf::Color::Blue;

	/*Separacion entre celdas*/
	separacion_ = 1;
	corriendo_ = false;
	numero_de_celdas_ = numero_celdas;

	/*Si bien el tablero es una matriz de dos dimensiones preferi trabajarlo en
	  un array continuo, se accede con la formula:
	  
	  tablero[x][y] => tablero[y * K + x]

	  donde K es el numero de celdas.
	*/
	tablero_ = new int[numero_de_celdas_*numero_de_celdas_];

	/*Inicia los valores del tablero*/
	iniciar_tablero();

	/*Para optimizar el dibujado, se usa una sola celula dibujada de formas
	  diferentes, ya que no cambia la forma basica.
	*/
	crear_celula();
}

/*Retorna el valor en la posicion dada, valida los limites del array y, si el tablero
  es infinito se encarga de retornar el valor del lado opuesto.
*/
int Gol::valor_en(int x, int y) {
	/*Array base 0*/
	int aux = numero_de_celdas_ - 1;

	if (infinito_) {
		/*Si es el ultimo retorna el primero y viceversa*/
		x = x > aux ? 0 : (x < 0 ? aux : x);
		y = y > aux ? 0 : (y < 0 ? aux : y);

		return tablero_[y*numero_de_celdas_ + x];
	}
	else {
		/*Si se sale de los limites retorna 0*/
		return (x < 0 || x > aux || y < 0 || y > aux) ? 0 : tablero_[y*numero_de_celdas_ + x];
	}
}

/*Para facilitar la manipulacion del array, en el arrat t, posicion x,y coloca el
  valor val, se podria hacer un objeto especial y sobrecargar el operador [], o usar
  algun contenedor de la std:: 
*/
void Gol::colocar_valor_en(int* t, int x, int y, int val) {
	t[y*numero_de_celdas_ + x] = val;
}

/*Todos los valores del tablero se vuelven 0*/
void Gol::iniciar_tablero() {
	for (int i = 0; i < numero_de_celdas_; i++) {
		for (int j = 0; j < numero_de_celdas_; j++) {
			colocar_valor_en(tablero_, i, j, 0);
		}
	}
}

Gol::~Gol() {
	delete celula_;
	delete tablero_;
}

/*Tick de la interfaz Estado que implementa, cada tick simula un ciclo*/
void Gol::tick() {
	if (corriendo_) {
		contexto_->titulo_ = "Corriendo | Generacion #"+std::to_string(generacion_);
		ciclo();
		generacion_++;
	}
	else {
		contexto_->titulo_ = "Pausa | Generacion #" + std::to_string(generacion_);
	}
}

/*Render de la interfaz Estado que implementa*/
void Gol::render(sf::RenderWindow* objetivo) {
	for (int i = 0; i < numero_de_celdas_; i++) {
		for (int j = 0; j < numero_de_celdas_; j++) {
			if (valor_en(i, j) == 0) {
				/*Si esta muerta solo se ve el borde, relleno transparente*/
				celula_->setFillColor(sf::Color::Transparent);
			}
			else {
				/*Si esta viva se ve el borde y el relleno azul*/
				celula_->setFillColor(color_celula_);
			}
			celula_->setPosition(i*(separacion_ + tamaño_de_celda_), j*(separacion_ + tamaño_de_celda_));
			objetivo->draw(*celula_);
		}
	}
}

/*Crea la unica celula que si dibuja de manera diferentes para representar graficamente
  el estado del tablero
*/
void Gol::crear_celula() {
	celula_ = new sf::RectangleShape(sf::Vector2f(tamaño_de_celda_, tamaño_de_celda_));

	/*El borde es hacia adentro para no alterar el tamaño*/
	celula_->setOutlineThickness(-1);
	celula_->setOutlineColor(color_celula_);
}

/*Procesa el click en una posicion, para cambiar el estado de una cierta celula*/
void Gol::mouse_click(int x, int y) {
	if (!corriendo_) {
		/**
		* Si se tiene un cuadrado de tamaño NxN, dividido K numero de celdas, y se quiere saber para cada
		* punto P(x,y) la respectiva celda S(i,j), sbaiendo que 0 <= x,y < N y 0 <= i,j < K, matemagicamente
		* obtenemos la celda con la formula:
		*
		*      i = xK/N | j = yK/N
		*
		* De esta forma no es necesario iterar sobre cada celda para saber en cual esta el mouse, efectivamente
		  cambiando el valor de una celda en O(1)
		*/

		int px = x*numero_de_celdas_ / contexto_->ancho_;
		int py = y*numero_de_celdas_ / contexto_->alto_;

		//valor = !valor
		colocar_valor_en(tablero_, px, py, valor_en(px, py) ? 0 : 1);

		//Logs
		utils::logd("Click x:" + std::to_string(px) + " y:" + std::to_string(py));
	}
}

/*Evento de la interfaz Estado que implementa, procesa los eventos que se envian desde 
  la ventana
*/
void Gol::evento(sf::Event* evento) {

	/*En caso de click (cualquier click)*/
	if (evento->type == sf::Event::MouseButtonPressed) {
		mouse_click(evento->mouseButton.x, evento->mouseButton.y);

	} /*En caso de tecla (cualquier tecla)*/
	else if (evento->type == sf::Event::KeyPressed) {
		/*Ctrl izquierdo*/
		if (evento->key.code == sf::Keyboard::LControl) {
			random();
		}/*Shift izquierdo*/
		else if (evento->key.code == sf::Keyboard::LShift) {
			fin();
		}/*Alt izquierdo*/
		else if (evento->key.code == sf::Keyboard::LAlt) {
			inicio();
		}
	}
}

int Gol::get_separacion() {
	return separacion_;
}

int Gol::get_tamaño_celda() {
	return tamaño_de_celda_;
}

/*Mas que iniciar alterna el estado entre corriendo/pausa*/
void Gol::inicio(){
	corriendo_ = !corriendo_;
}

/*Pausa y limpia el tablero*/
void Gol::fin(){
	if (!corriendo_) {
		iniciar_tablero();
		generacion_ = 0;
	}
}

/*Genera un tablero al azar*/
void Gol::random(){
	if (!corriendo_) {
		for (int i = 0; i < numero_de_celdas_; i++) {
			for (int j = 0; j < numero_de_celdas_; j++) {
				colocar_valor_en(tablero_, i, j, rand() % 2);
			}
		}

		generacion_ = 0;
	}
}

/*Simula un ciclo*/
void Gol::ciclo() {
	/*Se necesita escribir en un tablero nuevo mientras se lee en el actual para no interferir
	  celulas recien nacidas con las ya existentes y viceversa
	*/
	int *nuevo_tablero = new int[numero_de_celdas_*numero_de_celdas_];

	for (int i = 0; i < numero_de_celdas_; i++) {
		for (int j = 0; j < numero_de_celdas_; j++) {
			colocar_valor_en(nuevo_tablero, i, j, reglas(alrededor(i, j), valor_en(i, j)));
		}
	}

	/* /!\ Liberar el tablero viejo /!\*/
	delete tablero_;

	tablero_ = nuevo_tablero;
}

/**
* Dado el numero de vecinos de una celula, y si esta se encuentra viva o muerta
* se interpretan las reglas y se decide si vive o muere en la siguiente generacion.
*/
int Gol::reglas(int vecinos, int celula) {

	/*
	  - Una célula muerta con exactamente 3 células vecinas vivas "nace" (al turno siguiente estará viva).
      - Una célula viva con 2 ó 3 células vecinas vivas sigue viva, en otro caso muere 
	    o permanece muerta (por "soledad" o "superpoblación").
	*/
	if (celula == 0) {
		return vecinos == 3 ? 1 : 0;
	}
	else {
		return !(vecinos>3 || vecinos<2);
	}

}

/* Retorna el numero de celulas vivas adyacentes a una posicion
*/
int Gol::alrededor(int x, int y) {
	int resultado = 0;

	resultado += valor_en(x - 1, y - 1);
	resultado += valor_en(x, y - 1);
	resultado += valor_en(x + 1, y - 1);

	resultado += valor_en(x - 1, y);
	resultado += valor_en(x + 1, y);

	resultado += valor_en(x, y + 1);
	resultado += valor_en(x + 1, y + 1);
	resultado += valor_en(x - 1, y + 1);

	return resultado;
}