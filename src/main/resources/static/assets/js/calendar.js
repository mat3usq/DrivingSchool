const calendar = document.querySelector('.calendar'),
    date = document.querySelector('.date'),
    daysContainer = document.querySelector('.days'),
    prev = document.querySelector('.prev'),
    next = document.querySelector('.next'),
    todayBtn = document.querySelector('.today-btn'),
    gotoBtn = document.querySelector('.goto-btn'),
    dateInput = document.querySelector('.date-input'),
    eventDay = document.querySelector(".event-day"),
    eventDate = document.querySelector(".event-date"),
    eventsContainer = document.querySelector(".events"),
    addEventBtn = document.querySelector('.add-event'),
    addEventWrapper = document.querySelector('.add-event-wrapper'),
    addEventCloseBtn = document.querySelector('.close'),
    addEventTitle = document.querySelector(".event-name "),
    addEventFrom = document.querySelector(".event-time-from "),
    addEventTo = document.querySelector(".event-time-to "),
    addEventSubmit = document.querySelector(".add-event-btn ");

let today = new Date();
let activeDay;
let month = today.getMonth();
let year = today.getFullYear();

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
];

const days = [
    'Pon',
    'Wt',
    'Śr',
    'Czw',
    'Pt',
    'Sob',
    'Ndz',
];

const eventsArr = [
    {
        day: 20,
        month: 5,
        year: 2024,
        events: [
            {
                title: "Event 1 lorem ipsun dolar sit genfa tersd dsad ",
                timeFrom: "10:00",
                timeTo: "12:00",
            },
            {
                title: "Event 2",
                timeFrom: "11:00",
                timeTo: "12:00",
            },
        ],
    },
    {
        day: 24,
        month: 5,
        year: 2024,
        events: [
            {
                title: "Event 1 lorem ipsun dolar sit genfa tersd dsad ",
                timeFrom: "10:00",
                timeTo: "12:00",
            },
        ],
    },
];

function initCalendar() {
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const prevLastDay = new Date(year, month, 0);
    const prevDays = prevLastDay.getDate();
    const lastDate = lastDay.getDate();

    let day = firstDay.getDay();
    day = day === 0 ? 6 : day - 1;

    const nextDays = 7 - lastDay.getDay();

    date.innerHTML = months[month] + ' ' + year;

    let daysHtml = '';

    for (let x = day; x > 0; x--) {
        daysHtml += `<div class="day prev-date">${prevDays - x + 1}</div>`;
    }

    for (let i = 1; i <= lastDate; i++) {
        let event = false;
        eventsArr.forEach((eventObj) => {
            if (
                eventObj.day === i &&
                eventObj.month === month + 1 &&
                eventObj.year === year
            ) {
                event = true;
            }
        });
        if (
            i === new Date().getDate() &&
            year === new Date().getFullYear() &&
            month === new Date().getMonth()
        ) {
            activeDay = i;
            getActiveDay(i);
            if (event) {
                daysHtml += `<div class="day today event">${i}</div>`;
            } else {
                daysHtml += `<div class="day today">${i}</div>`;
            }
        } else {
            if (event) {
                daysHtml += `<div class="day event">${i}</div>`;
            } else {
                daysHtml += `<div class="day">${i}</div>`;
            }
        }
    }

    for (let j = 1; j <= nextDays; j++) {
        daysHtml += `<div class="day next-date">${j}</div>`;
    }
    daysContainer.innerHTML = daysHtml;
    addListner();
}

// zmiana miesiąca
function prevMonth() {
    month--;
    if (month < 0) {
        month = 11;
        year--;
    }
    initCalendar();
}

function nextMonth() {
    month++;
    if (month > 11) {
        month = 0;
        year++;
    }
    initCalendar();
}

prev.addEventListener('click', prevMonth);
next.addEventListener('click', nextMonth);

initCalendar();
updateEvents(activeDay);

// ustawienie listenerów na dni
function addListner() {
    const days = document.querySelectorAll(".day");
    days.forEach((day) => {
        day.addEventListener("click", (e) => {
            const target = e.target;
            updateEvents(Number(target.innerHTML));

            if (target.classList.contains("prev-date")) {
                prevMonth();
                setTimeout(() => {
                    const newDays = document.querySelectorAll(".day");
                    newDays.forEach((newDay) => {
                        if (
                            !newDay.classList.contains("prev-date") &&
                            newDay.innerHTML === target.innerHTML
                        ) {
                            newDay.classList.add("active");
                            activeDay = Number(newDay.innerHTML);
                            getActiveDay(activeDay);
                        }
                    });
                }, 100);
            } else if (target.classList.contains("next-date")) {
                nextMonth();
                setTimeout(() => {
                    const newDays = document.querySelectorAll(".day");
                    newDays.forEach((newDay) => {
                        if (
                            !newDay.classList.contains("next-date") &&
                            newDay.innerHTML === target.innerHTML
                        ) {
                            newDay.classList.add("active");
                            activeDay = Number(newDay.innerHTML);
                            getActiveDay(activeDay);
                        }
                    });
                }, 100);
            } else {
                days.forEach((day) => {
                    day.classList.remove("active");
                });
                target.classList.add("active");
                activeDay = Number(target.innerHTML);
                getActiveDay(activeDay);
            }
        });
    });
}

// przejście do dzisiejszego dnia
todayBtn.addEventListener('click', () => {
    today = new Date();
    month = today.getMonth();
    year = today.getFullYear();
    initCalendar();
});

// walidacja na goto
dateInput.addEventListener('input', e => {
    dateInput.value = dateInput.value.replace(/[^0-9]/g, '');

    if (dateInput.value.length >= 2 && !dateInput.value.includes('/')) {
        dateInput.value = dateInput.value.slice(0, 2) + '/' + dateInput.value.slice(2);
    }

    if (dateInput.value.length > 7) {
        dateInput.value = dateInput.value.slice(0, 7);
    }

    if (e.inputType === 'deleteContentBackward') {
        if (dateInput.value.length === 3 && dateInput.value.includes('/')) {
            dateInput.value = dateInput.value.slice(0, 2);
        }
    }
});

gotoBtn.addEventListener('click', gotoDate);

function gotoDate() {
    const dateArr = dateInput.value.split('/');
    if (dateArr.length === 2) {
        if (dateArr[0] > 0 && dateArr[0] < 13 && dateArr[1].length === 4) {
            month = dateArr[0] - 1;
            year = dateArr[1];
            initCalendar();
            return;
        }
    }
    alert('Nieprawidłowa Data');
}

// ustawienie listenerów na guziki
addEventBtn.addEventListener('click', () => {
    addEventWrapper.classList.toggle('active');
});

addEventCloseBtn.addEventListener('click', () => {
    addEventWrapper.classList.remove('active');
});

document.addEventListener('click', e => {
    if (e.target !== addEventBtn && !addEventWrapper.contains(e.target)) {
        addEventWrapper.classList.remove('active');
    }
});

// walidacja formularza
addEventTitle.addEventListener("input", () => {
    addEventTitle.value = addEventTitle.value.slice(0, 60);
});

function handleTimeInput(inputElement) {
    inputElement.addEventListener('input', e => {
        inputElement.value = inputElement.value.replace(/[^0-9]/g, '');

        if (inputElement.value.length >= 2 && !inputElement.value.includes(':')) {
            inputElement.value = inputElement.value.slice(0, 2) + ':' + inputElement.value.slice(2);
        }

        if (inputElement.value.length > 5) {
            inputElement.value = inputElement.value.slice(0, 5);
        }

        if (e.inputType === 'deleteContentBackward') {
            if (inputElement.value.length === 3 && inputElement.value.includes(':')) {
                inputElement.value = inputElement.value.slice(0, 2);
            }
        }
    });
}

handleTimeInput(addEventFrom);
handleTimeInput(addEventTo);

// ustawienie aktywnej daty
function getActiveDay(date) {
    const day = new Date(year, month, date);
    let dayOfWeek = day.getDay();
    dayOfWeek = (dayOfWeek === 0) ? 6 : dayOfWeek - 1;
    eventDay.innerHTML = days[dayOfWeek];
    eventDate.innerHTML = date + " " + months[month] + " " + year;
}

// funkcja do wyswietlania wydarzen
function updateEvents(date) {
    let events = "";
    eventsArr.forEach((event) => {
        if (
            date === event.day &&
            month + 1 === event.month &&
            year === event.year
        ) {
            event.events.forEach((event) => {
                events += `<div class="event">
            <div class="title">
              <i class="fas fa-circle"></i>
              <h3 class="event-title">${event.title}</h3>
            </div>
            <div class="event-time">
              <span class="event-time">${event.timeFrom} - ${event.timeTo}</span>
            </div>
        </div>`;
            });
        }
    });
    if (events === "") {
        events = `<div class="no-event">
            <h3>No Events</h3>
        </div>`;
    }
    eventsContainer.innerHTML = events;
}
