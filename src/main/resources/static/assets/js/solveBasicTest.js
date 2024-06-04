document.addEventListener('DOMContentLoaded', function () {
    const backBtn = document.getElementById('back');
    const skipBtn = document.getElementById('skip');
    const nextBtn = document.getElementById('next');
    const noBtn = document.getElementById('no');
    const yesBtn = document.getElementById('yes');

    if (backBtn)
        backBtn.addEventListener('click', function (event) {
            event.preventDefault();
            window.location.href = '/tests';
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
});