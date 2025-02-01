const date = document.querySelector('.date'),
    daysContainer = document.querySelector('.days'),
    dateInput = document.querySelector('.date-input'),
    eventDay = document.querySelector(".event-day"),
    eventDate = document.querySelector(".event-date"),
    addEventBtn = document.querySelector('.add-event'),
    addEventWrapper = document.querySelector('.add-event-wrapper'),
    addEventCloseBtn = document.querySelector('.close'),
    addEventFrom = document.querySelector(".event-time-from "),
    addEventTo = document.querySelector(".event-time-to ")

let today = new Date(date.textContent);
let activeDay = (Number(eventDate.innerHTML.slice(0, 2)));
let month = today.getMonth();
let year = today.getFullYear();
const months = [
    'Sty',
    'Lut',
    'Mar',
    'Kwi',
    'Maj',
    'Cze',
    'Lip',
    'Sie',
    'Wrz',
    'Paź',
    'Lis',
    'Gru',
];
const days = [
    'Pon.',
    'Wt.',
    'Śr.',
    'Czw.',
    'Pt.',
    'Sob.',
    'Niedz.',
];
let eventsArr = parseEvents();

initCalendar();
updateEvents(activeDay);
if (addEventFrom && addEventTo) {
    handleTimeInput(addEventFrom);
    handleTimeInput(addEventTo);
}

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
        if (eventsArr)
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
                daysHtml += `<div class="day today event" id="day-${i}">${i}</div>`;
            } else {
                daysHtml += `<div class="day today" id="day-${i}">${i}</div>`;
            }
        } else {
            if (event) {
                daysHtml += `<div class="day event" id="day-${i}">${i}</div>`;
            } else {
                daysHtml += `<div class="day" id="day-${i}">${i}</div>`;
            }
        }
    }

    for (let j = 1; j <= nextDays; j++) {
        daysHtml += `<div class="day next-date">${j}</div>`;
    }
    daysContainer.innerHTML = daysHtml;
    addListener();
}

function addListener() {
    const days = document.querySelectorAll(".day");
    days.forEach((day) => {
        day.addEventListener("click", (e) => {
            const target = e.target;
            if (!target.classList.contains("prev-date") && !target.classList.contains("next-date")) {
                updateEvents(Number(target.innerHTML));
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

function handleTimeInput(inputElement) {
    inputElement.addEventListener('input', e => {
        inputElement.value = inputElement.value.replace(/[^0-9:]/g, '');

        if (inputElement.value.length >= 2 && !inputElement.value.includes(':'))
            inputElement.value = inputElement.value.slice(0, 2) + ':' + inputElement.value.slice(2);

        if (inputElement.value.length > 5)
            inputElement.value = inputElement.value.slice(0, 5);

        if (e.inputType === 'deleteContentBackward' && inputElement.value.length === 3 && inputElement.value.includes(':'))
            inputElement.value = inputElement.value.slice(0, 2);

        const [hours, minutes] = inputElement.value.split(':');

        if (hours && (parseInt(hours) < 0 || parseInt(hours) > 23))
            inputElement.value = '23' + (minutes ? ':' + minutes : '');

        if (minutes && (parseInt(minutes) < 0 || parseInt(minutes) > 59))
            inputElement.value = hours + ':59';
    });
}

function getActiveDay(date) {
    const day = new Date(year, month, date);
    let dayOfWeek = day.getDay();
    dayOfWeek = (dayOfWeek === 0) ? 6 : dayOfWeek - 1;
    eventDay.innerHTML = days[dayOfWeek];
    eventDate.innerHTML = date + " " + months[month] + " " + year;
}

function toggleEvent(element) {
    element.classList.toggle('clicked');
}

function parseEvents() {
    const eventElements = document.querySelectorAll('.event');
    const eventsArr = [];

    eventElements.forEach(eventEl => {
        const elId = eventEl.querySelector('.id');
        const timeEl = eventEl.querySelector('.eventTime');
        elId.remove()
        timeEl.remove()

        if (elId && timeEl) {
            const id = elId.innerText.trim();
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

            eventDay.events.push({id, timeFrom, timeTo});

            eventEl.classList.add('hidden');
        }
    });

    return eventsArr;
}

function updateEvents(date) {
    let empty = true;
    document.getElementById(`day-${date}`).click()
    eventsArr.forEach((event) => {
        if (
            date === event.day &&
            month + 1 === event.month &&
            year === event.year
        ) {
            empty = false;
            event.events.forEach(e => document.getElementById('event-' + e.id).classList.remove("hidden"))
        } else
            event.events.forEach(e => document.getElementById('event-' + e.id).classList.add("hidden"))
    });

    if (empty)
        document.querySelector(".no-event").classList.remove("hidden")
    else
        document.querySelector(".no-event").classList.add("hidden")
}

function submitFormWithDate() {
    document.getElementById('dateInputToday').value = new Date().toISOString();
}

function processDate() {
    const userDateValue = document.getElementById('userDateInput').value;
    const [userMonth, userYear] = userDateValue.split('/');
    if (userMonth && userYear) {
        const userDate = new Date(userYear, userMonth - 1, 1);
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear();
        const currentMonth = currentDate.getMonth();

        let yearDifference = userDate.getFullYear() - currentYear;
        let monthDifference = userDate.getMonth() - currentMonth;

        if (monthDifference < 0) {
            yearDifference--;
            monthDifference += 12;
        }

        monthDifference += yearDifference * 12;
        document.getElementById('dateInputGoTo').value = currentDate.toISOString();
        document.getElementById('monthDifference').value = monthDifference;
    }
}

function addEvent() {
    const startTimeInput = document.getElementById('start-Time');
    const endTimeInput = document.getElementById('end-Time');

    let startTimeValue = startTimeInput.value;
    let endTimeValue = endTimeInput.value;

    function isValidTime(time) {
        const timePattern = /^([01]\d|2[0-3]):([0-5]\d)$/;
        return timePattern.test(time);
    }

    function getCurrentTime() {
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        return `${hours}:${minutes}`;
    }

    if (!isValidTime(startTimeValue)) {
        startTimeValue = getCurrentTime();
        startTimeInput.value = startTimeValue;
    }

    if (!isValidTime(endTimeValue)) {
        endTimeValue = getCurrentTime();
        endTimeInput.value = endTimeValue;
    }

    const startDateTime = `${year}-${String(month + 1).padStart(2, '0')}-${String(activeDay).padStart(2, '0')}T${startTimeValue}:00`;
    const endDateTime = `${year}-${String(month + 1).padStart(2, '0')}-${String(activeDay).padStart(2, '0')}T${endTimeValue}:00`;

    document.getElementById('startTime').value = startDateTime;
    document.getElementById('endTime').value = endDateTime;
}


dateInput.addEventListener('input', e => {
    let value = dateInput.value.replace(/[^0-9]/g, '');

    if (value.length >= 1) {
        const firstDigit = parseInt(value[0]);
        if (firstDigit > 1)
            value = '0' + value;
    }

    if (value.length >= 2) {
        const month = parseInt(value.slice(0, 2));
        if (month < 1 || month > 12)
            value = value.slice(0, 1);
    }

    if (value.length >= 2 && !value.includes('/'))
        value = value.slice(0, 2) + '/' + value.slice(2);

    if (value.length > 7)
        value = value.slice(0, 7);

    if (e.inputType === 'deleteContentBackward' && value.length === 3 && value.includes('/'))
        value = value.slice(0, 2);

    dateInput.value = value;
});

if (addEventBtn && addEventCloseBtn && addEventWrapper) {
    addEventBtn.addEventListener('click', () => {
        addEventWrapper.classList.toggle('active');
    });
    addEventCloseBtn.addEventListener('click', () => {
        addEventWrapper.classList.remove('active');
    });
    document.addEventListener('click', e => {
        if (e.target !== addEventBtn && !addEventWrapper.contains(e.target))
            addEventWrapper.classList.remove('active');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const hiddenStartTime = document.getElementById('startTime');
    const hiddenEndTime = document.getElementById('endTime');

    function extractTime(value) {
        const parts = value.split(', ');
        if (parts.length === 2) {
            return parts[1];
        }
        return null;
    }

    function extractDay(value) {
        const parts = value.split(', ');
        if (parts.length === 2) {
            const datePart = parts[0];
            const dateParts = datePart.split('.');
            if (dateParts.length === 3) {
                return parseInt(dateParts[0], 10);
            }
        }
        return null;
    }

    if (hiddenStartTime && hiddenStartTime.value) {
        const startTime = extractTime(hiddenStartTime.value);
        if (startTime) {
            const startTimeInput = document.getElementById('start-Time');
            if (startTimeInput) {
                startTimeInput.value = startTime;
            }
        }
    }

    if (hiddenEndTime && hiddenEndTime.value) {
        const endTime = extractTime(hiddenEndTime.value);
        if (endTime) {
            const endTimeInput = document.getElementById('end-Time');
            if (endTimeInput) {
                endTimeInput.value = endTime;
            }
        }
    }

    let day = null;
    if (hiddenStartTime && hiddenStartTime.value) {
        day = extractDay(hiddenStartTime.value);
    } else if (hiddenEndTime && hiddenEndTime.value) {
        day = extractDay(hiddenEndTime.value);
    }

    if (day !== null) {
        const dayElement = document.getElementById(`day-${day}`);
        if (dayElement) {
            dayElement.click();
        }
    }

    const warningElement = document.querySelector('.mini-title-warning');
    if (warningElement) {
        const addButton = document.querySelector('.add-event');
        if (addButton) {
            addButton.click();
        }
    }
});

