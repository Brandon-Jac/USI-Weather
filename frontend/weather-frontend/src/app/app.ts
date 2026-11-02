import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface ApiWeatherDay {
  date: string;
  high: number | null;
  low: number | null;
  conditions: string | null;
  icon: string | null;
  precipitationChance: number | null;
}

interface WeatherDay {
  date: string;
  day: string;
  high: number | null;
  low: number | null;
  conditions: string;
  iconUrl: string | null;
  rainChance: number | null;
}

interface ForecastPeriod {
  temperature: number | null;
  temperatureUnit: string | null;
  shortForecast: string | null;
  detailedForecast: string | null;
  windSpeed: string | null;
  windDirection: string | null;
}

interface DailyForecast {
  date: string;
  daytime: ForecastPeriod | null;
  nighttime: ForecastPeriod | null;
}

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/weather';

  loading = true;
  error = '';
  detailsLoading = false;
  detailsError = '';

  // Demo information, NOT live current conditions.
  current = {
    temperature: 72,
    conditions: 'Partly Cloudy',
    icon: '🌤️',
    high: 75,
    low: 58,
    rainChance: 20
  };

  forecast: WeatherDay[] = [];
  selectedDay: WeatherDay | null = null;
  selectedDetails: DailyForecast | null = null;

  ngOnInit(): void {
    this.loadForecast();
  }

  loadForecast(): void {
    this.loading = true;
    this.error = '';

    this.http.get<ApiWeatherDay[]>(
      `${this.apiUrl}/7-day`
    ).subscribe({
      next: data => {
        this.forecast = data.map(item => ({
          date: item.date,
          day: this.getDayName(item.date),
          high: item.high,
          low: item.low,
          conditions: item.conditions ?? 'Unavailable',
          iconUrl: item.icon,
          rainChance: item.precipitationChance
        }));

        this.loading = false;
      },
      error: err => {
        console.error('Weather API error:', err);
        this.error = 'Unable to load weather forecasts.';
        this.loading = false;
      }
    });
  }

  selectDay(day: WeatherDay): void {
    this.selectedDay = day;
    this.selectedDetails = null;
    this.detailsError = '';
    this.detailsLoading = true;

    this.http.get<DailyForecast>(
      `${this.apiUrl}/daily/${day.date}`
    ).subscribe({
      next: data => {
        if (this.selectedDay?.date === day.date) {
          this.selectedDetails = data;
          this.detailsLoading = false;
        }
      },
      error: err => {
        console.error('Daily forecast error:', err);

        if (this.selectedDay?.date === day.date) {
          this.detailsError = 'Daily forecast unavailable.';
          this.detailsLoading = false;
        }
      }
    });
  }

  private getDayName(date: string): string {
    const [year, month, day] = date.split('-').map(Number);
    const localDate = new Date(year, month - 1, day);

    return localDate.toLocaleDateString('en-US', {
      weekday: 'long'
    });
  }
}

