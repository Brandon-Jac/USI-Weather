import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { timeout } from 'rxjs';

// Data returned by the Java Spring Boot 7-day endpoint
interface ApiWeatherDay {
  date: string;
  high: number | null;
  low: number | null;
  conditions: string | null;
  icon: string | null;
  precipitationChance: number | null;
}

// Data used by the Angular dashboard
interface WeatherDay {
  date: string;
  day: string;
  high: number | null;
  low: number | null;
  conditions: string;
  iconUrl: string | null;
  rainChance: number | null;
}

// Detailed forecast data returned by Spring Boot
interface ForecastPeriod {
  temperature: number | null;
  temperatureUnit: string | null;
  shortForecast: string | null;
  detailedForecast: string | null;
  windSpeed: string | null;
  windDirection: string | null;
  icon: string | null;
  precipitationChance: number | null;
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
  private cdr = inject(ChangeDetectorRef);

  // Java Spring Boot backend
  private apiUrl = 'http://localhost:8080/api/weather';

  // Page status
  loading = true;
  error = '';

  // Selected-day status
  detailsLoading = false;
  detailsError = '';

  // Live weather data
  forecast: WeatherDay[] = [];
  selectedDay: WeatherDay | null = null;
  selectedDetails: DailyForecast | null = null;

  ngOnInit(): void {
    this.loadForecast();
  }

  // Load the 7-day forecast
  loadForecast(): void {
    this.loading = true;
    this.error = '';

    console.log('Requesting 7-day weather forecast...');

    this.http.get<ApiWeatherDay[]>(
      `${this.apiUrl}/7-day`
    ).pipe(
      timeout(15000)
    ).subscribe({
      next: data => {
        console.log('7-day forecast received:', data);

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
        this.cdr.detectChanges();
      },

      error: err => {
        console.error('7-day forecast error:', err);

        this.forecast = [];
        this.error = 'Unable to load weather forecasts.';
        this.loading = false;

        this.cdr.detectChanges();
      }
    });
  }

  // Load detailed weather for the selected date
  selectDay(day: WeatherDay): void {
    this.selectedDay = day;
    this.selectedDetails = null;
    this.detailsError = '';
    this.detailsLoading = true;

    console.log('Requesting daily forecast for:', day.date);

    this.http.get<DailyForecast>(
      `${this.apiUrl}/daily/${day.date}`
    ).pipe(
      timeout(15000)
    ).subscribe({
      next: data => {
        console.log('Daily forecast received:', data);

        // Only update if this is still the selected day
        if (this.selectedDay?.date === day.date) {
          this.selectedDetails = data;
          this.detailsLoading = false;
          this.cdr.detectChanges();
        }
      },

      error: err => {
        console.error('Daily forecast error:', err);

        if (this.selectedDay?.date === day.date) {
          this.detailsError =
            'Unable to load the detailed forecast. Please try again.';
          this.detailsLoading = false;
          this.cdr.detectChanges();
        }
      }
    });
  }

  // Convert the API date into a weekday name
  private getDayName(date: string): string {
    const [year, month, day] = date.split('-').map(Number);

    const localDate = new Date(year, month - 1, day);

    return localDate.toLocaleDateString('en-US', {
      weekday: 'long'
    });
  }
}

