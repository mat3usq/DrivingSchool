function updateProgressBars() {
	var testDetails = document.querySelectorAll('.tests-details')

	testDetails.forEach(detail => {
		var progressBar = detail.querySelector('.progres-bar')
		var progressText = detail.querySelector('.progres-span')
		var progress = parseInt(progressText.textContent)

		progressBar.style.width = progress + '%'

		if (progress < 5) {
			progressBar.style.backgroundColor = '#ff0000' // Czerwony
		} else if (progress < 10) {
			progressBar.style.backgroundColor = '#ff4500' // Pomarańczowo-czerwony
		} else if (progress < 15) {
			progressBar.style.backgroundColor = '#ff7f50' // Koralowy
		} else if (progress < 20) {
			progressBar.style.backgroundColor = '#ff8c00' // Ciemnopomarańczowy
		} else if (progress < 25) {
			progressBar.style.backgroundColor = '#ffd700' // Złoty
		} else if (progress < 30) {
			progressBar.style.backgroundColor = '#ffff00' // Żółty
		} else if (progress < 35) {
			progressBar.style.backgroundColor = '#9acd32' // Żółto-zielony
		} else if (progress < 40) {
			progressBar.style.backgroundColor = '#32cd32' // Zielony
		} else if (progress < 45) {
			progressBar.style.backgroundColor = '#00fa9a' // Wiosenny zielony
		} else if (progress < 50) {
			progressBar.style.backgroundColor = '#00ffff' // Turkusowy
		} else if (progress < 55) {
			progressBar.style.backgroundColor = '#1e90ff' // Niebieski
		} else if (progress < 60) {
			progressBar.style.backgroundColor = '#0000ff' // Głęboki niebieski
		} else if (progress < 65) {
			progressBar.style.backgroundColor = '#771eb6' // Indygo
		} else if (progress < 70) {
			progressBar.style.backgroundColor = '#800080' // Purpurowy
		} else if (progress < 75) {
			progressBar.style.backgroundColor = '#9400d3' // Ciemnofioletowy
		} else if (progress < 80) {
			progressBar.style.backgroundColor = '#ff00ff' // Magenta
		} else if (progress < 85) {
			progressBar.style.backgroundColor = '#ff1493' // Różowy
		} else if (progress < 90) {
			progressBar.style.backgroundColor = '#ff69b4' // Jasnoróżowy
		} else if (progress < 95) {
			progressBar.style.backgroundColor = '#ff8c00' // Jasnopomarańczowy
		} else {
			progressBar.style.backgroundColor = '#00ff00' // Jasnozielony
		}
	})
}

document.addEventListener('DOMContentLoaded', updateProgressBars)
