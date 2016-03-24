#pragma once
#include <string>
#include <SFML\Graphics.hpp>
#include "Estado.h"
#include "Contexto.h"
#include <chrono>

class Estado;
class Ventana :
	public Contexto
{
public:
	Ventana(std::string, int, int, std::string nombre = "Ventana");
	~Ventana();

	void iniciar();
	void detener();
	void set_estado_actual(Estado*);

private:
	sf::RenderWindow *ventana_;

	sf::Thread *hilo_principal_;
	sf::Thread *hilo_render_;
	sf::Thread *hilo_ticks_;

	/*Duracion de un render*/
	std::chrono::high_resolution_clock::duration duracion_render_;
	std::chrono::high_resolution_clock::duration duracion_tick_;
	std::chrono::high_resolution_clock::duration duracion_eventos_;

	Estado *estado_actual_;

	bool corriendo_;
	bool renderizando_;
	bool actualizando_;
	int descanso_ms_;

	int fps_;
	int f_hechos_;
	int ticks_por_segundo_;
	int fps_maximos_;

	bool validar();
	void bucle_de_juego();
	void tick();
	void render();
	void evento();
	void crear_ventana();
};