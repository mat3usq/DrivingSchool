let sublectureCounters = 0
let subjectCounters = []

function addSublectureForm() {
    if (sublectureCounters < 10) {
        const container = document.getElementById('sublecturesContainer')
        subjectCounters[sublectureCounters] = 0

        const htmlContent = `
<div class="sublectures-of-lecture" id="sublecture-${sublectureCounters}">
<p class="formTitle">Dodawanie ${sublectureCounters + 1}. Podwykładu</p>
<div class="input-field">
    <i class="fas fa-book"></i>
    <input type="text" name="sublectures[${sublectureCounters}].title" placeholder="Nazwa Podwykładu">
</div>
<div class="input-field">
    <i class="fas fa-align-left"></i>
    <textarea name="sublectures[${sublectureCounters}].content" placeholder="Treść Podwykładu" rows="4"></textarea>
</div>
<div class="box-btn" id="subject-container-${sublectureCounters}">
    <button type="button" class="delete-btn" onclick="removeSublectureForm(${sublectureCounters})">Usuń Podwykład</button>
    <button type="button" class="info-btn" onclick="addSubjectForm(${sublectureCounters})">Dodaj Rozdział</button>
</div>
</div>
`
        container.insertAdjacentHTML('beforeend', htmlContent)
        sublectureCounters++
        autoResizeTextAreas();
    }
}

function removeSublectureForm(sublectureIndex) {
    let sublectureForm = document.getElementById(`sublecture-${sublectureIndex}`)
    sublectureForm.remove()
    sublectureCounters--
    subjectCounters[sublectureIndex] = 0

    updateFollowingSublectures(sublectureIndex)
}

function updateFollowingSublectures(sublectureIndex) {
    let start = sublectureIndex + 1

    while (document.getElementById(`sublecture-${start}`)) {
        const current = start
        const next = current - 1

        let currentSublecture = document.getElementById(`sublecture-${current}`)
        if (!currentSublecture) break

        currentSublecture.id = `sublecture-${next}`

        const titleElement = currentSublecture.querySelector('.formTitle')
        if (titleElement) {
            titleElement.textContent = `Dodawanie ${next + 1}. Podwykładu`
        }

        const inputs = currentSublecture.querySelectorAll('input, textarea')
        inputs.forEach(input => {
            const nameAttr = input.getAttribute('name')
            input.setAttribute('name', nameAttr.replace(`[${current}]`, `[${next}]`))
        })

        const btnContainer = document.getElementById(`subject-container-${current}`)
        btnContainer.id = `subject-container-${next}`
        const btns = btnContainer.querySelectorAll('button')
        btns.forEach(btn => {
            const onClickAttr = btn.getAttribute('onclick')
            btn.setAttribute('onclick', onClickAttr.replace(`(${current})`, `(${next})`))
        })

        for (let i = 0; i < subjectCounters[current]; i++) {
            let currentSubject = document.getElementById(`subject-${current}-${i}`)
            if (currentSubject) {
                currentSubject.id = `subject-${next}-${i}`

                const subjectInputs = currentSubject.querySelectorAll('input, textarea')
                subjectInputs.forEach(input => {
                    const nameAttr = input.getAttribute('name')
                    input.setAttribute('name', nameAttr.replace(`sublectures[${current}]`, `sublectures[${next}]`))
                })

                const deleteButton = currentSubject.querySelector('.delete-btn')
                if (deleteButton) {
                    deleteButton.setAttribute('onclick', `removeSubjectForm(${next}, ${i})`)
                }

                const fileInputDiv = currentSubject.querySelector('.input-file')
                if (fileInputDiv) {
                    fileInputDiv.setAttribute('data-sublecture', next)
                    fileInputDiv.setAttribute('data-subject', i)
                }
            }
        }

        subjectCounters[next] = subjectCounters[current]
        subjectCounters[current] = 0

        start++
    }
}

function addSubjectForm(sublectureIndex) {
    if (subjectCounters[sublectureIndex] < 10) {
        const subjectIndex = subjectCounters[sublectureIndex]
        const btnContainer = document.getElementById(`subject-container-${sublectureIndex}`)
        const htmlContent = `
<div class="subjects-of-sublecture-of-lecture" id="subject-${sublectureIndex}-${subjectIndex}">
    <p class="formTitle">Dodawanie ${subjectIndex + 1}. Rozdziału</p>
    <div class="input-field">
        <i class="fas fa-bookmark"></i>
        <input type="text" name="sublectures[${sublectureIndex}].subjects[${subjectIndex}].title" placeholder="Nazwa Rozdziału">
    </div>
    <div class="input-file" data-sublecture="${sublectureIndex}" data-subject="${subjectIndex}">
        <h3 class="title-file">Prześlij swój plik</h3>
            <div class="drag-area">
                <div class="icon">
                    <i class="fas fa-images"></i>
                </div>
                <span class="header first">Przeciągnij & Upuść</span>
                <span class="header second">lub <span class="button">Zaimportuj</span></span>
                <span class="support">Wspieramy: JPEG, PNG, JPG</span>
            </div>
            <input type="file" hidden name="sublectures[${sublectureIndex}].subjects[${subjectIndex}].file" accept="image/*">
    </div>
    <div class="input-field">
        <i class="fas fa-align-left"></i>
        <textarea name="sublectures[${sublectureIndex}].subjects[${subjectIndex}].content" placeholder="Treść Rozdziału" rows="4"></textarea>
    </div>

    <div class="box-btn">
        <button type="button" class="delete-btn" onclick="removeSubjectForm(${sublectureIndex}, ${subjectIndex})">Usuń Rozdział</button>
    </div>
</div>
`
        btnContainer.insertAdjacentHTML('beforebegin', htmlContent)
        subjectCounters[sublectureIndex]++
        initializeDragAndDrop(sublectureIndex, subjectIndex)
        autoResizeTextAreas();
    }
}

function removeSubjectForm(sublectureIndex, subjectIndex) {
    let subjectForm = document.getElementById(`subject-${sublectureIndex}-${subjectIndex}`)
    subjectForm.remove()
    subjectCounters[sublectureIndex]--
    updateFollowingSubjects(sublectureIndex, subjectIndex)
}

function updateFollowingSubjects(sublectureIndex, subjectIndex) {
    let start = subjectIndex + 1
    const sublecturePrefix = `subject-${sublectureIndex}-`

    while (document.getElementById(`${sublecturePrefix}${start}`)) {
        const current = start
        const next = current - 1
        const currentSubject = document.getElementById(`${sublecturePrefix}${current}`)

        if (!currentSubject) break

        currentSubject.id = `${sublecturePrefix}${next}`

        const titleElement = currentSubject.querySelector('.formTitle')
        if (titleElement) {
            titleElement.textContent = `Dodawanie ${next + 1}. Rozdziału`
        }

        const inputs = currentSubject.querySelectorAll('input, textarea')
        inputs.forEach(input => {
            const nameAttr = input.getAttribute('name')
            if (nameAttr) {
                input.setAttribute('name', nameAttr.replace(`[${current}]`, `[${next}]`))
            }
        })

        const fileInputDiv = currentSubject.querySelector('.input-file')
        if (fileInputDiv) {
            fileInputDiv.setAttribute('data-sublecture', sublectureIndex)
            fileInputDiv.setAttribute('data-subject', next)
        }

        const deleteButton = currentSubject.querySelector('.delete-btn')
        if (deleteButton) {
            deleteButton.setAttribute('onclick', `removeSubjectForm(${sublectureIndex}, ${next})`)
        }

        start++
    }
}

let subjectCounter = 0;

function addFormSubject() {
    if (subjectCounter < 10) {
        const container = document.getElementById('subjectsContainer');
        const subjectIndex = subjectCounter;
        const htmlContent = `
<div class="subjects-of-sublecture" id="subject-${subjectIndex}">
    <p class="formTitle">Dodawanie ${subjectIndex + 1}. Rozdziału</p>
    <div class="input-field">
        <i class="fas fa-bookmark"></i>
        <input type="text" name="subjects[${subjectIndex}].title" placeholder="Nazwa Rozdziału">
    </div>
    <div class="input-file" sublecture-data-subject="${subjectIndex}">
        <h3 class="title-file">Prześlij swój plik</h3>
        <div class="drag-area">
            <div class="icon">
                <i class="fas fa-images"></i>
            </div>
            <span class="header first">Przeciągnij & Upuść</span>
            <span class="header second">lub <span class="button">Zaimportuj</span></span>
            <span class="support">Wspieramy: JPEG, PNG, JPG</span>
        </div>
        <input type="file" hidden name="subjects[${subjectIndex}].file" accept="image/*">
    </div>
    <div class="input-field">
        <i class="fas fa-align-left"></i>
        <textarea name="subjects[${subjectIndex}].content" placeholder="Treść Rozdziału" rows="4"></textarea>
    </div>
    <div class="box-btn">
        <button type="button" class="delete-btn" onclick="removeFormSubject(${subjectIndex})">Usuń Rozdział</button>
    </div>
</div>
`;
        container.insertAdjacentHTML('beforeend', htmlContent);
        subjectCounter++;
        initializeDragAndDropSublecture(subjectIndex);
        autoResizeTextAreas();
    }
}

function removeFormSubject(subjectIndex) {
    const subjectForm = document.getElementById(`subject-${subjectIndex}`);
    subjectForm.remove();
    subjectCounter--;
    updateFollowingSubjectsSublecture(subjectIndex);
}

function initializeDragAndDropSublecture(subjectIndex) {
    const inputFileDiv = document.querySelector(
        `div.input-file[sublecture-data-subject="${subjectIndex}"]`
    );
    const dragArea = inputFileDiv.querySelector('.drag-area');
    const dragText = dragArea.querySelector('.header.first');
    const secondHeader = dragArea.querySelector('.header.second');
    const titleFile = inputFileDiv.querySelector('.title-file');
    const button = inputFileDiv.querySelector('span.button');
    let input = inputFileDiv.querySelector(
        `input[type="file"][name="subjects[${subjectIndex}].file"]`
    );
    let file;

    button.onclick = () => {
        input.click();
    };

    input.addEventListener('change', function () {
        file = this.files[0];
        dragArea.classList.add('active');
        displayFile();
    });

    dragArea.addEventListener('dragover', event => {
        event.preventDefault();
        if (document.elementFromPoint(event.clientX, event.clientY) === dragArea) {
            dragText.textContent = 'Opuść przesyłany Plik';
            dragArea.classList.add('active');
            secondHeader.style.display = 'none';
        }
    });

    dragArea.addEventListener('dragleave', event => {
        const rect = dragArea.getBoundingClientRect();
        if (
            event.clientX < rect.left ||
            event.clientX > rect.right ||
            event.clientY < rect.top ||
            event.clientY > rect.bottom
        ) {
            dragText.textContent = 'Przeciągnij & Upuść';
            dragArea.classList.remove('active');
            secondHeader.style.display = 'block';
        }
    });

    dragArea.addEventListener('drop', event => {
        event.preventDefault();
        const dt = new DataTransfer();
        dt.items.add(event.dataTransfer.files[0]);
        input.files = dt.files;

        file = input.files[0];
        displayFile();
    });

    function displayFile() {
        if (file) {
            let validExtensions = ['image/jpeg', 'image/jpg', 'image/png'];
            if (validExtensions.includes(file.type)) {
                let fileReader = new FileReader();
                fileReader.onload = () => {
                    let fileUrl = fileReader.result;
                    dragArea.innerHTML = `<img src="${fileUrl}" alt="">`;
                };
                fileReader.readAsDataURL(file);

                let fileName = file.name;
                let fileSize = file.size;
                if (fileName.length > 20) {
                    fileName = fileName.substring(0, 20) + '...';
                }
                let fileSizeFormatted = (fileSize / 1024).toFixed(2) + ' KB';
                if (fileSize > 1024 * 1024) {
                    fileSizeFormatted = (fileSize / (1024 * 1024)).toFixed(2) + ' MB';
                }

                titleFile.innerHTML = `Przesłano: ${fileName}, - ${fileSizeFormatted}`;
            } else {
                titleFile.innerHTML = 'Nieprawidłowy plik';
                dragArea.classList.remove('active');
            }
        }
    }
}

function updateFollowingSubjectsSublecture(subjectIndex) {
    let start = subjectIndex + 1;
    while (document.getElementById(`subject-${start}`)) {
        const current = start;
        const next = current - 1;
        const currentSubject = document.getElementById(`subject-${current}`);
        currentSubject.id = `subject-${next}`;
        const titleElement = currentSubject.querySelector('.formTitle');
        titleElement.textContent = `Dodawanie ${next + 1}. Rozdziału`;
        const inputs = currentSubject.querySelectorAll('input, textarea');
        inputs.forEach(input => {
            const nameAttr = input.getAttribute('name');
            input.setAttribute('name', nameAttr.replace(`[${current}]`, `[${next}]`));
        });
        const fileInputDiv = currentSubject.querySelector('.input-file');
        fileInputDiv.setAttribute('sublecture-data-subject', next);
        const deleteButton = currentSubject.querySelector('.delete-btn');
        deleteButton.setAttribute('onclick', `removeFormSubject(${next})`);
        start++;
    }
}

function autoResizeTextAreas() {
    const textareas = document.querySelectorAll('textarea');
    textareas.forEach(textarea => {
        textarea.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    });
}

function filterSublectures() {
    const lectureSelect = document.getElementById('sublecture-lectureSelect');
    const sublectureSelect = document.getElementById('sublecture-sublectureSelect');
    const selectedLectureId = lectureSelect.value;

    for (let i = 0; i < sublectureSelect.options.length; i++) {
        const option = sublectureSelect.options[i];
        const lectureId = option.getAttribute('sublecture-data-lecture-id');

        if (lectureId === selectedLectureId || lectureId === null)
            option.style.display = '';
        else
            option.style.display = 'none';
    }
}

function filterSubjects() {
    const sublectureSelect = document.getElementById('subject-sublectureSelect');
    const subjectSelect = document.getElementById('subject-subjectSelect');
    const selectedSublectureId = sublectureSelect.value;

    for (let i = 0; i < subjectSelect.options.length; i++) {
        const option = subjectSelect.options[i];
        const sublectureId = option.getAttribute('data-sublecture-id');

        if (sublectureId === selectedSublectureId || sublectureId === null) {
            option.style.display = '';
        } else {
            option.style.display = 'none';
        }
    }
}

function initializeDragAndDropSubject() {
    const inputFileDiv = document.querySelector('div#subject-file-form');
    const dragArea = inputFileDiv.querySelector('.drag-area');
    const dragText = dragArea.querySelector('.header.first');
    const secondHeader = dragArea.querySelector('.header.second');
    const titleFile = inputFileDiv.querySelector('.title-file');
    const button = inputFileDiv.querySelector('span.button');
    let input = inputFileDiv.querySelector('input[type="file"]');
    let file;

    button.onclick = () => {
        input.click();
    };

    input.addEventListener('change', function () {
        file = this.files[0];
        dragArea.classList.add('active');
        displayFile();
    });

    dragArea.addEventListener('dragover', event => {
        event.preventDefault();
        if (document.elementFromPoint(event.clientX, event.clientY) === dragArea) {
            dragText.textContent = 'Opuść przesyłany Plik';
            dragArea.classList.add('active');
            secondHeader.style.display = 'none';
        }
    });

    dragArea.addEventListener('dragleave', event => {
        const rect = dragArea.getBoundingClientRect();
        if (
            event.clientX < rect.left ||
            event.clientX > rect.right ||
            event.clientY < rect.top ||
            event.clientY > rect.bottom
        ) {
            dragText.textContent = 'Przeciągnij & Upuść';
            dragArea.classList.remove('active');
            secondHeader.style.display = 'block';
        }
    });

    dragArea.addEventListener('drop', event => {
        event.preventDefault();
        const dt = new DataTransfer();
        dt.items.add(event.dataTransfer.files[0]);
        input.files = dt.files;

        file = input.files[0];
        displayFile();
    });

    function displayFile() {
        if (file) {
            let validExtensions = ['image/jpeg', 'image/jpg', 'image/png'];
            if (validExtensions.includes(file.type)) {
                let fileReader = new FileReader();
                fileReader.onload = () => {
                    let fileUrl = fileReader.result;
                    dragArea.innerHTML = `<img src="${fileUrl}" alt="">`;
                };
                fileReader.readAsDataURL(file);

                let fileName = file.name;
                let fileSize = file.size;
                if (fileName.length > 20) {
                    fileName = fileName.substring(0, 20) + '...';
                }
                let fileSizeFormatted = (fileSize / 1024).toFixed(2) + ' KB';
                if (fileSize > 1024 * 1024) {
                    fileSizeFormatted = (fileSize / (1024 * 1024)).toFixed(2) + ' MB';
                }

                titleFile.innerHTML = `Przesłano: ${fileName}, - ${fileSizeFormatted}`;
            } else {
                titleFile.innerHTML = 'Nieprawidłowy plik';
                dragArea.classList.remove('active');
            }
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
    initializeDragAndDropSubject();
});