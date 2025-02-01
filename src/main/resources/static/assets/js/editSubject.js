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

    filterSubjects();
});

function filterSubjects() {
    const sublectureSelect = document.getElementById('subject-sublectureSelect');
    const subjectSelect = document.getElementById('subject-subjectSelect');
    const selectedSublectureId = sublectureSelect.value;

    for (let i = 0; i < subjectSelect.options.length; i++) {
        const option = subjectSelect.options[i];
        const sublectureId = option.getAttribute('data-sublecture-id');

        if (sublectureId === selectedSublectureId || sublectureId === null) {
            option.style.display = '';
        } else {
            option.style.display = 'none';
        }
    }
}
