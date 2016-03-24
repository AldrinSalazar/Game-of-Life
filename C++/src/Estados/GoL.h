#pragma once
#include "Estado.h"
#include "SFML\Window.hpp"
#include "SFML\Window\Event.hpp"

class Gol : public Estado {
public:
	Gol(int, bool infinito = true);
	~Gol();

	int tamaño_de_celda_;
	int numero_de_celdas_;
	int separacion_;

	void tick();
	void render(sf::RenderWindow*);
	void evento(sf::Event*);

	int get_tamaño_celda();
	int get_separacion();

private:
	int* tablero_;
	int generacion_;
	bool corriendo_;
	bool infinito_;
	sf::RectangleShape* celula_;
	sf::Color color_celula_;

	void iniciar_tablero();
	int valor_en(int, int);
	void colocar_valor_en(int*, int, int, int);
	void mouse_click(int,int);
	void inicio();
	void fin();
	void random();

	void ciclo();
	int reglas(int,int);
	int alrededor(int, int);

	void crear_celula();
};