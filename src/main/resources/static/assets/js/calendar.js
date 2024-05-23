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

let today = new Date(date.textContent);
console.log(date.textContent);
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

let eventsArr = parseEvents();

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
        if(eventsArr)
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
// TODO: ogolnie zamysl jest taki ze wysylasz date i
// TODO: potem controller ciebie spowrotem tutaj przekierowuje
// TODO: to nie dziala musisz to jakos naprawic XD
function prevMonth() {
    if (month === 0)
        window.location.href = `/calendar?month=${12}&year=${year - 1}&day=${activeDay}`;
    else
    window.location.href = `/calendar?month=${month}&year=${year}&day=${activeDay}`;
}

// TODO: to nie dziala musisz to jakos naprawic XD
function nextMonth() {
    if (month === 11)
        window.location.href = `/calendar?month=${1}&year=${year + 1}&day=${activeDay}`;
    else
        window.location.href = `/calendar?month=${month}&year=${year}&day=${activeDay}`;
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
    saveEvents();
}

// Dodawanie wydarzenia
addEventSubmit.addEventListener("click", () => {
    const eventTitle = addEventTitle.value;
    const eventTimeFrom = addEventFrom.value;
    const eventTimeTo = addEventTo.value;
    if (eventTitle === "" || eventTimeFrom === "" || eventTimeTo === "") {
        alert("Wypełnij wszystkie pola");
        return;
    }

    const timeFromArr = eventTimeFrom.split(":");
    const timeToArr = eventTimeTo.split(":");
    if (
        timeFromArr.length !== 2 ||
        timeToArr.length !== 2 ||
        timeFromArr[0] > 23 ||
        timeFromArr[1] > 59 ||
        timeToArr[0] > 23 ||
        timeToArr[1] > 59
    ) {
        alert("Niepoprawny format godzin");
        return;
    }

    // const timeFrom = convertTime(eventTimeFrom);
    // const timeTo = convertTime(eventTimeTo);
    //
    // const newEvent = {
    //     title: eventTitle,
    //     timeFrom: timeFrom,
    //     timeTo: timeTo
    // };
    //
    // let eventAdded = false;
    // if (eventsArr.length > 0) {
    //     eventsArr.forEach((item) => {
    //         if (
    //             item.day === activeDay &&
    //             item.month === month + 1 &&
    //             item.year === year
    //         ) {
    //             item.events.push(newEvent);
    //             eventAdded = true;
    //         }
    //     });
    // }
    //
    // if (!eventAdded) {
    //     eventsArr.push({
    //         day: activeDay,
    //         month: month + 1,
    //         year: year,
    //         events: [newEvent],
    //     });
    // }
    //
    // // dodawanie do bazki elementu eventAdded
    //
    // addEventWrapper.classList.remove("active");
    // addEventTitle.value = "";
    // addEventFrom.value = "";
    // addEventTo.value = "";
    // updateEvents(activeDay);
    //
    // const activeDayEl = document.querySelector(".day.active");
    // if (!activeDayEl.classList.contains("event")) {
    //     activeDayEl.classList.add("event");
    // }
});

function convertTime(time) {
    let timeArr = time.split(":");
    let timeHour = timeArr[0];
    let timeMin = timeArr[1];
    time = timeHour + ":" + timeMin;
    return time;
}

// funkcja do usuwania wydarzenia gdy sie kliknie na niego
eventsContainer.addEventListener("click", (e) => {
    if (e.target.classList.contains("event")) {
        if (confirm("Czy chcial(a)bys usunac te wydarzenie?")) {
            const eventTitle = e.target.children[0].children[1].innerHTML;
            eventsArr.forEach((event) => {
                if (
                    event.day === activeDay &&
                    event.month === month + 1 &&
                    event.year === year
                ) {
                    event.events.forEach((item, index) => {
                        if (item.title === eventTitle) {
                            event.events.splice(index, 1);
                            // tutaj mozna usuwac element z bazki
                            console.log(item)
                        }
                    });

                    if (event.events.length === 0) {
                        eventsArr.splice(eventsArr.indexOf(event), 1);
                        const activeDayEl = document.querySelector(".day.active");
                        if (activeDayEl.classList.contains("event")) {
                            activeDayEl.classList.remove("event");
                        }
                    }
                }
            });
            updateEvents(activeDay);
        }
    }
});

// zapis wydarzen
function saveEvents() {
    localStorage.setItem("events", JSON.stringify(eventsArr));
}

function parseEvents() {
    const eventElements = document.querySelectorAll('.event');
    const eventsArr = [];

    eventElements.forEach(eventEl => {
        const titleEl = eventEl.querySelector('.title');
        const timeEl = eventEl.querySelector('.event-time');

        if (titleEl && timeEl) {
            const title = titleEl.innerText.trim();
            const timeText = timeEl.innerText.trim();

            const [start, end] = timeText.split(' - ').map(time => new Date(time));
            const day = start.getDate();
            const month = start.getMonth() + 1;
            const year = start.getFullYear();
            const timeFrom = start.toTimeString().slice(0, 5);
            const timeTo = end.toTimeString().slice(0, 5);

            let eventDay = eventsArr.find(event => event.day === day && event.month === month && event.year === year);

            if (!eventDay) {
                eventDay = {day, month, year, events: []};
                eventsArr.push(eventDay);
            }

            eventDay.events.push({title, timeFrom, timeTo});

            eventEl.remove();
        }
    });

    return eventsArr;
}