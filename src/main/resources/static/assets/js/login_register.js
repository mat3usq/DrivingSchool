const sign_in_btn = document.querySelector('#sign-in-btn')
const sign_in_form = document.querySelector('.sign-in-form')
const sign_up_btn = document.querySelector('#sign-up-btn')

const reset_btn = document.querySelector('.reset-text .link')
const reset_form = document.querySelector('.reset-form')

const back_login_btn = document.querySelector('.back-login .link')
const container = document.querySelector('.login .container')

const resetFormElements = reset_form.querySelectorAll(
	'.title, .underline, p, .input-field, .btn, .social-text, .social-media, .back-login'
)
const loginFormElements = sign_in_form.querySelectorAll(
	'.title, .underline, .input-field, label, .btn, .social-text, .social-media, .reset-text'
)

isReset = false

sign_up_btn.addEventListener('click', () => {
	if (isReset) hideReset()
	container.classList.add('sign-up-mode')
})

sign_in_btn.addEventListener('click', () => {
	container.classList.remove('sign-up-mode')
})

const addAnimation = (elements, animation) => {
	elements.forEach((element, index) => {
		setTimeout(() => {
			element.classList.add(animation)
		}, index * 100)
	})
}

const removeAnimation = (elements, animation) => {
	elements.forEach(element => {
		element.classList.remove(animation)
	})
}

const hideReset = () => {
	isReset = false
	removeAnimation(loginFormElements, 'loginAnimationReverse')
	removeAnimation(loginFormElements, 'loginAnimation')

	addAnimation(resetFormElements, 'loginAnimationReverse')
	loginFormElements.forEach(e => (e.style.transform = 'translate(-300%)'))

	setTimeout(() => {
		reset_form.style.display = 'none'
		sign_in_form.style.display = 'flex'
		addAnimation(loginFormElements, 'loginAnimation')
	}, 1000)
}

reset_btn.addEventListener('click', () => {
	isReset = true
	removeAnimation(resetFormElements, 'loginAnimationReverse')
	removeAnimation(resetFormElements, 'loginAnimation')

	addAnimation(loginFormElements, 'loginAnimationReverse')

	resetFormElements.forEach(e => (e.style.transform = 'translate(-300%)'))

	setTimeout(() => {
		sign_in_form.style.display = 'none'
		reset_form.style.display = 'flex'
		addAnimation(resetFormElements, 'loginAnimation')
	}, 1000)
})

back_login_btn.addEventListener('click', hideReset)
