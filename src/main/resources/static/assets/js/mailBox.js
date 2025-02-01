const writeMailBtn = document.querySelector('.write-mail'),
	writeMailWrapper = document.querySelector('.write-mail-wrapper'),
	writeMailCloseBtn = document.querySelector('.close')

if (writeMailBtn && writeMailCloseBtn && writeMailWrapper) {
	writeMailBtn.addEventListener('click', () => {
		writeMailWrapper.classList.toggle('active')
	})
	writeMailCloseBtn.addEventListener('click', () => {
		writeMailWrapper.classList.remove('active')
	})
	document.addEventListener('click', e => {
		if (e.target !== writeMailBtn && !writeMailWrapper.contains(e.target)) writeMailWrapper.classList.remove('active')
	})
}

const textarea = document.getElementById('messageInput')
function autoResize() {
	this.style.height = 'auto'
	this.style.height = this.scrollHeight + 'px'
}

textarea.addEventListener('input', autoResize)

function toggleEvent(element) {
	element.classList.toggle('clicked')
}

const titleElement = document.querySelector('.mail-title')
switch (window.location.pathname) {
	case '/mailBox/read':
		titleElement.textContent = 'Odebrane Wiadomości'
		break
	case '/mailBox/unread':
		titleElement.textContent = 'Nieprzeczytane Wiadomości'
		break
	case '/mailBox/sent':
		titleElement.textContent = 'Wysłane Wiadomości'
		break
	case '/mailBox/trash':
		titleElement.textContent = 'Wiadomości w Koszu'
		break
	case '/mailBox/showMail':
		titleElement.textContent = 'Szczegóły Wiadomości'
		break
	default:
		titleElement.textContent = 'Skrzynka Pocztowa'
		break
}

document.addEventListener('DOMContentLoaded', function() {
	let warningElement = document.querySelector('.mini-title-warning');
	if (warningElement) {
		let writeMailButton = document.querySelector('.write-mail');
		if (writeMailButton) {
			writeMailButton.click();
		}
	}
});

