import { Component } from '@angular/core';

interface WeatherDay {
  day: string;
  icon: string;
  high: number;
  low: number;
  conditions: string;
  rainChance: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  current = {
    temperature: 72,
    conditions: 'Partly Cloudy',
    icon: '🌤️',
    high: 75,
    low: 58,
    rainChance: 20
  };

  forecast: WeatherDay[] = [
    { day: 'Monday', icon: '☀️', high: 75, low: 58, conditions: 'Sunny', rainChance: 5 },
    { day: 'Tuesday', icon: '🌤️', high: 70, low: 54, conditions: 'Partly Cloudy', rainChance: 10 },
    { day: 'Wednesday', icon: '🌧️', high: 65, low: 51, conditions: 'Showers', rainChance: 70 },
    { day: 'Thursday', icon: '☁️', high: 68, low: 50, conditions: 'Cloudy', rainChance: 25 },
    { day: 'Friday', icon: '☀️', high: 73, low: 55, conditions: 'Sunny', rainChance: 5 },
    { day: 'Saturday', icon: '🌦️', high: 71, low: 56, conditions: 'Light Rain', rainChance: 40 },
    { day: 'Sunday', icon: '🌤️', high: 69, low: 52, conditions: 'Partly Cloudy', rainChance: 15 }
  ];

  selectedDay: WeatherDay | null = null;

  selectDay(day: WeatherDay): void {
    this.selectedDay = day;
  }
}
