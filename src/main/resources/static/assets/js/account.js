document.addEventListener('DOMContentLoaded', () => {
    const rows = document.querySelectorAll('.table-student-row');
    rows.forEach(row => {
        row.addEventListener('click', () => {
            row.classList.toggle('clicked');
        });
    });
});