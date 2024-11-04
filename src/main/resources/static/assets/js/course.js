document.addEventListener('DOMContentLoaded', () => {
    const drivingSessions = document.querySelectorAll('.drivingSession');
    const testCourses = document.querySelectorAll('.testCourse');

    const options = [...drivingSessions, ...testCourses];

    options.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });
});

function deleteComment(commentId) {
    const form = document.getElementById('deleteCommentCourse');
    const hiddenInput = document.getElementById('commentCourseId');
    hiddenInput.value = commentId;
    form.submit();
}