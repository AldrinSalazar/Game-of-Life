#include "Estado.h"
#include "Utils.h"

Estado::Estado() {
	contexto_ = nullptr;
}

Estado::Estado(std::string nombre) {
	nombre_ = nombre;

	//Logs
	utils::logi("Estado " + nombre + " creado.");
}

Estado::~Estado() {

}

void Estado::set_contexto(Contexto* contexto) {
	contexto_ = contexto;
}