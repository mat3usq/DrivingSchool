document.addEventListener('DOMContentLoaded', () => {
    const threeOptions = document.querySelectorAll('.table-three-options');
    threeOptions.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });

    const twoOptions = document.querySelectorAll('.table-two-options');
    twoOptions.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });

    const oneOption = document.querySelectorAll('.table-one-option');
    oneOption.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });
});