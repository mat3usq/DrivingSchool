document.addEventListener("DOMContentLoaded", function () {
    const deleteButton = document.querySelector('.delete-btn');
    deleteButton.addEventListener('click', function () {
        document.getElementById('deleteLectureForm').submit();
    });

    const backButton = document.querySelector('.back-btn');
    backButton.addEventListener('click', function () {
        window.location.href = '/lecture';
    });
});