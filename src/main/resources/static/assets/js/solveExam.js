document.addEventListener('DOMContentLoaded', function () {
    const skipBtn = document.getElementById('skip');
    const noBtn = document.getElementById('no');
    const yesBtn = document.getElementById('yes');
    const answerA = document.getElementById('answerA');
    const answerB = document.getElementById('answerB');
    const answerC = document.getElementById('answerC');

    if (noBtn)
        noBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'NIE';
        });

    if (yesBtn)
        yesBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'TAK';
        });

    if (answerA)
        answerA.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'A';
        });

    if (answerB)
        answerB.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'B';
        });

    if (answerC)
        answerC.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'C';
        });

    if (skipBtn)
        skipBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.getElementById('form').submit();
        });
});