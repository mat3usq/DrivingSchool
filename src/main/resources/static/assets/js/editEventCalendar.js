handleTimeInputInEdit(document.querySelector(".event-time-from "));
handleTimeInputInEdit(document.querySelector(".event-time-to "));

function togglePerson(element) {
    element.classList.toggle('clicked');
}

function handleTimeInputInEdit(inputElement) {
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

function editEvent() {
    const startTimeInput = document.getElementById('start-Time');
    const endTimeInput = document.getElementById('end-Time');
    const startTimeSave = document.getElementById('startTime');
    const endTimeSave = document.getElementById('endTime');

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

    function updateDateTime(dateTimeStr, newTime) {
        const [date, time] = dateTimeStr.split(', ');
        return `${date}, ${newTime}`;
    }

    const updatedStartTime = updateDateTime(startTimeSave.value, startTimeValue);
    const updatedEndTime = updateDateTime(endTimeSave.value, endTimeValue);

    startTimeSave.value = updatedStartTime;
    endTimeSave.value = updatedEndTime;

    document.querySelector('.edit-event-body').submit();
}