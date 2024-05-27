document.addEventListener('DOMContentLoaded', () => {
    const rowsInstructor = document.querySelectorAll('.table-instructor-row');
    rowsInstructor.forEach(row => {
        row.addEventListener('click', () => {
            row.classList.toggle('clicked');
        });
    });

    const rowsStudent = document.querySelectorAll('.table-student-row-pending');
    rowsStudent.forEach(row => {
        row.addEventListener('click', () => {
            row.classList.toggle('clicked');
        });
    });
});