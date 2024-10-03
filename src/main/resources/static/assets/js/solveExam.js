document.addEventListener('DOMContentLoaded', function () {
    const skipBtn = document.getElementById('skip');
    const noBtn = document.getElementById('no');
    const yesBtn = document.getElementById('yes');
    const answerA = document.getElementById('answerA');
    const answerB = document.getElementById('answerB');
    const answerC = document.getElementById('answerC');
    const buttons = [];

    if (noBtn) {
        buttons.push(noBtn);
        noBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'NIE';
        });
    }

    if (yesBtn)
    {
        buttons.push(yesBtn);
        yesBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'TAK';
        });
    }

    if (answerA) {
        buttons.push(answerA);
        answerA.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'A';
        });
    }

    if (answerB) {
        buttons.push(answerB);
        answerB.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'B';
        });
    }

    if (answerC) {
        buttons.push(answerC);
        answerC.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'C';
        });
    }

    if (skipBtn)
        skipBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.getElementById('form').submit();
        });

    Object.values(buttons).forEach(button => {
        button.addEventListener("click", () => {
            Object.values(buttons).forEach(btn => btn.classList.remove("active-button"));
            button.classList.add("active-button");
        });
    });
});