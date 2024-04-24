document.addEventListener('DOMContentLoaded', function () {
    const menuBars = document.querySelector('.menu-bars')
    const navLinks = document.querySelectorAll('.navbar-left .nav-link')
    const navBar = document.querySelector('.navbar-left')
    let isMenuOpen = false

    function animateLinks(animationName) {
        let delayTime = 0
        navLinks.forEach(item => {
            item.classList.remove('animation', 'animationReverse')
            item.style.animationDelay = delayTime + 's'
            delayTime += 0.1
            setTimeout(() => item.classList.add(animationName), 10)
        })
    }

    menuBars.addEventListener('click', () => {
        isMenuOpen = !isMenuOpen
        if (isMenuOpen) {
            navBar.classList.remove('animationReverse')
            navBar.classList.add('show', 'animation')
            animateLinks('animation')
        } else {
            animateLinks('animationReverse')
            const lastLink = navLinks[navLinks.length - 1]
            lastLink.addEventListener(
                'animationend',
                () => {
                    if (!isMenuOpen) {
                        navBar.classList.remove('show', 'animation')
                    }
                },
                { once: true }
            )
        }
    })

    navLinks.forEach(link =>
        link.addEventListener('click', () => {
            isMenuOpen = false
            animateLinks('animationReverse')
            const lastLink = navLinks[navLinks.length - 1]
            lastLink.addEventListener(
                'animationend',
                () => {
                    navBar.classList.remove('show')
                },
                { once: true }
            )
        })
    )

    window.addEventListener('resize', () => {
        if (!isMenuOpen) {
            navBar.classList.add('animation')
            navLinks.forEach(item => {
                item.classList.remove('animation', 'animationReverse')
                item.style.animationDelay = '0s'
            })
        }
    })
})
