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

const textarea = document.getElementById('messageInput');
    function autoResize() {
        this.style.height = 'auto'; 
        this.style.height = this.scrollHeight + 'px'; 
    }

    textarea.addEventListener('input', autoResize);