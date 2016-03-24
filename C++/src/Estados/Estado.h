#pragma once
#include "SFML\Graphics.hpp"
#include "SFML\Window\Event.hpp"
#include "Contexto.h"
#include <string>

class Estado
{
public:
	Estado();
	Estado(std::string);
	~Estado();

	std::string nombre_;

	virtual void set_contexto(Contexto*);
	virtual void tick() = 0;
	virtual void render(sf::RenderWindow*) = 0;
	virtual void evento(sf::Event*) = 0;

protected:
	Contexto* contexto_;
};
