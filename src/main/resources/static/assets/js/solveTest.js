document.addEventListener('DOMContentLoaded', function () {
    const backBtn = document.getElementById('back');
    const skipBtn = document.getElementById('skip');
    const nextBtn = document.getElementById('next');
    const noBtn = document.getElementById('no');
    const yesBtn = document.getElementById('yes');
    const answerA = document.getElementById('answerA');
    const answerB = document.getElementById('answerB');
    const answerC = document.getElementById('answerC');

    if (backBtn)
        backBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'BACK';
            document.getElementById('form').submit();
        });

    if (skipBtn)
        skipBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'SKIP';
            document.getElementById('form').submit();
        });

    if (nextBtn)
        nextBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'NEXT';
            document.getElementById('form').submit();
        });

    if (noBtn)
        noBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'NIE';
            document.getElementById('form').submit();
        });

    if (yesBtn)
        yesBtn.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'TAK';
            document.getElementById('form').submit();
        });

    if (answerA)
        answerA.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'A';
            document.getElementById('form').submit();
        });

    if (answerB)
        answerB.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'B';
            document.getElementById('form').submit();
        });

    if (answerC)
        answerC.addEventListener('click', function (event) {
            event.preventDefault();
            document.querySelector('input[name="action"]').value = 'C';
            document.getElementById('form').submit();
        });
});
if (document.querySelector(".star"))
    document.querySelector(".star").addEventListener("click", function () {
        const starElement = this;
        const orangeCircle = document.querySelector(".c-orange");
        const innerCircle = document.querySelector(".inner-circle");
        const circles = [".c-1", ".c-2", ".c-3", ".c-4"];
        const isLiked = "-is-running";

        if (starElement.classList.contains(isLiked)) {
            document.querySelector('input[name="isLiked"]').value = 'false';
            const allEl = document.querySelectorAll(`.${isLiked}`);
            allEl.forEach(item => {
                item.classList.remove(isLiked);
            });

            const starBox = starElement.parentElement;
            starBox.removeChild(starElement);

            const newStar = document.createElement("i");
            newStar.className = "fas fa-star star";
            newStar.id = "star";

            starBox.appendChild(newStar);

            newStar.addEventListener("click", arguments.callee);
        } else {
            document.querySelector('input[name="isLiked"]').value = 'true';
            starElement.classList.add(isLiked);
            orangeCircle.classList.add(isLiked);
            innerCircle.classList.add(isLiked);
            circles.forEach(circle => {
                const element = document.querySelector(circle);
                element.classList.add(isLiked);
                element.style.animation = 'none';
                element.offsetHeight;
                element.style.animation = '';
            });
        }
    });

document.addEventListener("DOMContentLoaded", function () {
    if (document.getElementById("isLiked")) {
        let isLiked = document.getElementById("isLiked").getAttribute("data-is-liked");
        if (isLiked === "true")
            document.getElementById("star").click();
    }
});
