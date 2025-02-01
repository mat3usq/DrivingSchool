const textareas = document.querySelectorAll('textarea');

textareas.forEach(textarea => {
    textarea.addEventListener('input', function () {
        this.style.height = 'auto';
        this.style.height = this.scrollHeight + 'px';
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const formElement = document.getElementById("editable");
    if(formElement)
    {
        formElement.scrollIntoView({behavior: "smooth"});
        window.location.hash = 'editable'
    }
});
