#include "Contexto.h"
#include "Utils.h"

Contexto::Contexto(int ancho, int alto, std::string nombre, std::string titulo)
{
	ancho_ = ancho;
	alto_ = alto;
	nombre_ = nombre;
	titulo_ = titulo;

	//Logs
	utils::logi("Contexto || " + titulo_ + " || " + nombre_);
}

Contexto::~Contexto()
{

}