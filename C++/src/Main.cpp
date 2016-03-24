#include "Ventana.h"
#include "Utils.h"
#include "GoL.h"
#include <iostream>

void main(int argc, char *argv[]) {
	int num_celdas;

	if (argc != 2) {
		std::cout << "Uso: " << argv[0] << " <tamaño de tablero>\n";
		exit(-1);
	}
	else {
		num_celdas = std::atoi(argv[1]);
	}
	
	Gol *p = new Gol(num_celdas);

	int ancho = (p->get_tamaño_celda() * num_celdas) + (p->get_separacion() * num_celdas + 1);
	int alto = (p->get_tamaño_celda() * num_celdas) + (p->get_separacion() * num_celdas + 1);

	Ventana *v = new Ventana("GoL", ancho, alto, "GoL, Version C++ @SAldrin");
	v->set_estado_actual(p);

	utils::logi("Alt: inicio/pausa | Ctrl:Aleatorio | Shift:Limpiar");
	utils::logi("Alt: inicio/pausa | Ctrl:Aleatorio | Shift:Limpiar");
	utils::logi("Alt: inicio/pausa | Ctrl:Aleatorio | Shift:Limpiar");
	utils::logi("Alt: inicio/pausa | Ctrl:Aleatorio | Shift:Limpiar");

	v->iniciar();

	utils::logd("Fin de main");
}