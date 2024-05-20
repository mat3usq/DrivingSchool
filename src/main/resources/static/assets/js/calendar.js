const calendar = document.querySelector('.calendar'),
    date = document.querySelector('.date'),
    daysContainer = document.querySelector('.days'),
    prev = document.querySelector('.prev'),
    next = document.querySelector('.next'),
    todayBtn = document.querySelector('.today-btn'),
    gotoBtn = document.querySelector('.goto-btn'),
    dateInput = document.querySelector('.date-input')

let today = new Date()
let activeDay
let month = today.getMonth()
let year = today.getFullYear()

const months = [
    'Styczeń',
    'Luty',
    'Marzec',
    'Kwiecień',
    'Maj',
    'Czerwiec',
    'Lipiec',
    'Sierpień',
    'Wrzesień',
    'Październik',
    'Listopad',
    'Grudzień',
]

function initCalendar() {
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)
    const prevLastDay = new Date(year, month, 0)
    const prevDays = prevLastDay.getDate()
    const lastDate = lastDay.getDate()

    let day = firstDay.getDay()
    day = day === 0 ? 6 : day - 1

    const nextDays = 7 - lastDay.getDay()

    date.innerHTML = months[month] + ' ' + year

    let days = ''

    for (let x = day; x > 0; x--) {
        days += `<div class="day prev-date">${prevDays - x + 1}</div>`
    }

    for (let i = 1; i <= lastDate; i++) {
        if (i === new Date().getDate() && year === new Date().getFullYear() && month === new Date().getMonth()) {
            days += `<div class="day today">${i}</div>`
        } else days += `<div class="day">${i}</div>`
    }

    for (let j = 1; j <= nextDays; j++) {
        days += `<div class="day next-date">${j}</div>`
    }
    daysContainer.innerHTML = days
}

initCalendar()

//function to add month and year on prev and next button
function prevMonth() {
    month--
    if (month < 0) {
        month = 11
        year--
    }
    initCalendar()
}

function nextMonth() {
    month++
    if (month > 11) {
        month = 0
        year++
    }
    initCalendar()
}

prev.addEventListener('click', prevMonth)
next.addEventListener('click', nextMonth)

todayBtn.addEventListener("click", () => {
    today = new Date();
    month = today.getMonth();
    year = today.getFullYear();
    initCalendar();
});

dateInput.addEventListener("input", (e) => {
    dateInput.value = dateInput.value.replace(/[^0-9/]/g, "");

    if (dateInput.value.length === 2)
        dateInput.value += "/";

    if (dateInput.value.length > 7)
        dateInput.value = dateInput.value.slice(0, 7);

    if (e.inputType === "deleteContentBackward")
        if (dateInput.value.length === 3 && dateInput.value.includes("/"))
            dateInput.value = dateInput.value.slice(0, 2);
});


gotoBtn.addEventListener("click", gotoDate);

function gotoDate() {
    const dateArr = dateInput.value.split("/");
    if (dateArr.length === 2)
        if (dateArr[0] > 0 && dateArr[0] < 13 && dateArr[1].length === 4) {
            month = dateArr[0] - 1;
            year = dateArr[1];
            initCalendar();
            return;
        }
    alert("Nieprawidłowa Data");
}
