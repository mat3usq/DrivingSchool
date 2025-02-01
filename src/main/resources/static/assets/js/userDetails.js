document.addEventListener('DOMContentLoaded', () => {

    const options = document.querySelectorAll('.payment');
    options.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });


});
