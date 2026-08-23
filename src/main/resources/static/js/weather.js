document.addEventListener("DOMContentLoaded", () => {

    const weatherElement = document.getElementById("weather");

    if (!weatherElement) {
        return;
    }

    const cachedLocation = localStorage.getItem("weatherLocation");

    if (cachedLocation) {

        const location = JSON.parse(cachedLocation);

        loadWeather(
            location.lat,
            location.lon,
            weatherElement
        );

        return;
    }

    if (!navigator.geolocation) {
        weatherElement.textContent =
            "Локацията не се поддържа.";
        return;
    }

    navigator.geolocation.getCurrentPosition(

        position => {

            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            localStorage.setItem(
                "weatherLocation",
                JSON.stringify({
                    lat: lat,
                    lon: lon
                })
            );

            loadWeather(lat, lon, weatherElement);
        },

        error => {

            console.error("Location error:", error);

            weatherElement.textContent =
                "Необходим е достъп до местоположението.";
        },

        {
            enableHighAccuracy: false,
            timeout: 5000,
            maximumAge: 3600000
        }
    );
});


function loadWeather(lat, lon, weatherElement) {

    console.log("Loading weather:", lat, lon);

    const url =
        `/api/weather/current?lat=${lat}&lon=${lon}`;

    fetch(url)
        .then(response => {

            if (!response.ok) {
                throw new Error(
                    "Weather API error: " + response.status
                );
            }

            return response.json();
        })
        .then(data => {

            console.log("Weather data:", data);

            weatherElement.innerHTML = `
                <span>🌤️ ${data.city}</span>
                <strong>${Number(data.temperature).toFixed(1)}°C</strong>
                <span>💨 ${data.windSpeed} m/s</span>
            `;
        })
        .catch(error => {

            console.error("Weather error:", error);

            weatherElement.textContent =
                "Времето не е налично";
        });
}