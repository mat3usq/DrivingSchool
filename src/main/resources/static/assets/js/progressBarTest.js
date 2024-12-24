function generateGradientColors() {
	const stops = [
		{ color: '#ff0000', pos: 0 },
		{ color: '#ff1a00', pos: 1 },
		{ color: '#ff3300', pos: 2 },
		{ color: '#ff4d00', pos: 3 },
		{ color: '#ff6600', pos: 10 },
		{ color: '#ff9933', pos: 15 },
		{ color: '#ffcc00', pos: 20 },
		{ color: '#ffff00', pos: 30 },
		{ color: '#ccff00', pos: 40 },
		{ color: '#66ff66', pos: 50 },
		{ color: '#00ffc9', pos: 60 },
		{ color: '#00ffff', pos: 70 },
		{ color: '#00ccff', pos: 80 },
		{ color: '#00ff80', pos: 90 },
		{ color: '#00ff00', pos: 100 },
	]
	function hexToRgb(hex) {
		const c = hex.replace('#', '')
		return {
			r: parseInt(c.substring(0, 2), 16),
			g: parseInt(c.substring(2, 4), 16),
			b: parseInt(c.substring(4, 6), 16),
		}
	}
	function rgbToHex(r, g, b) {
		const f = v => {
			const h = v.toString(16)
			return h.length === 1 ? '0' + h : h
		}
		return '#' + f(r) + f(g) + f(b)
	}
	function interpolateColor(c1, c2, f) {
		const r = Math.round(c1.r + f * (c2.r - c1.r))
		const g = Math.round(c1.g + f * (c2.g - c1.g))
		const b = Math.round(c1.b + f * (c2.b - c1.b))
		return { r, g, b }
	}
	function getColorForPercentage(p) {
		if (p <= 0) return stops[0].color
		if (p >= 100) return stops[stops.length - 1].color
		let lower = stops[0]
		let upper = stops[stops.length - 1]
		for (let i = 0; i < stops.length - 1; i++) {
			if (p >= stops[i].pos && p <= stops[i + 1].pos) {
				lower = stops[i]
				upper = stops[i + 1]
				break
			}
		}
		const range = upper.pos - lower.pos
		const factor = (p - lower.pos) / range
		const c1 = hexToRgb(lower.color)
		const c2 = hexToRgb(upper.color)
		const c = interpolateColor(c1, c2, factor)
		return rgbToHex(c.r, c.g, c.b)
	}
	const colors = []
	for (let i = 0; i <= 100; i++) {
		colors.push(getColorForPercentage(i))
	}
	return colors
}

function updateProgressBars() {
	const gradientColors = generateGradientColors()
	document.querySelectorAll('.tests-details').forEach(detail => {
		const bar = detail.querySelector('.progres-bar')
		const span = detail.querySelector('.progres-span')
		const value = parseInt(span.textContent)
		bar.style.width = value + '%'
		bar.style.backgroundColor = gradientColors[Math.max(0, Math.min(value, 100))]
	})
}

document.addEventListener('DOMContentLoaded', () => {
	updateProgressBars()
	document.querySelectorAll('.tests-details').forEach(form => {
		form.addEventListener('click', () => {
			form.submit()
		})
	})
})
