#include "Ventana.h"
#include "Utils.h"
#include <sstream>
#include <thread>

/* Encapsula la creacion de la ventana y lleva los tiempos de actualizacion y dibujado
   para cada estado asignado.
*/
Ventana::Ventana(std::string titulo, int ancho, int alto, std::string nombre):
	Contexto(ancho, alto, nombre, titulo)
{
	corriendo_ = false;
	renderizando_ = false;
	actualizando_ = false;
	descanso_ms_ = 50;

	/*FPS logrados efectivamente*/
	fps_ = 0;
	f_hechos_ = 0;
	
	/*Actualizaciones logica por segundo*/
	ticks_por_segundo_ = 10;
	fps_maximos_ = 30;

	/*Iniciar punteros*/
	hilo_principal_ = nullptr;
	estado_actual_ = nullptr;
	ventana_ = nullptr;
	hilo_render_ = nullptr;

	//Logs
	utils::logi("Clase ventana creada");
}

Ventana::~Ventana()
{
	detener();
	delete hilo_principal_;
	delete ventana_;
	delete estado_actual_;
	delete hilo_render_;
}

/*Valida que al iniciar se tengan referencias validas*/
bool Ventana::validar() {
	if (estado_actual_ == nullptr) {
		utils::loge("No se puede iniciar sin estado definido.");
		return false;
	}
	return true;
}

/*Instancia, ejecuta y pausa el hilo actual, la ejecucion del programa
  continua en el bucle de juego
*/
void Ventana::iniciar() {
	if (!validar())
		exit(EXIT_FAILURE);

	corriendo_ = true;
	renderizando_ = true;
	actualizando_ = true;

	hilo_principal_ = new sf::Thread(&Ventana::bucle_de_juego, this);
	hilo_principal_->launch();
	utils::logi("Bucle principal iniciado.");

	/*Aqui termina el hilo de ejecucion de donde viene main, se pausa
	 hasta que hilo_principal_ termine.
	*/
	hilo_principal_->wait();
}

/*Detiene totalmente la ejecucion, efectivamente cerrando el programa*/
void Ventana::detener() {
	hilo_principal_->terminate();
	corriendo_ = false;

	utils::logi("Ventana detenida.");
}

/*Propaga una actualizacion logica a el estado actual*/
void Ventana::tick(){

	/*Cada cuantos segundos se muestra la informacion del bucle*/
	int cada_info = 5;

	/*Ultimo tick*/
	auto ultimo = std::chrono::high_resolution_clock::now();

	/*Ultima informacion mostrada*/
	auto tiempo_info = std::chrono::high_resolution_clock::now();

	/*Cuantos nanosegundos deberia durar 1 actualizacion*/
	double ns = std::chrono::high_resolution_clock::period::den / ticks_por_segundo_;

	/*Ultima actualizacion*/
	double delta = 0;

	while (corriendo_) {

		/*Tiempo desde ultima actualizacion*/
		auto ahora = std::chrono::high_resolution_clock::now();
		delta += (ahora.time_since_epoch().count() - ultimo.time_since_epoch().count()) / ns;
		ultimo = ahora;

		while (delta >= 1) {
			if (actualizando_) {

				/*Medir el tiempo en propagarse el tick a cada entidad*/
				auto principio = std::chrono::high_resolution_clock::now();
				estado_actual_->tick();
				ventana_->setTitle(titulo_);
				auto fin = std::chrono::high_resolution_clock::now();

				duracion_tick_ = fin - principio;
			}

			/*Un segundo menos, aun queda cierta cantidad de tiempo en el delta dependiendo
			de la presicion del reloj.
			*/
			delta -= 1;
		}

		std::this_thread::sleep_for(std::chrono::milliseconds(descanso_ms_));

		/*Cada cierta cantidad de segundos se actualiza la informacion de los FPS logrados*/
		if (std::chrono::high_resolution_clock::now() - tiempo_info > std::chrono::seconds(cada_info)) {

			tiempo_info += std::chrono::seconds(cada_info);
			std::stringstream log;

			fps_ = f_hechos_ / cada_info;
			f_hechos_ = 0;

			//Logs
			log << fps_ <<
				"FPS, " <<
				std::chrono::duration_cast<std::chrono::milliseconds>(duracion_tick_).count() <<
				"ms tick, " <<
				std::chrono::duration_cast<std::chrono::milliseconds>(duracion_render_).count() <<
				"ms render, " <<
				std::chrono::duration_cast<std::chrono::milliseconds>(duracion_eventos_).count() <<
				"ms eventos.";
			utils::logi(log.str());
		}
	}
}

/*Propaga una actualizacion grafica al estado actual, y da un fondo
  de ventana.
*/
void Ventana::render(){

	while( ventana_->isOpen() && renderizando_) {
		/*Medir el tiempo en hacer un dibujado*/
		auto inicio = std::chrono::high_resolution_clock::now();

		/*TODO: Pasar render a otro hilo, para no bloquearse en eventos continuos*/
		ventana_->clear(sf::Color::White);
		estado_actual_->render(ventana_);
		auto fin = std::chrono::high_resolution_clock::now();

		/*display() incluye un delay para dormir el CPU y limitar la cantidad de FPS maximos*/
		ventana_->display();
		f_hechos_++;

		duracion_render_ = fin - inicio;
	}
}

/*Envia los eventos al estado actual luego de revisar si el evento
  es para cerrar la ventana, en ese caso la cierra.
*/
void Ventana::evento() {
	sf::Event evento;

	while(ventana_->pollEvent(evento)) {
		if (evento.type == sf::Event::Closed) {
			detener();
			ventana_->close();
		}
		estado_actual_->evento(&evento);
	}
}

/*Crea la ventana en si, con su contexto OpenGL interno*/
void Ventana::crear_ventana() {
	ventana_ = new sf::RenderWindow(sf::VideoMode(ancho_, alto_), titulo_);

	/*Limite de cuadros por segundo*/
	ventana_->setFramerateLimit(fps_maximos_);

	//Logs
	std::stringstream log;
	sf::Vector2u tamaño_efectivo = ventana_->getSize();
	log << "Ventana creada [" << std::to_string(ancho_) << "x" << std::to_string(alto_) << "] - " <<
		"Tamaño efectivo [" << tamaño_efectivo.x << "x" << tamaño_efectivo.y << "]" << std::endl;
	utils::logi(log.str());
}

/*Mantiene viva la ejecucion del programa y lleva los tiempos de actualizacion logica
  mide los tiempos de cada proceso y los FPS efectivos logrados.
*/
void Ventana::bucle_de_juego() {
	/*
		El contexto OpenGL tiene que ser creado en el mismo hilo que se manipula
	*/
	crear_ventana();
	ventana_->setActive(false);

	/*Un hilo para cada proceso asi no se bloquea la ventana al arrastrarla*/
	hilo_render_ = new sf::Thread(&Ventana::render, this);
	hilo_render_->launch();

	hilo_ticks_ = new sf::Thread(&Ventana::tick, this);
	hilo_ticks_->launch();

	while (corriendo_) {
		auto principio_evento = std::chrono::high_resolution_clock::now();
		evento();
		auto fin_evento = std::chrono::high_resolution_clock::now();

		duracion_eventos_ = fin_evento - principio_evento;

		std::this_thread::sleep_for(std::chrono::milliseconds(descanso_ms_));
	}

	/*Terminar el hilo principal detiene totalmente la ejecucion*/
	detener();
}

/*Establece el estado actual del juego y a ese estado se le establece el contexto en el que esta
  corriendo
*/
void Ventana::set_estado_actual(Estado *e) {
	estado_actual_ = e;
	estado_actual_->set_contexto(this);

	//Logs
	utils::logi("Añadido contexto a estado.");
}