function updateProgressBar() {
    let progressBar = document.querySelector('.progres-bar')
    let progressText = document.querySelector('.progres span')
    let progress = parseInt(progressText.textContent)

    progressBar.style.width = progress + '%'

    if (progress < 15) {
        progressBar.style.backgroundColor = '#f72585' // Pink
    } else if (progress < 30) {
        progressBar.style.backgroundColor = '#fb8500' // Orange
    } else if (progress < 45) {
        progressBar.style.backgroundColor = '#b5179e' // Dark Pink
    } else if (progress < 60) {
        progressBar.style.backgroundColor = '#7209b7' // Purple
    } else if (progress < 75) {
        progressBar.style.backgroundColor = '#118ab2' // Blue
    } else if (progress < 90) {
        progressBar.style.backgroundColor = '#4cc9f0' // Light Blue
    } else {
        progressBar.style.backgroundColor = '#06d6a0' // Green
    }
}

document.addEventListener('DOMContentLoaded', updateProgressBar)

document.querySelectorAll('.content').forEach(link => {
    link.addEventListener('click', function (e) {
        e.preventDefault()
        const destinationSelector = this.getAttribute('href')
        const destinationElement = document.querySelector(destinationSelector)

        if (destinationElement) {
            destinationElement.setAttribute('tabindex', '-1')
            destinationElement.focus()

            destinationElement.scrollIntoView({
                behavior: 'smooth',
            })

            destinationElement.removeAttribute('tabindex')
        }
    })
})