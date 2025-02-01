document.addEventListener('DOMContentLoaded', () => {
    const drivingSessions = document.querySelectorAll('.drivingSession');
    const testCourses = document.querySelectorAll('.testCourse');

    const options = [...drivingSessions, ...testCourses];

    options.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });

    const element = document.querySelector('.mini-title-warning');
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
});

function deleteComment(commentId) {
    const form = document.getElementById('deleteCommentCourse');
    const hiddenInput = document.getElementById('commentCourseId');
    hiddenInput.value = commentId;
    form.submit();
}