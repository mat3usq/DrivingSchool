document.addEventListener('DOMContentLoaded', function () {
    const nav = document.querySelector('.navbar')
    const navLinks = document.querySelectorAll('.nav-link')
    const navBar = document.querySelector('.navbar-collapse')
    const menuBars = document.getElementById('menuBars')

    function addShadow() {
        if (window.scrollY >= 20) nav.classList.add('shadow-bg')
        else nav.classList.remove('shadow-bg')
    }

    navLinks.forEach(link => link.addEventListener('click', () => navBar.classList.remove('show')))
    window.addEventListener('scroll', addShadow)
    menuBars.addEventListener('click', () => {
        nav.classList.add('shadow-bg')
    })
})

document.addEventListener("DOMContentLoaded", function () {
    const url = new URL(window.location.href);

    if (url.pathname === '/register' && new URLSearchParams(url.search).get('register') === 'false') {
        const signUpBtn = document.getElementById('sign-up-btn');
        if (signUpBtn) {
            signUpBtn.click();
        }
    } else if (url.pathname === '/login' && new URLSearchParams(url.search).get('error') === 'true') {
        window.location.hash = 'login';
    } else if (url.pathname === '/login' && new URLSearchParams(url.search).get('logout') === 'true') {
        window.location.hash = 'login';
    }
});
