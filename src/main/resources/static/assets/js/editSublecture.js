document.addEventListener("DOMContentLoaded", function () {
    const deleteButton = document.querySelector('.delete-btn');
    deleteButton.addEventListener('click', function () {
        document.getElementById('deleteSublectureForm').submit();
    });

    const backButton = document.querySelector('.back-btn');
    backButton.addEventListener('click', function () {
        window.location.href = '/lecture';
    });

    filterSublectures();
});

function filterSublectures() {
    const lectureSelect = document.getElementById('sublecture-lectureSelect');
    const sublectureSelect = document.getElementById('sublecture-sublectureSelect');
    const selectedLectureId = lectureSelect.value;

    for (let i = 0; i < sublectureSelect.options.length; i++) {
        const option = sublectureSelect.options[i];
        console.log(option)
        const lectureId = option.getAttribute('sublecture-data-lecture-id');

        if (lectureId === selectedLectureId || lectureId === null)
            option.style.display = '';
        else
            option.style.display = 'none';
    }
}