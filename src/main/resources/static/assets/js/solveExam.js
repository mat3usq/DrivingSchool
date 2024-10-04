document.addEventListener('DOMContentLoaded', function () {
    const skipBtn = document.getElementById('skip');
    const noBtn = document.getElementById('no');
    const yesBtn = document.getElementById('yes');
    const answerA = document.getElementById('answerA');
    const answerB = document.getElementById('answerB');
    const answerC = document.getElementById('answerC');
    const timeToEnd = document.getElementById('timeToEnd');
    const specificTimer = document.getElementById('specificTimer');

    const buttons = [];

    if (noBtn) {
        buttons.push(noBtn);
        noBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'NIE';
        });
    }

    if (yesBtn) {
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

    function startCountdown() {
        let timeText = timeToEnd.innerText.trim();
        let minutes = parseInt(timeText.split('m')[0].trim());
        let seconds = parseInt(timeText.split('m')[1].replace('s', '').trim());

        if (isNaN(minutes) || isNaN(seconds)) {
            minutes = 0;
            seconds = 0;
        }

        const interval = setInterval(function () {
            if (seconds === 0) {
                if (minutes === 0) {
                    clearInterval(interval);
                    return;
                } else {
                    minutes--;
                    seconds = 59;
                }
            } else {
                seconds--;
            }

            timeToEnd.innerText = `${minutes}m ${seconds}s`;
        }, 1000);
    }
    startCountdown();

    function startSpecificTimerCountdown() {
        let seconds = parseInt(specificTimer.innerText.replace('s', '').trim());
        if (isNaN(seconds)) {
            seconds = 50;
        }

        const interval = setInterval(function () {
            if (seconds === 0) {
                clearInterval(interval);
                document.getElementById('form').submit();
                return;
            } else {
                seconds--;
            }

            specificTimer.innerText = `${seconds}s`;
        }, 1000);
    }
    startSpecificTimerCountdown();
});