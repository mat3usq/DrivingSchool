document.addEventListener('DOMContentLoaded', function () {
	document.getElementById('yes').addEventListener('click', function () {
		document.getElementById('yes').style.border = '2px solid springgreen'
		document.getElementById('yes').style.color = 'springgreen'
		document.getElementById('correct').style.display = 'block'
	})

	document.getElementById('no').addEventListener('click', function () {
		document.getElementById('no').style.border = '2px solid #EF476F'
		document.getElementById('no').style.color = '#EF476F'
		document.getElementById('wrong').style.display = 'block'
		document.querySelector('.explain').style.display = 'block'
	})
})