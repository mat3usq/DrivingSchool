function updateEvents(date) {
    let events = "";
    eventsArr.forEach((event) => {
        if (
            date === event.day &&
            month + 1 === event.month &&
            year === event.year
        ) {
            event.events.forEach((event) => {
                let actionLink;
                actionLink = `<a href="/calendar/deleteEvent?id=${event.id}"><i class="fas fa-trash delete-icon"></i></a>`;
                events += `
        <div class="event" onclick="toggleEvent(this)">
            ${actionLink}
            <div class="title">
              <i class="fas fa-font"></i>
              <h3 class="event-title">${event.title}</h3>
            </div>
            <div class="subject">
              <i class="fas fa-quote-left"></i>
              <h3 class="event-subject">${event.subject}</h3>
              <i class="fas fa-quote-right"></i>
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
