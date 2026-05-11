package com.example.demo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller

public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String getDashboard(Model model) {
        model.addAttribute("fresnoWeather", weatherService.getFresnoWeather());
        model.addAttribute("nyWeather", weatherService.getNewYorkWeather());
        model.addAttribute("londonWeather", weatherService.getLondonWeather());

        return "dashboard";
    }

}
