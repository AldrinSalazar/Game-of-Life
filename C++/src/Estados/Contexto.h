#pragma once
#include <string>

class Contexto
{
public:
	Contexto(int,int,std::string,std::string);
	~Contexto();
	
	int ancho_;
	int alto_;
	std::string nombre_;
	std::string titulo_;
};

