document.addEventListener('DOMContentLoaded', () => {
    const options = document.querySelectorAll('.drivingSession');
    options.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });
});