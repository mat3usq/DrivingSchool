document.addEventListener('DOMContentLoaded', () => {
    const inputFile = document.querySelector('.input-file');

    const sublectureId = inputFile.getAttribute('data-sublecture');
    const subjectId = inputFile.getAttribute('data-subject');
    initializeDragAndDrop(sublectureId, subjectId);

    const deleteButton = document.querySelector('.delete-btn');
    deleteButton.addEventListener('click', function () {
        document.getElementById('deleteSubjectForm').submit();
    });

    const backButton = document.querySelector('.back-btn');
    backButton.addEventListener('click', function () {
        window.location.href = '/lecture';
    });
});
