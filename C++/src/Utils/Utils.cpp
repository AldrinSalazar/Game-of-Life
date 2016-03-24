#include <iostream>
#include <string>

namespace utils {
	void logi(std::string mensaje)
	{
		std::cout << "[INFO] - " << mensaje << std::endl;
	}

	void loge(std::string mensaje)
	{
		std::cout << "[ERROR] - " << mensaje << std::endl;
	}

	void logd(std::string mensaje)
	{
		std::cout << "[DEBUG] - " << mensaje << std::endl;
	}

	void pausa()
	{
		std::cin.ignore();
	}
}